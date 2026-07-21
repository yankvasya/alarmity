package com.yankvasya.alarmity.domain.model

import java.time.LocalDate

/**
 * Consecutive days (ending today or yesterday) with at least one dismissal. A streak stays alive
 * through the current day even with no dismissal yet today — it only breaks once a full day passes
 * with none, same as most streak-tracking apps.
 */
fun computeCurrentStreak(dismissalDates: Set<LocalDate>, today: LocalDate = LocalDate.now()): Int {
    val start = when {
        today in dismissalDates -> today
        today.minusDays(1) in dismissalDates -> today.minusDays(1)
        else -> return 0
    }
    var streak = 0
    var date = start
    while (date in dismissalDates) {
        streak++
        date = date.minusDays(1)
    }
    return streak
}

/** The longest run of consecutive dismissal days ever recorded. */
fun computeLongestStreak(dismissalDates: Set<LocalDate>): Int {
    if (dismissalDates.isEmpty()) return 0
    val sorted = dismissalDates.sorted()
    var longest = 1
    var current = 1
    for (i in 1 until sorted.size) {
        current = if (sorted[i] == sorted[i - 1].plusDays(1)) current + 1 else 1
        longest = maxOf(longest, current)
    }
    return longest
}
