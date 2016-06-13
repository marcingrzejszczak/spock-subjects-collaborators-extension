package com.blogspot.toomuchcoding.spock.subjcollabs

import spock.lang.Issue
import spock.lang.Specification

@Issue("#14")
class MultipleInjectionSpec extends Specification {

    @Collaborator
    Integer a = 1
    @Collaborator
    String b = "22"
    @Collaborator
    Boolean c = true

    @Subject
    B underTest

    def "should inject collaborators using all possible injection methods"() {
        expect:
        underTest.a == a
        underTest.b == b
        underTest.c == c
    }

    public abstract class A {
        Integer a;
    }

    public class B extends A {
        private String b
        Boolean c

        B(Boolean c) {
            this.c = c
        }

        // no setter
        String getB() {
            return b
        }

        void setC(Boolean c) {
            this.c = false
        }
    }


}


