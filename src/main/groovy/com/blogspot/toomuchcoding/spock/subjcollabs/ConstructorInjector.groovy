package com.blogspot.toomuchcoding.spock.subjcollabs

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.spockframework.runtime.model.FieldInfo
import spock.lang.Specification

import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Type

@PackageScope
@CompileStatic
class ConstructorInjector implements Injector {

    private static final ConstructorSizeComparator CONSTRUCTOR_SIZE_COMPARATOR = new ConstructorSizeComparator()

    @Override
    boolean tryToInject(Collection<Field> injectionCandidates, Specification specInstance, FieldInfo fieldInfo) {
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
        Constructor constructor = fieldInfo.type.constructors.max(CONSTRUCTOR_SIZE_COMPARATOR)
        constructor.accessible = true
        return constructor
    }

    private Object[] collectAllParametersFromCandidatesSetNullIfMissing(Constructor constructorWithMaxParams, Collection<Field> injectionCandidates, Specification specInstance) {
        Collection parameters = []
        constructorWithMaxParams.genericParameterTypes.each { Type param ->
            Field field = getMatchingInjectionCandidateIfOneExists(injectionCandidates, param)
            parameters << (field == null ? field : specInstance[field.name])
        }
        return parameters.toArray()
    }

    private boolean atLeastOneInjectionCandidateClassExistsAsConstructorParameter(Constructor constructorWithMaxParams, Collection<Field> injectionCandidates) {
        constructorWithMaxParams.genericParameterTypes.any { Type param ->
            return getMatchingInjectionCandidateIfOneExists(injectionCandidates, param)
        }
    }

    private Field getMatchingInjectionCandidateIfOneExists(Collection<Field> injectionCandidates, Type parameterType) {
        return injectionCandidates.find {
            return it.genericType == parameterType
        }
    }
}
