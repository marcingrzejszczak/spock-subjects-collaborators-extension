package com.blogspot.toomuchcoding.spock.subjcollabs

import groovy.transform.PackageScope
import org.codehaus.groovy.reflection.ClassInfo
import org.spockframework.runtime.model.FieldInfo

import java.lang.reflect.Field

@PackageScope
class FieldRetriever {

	Map<Field, Field> getAllUnsetFields(Collection<Field> injectionCandidates, FieldInfo fieldInfo, Object subject) {
		Map<Field, Field> fields = getAllMatchingFields(injectionCandidates, fieldInfo)
		return fields.findAll { Field field, Field injectionCandidate -> field.get(subject) == null }
	}

	private Map<Field, Field> getAllMatchingFields(Collection<Field> injectionCandidates, FieldInfo fieldInfo) {
		List<Field> fields = getAllFieldsFromSubject(fieldInfo.type, [])
		return getMatchingFieldsBasingOnTypeAndPropertyName(injectionCandidates, fields)
	}

	private List<Field> getAllFieldsFromSubject(Class type, List<Field> fields) {
		fields.addAll(type.declaredFields.findAll { Field field -> !field.isSynthetic() && field.type != ClassInfo })
		if (type.superclass != null) {
			fields.addAll(getAllFieldsFromSubject(type.superclass, fields))
		}
		fields*.setAccessible(true)
		return fields
	}

	private Map getMatchingFieldsBasingOnTypeAndPropertyName(Collection<Field> injectionCandidates, List<Field> allFields) {
		Map matchingFields = [:]
		injectionCandidates.each { Field injectionCandidate ->
			List<Field> matchingTypes = allFields.findAll { it.type == injectionCandidate.type }
			Field injectionCandidateByNameAndType = matchingTypes.find { it.name.equalsIgnoreCase(injectionCandidate.name) }
			if (injectionCandidateByNameAndType) {
				matchingFields[injectionCandidateByNameAndType] = injectionCandidate
			} else {
				matchingFields = matchingFields << matchingTypes.collectEntries { [(it) : injectionCandidate] }
			}
		}
		return matchingFields
	}
}
