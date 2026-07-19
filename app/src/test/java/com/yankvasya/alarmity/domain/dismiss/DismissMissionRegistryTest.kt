package com.yankvasya.alarmity.domain.dismiss

import androidx.compose.runtime.Composable
import org.junit.Assert.assertEquals
import org.junit.Test

private class FakeDismissMission(override val id: String) : DismissMission {
    override val displayName: String = id

    @Composable
    override fun Content(onComplete: () -> Unit) = Unit
}

class DismissMissionRegistryTest {

    @Test
    fun `get returns the mission with a matching id`() {
        val registry = DismissMissionRegistry(
            setOf(FakeDismissMission(DismissMission.DEFAULT_MISSION_ID), FakeDismissMission("math_problem")),
        )
        assertEquals("math_problem", registry.get("math_problem").id)
    }

    @Test
    fun `get falls back to the default mission for an unknown id`() {
        val registry = DismissMissionRegistry(
            setOf(FakeDismissMission(DismissMission.DEFAULT_MISSION_ID), FakeDismissMission("math_problem")),
        )
        assertEquals(DismissMission.DEFAULT_MISSION_ID, registry.get("unregistered_id").id)
    }

    @Test
    fun `all is sorted by id`() {
        val registry = DismissMissionRegistry(
            setOf(FakeDismissMission("shake"), FakeDismissMission("math_problem"), FakeDismissMission("qr_scan")),
        )
        assertEquals(listOf("math_problem", "qr_scan", "shake"), registry.all.map { it.id })
    }
}
