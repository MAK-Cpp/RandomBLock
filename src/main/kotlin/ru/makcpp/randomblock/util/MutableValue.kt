package ru.makcpp.randomblock.util

/**
 * Удобная обертка для того, чтобы можно было передать изменяющееся значение "по ссылке"
 *
 * Обязательное условие состоит в том, что `\forall x: if set(x) then get() = x`
 */
data class MutableValue<T>(
    private val getFunction: () -> T,
    private val setFunction: (T) -> Unit
) {
    fun get(): T = getFunction()

    fun set(value: T) = setFunction(value)
}