package com.blogspot.toomuchcoding.spock.subjcollabs

import java.lang.reflect.Field

import groovy.transform.PackageScope
import org.spockframework.runtime.extension.IMethodInvocation
import spock.lang.Specification

@PackageScope
class SubjectsCollaboratorsUtils {

    public static Specification getSpec(IMethodInvocation invocation) {
        (Specification) invocation.target.with {
            delegate instanceof Specification ? delegate : invocation.instance
        }
    }

    public static Collection<Field> findAllDeclaredFieldsWithAnnotation(Object object, Class... annotatedClasses) {
        return getInheritedFields(object.class).findAll {Field field ->
            annotatedClasses.any {Class annotatedClass ->
                return annotatedClass in field.declaredAnnotations*.annotationType()
            }
        }
    }

    static Collection<Field> getInheritedFields(Class<?> type) {
        List<Field> fields = new ArrayList<Field>()
        for (Class<?> c = type; c != null; c = c.superclass) {
            fields.addAll(c.declaredFields)
        }
        return fields
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
