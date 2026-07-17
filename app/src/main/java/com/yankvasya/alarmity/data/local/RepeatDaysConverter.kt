package com.yankvasya.alarmity.data.local

import androidx.room.TypeConverter
import java.time.DayOfWeek

/** Stores the repeat-day set as a 7-bit mask (bit 0 = Monday .. bit 6 = Sunday). */
class RepeatDaysConverter {
    @TypeConverter
    fun fromRepeatDays(days: Set<DayOfWeek>): Int =
        days.fold(0) { mask, day -> mask or (1 shl (day.value - 1)) }

    @TypeConverter
    fun toRepeatDays(mask: Int): Set<DayOfWeek> =
        DayOfWeek.values().filterTo(mutableSetOf()) { day -> mask and (1 shl (day.value - 1)) != 0 }
}
