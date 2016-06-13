package com.blogspot.toomuchcoding.spock.subjcollabs

import groovy.transform.PackageScope
import org.spockframework.runtime.model.FieldInfo
import spock.lang.Specification

import java.lang.reflect.Field

@PackageScope
class PropertyInjector extends NonConstructorBasedInjector {

    PropertyInjector(FieldRetriever fieldRetriever) {
        super(fieldRetriever)
    }

    @Override
    boolean tryToInject(Collection<Field> injectionCandidates, Specification specInstance, FieldInfo fieldInfo) {
        // Field injection; mocks will first be resolved by type, then, if there is several property of the same type, by the match of the field name and the mock name.
        // Note 1: If you have fields with the same type (or same erasure), it's better to name all @Mock annotated fields with the matching fields, otherwise Mockito might get confused and injection won't happen.
        Object subject = instantiateSubjectAndSetOnSpecification(specInstance, fieldInfo)
        Map matchingFields = fieldRetriever.getAllUnsetFields(injectionCandidates, fieldInfo, subject)
        matchingFields.each { Field field, Field injectionCandidate ->
            field.set(subject, specInstance[injectionCandidate.name])
        }
    }
}
