package com.blogspot.toomuchcoding.spock.subjcollabs
import org.codehaus.groovy.reflection.ClassInfo
import org.spockframework.runtime.extension.IMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation
import org.spockframework.runtime.model.FieldInfo
import org.spockframework.runtime.model.MethodInfo
import spock.lang.Specification

import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Type

/**
 * 
 * <h1>Collaborator injection to Subjects</h1>
 *
 * The injection of collaborators to subjects (yes, you can annotate several objects with {@link Subject @Subject} annotation) is done in the following order:
 *
 * <h2>Inject via Constructor</h2>
 *
 * The biggest constructor is chosen, then arguments are resolved with {@link Collaborator @Collaborator} annotated fields. If there is not even one matching constructor argument that is of 
 * one of the {@link Collaborator @Collaborator} annotated object's type then injection via constructor will not happen. 
 *
 * Note: If arguments can not be found, then null is passed
 *
 * <h2>Inject via Setter</h2>
 *
 * Subject is instantiated and its setter methods are used to find types that can be injected. Basing on those types matching {@link Collaborator @Collaborator} annotated objects are picked.
 *
 * Note 1: If you have properties with the same type (or same erasure), it's better to name all {@link Collaborator @Collaborator} annotated fields with the matching properties, otherwise injection might not happen.
 * Note 2: If {@link Subject @Subject} instance wasn't initialized before and have a no-arg constructor, then it will be initialized with this constructor.
 *
 * <h2>Inject via Property</h2>
 *
 * Field injection; collaborators will first be resolved by type, then, if there is several property of the same type, by the match of the field name and the mock name.
 *
 * Note 1: If you have fields with the same type (or same erasure), it's better to name all {@link Collaborator @Collaborator} annotated fields with the matching fields, otherwise injection might not happen.
 * Note 2: If {@link Subject @Subject} instance wasn't initialized before and have a no-arg constructor, then it will be initialized with this constructor.
 */
class SubjectsCollaboratorsInterceptor implements IMethodInterceptor {

    private static Comparator<Constructor> CONSTRUCTOR_COMPARATOR = [compare: {
        Constructor a, Constructor b -> a.parameterTypes.size().compareTo(b.parameterTypes.size())
    }] as Comparator<Constructor>

    private static final List<String> SETTERS_TO_IGNORE = ["setMetaClass"]

    // TODO: There has to be a better way to get ridd of those kind of dynamic methods
    private static final List<String> TYPE_REGEXP_IN_PROP_INJECTION_TO_IGNORE = ["\$class\$", "\$callSiteArray","_\$stMC", "metaClass", "this\$0"]

    private List<FieldInfo> fields = []

    public void install(MethodInfo method) {
        method.addInterceptor(this)    
    }
    
    public void add(FieldInfo fieldInfo) {
        fields.add(fieldInfo)
    }

    @Override
    void intercept(IMethodInvocation invocation) {
        Specification specInstance = SubjectsCollaboratorsUtils.getSpec(invocation)
        Collection<Field> injectionCandidates = getInjectionCandidates(specInstance)
        fields.each {
            // either by constructor injection, setter injection, or property injection
            tryToInjectCandidatesIntoSubject(injectionCandidates, specInstance, it)
        }
        invocation.proceed()
    }

    private void tryToInjectCandidatesIntoSubject(Collection<Field> injectionCandidates, Specification specInstance, FieldInfo fieldInfo) {
        boolean injectionSuccess = tryToInjectViaConstructor(injectionCandidates, specInstance, fieldInfo)
        if (!injectionSuccess) {
            injectionSuccess = tryToInjectViaSetters(injectionCandidates, specInstance, fieldInfo)
        }
        if (!injectionSuccess) {
            tryToInjectViaProperties(injectionCandidates, specInstance, fieldInfo)
        }
    }

    private boolean tryToInjectViaConstructor(Collection<Field> injectionCandidates, Specification specInstance, FieldInfo fieldInfo) {
        try {
            Constructor constructorWithMaxParams = findConstructorWithMaxParams(fieldInfo)
            if (!atLeastOneInjectionCandidateClassExistsAsConstructorParameter(constructorWithMaxParams, injectionCandidates)) {
                return false
            }
            Object[] parameters = collectAllParametersFromCandidatesSetNullIfMissing(constructorWithMaxParams, injectionCandidates, specInstance)
            Object instantiatedSubject = constructorWithMaxParams.newInstance(parameters)
            fieldInfo.writeValue(specInstance, instantiatedSubject)
            return true
        }
        catch (Exception e) {
            return false;
        }
    }

    private Constructor findConstructorWithMaxParams(FieldInfo fieldInfo) {
        Constructor constructor = fieldInfo.type.constructors.max(CONSTRUCTOR_COMPARATOR)
        constructor.accessible = true
        return constructor
    }

    private Object[] collectAllParametersFromCandidatesSetNullIfMissing(Constructor constructorWithMaxParams, Collection<Field> injectionCandidates, Specification specInstance) {
        Collection parameters = []
        constructorWithMaxParams.genericParameterTypes.each {
            Field field = getMatchingInjectionCandidateIfOneExists(injectionCandidates, it)
            parameters << (field == null ? field : specInstance[field.name])
        }
        return parameters.toArray()
    }

