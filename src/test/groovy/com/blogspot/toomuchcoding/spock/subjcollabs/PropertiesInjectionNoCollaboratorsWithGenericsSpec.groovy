package com.blogspot.toomuchcoding.spock.subjcollabs

import com.blogspot.toomuchcoding.spock.subjcollabs.Subject
import spock.lang.Specification

class PropertiesInjectionNoCollaboratorsWithGenericsSpec extends Specification {

    public static final String TEST_METHOD_1 = "Test method 1"

    public static final String TEST_METHOD_2 = "Test method 2"

    List<SomeOtherClass> listNotToBeInjected = []

    List<SomeOtherClass> someOtherClassToBeInjected = []

    List<SomeOtherClass> someOtherClass = []

	@Subject
	SomeClass systemUnderTest

    def "should inject fields into subject via properties with no collaborators"() {
        expect:
        systemUnderTest.someOtherClass == someOtherClass

        and:
        systemUnderTest.someOtherClassToBeInjected == someOtherClassToBeInjected
    }


    class SomeClass {
        private List<SomeOtherClass> someOtherClass
        private List<SomeOtherClass> someOtherClassToBeInjected
    }

    class SomeOtherClass {
        String someMethod() {
            "Some other class"
        }
    }

}


