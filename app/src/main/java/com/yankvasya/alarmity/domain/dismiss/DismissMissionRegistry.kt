package com.yankvasya.alarmity.domain.dismiss

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DismissMissionRegistry @Inject constructor(
    missions: Set<@JvmSuppressWildcards DismissMission>,
) {
    val all: List<DismissMission> = missions.sortedBy { it.id }

    private val missionsById = missions.associateBy { it.id }

    fun get(id: String): DismissMission =
        missionsById[id] ?: missionsById.getValue(DismissMission.DEFAULT_MISSION_ID)
}
