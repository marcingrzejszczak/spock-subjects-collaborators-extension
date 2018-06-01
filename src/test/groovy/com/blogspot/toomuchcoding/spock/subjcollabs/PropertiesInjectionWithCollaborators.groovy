package com.blogspot.toomuchcoding.spock.subjcollabs

import spock.lang.Issue
import spock.lang.Specification

class PropertiesInjectionWithCollaborators extends Specification {
    @Collaborator SomeOtherClass someOtherClass = Mock()
    @Subject SomeClass someClass

    @Issue("#20")
    def 'someMethod should be called from the collaborator object'() {
        given:
            someOtherClass.someMethod() >> "collaborator method"
        when:
            String result = someClass.someOtherClass.someMethod()
        then:
            result == "collaborator method"
    }
}

class SomeClass {
    SomeOtherClass someOtherClass = new SomeOtherClass()

    SomeClass(SomeOtherClass someOtherClass) {
        this.someOtherClass = someOtherClass
    }
}

class SomeOtherClass {
    String someMethod() {
        "real method"
    }
}