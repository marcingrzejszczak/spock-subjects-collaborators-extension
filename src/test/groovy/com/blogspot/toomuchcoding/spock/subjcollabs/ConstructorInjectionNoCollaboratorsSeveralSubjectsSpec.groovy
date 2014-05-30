package com.blogspot.toomuchcoding.spock.subjcollabs

import spock.lang.Specification

class ConstructorInjectionNoCollaboratorsSeveralSubjectsSpec extends Specification {

    public static final String TEST_METHOD_1 = "Test method 1"

    SomeClass someClass = Mock()

    SomeOtherClass someOtherClassToBeInjected = Mock()

    SomeOtherClass someOtherClass = Mock()

	@Subject
	SomeClass systemUnderTest

	@Subject
	SomeClass systemUnderTest2

    def "should inject first acceptable object into each subject"() {
        given:
        someOtherClassToBeInjected.someMethod() >> TEST_METHOD_1

        when:
        String firstResult = systemUnderTest.someOtherClass.someMethod()
        String secondResult = systemUnderTest2.someOtherClass.someMethod()

        then:
        firstResult == TEST_METHOD_1
        systemUnderTest.someOtherClass == someOtherClassToBeInjected

        and:
        secondResult == TEST_METHOD_1
        systemUnderTest2.someOtherClass == someOtherClassToBeInjected
    }


    class SomeClass {
        SomeOtherClass someOtherClass

        SomeClass(SomeOtherClass someOtherClass) {
            this.someOtherClass = someOtherClass
        }
    }

    class SomeOtherClass {
        String someMethod() {
            "Some other class"
        }
    }


}


