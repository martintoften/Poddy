package com.bakkenbaeck.poddy.extensions

import com.bakkenbaeck.poddy.util.OUTPUT_DATE_FORMAT
import com.bakkenbaeck.poddy.util.parseDate
import java.util.*

fun Long.toSeconds() = this / 60

fun Int.toSeconds() = this / 60

fun Long.toDate() = parseDate(Date(this), OUTPUT_DATE_FORMAT)
