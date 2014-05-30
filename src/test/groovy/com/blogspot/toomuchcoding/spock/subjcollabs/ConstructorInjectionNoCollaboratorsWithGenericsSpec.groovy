package com.blogspot.toomuchcoding.spock.subjcollabs

import com.blogspot.toomuchcoding.spock.subjcollabs.Subject
import spock.lang.Specification

class ConstructorInjectionNoCollaboratorsWithGenericsSpec extends Specification {

    public static final String TEST_METHOD_1 = "Test method 1"

    SomeOtherClass someOtherClassNotToBeInjected = Mock()

    List<SomeClass> listThatShouldNotBeInjected = []

    List<SomeOtherClass> someOtherClassList = [new SomeOtherClass()]

	@Subject
	SomeClass systemUnderTest

    def "should inject list of some other classes into subject"() {
        expect:
            systemUnderTest.someOtherClassList == someOtherClassList
    }


    class SomeClass {
        List<SomeOtherClass> someOtherClassList

        SomeClass(List<SomeOtherClass> someOtherClassList) {
            this.someOtherClassList = someOtherClassList
        }
    }

    class SomeOtherClass {
        String someMethod() {
            "Some other class"
        }
    }

}


