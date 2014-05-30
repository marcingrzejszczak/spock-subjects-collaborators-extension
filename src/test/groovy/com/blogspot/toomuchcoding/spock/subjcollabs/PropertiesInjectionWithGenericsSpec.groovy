package com.blogspot.toomuchcoding.spock.subjcollabs

import com.blogspot.toomuchcoding.spock.subjcollabs.Collaborator
import com.blogspot.toomuchcoding.spock.subjcollabs.Subject
import spock.lang.Specification

class PropertiesInjectionWithGenericsSpec extends Specification {

    public static final String TEST_METHOD_1 = "Test method 1"

    public static final String TEST_METHOD_2 = "Test method 2"

    List<SomeOtherClass> someOtherClassNonCollaboratorNotToBeInjected

    @Collaborator
    List<Integer> listNotToBeInjected = Mock()

    @Collaborator
    List<SomeOtherClass> someOtherClassToBeInjected = Mock()

    @Collaborator
    List<SomeOtherClass> someOtherClass = Mock()

	@Subject
	SomeClass systemUnderTest

    def "should inject collaborator into subject via properties"() {
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


