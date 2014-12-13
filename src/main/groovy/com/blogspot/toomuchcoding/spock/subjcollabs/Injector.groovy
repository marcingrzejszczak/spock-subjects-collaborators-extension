package com.blogspot.toomuchcoding.spock.subjcollabs

import org.spockframework.runtime.model.FieldInfo
import spock.lang.Specification

import java.lang.reflect.Field


interface Injector {
    boolean tryToInject(Collection<Field> injectionCandidates, Specification specInstance, FieldInfo fieldInfo)
}