Spock Subjects-Collaborators Extension
===============

Spock subject-collaborators extension is a port of the Mockito's @InjectMocks functionality - [click here to check Mockito's docs](http://docs.mockito.googlecode.com/hg/1.9.5/org/mockito/InjectMocks.html).
That's why parts of the following explanations are taken out from Mockito documentation.
 
It allows you to annotate your objects (most preferably system under test) with *@Subject* annotation (In Mockito it would be *@InjectMocks*). 
Any object that you would like to have injected to your *@Subject* annotated object you have to annotate with *@Collaborator*. 
The main difference between the approaches is such that here in Spock you have to create mocks manually and in Mockito *@Mock* annotation does that for you.

Collaborator injection to Subjects
----------------------------------

The injection of collaborators to subjects (yes, you can annotate several objects with *@Subject* annotation) is done in the following order:

###Inject via Constructor

The biggest constructor is chosen, then arguments are resolved with *@Collaborator* annotated fields. If there is not even one matching constructor argument that is of 
one of the *@Collaborator* annotated object's type then injection via constructor will not happen. 

Note: If arguments can not be found, then null is passed

###Inject via Setter

Subject is instantiated and its setter methods are used to find types that can be injected. Basing on those types matching *@Collaborator* annotated objects are picked.

Note 1: If you have properties with the same type (or same erasure), it's better to name all *@Collaborator* annotated fields with the matching properties, otherwise injection might not happen.
Note 2: If @Subject instance wasn't initialized before and have a no-arg constructor, then it will be initialized with this constructor.

###Inject via Property

Field injection; collaborators will first be resolved by type, then, if there is several property of the same type, by the match of the field name and the mock name.

Note 1: If you have fields with the same type (or same erasure), it's better to name all *@Collaborator* annotated fields with the matching fields, otherwise injection might not happen.
Note 2: If @Subject instance wasn't initialized before and have a no-arg constructor, then it will be initialized with this constructor.

Examples
----------------------------------

Take a look at examples of specifications that show how to use this extension: [link to specs](/src/test/groovy/com/blogspot/toomuchcoding/spock/subjcollabs)
Below you can find an example of usage:

```groovy
package com.blogspot.toomuchcoding.spock.subjcollabs

import spock.lang.Specification

class ConstructorInjectionSpec extends Specification {

    public static final String TEST_METHOD_1 = "Test method 1"

    SomeOtherClass someOtherClassNotToBeInjected = Mock()

    @Collaborator
    SomeOtherClass someOtherClass = Mock()

	@Subject
	SomeClass systemUnderTest

    def "should inject collaborator into subject"() {
        given:
        someOtherClass.someMethod() >> TEST_METHOD_1

        when:
        String firstResult = systemUnderTest.someOtherClass.someMethod()

        then:
        firstResult == TEST_METHOD_1
        systemUnderTest.someOtherClass == someOtherClass
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
```

How to add it
----------------------------------


