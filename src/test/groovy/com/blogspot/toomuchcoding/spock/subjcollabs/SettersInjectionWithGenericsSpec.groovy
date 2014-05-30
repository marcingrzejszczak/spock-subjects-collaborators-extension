package com.blogspot.toomuchcoding.spock.subjcollabs

import com.blogspot.toomuchcoding.spock.subjcollabs.Collaborator
import com.blogspot.toomuchcoding.spock.subjcollabs.Subject
import spock.lang.Specification

class SettersInjectionWithGenericsSpec extends Specification {

    public static final String TEST_METHOD_1 = "Test method 1"

    public static final String TEST_METHOD_2 = "Test method 2"

    @Collaborator
    List<Integer> listNotToBeInjected = Mock()

    @Collaborator
    List<SomeOtherClass> someOtherClassNotToBeInjected = Mock()

    @Collaborator
    List<SomeOtherClass> someOtherClassToBeInjected = Mock()

    @Collaborator
    List<SomeOtherClass> someOtherClass = Mock()

	@Subject
	SomeClass systemUnderTest

    def "should inject collaborator into subject via setters"() {
        expect:
        systemUnderTest.someOtherClass == someOtherClass

        and:
        systemUnderTest.someOtherClassToBeInjected == someOtherClassToBeInjected
    }


    class SomeClass {
        List<SomeOtherClass> someOtherClass
        List<SomeOtherClass> someOtherClassToBeInjected
    }

    class SomeOtherClass {
        String someMethod() {
            "Some other class"
        }
    }

}


