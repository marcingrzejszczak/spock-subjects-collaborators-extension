package com.blogspot.toomuchcoding.spock.subjcollabs
import spock.lang.Specification

import java.lang.reflect.Field

class PropertiesInjectionWithSuperclassSpec extends Specification {

    public static final String TEST_METHOD_1 = "Test method 1"

    public static final String TEST_METHOD_2 = "Test method 2"

    @Collaborator
    SomeOtherClass someOtherClassToBeInjected = Mock()

    @Collaborator
    SomeOtherClass someOtherClass = Mock()

	@Subject
	SomeClass systemUnderTest

    def "should inject collaborator into subject via properties"() {
        given:
        Field someOtherClassField = systemUnderTest.class.superclass.declaredFields.find {it.name == 'someOtherClass'}
        Field someOtherClassToBeInjectedField = systemUnderTest.class.superclass.declaredFields.find {it.name == 'someOtherClassToBeInjected'}
        someOtherClassField.accessible = true
        someOtherClassToBeInjectedField.accessible = true

        and:
        SomeOtherClass injectedSomeOtherClass = someOtherClassField.get(systemUnderTest)
        SomeOtherClass injectedSomeOtherClassToBeInjected = someOtherClassToBeInjectedField.get(systemUnderTest)

        and:
        someOtherClass.someMethod() >> TEST_METHOD_1
        someOtherClassToBeInjected.someMethod() >> TEST_METHOD_2

        when:
        String firstResult = injectedSomeOtherClass.someMethod()
        String secondResult = injectedSomeOtherClassToBeInjected.someMethod()

        then:
        firstResult == TEST_METHOD_1
        injectedSomeOtherClass == someOtherClass

        and:
        secondResult == TEST_METHOD_2
        injectedSomeOtherClassToBeInjected == someOtherClassToBeInjected
    }


    class SomeClassParent {
        private SomeOtherClass someOtherClass
        private SomeOtherClass someOtherClassToBeInjected
    }

    class SomeClass extends SomeClassParent {
    }

    class SomeOtherClass {
        String someMethod() {
            "Some other class"
        }
    }

}


