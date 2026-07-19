package com.yankvasya.alarmity.data.local

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek

class RepeatDaysConverterTest {

    private val converter = RepeatDaysConverter()

    @Test
    fun `empty set round-trips to zero mask`() {
        assertEquals(0, converter.fromRepeatDays(emptySet()))
        assertEquals(emptySet<DayOfWeek>(), converter.toRepeatDays(0))
    }

    @Test
    fun `single day round-trips`() {
        val mask = converter.fromRepeatDays(setOf(DayOfWeek.MONDAY))
        assertEquals(1, mask)
        assertEquals(setOf(DayOfWeek.MONDAY), converter.toRepeatDays(mask))
    }

    @Test
    fun `all days round-trip`() {
        val allDays = DayOfWeek.values().toSet()
        val mask = converter.fromRepeatDays(allDays)
        assertEquals(127, mask)
        assertEquals(allDays, converter.toRepeatDays(mask))
    }

    @Test
    fun `arbitrary combination round-trips`() {
        val days = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
        val mask = converter.fromRepeatDays(days)
        assertEquals(days, converter.toRepeatDays(mask))
    }
}
