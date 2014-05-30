package com.blogspot.toomuchcoding.spock.subjcollabs

import com.blogspot.toomuchcoding.spock.subjcollabs.Collaborator
import com.blogspot.toomuchcoding.spock.subjcollabs.Subject
import spock.lang.Specification

class ConstructorInjectionWithGenericsSpec extends Specification {

    public static final String TEST_METHOD_1 = "Test method 1"

    SomeOtherClass someOtherClassNotToBeInjected = Mock()

    @Collaborator
    List<Integer> listNotToBeInjected = []

    @Collaborator
    List<SomeOtherClass> someOtherClassList = [new SomeOtherClass()]

	@Subject
	SomeClass systemUnderTest

    def "should inject collaborator list into subject"() {
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


