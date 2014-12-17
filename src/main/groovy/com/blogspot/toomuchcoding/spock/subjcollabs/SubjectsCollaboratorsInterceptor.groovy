package com.blogspot.toomuchcoding.spock.subjcollabs

import org.spockframework.runtime.extension.IMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation
import org.spockframework.runtime.model.FieldInfo
import org.spockframework.runtime.model.MethodInfo
import spock.lang.Specification

import java.lang.reflect.Field
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
 * @since 1.0.0 - allows a hideous hack that you can inject values to Subject's superclass' private properties.
 * Please do not ever use it unless you're really desperate.
 *
 * Note 1: If you have fields with the same type (or same erasure), it's better to name all {@link Collaborator @Collaborator} annotated fields with the matching fields, otherwise injection might not happen.
 * Note 2: If {@link Subject @Subject} instance wasn't initialized before and have a no-arg constructor, then it will be initialized with this constructor.
 */
class SubjectsCollaboratorsInterceptor implements IMethodInterceptor {

    private final List<FieldInfo> fields = []
    private final List<Injector> injectors

    SubjectsCollaboratorsInterceptor() {
        this.injectors = [new ConstructorInjector(), new SetterInjector(), new PropertyInjector()]
    }

    protected SubjectsCollaboratorsInterceptor(List<Injector> injectors) {
        this.injectors = injectors
    }

    public void install(MethodInfo method) {
        method.addInterceptor(this)
    }

    public void install(List<IMethodInterceptor> method) {
        method.add(this)
    }
    
    public void add(FieldInfo fieldInfo) {
        fields.add(fieldInfo)
    }

    @Override
    void intercept(IMethodInvocation invocation) {
        Specification specInstance = SubjectsCollaboratorsUtils.getSpec(invocation)
        Collection<Field> injectionCandidates = getInjectionCandidates(specInstance)
        fields.each { tryToInjectCandidatesIntoSubject(injectionCandidates, specInstance, it) }
        invocation.proceed()
    }

    private void tryToInjectCandidatesIntoSubject(Collection<Field> injectionCandidates, Specification specInstance, FieldInfo fieldInfo) {
        injectors.find { it.tryToInject(injectionCandidates, specInstance, fieldInfo) }
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
