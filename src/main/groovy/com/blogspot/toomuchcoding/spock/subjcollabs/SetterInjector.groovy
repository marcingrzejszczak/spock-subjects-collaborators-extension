package com.blogspot.toomuchcoding.spock.subjcollabs
import groovy.transform.PackageScope
import org.spockframework.runtime.model.FieldInfo
import spock.lang.Specification

import java.lang.reflect.Field
import java.lang.reflect.Method

@PackageScope
class SetterInjector extends NonConstructorBasedInjector {

    private static final List<String> SETTERS_TO_IGNORE = ["setMetaClass"]

    SetterInjector(FieldRetriever fieldRetriever) {
        super(fieldRetriever)
    }

    @Override
    boolean tryToInject(Collection<Field> injectionCandidates, Specification specInstance, FieldInfo fieldInfo) {
        // Property setter injection; mocks will first be resolved by type, then, if there is several property of the same type
        try {
            Object subject = instantiateSubjectAndSetOnSpecification(specInstance, fieldInfo)
            List<Method> setters = getAllSettersFromSubject(fieldInfo)
            Map matchingSetters = getMatchingSettersBasingOnTypeAndPropertyName(injectionCandidates, setters)
            if (matchingSetters.isEmpty()) {
                return false
            }
            Map<Field, Field> fields = fieldRetriever.getAllUnsetFields(injectionCandidates, fieldInfo, subject)
            matchingSetters.each { Method method, Field injectionCandidate ->
                if (fields.containsValue(injectionCandidate)) {
                    method.invoke(subject, specInstance[injectionCandidate.name])
                }
            }
            return true
        } catch (Exception e) {
            return false
        }
    }

    private Map<Method, Field> getMatchingSettersBasingOnTypeAndPropertyName(Collection<Field> injectionCandidates, List<Method> setters) {
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

}
