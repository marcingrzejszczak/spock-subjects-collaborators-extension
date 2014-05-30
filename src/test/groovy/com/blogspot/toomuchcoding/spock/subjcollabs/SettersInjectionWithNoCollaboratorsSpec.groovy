package com.blogspot.toomuchcoding.spock.subjcollabs

import com.blogspot.toomuchcoding.spock.subjcollabs.Subject
import spock.lang.Specification

class SettersInjectionWithNoCollaboratorsSpec extends Specification {

    public static final String TEST_METHOD_1 = "Test method 1"

    public static final String TEST_METHOD_2 = "Test method 2"

    SomeOtherClass someOtherClassToBeInjected = Mock()

    SomeOtherClass someOtherClass = Mock()

	@Subject
	SomeClass systemUnderTest

    def "should inject objects into subject via setters"() {
        given:
        someOtherClass.someMethod() >> TEST_METHOD_1
        someOtherClassToBeInjected.someMethod() >> TEST_METHOD_2

        when:
        String firstResult = systemUnderTest.someOtherClass.someMethod()
        String secondResult = systemUnderTest.someOtherClassToBeInjected.someMethod()

        then:
        firstResult == TEST_METHOD_1
        systemUnderTest.someOtherClass == someOtherClass

        and:
        secondResult == TEST_METHOD_2
        systemUnderTest.someOtherClassToBeInjected == someOtherClassToBeInjected
    }


    class SomeClass {
        SomeOtherClass someOtherClass
        SomeOtherClass someOtherClassToBeInjected
    }

    class SomeOtherClass {
        String someMethod() {
            "Some other class"
        }
    }

}


