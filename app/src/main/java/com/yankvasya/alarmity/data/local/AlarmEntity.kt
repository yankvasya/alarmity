package com.yankvasya.alarmity.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yankvasya.alarmity.domain.dismiss.DismissMission
import java.time.DayOfWeek

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val label: String = "",
    val enabled: Boolean = true,
    /** Empty set means a one-off alarm rather than a repeating one. */
    val repeatDays: Set<DayOfWeek> = emptySet(),
    val dismissMissionId: String = DismissMission.DEFAULT_MISSION_ID,
)
