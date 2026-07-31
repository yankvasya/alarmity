package com.yankvasya.alarmity.domain.dismiss

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TypePhraseDismissMissionTest {

    @Test
    fun `exact match succeeds`() {
        assertTrue(phraseMatches("Today will be a good day", "Today will be a good day"))
    }

    @Test
    fun `match is case-insensitive`() {
        assertTrue(phraseMatches("TODAY WILL BE A GOOD DAY", "Today will be a good day"))
    }

    @Test
    fun `match ignores leading and trailing whitespace`() {
        assertTrue(phraseMatches("  Today will be a good day  ", "Today will be a good day"))
    }

    @Test
    fun `mismatched text fails`() {
        assertFalse(phraseMatches("Today will be a great day", "Today will be a good day"))
    }

    @Test
    fun `empty input fails`() {
        assertFalse(phraseMatches("", "Today will be a good day"))
    }
}
