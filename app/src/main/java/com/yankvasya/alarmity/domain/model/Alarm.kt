package com.yankvasya.alarmity.domain.model

import com.yankvasya.alarmity.domain.dismiss.DismissMission
import java.time.DayOfWeek
import java.time.LocalDateTime

data class Alarm(
    val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val label: String = "",
    val enabled: Boolean = true,
    val repeatDays: Set<DayOfWeek> = emptySet(),
    val dismissMissionId: String = DismissMission.DEFAULT_MISSION_ID,
) {
    val isOneOff: Boolean get() = repeatDays.isEmpty()
}

/**
 * The next date/time this alarm should fire at or after [now]. For a repeating alarm whose
 * time today already passed, this searches forward up to a full week so today's weekday is
 * only picked again once it's actually the next valid occurrence.
 */
fun Alarm.nextTriggerTime(now: LocalDateTime = LocalDateTime.now()): LocalDateTime {
    val today = now.toLocalDate()
    if (isOneOff) {
        val todayAtAlarmTime = today.atTime(hour, minute)
        return if (todayAtAlarmTime.isAfter(now)) todayAtAlarmTime else todayAtAlarmTime.plusDays(1)
    }
    for (dayOffset in 0..7L) {
        val candidateDate = today.plusDays(dayOffset)
        val candidateDateTime = candidateDate.atTime(hour, minute)
        if (candidateDate.dayOfWeek in repeatDays && candidateDateTime.isAfter(now)) {
            return candidateDateTime
        }
    }
    error("Unreachable: a non-empty repeatDays set always has a match within 7 days")
}
