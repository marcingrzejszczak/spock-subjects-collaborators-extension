package com.blogspot.toomuchcoding.spock.subjcollabs

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target


/**
 * Annotate fields that you would like to have injected into {@link Subject @Subject} annotated objects
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Collaborator {

}