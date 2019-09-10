package com.bakkenbaeck.poddy.time

import com.bakkenbaeck.poddy.extensions.getFormattedTime
import org.junit.Assert.assertEquals
import org.junit.Test

class TimeFormattingTest {

    @Test
    fun `1000 to 0001`() {
        val time = getFormattedTime(1000)
        assertEquals("00:01", time)
    }

    @Test
    fun `10000 to 0010`() {
        val time = getFormattedTime(10000)
        assertEquals("00:10", time)
    }

    @Test
    fun `38110 to 0038`() {
        val time = getFormattedTime(38110)
        assertEquals("00:38", time)
    }

    @Test
    fun `308110 to 0038`() {
        val time = getFormattedTime(308110)
        assertEquals("05:08", time)
    }

    @Test
    fun `3030110 to 0038`() {
        val time = getFormattedTime(3030110)
        assertEquals("50:30", time)
    }

    @Test
    fun `3930110 to 0038`() {
        val time = getFormattedTime(3930110)
        assertEquals("01:05:30", time)
    }
}
