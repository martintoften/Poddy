package com.bakkenbaeck.poddy.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val OUTPUT_DATE_FORMAT = "MMMM d, yyyy"
const val INPUT_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z"

fun parseDateString(date: String?, format: String): String {
    val parsedDate = parseDateString(date)
    return parseDate(parsedDate, format)
}

fun parseDateString(date: String?): Date? {
    val sdf = SimpleDateFormat(INPUT_DATE_FORMAT, Locale.getDefault())
    return try {
        sdf.parse(date)
    } catch (e: Exception) {
        null
    }
}

fun parseDate(date: Date?, format: String): String {
    if (date == null) return ""
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    return sdf.format(date)
}
