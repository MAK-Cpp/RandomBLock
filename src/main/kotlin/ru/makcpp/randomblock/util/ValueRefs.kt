package ru.makcpp.randomblock.util

import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty0

open class ValueRef<T>(private val getFunction: () -> T) {
    fun get(): T = getFunction()
}

/**
 * Удобная обертка для того, чтобы можно было передать изменяющееся значение "по ссылке"
 *
 * Обязательное условие состоит в том, что `\forall x: if set(x) then get() = x`
 */
class MutableValueRef<T>(getFunction: () -> T, private val setFunction: (T) -> Unit) : ValueRef<T>(getFunction) {
    @Synchronized
    fun set(value: T) {
        setFunction(value)
        require(get() == value)
    }
}

val <T> KProperty0<T>.reference
    get() = ValueRef(getter)

val <T> KMutableProperty0<T>.reference
    get() = MutableValueRef(getter, setter)
