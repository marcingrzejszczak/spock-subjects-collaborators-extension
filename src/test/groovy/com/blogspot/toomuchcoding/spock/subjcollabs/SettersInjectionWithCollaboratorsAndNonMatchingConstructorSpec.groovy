package com.blogspot.toomuchcoding.spock.subjcollabs

import com.blogspot.toomuchcoding.spock.subjcollabs.Collaborator
import com.blogspot.toomuchcoding.spock.subjcollabs.Subject
import spock.lang.Specification

class SettersInjectionWithCollaboratorsAndNonMatchingConstructorSpec extends Specification {

    public static final String TEST_METHOD_1 = "Test method 1"

    SomeOtherClass someOtherClassToBeInjected = Mock()

    @Collaborator
    SomeOtherClass someOtherClass = Mock()

    @Subject
    SomeClass systemUnderTest

    def "should inject collaborator into subject via setters ignoring nonmatching constructor"() {
        given:
        someOtherClass.someMethod() >> TEST_METHOD_1

        when:
        String firstResult = systemUnderTest.someOtherClass.someMethod()

        then:
        firstResult == TEST_METHOD_1
        systemUnderTest.someOtherClass == someOtherClass
    }


    class SomeClass {
        SomeOtherClass someOtherClass
        SomeOtherClass someOtherClassToBeInjected

        SomeClass() {

        }

        SomeClass(Integer integer) {

        }
    }

    class SomeOtherClass {
        String someMethod() {
            "Some other class"
        }
    }

}


