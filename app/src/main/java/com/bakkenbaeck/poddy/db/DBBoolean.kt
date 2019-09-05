package com.bakkenbaeck.poddy.db

fun intToBool(value: Int): Boolean = value == 1
fun longToBool(value: Long): Boolean = value == 1L
fun boolToInt(value: Boolean): Int = if (value) 1 else 0
fun boolToLong(value: Boolean): Long = if (value) 1 else 0
