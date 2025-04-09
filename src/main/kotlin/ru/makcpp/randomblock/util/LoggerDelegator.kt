package ru.makcpp.randomblock.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KProperty

object LoggerDelegator {
    operator fun getValue(thisRef: Any, property: KProperty<*>): Logger {
        val kClass = thisRef::class
        val jClass =
            if (kClass.isCompanion) {
                kClass.java.declaringClass
            } else {
                kClass.java
            }
        return LoggerFactory.getLogger(jClass)
    }
}
