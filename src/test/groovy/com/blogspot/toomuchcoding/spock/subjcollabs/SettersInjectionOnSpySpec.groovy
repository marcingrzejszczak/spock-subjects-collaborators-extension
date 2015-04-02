package com.blogspot.toomuchcoding.spock.subjcollabs

import spock.lang.Issue
import spock.lang.Specification

@Issue('#8')
class SettersInjectionOnSpySpec extends Specification {

    public static final String TEST_METHOD_1 = "Test method 1"

    @Collaborator SomeOtherClass someOtherClassInjected = Mock()
    @Subject SomeClass systemUnderTestInjected = Spy()
    SomeOtherClass someOtherClassPlain = Mock()
    SomeClass systemUnderTestPlain = Spy()

    def "should inject collaborator into subject without disrupting spy stubbing"() {
        given:
            someOtherClassInjected.getResult() >> 'mock called'
            systemUnderTestInjected.throwException() >> {}

        when:
            def result = systemUnderTestInjected.collaborate()

        then:
            result == 'mock called'
    }

    class SomeClass {
        SomeOtherClass collaborator

        String collaborate() {
            throwException()
            return collaborator.getResult()
        }

        void throwException() {
            throw new RuntimeException()
        }
    }

    class SomeOtherClass {
        def getResult() {
            return 'SomeOtherClass called'
        }
    }
}