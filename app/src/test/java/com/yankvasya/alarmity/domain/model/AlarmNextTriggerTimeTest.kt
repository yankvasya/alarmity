package com.yankvasya.alarmity.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDateTime

class AlarmNextTriggerTimeTest {

    // Monday, 2024-01-01, 10:00 — a fixed reference point so every test is deterministic.
    private val now = LocalDateTime.of(2024, 1, 1, 10, 0)

    @Test
    fun `one-off alarm later today fires today`() {
        val alarm = Alarm(hour = 15, minute = 0)
        assertEquals(LocalDateTime.of(2024, 1, 1, 15, 0), alarm.nextTriggerTime(now))
    }

    @Test
    fun `one-off alarm earlier today rolls to tomorrow`() {
        val alarm = Alarm(hour = 8, minute = 0)
        assertEquals(LocalDateTime.of(2024, 1, 2, 8, 0), alarm.nextTriggerTime(now))
    }

    @Test
    fun `one-off alarm at exactly now rolls to tomorrow`() {
        val alarm = Alarm(hour = 10, minute = 0)
        assertEquals(LocalDateTime.of(2024, 1, 2, 10, 0), alarm.nextTriggerTime(now))
    }

    @Test
    fun `repeating alarm on today's weekday later today fires today`() {
        val alarm = Alarm(hour = 15, minute = 0, repeatDays = setOf(DayOfWeek.MONDAY))
        assertEquals(LocalDateTime.of(2024, 1, 1, 15, 0), alarm.nextTriggerTime(now))
    }

    @Test
    fun `repeating alarm on today's weekday already passed rolls to next week`() {
        val alarm = Alarm(hour = 8, minute = 0, repeatDays = setOf(DayOfWeek.MONDAY))
        assertEquals(LocalDateTime.of(2024, 1, 8, 8, 0), alarm.nextTriggerTime(now))
    }

    @Test
    fun `repeating alarm not on today picks the nearest matching weekday`() {
        val alarm = Alarm(hour = 9, minute = 0, repeatDays = setOf(DayOfWeek.WEDNESDAY))
        assertEquals(LocalDateTime.of(2024, 1, 3, 9, 0), alarm.nextTriggerTime(now))
    }

    @Test
    fun `repeating alarm with multiple days picks the earliest upcoming one`() {
        val alarm = Alarm(hour = 9, minute = 0, repeatDays = setOf(DayOfWeek.FRIDAY, DayOfWeek.WEDNESDAY))
        assertEquals(LocalDateTime.of(2024, 1, 3, 9, 0), alarm.nextTriggerTime(now))
    }

    @Test
    fun `repeating alarm wraps a full week when only today's weekday is selected and already passed`() {
        val alarm = Alarm(hour = 0, minute = 0, repeatDays = setOf(DayOfWeek.SUNDAY))
        assertEquals(LocalDateTime.of(2024, 1, 7, 0, 0), alarm.nextTriggerTime(now))
    }
}
