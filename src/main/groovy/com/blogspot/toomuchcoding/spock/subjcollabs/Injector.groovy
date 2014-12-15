package com.blogspot.toomuchcoding.spock.subjcollabs

import groovy.transform.PackageScope
import org.spockframework.runtime.model.FieldInfo
import spock.lang.Specification

import java.lang.reflect.Field

@PackageScope
interface Injector {
    boolean tryToInject(Collection<Field> injectionCandidates, Specification specInstance, FieldInfo fieldInfo)
}