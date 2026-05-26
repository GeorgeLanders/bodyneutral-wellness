package com.example.bodyneutralwellness.data

import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AiCoachRepositoryTest {
    @Test
    fun generateReply_withoutProxy_usesLocalSupport() = runTest {
        val reply = AiCoachRepository(proxyUrl = "").generateReply(
            userText = "I feel anxious",
            context = AiCoachContext(
                userName = "Alex",
                goals = emptySet(),
                mobilityPreference = "mix",
                dailyIntention = "",
                streakCount = 1,
                hydrationCups = 3,
                movementMinutes = 0,
                nourishmentCount = 1,
                sleepHours = 7
            )
        )

        assertTrue(reply.contains("safe", ignoreCase = true))
    }
}
