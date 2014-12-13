package com.blogspot.toomuchcoding.spock.subjcollabs

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.spockframework.runtime.model.FieldInfo
import spock.lang.Specification

import java.lang.reflect.Constructor

@PackageScope
@CompileStatic
abstract class NonConstructorBasedInjector implements Injector {

    protected static final ConstructorSizeComparator CONSTRUCTOR_SIZE_COMPARATOR = new ConstructorSizeComparator()

    protected Object instantiateSubjectAndSetOnSpecification(Specification specInstance, FieldInfo fieldInfo) {
        final Object subject
        Constructor constructorWithMinArgs = fieldInfo.type.constructors.min(CONSTRUCTOR_SIZE_COMPARATOR)
        if (constructorWithMinArgs.parameterTypes.size() == 0) {
            subject = fieldInfo.type.newInstance()
        } else {
            // must be inner class or some nonmatching constructor
            subject = fieldInfo.type.newInstance(null)
        }
        specInstance[fieldInfo.name] = subject
        return subject
    }
}
