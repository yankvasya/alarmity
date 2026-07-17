package com.yankvasya.alarmity.domain.model

import com.yankvasya.alarmity.domain.dismiss.DismissMission
import java.time.DayOfWeek

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
