package com.blogspot.toomuchcoding.spock.subjcollabs

import com.blogspot.toomuchcoding.spock.subjcollabs.Collaborator
import com.blogspot.toomuchcoding.spock.subjcollabs.Subject
import spock.lang.Specification

class PropertiesInjectionWithSeveralGenericsSpec extends Specification {

    public static final String TEST_METHOD_1 = "Test method 1"

    SomeOtherClass someOtherClassNotToBeInjected = Mock()

    @Collaborator
    Map<String, Integer> mapNotToBeInjected = [:]

    @Collaborator
    Map<Integer, String> someOtherClassMap = [:]

	@Subject
	SomeClass systemUnderTest

    def "should inject collaborator list into subject"() {
        expect:
            systemUnderTest.someOtherClassMap == someOtherClassMap
    }


    class SomeClass {
        private Map<Integer, String> someOtherClassMap
    }

    class SomeOtherClass {
        String someMethod() {
            "Some other class"
        }
    }

}


