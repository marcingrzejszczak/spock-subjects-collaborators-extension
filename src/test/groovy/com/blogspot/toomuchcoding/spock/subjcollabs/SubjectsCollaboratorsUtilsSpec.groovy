package com.blogspot.toomuchcoding.spock.subjcollabs

import spock.lang.Specification


class SubjectsCollaboratorsUtilsSpec extends Specification {

    def "should find collaborator fields in class with no parent"() {
        given:
        BaseSpec specClass = new BaseSpec()

        when:
        def fields = SubjectsCollaboratorsUtils.findAllDeclaredFieldsWithAnnotation(specClass, Collaborator)

        then:
        fields.size() == 1
        fields[0].name == "field1"
    }

    def "should find collaborator fields in class with parent class"() {
        given:
        SpecClass specClass = new SpecClass()

        when:
        def fields = SubjectsCollaboratorsUtils.findAllDeclaredFieldsWithAnnotation(specClass, Collaborator)

        then:
        fields.size() == 2
        fields.collect({it.getName()}).containsAll(["field1", "field3"])
    }

    class BaseSpec {
        @Collaborator
        String field1

        String field2
    }

    class SpecClass extends BaseSpec {
        @Collaborator
        String field3

        String field4
    }

}
