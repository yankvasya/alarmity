package com.yankvasya.alarmity.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class StreakTest {

    // Monday, 2024-01-08 — arbitrary fixed reference point for determinism.
    private val today = LocalDate.of(2024, 1, 8)

    @Test
    fun `current streak is zero with no dismissals`() {
        assertEquals(0, computeCurrentStreak(emptySet(), today))
    }

    @Test
    fun `current streak counts today when dismissed today`() {
        assertEquals(1, computeCurrentStreak(setOf(today), today))
    }

    @Test
    fun `current streak stays alive through today when last dismissal was yesterday`() {
        assertEquals(1, computeCurrentStreak(setOf(today.minusDays(1)), today))
    }

    @Test
    fun `current streak is broken when last dismissal was two days ago`() {
        assertEquals(0, computeCurrentStreak(setOf(today.minusDays(2)), today))
    }

    @Test
    fun `current streak counts a consecutive run ending today`() {
        val dates = setOf(today, today.minusDays(1), today.minusDays(2))
        assertEquals(3, computeCurrentStreak(dates, today))
    }

    @Test
    fun `current streak counts a consecutive run ending yesterday`() {
        val dates = setOf(today.minusDays(1), today.minusDays(2), today.minusDays(3))
        assertEquals(3, computeCurrentStreak(dates, today))
    }

    @Test
    fun `current streak stops at a gap`() {
        val dates = setOf(today, today.minusDays(1), today.minusDays(3))
        assertEquals(2, computeCurrentStreak(dates, today))
    }

    @Test
    fun `longest streak is zero with no dismissals`() {
        assertEquals(0, computeLongestStreak(emptySet()))
    }

    @Test
    fun `longest streak finds the longest run even if it is not the most recent`() {
        val dates = setOf(
            today.minusDays(10), today.minusDays(9), today.minusDays(8), today.minusDays(7), // 4-day run
            today.minusDays(1), // isolated day
        )
        assertEquals(4, computeLongestStreak(dates))
    }

    @Test
    fun `longest streak counts every day exactly once regardless of set ordering`() {
        val dates = setOf(today, today.minusDays(2), today.minusDays(1))
        assertEquals(3, computeLongestStreak(dates))
    }
}
