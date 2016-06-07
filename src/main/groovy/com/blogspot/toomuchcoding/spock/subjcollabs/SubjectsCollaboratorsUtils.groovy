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

    public static boolean isFieldSet(Field field, Object target) {
        try {
            boolean accessible = field.accessible
            field.accessible = true
            boolean fieldSet = (field.get(target) != null)
            field.accessible = accessible
            return fieldSet
        } catch (Exception e) {
            return false
        }
    }
}