    private boolean atLeastOneInjectionCandidateClassExistsAsConstructorParameter(Constructor constructorWithMaxParams, Collection<Field> injectionCandidates) {
        constructorWithMaxParams.genericParameterTypes.any {
            return getMatchingInjectionCandidateIfOneExists(injectionCandidates, it)
        }
    }

    private Field getMatchingInjectionCandidateIfOneExists(Collection<Field> injectionCandidates, Type parameterType) {
        return injectionCandidates.find {
            return it.genericType == parameterType
        }
    }

    private boolean tryToInjectViaSetters(Collection<Field> injectionCandidates, Specification specInstance, FieldInfo fieldInfo) {
        // Property setter injection; mocks will first be resolved by type, then, if there is several property of the same type
        try {
            Object subject = instantiateSubjectAndSetOnSpecification(specInstance, fieldInfo)
            List<Method> setters = getAllSettersFromSubject(fieldInfo)
            Map matchingSetters = getMatchingSettersBasingOnTypeAndPropertyName(injectionCandidates, setters)
            if (matchingSetters.isEmpty()) {
                return false
            }
            matchingSetters.each { Method method, Field injectionCandidate ->
                method.invoke(subject, specInstance[injectionCandidate.name])
            }
            return true
        } catch (Exception e) {
            return false
        }
    }

    private Map getMatchingSettersBasingOnTypeAndPropertyName(Collection<Field> injectionCandidates, List<Method> setters) {
        Map matchingSetters = [:]
        injectionCandidates.each { Field injectionCandidate ->
            setters.each {
                if (it.genericParameterTypes.size() == 1 && it.genericParameterTypes[0] == injectionCandidate.type) {
                    // if there is several property of the same type by the match of the property name and the mock name.
                    if (matchingSetters[it] && it.name.substring(3).equalsIgnoreCase(injectionCandidate.name)) {
                        matchingSetters[it] = injectionCandidate
                    } else if (!matchingSetters[it]) {
                        matchingSetters[it] = injectionCandidate
                    }
                }
            }
        }
        return matchingSetters
    }

    private List<Method> getAllSettersFromSubject(FieldInfo fieldInfo) {
        return fieldInfo.type.methods.findAll {
            return it.name.startsWith("set") && !SETTERS_TO_IGNORE.contains(it.name)
        }
    }

    private Object instantiateSubjectAndSetOnSpecification(Specification specInstance, FieldInfo fieldInfo) {
        final Object subject
        Constructor constructorWithMinArgs = fieldInfo.type.constructors.min(CONSTRUCTOR_COMPARATOR)
        if (constructorWithMinArgs.parameterTypes.size() == 0) {
            subject = fieldInfo.type.newInstance()
        } else {
            // must be inner class or some nonmatching constructor
            subject = fieldInfo.type.newInstance(null)
        }
        specInstance[fieldInfo.name] = subject
        return subject
    }

    private boolean tryToInjectViaProperties(Collection<Field> injectionCandidates, Specification specInstance, FieldInfo fieldInfo) {
        // Field injection; mocks will first be resolved by type, then, if there is several property of the same type, by the match of the field name and the mock name.
        // Note 1: If you have fields with the same type (or same erasure), it's better to name all @Mock annotated fields with the matching fields, otherwise Mockito might get confused and injection won't happen.
        Object subject = instantiateSubjectAndSetOnSpecification(specInstance, fieldInfo)
        List<Field> fields = getAllFieldsFromSubject(fieldInfo)
        Map matchingFields = getMatchingFieldsBasingOnTypeAndPropertyName(injectionCandidates, fields)
        matchingFields.each { Field field, Field injectionCandidate ->
            subject[injectionCandidate.name] = specInstance[injectionCandidate.name]
        }
   }

    private List<Field> getAllFieldsFromSubject(FieldInfo fieldInfo) {
        List<Field> fields = []
        fieldInfo.type.declaredFields.each { Field field ->
            if( TYPE_REGEXP_IN_PROP_INJECTION_TO_IGNORE.every { return !field.name.contains(it) }
                && field.type != ClassInfo) {
                fields << field
            }
        }
        return fields
    }

    private Map getMatchingFieldsBasingOnTypeAndPropertyName(Collection<Field> injectionCandidates, List<Field> allFields) {
        Map matchingFields = [:]
        injectionCandidates.each { Field injectionCandidate ->
            allFields.each {
                if (it.type == injectionCandidate.type) {
                    // if there is several property of the same type by the match of the property name and the mock name.
                    if (matchingFields[it] && it.name.equalsIgnoreCase(injectionCandidate.name)) {
                        matchingFields[it] = injectionCandidate
                    } else if (!matchingFields[it]) {
                        matchingFields[it] = injectionCandidate
                    }
                }
            }
        }
        return matchingFields
    }

    private Collection<Field> getInjectionCandidates(Specification specInstance) {
        Collection<Field> collaborators = SubjectsCollaboratorsUtils.findAllDeclaredFieldsWithAnnotation(specInstance, Collaborator)
        // check if there are any collaborators
        if (collaborators.empty) {
            // if there are none pick all non subject annotated fields for injection into the collaborator
            return specInstance.class.declaredFields.findAll {
                return !it.declaredAnnotations.contains(Subject)
            }
        } else {
            // if there are collaborators pick only them
            return collaborators
        }
    }

}
