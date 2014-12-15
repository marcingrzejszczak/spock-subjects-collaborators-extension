package com.blogspot.toomuchcoding.spock.subjcollabs
import groovy.transform.PackageScope
import org.codehaus.groovy.reflection.ClassInfo
import org.spockframework.runtime.model.FieldInfo
import spock.lang.Specification

import java.lang.reflect.Field

@PackageScope
class PropertyInjector extends NonConstructorBasedInjector {

    @Override
    boolean tryToInject(Collection<Field> injectionCandidates, Specification specInstance, FieldInfo fieldInfo) {
        // Field injection; mocks will first be resolved by type, then, if there is several property of the same type, by the match of the field name and the mock name.
        // Note 1: If you have fields with the same type (or same erasure), it's better to name all @Mock annotated fields with the matching fields, otherwise Mockito might get confused and injection won't happen.
        Object subject = instantiateSubjectAndSetOnSpecification(specInstance, fieldInfo)
        List<Field> fields = getAllFieldsFromSubject(fieldInfo.type, [])
        Map matchingFields = getMatchingFieldsBasingOnTypeAndPropertyName(injectionCandidates, fields)
        matchingFields.each { Field field, Field injectionCandidate ->
            field.set(subject, specInstance[injectionCandidate.name])
        }
    }

    private List<Field> getAllFieldsFromSubject(Class type, List<Field> fields) {
        fields.addAll(type.declaredFields.findAll { Field field -> !field.isSynthetic() && field.type != ClassInfo })
        if (type.superclass != null) {
            fields.addAll(getAllFieldsFromSubject(type.superclass, fields))
        }
        fields*.setAccessible(true)
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
}
