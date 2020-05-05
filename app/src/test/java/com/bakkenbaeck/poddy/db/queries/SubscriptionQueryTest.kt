package com.bakkenbaeck.poddy.db.queries

import com.bakkenbaeck.poddy.db.mockData.subMockList
import com.bakkenbaeck.poddy.di.testDBModule
import org.db.SubQueries
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject

class SubscriptionQueryTest : KoinTest {
    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(testDBModule)
    }

    private val queries: SubQueries by inject()

    @Test
    fun `select all - multiple subs`() {
        subMockList.forEach {
            queries.insert(it)
        }

        val subs = queries.selectAll().executeAsList()
        assertEquals(3, subs.count())
    }

    @Test
    fun `select all - no subs`() {
        val subs = queries.selectAll().executeAsList()
        assertEquals(0, subs.count())
    }

    @Test
    fun `already exists - multiple subs`() {
        subMockList.forEach {
            queries.insert(it)
        }

        val alreadyExists = queries.doesAlreadyExist("11").executeAsOneOrNull() == 1L
        assertEquals(true, alreadyExists)
    }

    @Test
    fun `already exists - non existing sub`() {
        subMockList.forEach {
            queries.insert(it)
        }

        val alreadyExists = queries.doesAlreadyExist("112").executeAsOneOrNull() == 1L
        assertEquals(false, alreadyExists)
    }

    @Test
    fun `delete - multiple subs`() {
        subMockList.forEach {
            queries.insert(it)
        }

        queries.delete("11")
        val subs = queries.selectAll().executeAsList()
        val isItemDeleted = subs.filter { it == "11" }.count() == 0

        assertEquals(2, subs.count())
        assertEquals(true, isItemDeleted)
    }

    @Test
    fun `delete - non existing sub`() {
        subMockList.forEach {
            queries.insert(it)
        }

        queries.delete("112")
        val subs = queries.selectAll().executeAsList()

        assertEquals(subMockList.count(), subs.count())
    }
}