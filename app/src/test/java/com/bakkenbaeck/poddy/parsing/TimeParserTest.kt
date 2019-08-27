package com.bakkenbaeck.poddy.parsing

import com.bakkenbaeck.poddy.util.parseSecondsToMinutes
import org.junit.Assert.assertEquals
import org.junit.Test

class TimeParserTest {
    @Test
    fun `parse 2386`() {
        val minutesString = parseSecondsToMinutes(2386)
        assertEquals("39 min", minutesString)
    }

    @Test
    fun `parse 1911`() {
        val minutesString = parseSecondsToMinutes(1911)
        assertEquals("31 min", minutesString)
    }

    @Test
    fun `parse 0`() {
        val minutesString = parseSecondsToMinutes(0)
        assertEquals("0 min", minutesString)
    }
}
