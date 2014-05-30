package com.blogspot.toomuchcoding.spock.subjcollabs

import groovy.transform.PackageScope
import org.spockframework.runtime.extension.IMethodInvocation
import spock.lang.Specification

import java.lang.reflect.Field

@PackageScope
class SubjectsCollaboratorsUtils {

    public static Specification getSpec( IMethodInvocation invocation ) {
        ( Specification ) invocation.target.with {
            delegate instanceof Specification ? delegate : invocation.instance
        }
    }

    public static Collection<Field> findAllDeclaredFieldsWithAnnotation(Object object, Class... annotatedClasses) {
        return object.class.declaredFields.findAll { Field field ->
            annotatedClasses.any { Class annotatedClass ->
                return annotatedClass in field.declaredAnnotations*.annotationType()
            }
        }
    }
}
