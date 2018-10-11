[![Build Status](https://travis-ci.org/marcingrzejszczak/spock-subjects-collaborators-extension.svg?branch=master)](https://travis-ci.org/marcingrzejszczak/spock-subjects-collaborators-extension)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.blogspot.toomuchcoding/spock-subjects-collaborators-extension/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/com.blogspot.toomuchcoding/spock-subjects-collaborators-extension)
[![Join the chat at https://gitter.im/marcingrzejszczak/spock-subjects-collaborators-extension](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/marcingrzejszczak/spock-subjects-collaborators-extension?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Google Group at https://groups.google.com/forum/?hl=pl#!forum/spock-subjects-collaborators-extension](https://img.shields.io/badge/discuss-google--group-blue.svg)](https://groups.google.com/forum/?hl=pl#!forum/spock-subjects-collaborators-extension)

Spock Subjects-Collaborators Extension
===============

Spock subject-collaborators extension is a port of the Mockito's @InjectMocks functionality - [click here to check Mockito's docs](http://docs.mockito.googlecode.com/hg/1.9.5/org/mockito/InjectMocks.html).
That's why parts of the following explanations are taken out from Mockito documentation.

It allows you to annotate your objects (most preferably system under test) with *@Subject* annotation (In Mockito it would be *@InjectMocks*).
Any object that you would like to have injected to your *@Subject* annotated object you have to annotate with *@Collaborator*.
The main difference between the approaches is such that here in Spock you have to create mocks manually and in Mockito *@Mock* annotation does that for you.

Collaborator injection to Subjects
----------------------------------

Note that since version 1.1.0 if you instantiate your subject by yourself then the extension will honor that and try to perform any further
 injections via setters or properties.

The injection of collaborators to subjects (yes, you can annotate several objects with *@Subject* annotation) is done in the following order:

### Inject via Constructor

The biggest constructor is chosen, then arguments are resolved with *@Collaborator* annotated fields. If there is not even one matching constructor argument that is of
one of the *@Collaborator* annotated object's type then injection via constructor will not happen.

Note: If arguments can not be found, then null is passed

version 1.0.2 allows you to inject collaborators into subject having a private constructor. Of course such a need
signifies that your architecture sucks and maybe you should first refactor it instead of doing all of these aweful hacks.

### Inject via Setter

Subject is instantiated and its setter methods are used to find types that can be injected. Basing on those types matching *@Collaborator* annotated objects are picked.

Note 1: If you have properties with the same type (or same erasure), it's better to name all *@Collaborator* annotated fields with the matching properties, otherwise injection might not happen.
Note 2: If @Subject instance wasn't initialized before and have a no-arg constructor, then it will be initialized with this constructor.

### Inject via Property

Field injection; collaborators will first be resolved by type, then, if there is several property of the same type, by the match of the field name and the mock name.

Note 1: If you have fields with the same type (or same erasure), it's better to name all *@Collaborator* annotated fields with the matching fields, otherwise injection might not happen.
Note 2: If @Subject instance wasn't initialized before and have a no-arg constructor, then it will be initialized with this constructor.

version 1.0.0 allows a hideous hack that you can inject values to Subject's superclass' private properties.
Please do not ever use it unless you're really desperate.

Examples
----------------------------------

Take a look at examples of specifications that show how to use this extension: [link to specs](/src/test/groovy/com/blogspot/toomuchcoding/spock/subjcollabs)
Below you can find an example of usage:

```groovy
package com.blogspot.toomuchcoding.spock.subjcollabs

import spock.lang.Specification
import com.blogspot.toomuchcoding.spock.subjcollabs.Collaborator
import com.blogspot.toomuchcoding.spock.subjcollabs.Subject

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

**SINCE VERSION 1.1.0 THE EXTENSION IS AVAILABLE AT MAVEN CENTRAL!!**

### For Maven:

Add JCenter repository:

```xml
<repositories>
    <repository>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
        <id>central</id>
        <name>bintray</name>
        <url>http://jcenter.bintray.com</url>
    </repository>
</repositories>
```

Or use Maven Central.

Add dependency:

```xml
<dependency>
      <groupId>com.blogspot.toomuchcoding</groupId>
      <artifactId>spock-subjects-collaborators-extension</artifactId>
      <version>1.2.2</version>
      <scope>test</scope>
</dependency>
```

### For Gradle:

Add JCenter repository:

```gradle
repositories {
    jcenter()
}
```

or Maven Central

```gradle
repositories {
    mavenCentral()
}
```

Add dependency:

```gradle
dependencies {
    testCompile 'com.blogspot.toomuchcoding:spock-subjects-collaborators-extension:1.2.2'
}
```

Changelog
--------------------
[Changes are available here](CHANGELOG.md)

Contact
--------------------
[Google Group](https://groups.google.com/forum/?hl=pl#!forum/spock-subjects-collaborators-extension)
[Gitter chat](https://gitter.im/marcingrzejszczak/spock-subjects-collaborators-extension)
