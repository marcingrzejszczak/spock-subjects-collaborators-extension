package com.blogspot.toomuchcoding.spock.subjcollabs

import groovy.transform.CompileStatic
import groovy.transform.PackageScope

import java.lang.reflect.Constructor

@PackageScope
@CompileStatic
class ConstructorSizeComparator implements Comparator<Constructor> {
    @Override
    int compare(Constructor a, Constructor b) {
        return a.parameterTypes.size().compareTo(b.parameterTypes.size())
    }
}
