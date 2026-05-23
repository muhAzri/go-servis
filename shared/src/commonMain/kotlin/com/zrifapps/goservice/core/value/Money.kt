package com.zrifapps.goservice.core.value

import kotlin.jvm.JvmInline

@JvmInline
value class Money(val amountIdr: Long) : Comparable<Money> {
    init {
        require(amountIdr >= 0) { "Money amount must not be negative" }
    }

    operator fun plus(other: Money): Money = Money(amountIdr + other.amountIdr)
    operator fun minus(other: Money): Money = Money((amountIdr - other.amountIdr).coerceAtLeast(0))
    operator fun times(multiplier: Int): Money = Money(amountIdr * multiplier)
    override fun compareTo(other: Money): Int = amountIdr.compareTo(other.amountIdr)

    companion object {
        val ZERO: Money = Money(0L)
        fun ofIdr(amount: Long): Money = Money(amount.coerceAtLeast(0))
        fun ofIdrOrNull(amount: Long?): Money? = amount?.let { ofIdr(it) }
    }
}
