package com.blogspot.toomuchcoding.spock.subjcollabs

import spock.lang.Specification

class ConstructorInjectionSeveralConstructorsNoCollaboratorsSpec extends Specification {

    public static final String TEST_METHOD_1 = "Test method 1"

    String string = "string"

    Integer integer = 10

    SomeOtherClass someOtherClassNotToBeInjected = Mock()

    SomeOtherClass someOtherClass = Mock()

	@Subject
	SomeClass systemUnderTest

    def "should inject all fields into subject"() {
        given:
        someOtherClassNotToBeInjected.someMethod() >> TEST_METHOD_1

        when:
        String firstResult = systemUnderTest.someOtherClass.someMethod()

        then:
        firstResult == TEST_METHOD_1
        systemUnderTest.someOtherClass == someOtherClassNotToBeInjected
        systemUnderTest.string == TEST_METHOD_1
        systemUnderTest.integer == integer
    }


    class SomeClass {
        SomeOtherClass someOtherClass
        String string
        Integer integer

        SomeClass(SomeOtherClass someOtherClass) {
            this.someOtherClass = someOtherClass
        }

        SomeClass(SomeOtherClass someOtherClass, String string) {
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


