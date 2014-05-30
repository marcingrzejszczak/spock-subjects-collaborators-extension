package com.blogspot.toomuchcoding.spock.subjcollabs
import org.spockframework.runtime.extension.ExtensionAnnotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Marker annotation that shows that this is the object that should get 
 * instantiated and have {@link Collaborator @Collaborator} annotated objects injected.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@ExtensionAnnotation(SubjectsCollaboratorsExtension)
@interface Subject {}
