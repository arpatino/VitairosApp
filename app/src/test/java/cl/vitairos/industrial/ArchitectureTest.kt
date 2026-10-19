package cl.vitairos.industrial

import cl.vitairos.industrial.data.sync.RetryPolicy
import cl.vitairos.industrial.domain.validation.AttachmentRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Smoke tests de las reglas acordadas con VITAIROS (capas domain y data). */
class ArchitectureTest {
    @Test
    fun retryPolicy_usesAgreedBackoff() {
        assertEquals(listOf(5L, 15L, 30L), RetryPolicy.BACKOFF_SECONDS)
        assertEquals(3, RetryPolicy.MAX_AUTOMATIC_RETRIES)
    }

    @Test
    fun attachmentRules_acceptValidAndRejectInvalid() {
        assertNull(AttachmentRules.validate("image/png", 1_000, currentCount = 0))
        assertTrue(AttachmentRules.validate("image/gif", 1_000, 0) is AttachmentRules.Violation.UnsupportedFormat)
        assertTrue(AttachmentRules.validate("application/pdf", 0, 0) is AttachmentRules.Violation.EmptyFile)
        assertTrue(AttachmentRules.validate("image/jpeg", 11L * 1024 * 1024, 0) is AttachmentRules.Violation.FileTooLarge)
        assertTrue(AttachmentRules.validate("image/jpeg", 1_000, 5) is AttachmentRules.Violation.TooManyAttachments)
    }
}
