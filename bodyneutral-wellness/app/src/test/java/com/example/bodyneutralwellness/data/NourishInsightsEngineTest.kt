package com.example.bodyneutralwellness.data

import junit.framework.TestCase.assertTrue
import org.junit.Test

class NourishInsightsEngineTest {
    @Test
    fun insightFor_logs_mentionsPattern() {
        val insight = NourishInsightsEngine.insightFor(
            logs = listOf(
                NourishLog("2026-05-26", "Soup", "Comfort", 4f, 3f, 5f, 4f, "warm"),
                NourishLog("2026-05-26", "Tea", "Comfort", 3f, 2f, 5f, 4f, "calm")
            ),
            hydrationCups = 4
        )

        assertTrue(insight.title.contains("Pattern"))
        assertTrue(insight.body.contains("Comfort"))
    }
}
