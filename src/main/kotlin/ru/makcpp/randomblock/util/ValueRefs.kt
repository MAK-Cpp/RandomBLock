package ru.makcpp.randomblock.util

import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty0

open class ValueRef<T>(private val getFunction: () -> T) {
    constructor(property: KProperty0<T>) : this(property::getter.get())

    fun get(): T = getFunction()
}

/**
 * Удобная обертка для того, чтобы можно было передать изменяющееся значение "по ссылке"
 *
 * Обязательное условие состоит в том, что `\forall x: if set(x) then get() = x`
 */
class MutableValueRef<T>(getFunction: () -> T, private val setFunction: (T) -> Unit) : ValueRef<T>(getFunction) {
    constructor(property: KMutableProperty0<T>) : this(property::getter.get(), property::setter.get())

    @Synchronized
    fun set(value: T) {
        setFunction(value)
        require(get() == value)
    }
}
