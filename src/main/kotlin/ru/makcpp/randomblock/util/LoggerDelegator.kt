package ru.makcpp.randomblock.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KProperty

object LoggerDelegator {
    operator fun getValue(thisRef: Any, property: KProperty<*>): Logger {
        val kClass = thisRef::class
        val jClass = kClass.java.run { if (kClass.isCompanion) declaringClass else this }
        return LoggerFactory.getLogger(jClass)
    }
}
