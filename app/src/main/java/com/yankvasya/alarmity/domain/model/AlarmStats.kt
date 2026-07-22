package com.yankvasya.alarmity.domain.model

data class AlarmStats(
    val currentStreak: Int,
    val longestStreak: Int,
    val totalDismissals: Int,
    val totalSnoozes: Int,
    /** The last 7 days, oldest first, each true if an alarm was dismissed that day. */
    val last7Days: List<Boolean>,
) {
    companion object {
        val EMPTY = AlarmStats(
            currentStreak = 0,
            longestStreak = 0,
            totalDismissals = 0,
            totalSnoozes = 0,
            last7Days = List(7) { false },
        )
    }
}
