package com.blogspot.toomuchcoding.spock.subjcollabs

import com.blogspot.toomuchcoding.spock.subjcollabs.Collaborator
import com.blogspot.toomuchcoding.spock.subjcollabs.Subject
import spock.lang.Specification

class ConstructorInjectionSeveralConstructorsSeveralCollaboratorsSpec extends Specification {

    public static final String TEST_METHOD_1 = "Test method 1"

    @Collaborator
    String string = "string"

    @Collaborator
    Integer integer = 10

    SomeOtherClass someOtherClassNotToBeInjected = Mock()

    @Collaborator
    SomeOtherClass someOtherClass = Mock()

	@Subject
	SomeClass systemUnderTest

    def "should inject collaborators into subject"() {
        given:
        someOtherClass.someMethod() >> TEST_METHOD_1

        when:
        String firstResult = systemUnderTest.someOtherClass.someMethod()

        then:
        firstResult == TEST_METHOD_1
        systemUnderTest.someOtherClass == someOtherClass
        systemUnderTest.string == string
        systemUnderTest.integer == integer
    }


    class SomeClass {
        SomeOtherClass someOtherClass
        String string
        Integer integer

        SomeClass(SomeOtherClass someOtherClass) {
            this.integer = integer
            this.string = string
            this.someOtherClass = someOtherClass
        }

        SomeClass(SomeOtherClass someOtherClass, String string) {
            this.integer = integer
            this.string = string
            this.someOtherClass = someOtherClass
        }

        SomeClass(SomeOtherClass someOtherClass, String string, Integer integer) {
            this.integer = integer
            this.string = string
            this.someOtherClass = someOtherClass
        }
    }

    class SomeOtherClass {
        String someMethod() {
            "Some other class"
        }
    }

}


