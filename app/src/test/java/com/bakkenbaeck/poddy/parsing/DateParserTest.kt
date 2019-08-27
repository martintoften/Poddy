package com.bakkenbaeck.poddy.parsing

import com.bakkenbaeck.poddy.util.parseDate
import com.bakkenbaeck.poddy.util.parseDateString
import org.junit.Assert.assertEquals
import org.junit.Test

class DateParserTest {

    @Test
    fun `parse to date - null`() {
        val date = parseDateString(null)
        assertEquals(null, date)
    }

    @Test
    fun `parse to date - Tue, 23 Jun 2015 01 00 00 0000`() {
        val date = parseDateString("Tue, 23 Jun 2015 01:00:00 -0000")
        assertEquals(23, date?.date)
        assertEquals(2, date?.day)
        assertEquals(5, date?.month)
        assertEquals(3, date?.hours)
        assertEquals(0, date?.minutes)
    }

    @Test
    fun `parse to date - Thu, 06 Dec 2018 110000 0000`() {
        val date = parseDateString("Thu, 06 Dec 2018 11:00:00 -0000")
        assertEquals(6, date?.date)
        assertEquals(4, date?.day)
        assertEquals(11, date?.month)
        assertEquals(12, date?.hours)
        assertEquals(0, date?.minutes)
    }

    @Test
    fun `parse to string - Tue, 23 Jun 2015 010000 0000`() {
        val date = parseDateString("Tue, 23 Jun 2015 01:00:00 -0000")
        val dateString = parseDate(date, "MMMMM d, yyyy")

        assertEquals("June 23, 2015", dateString)
    }

    @Test
    fun `parse to string - Thu, 22 Aug 2019 100000 0000`() {
        val date = parseDateString("Thu, 22 Aug 2019 10:00:00 -0000")
        val dateString = parseDate(date, "MMMMM d, yyyy")

        assertEquals("August 22, 2019", dateString)
    }

    @Test
    fun `parse to string - Thu, 06 Dec 2018 110000 0000`() {
        val date = parseDateString("Thu, 06 Dec 2018 11:00:00 -0000")
        val dateString = parseDate(date, "MMMMM d, yyyy")

        assertEquals("December 6, 2018", dateString)
    }

    @Test
    fun `parse to string - null`() {
        val date = parseDateString(null)
        val dateString = parseDate(date, "MMMMM d, yyyy")

        assertEquals("", dateString)
    }
}
