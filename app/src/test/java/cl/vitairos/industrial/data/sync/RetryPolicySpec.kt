package cl.vitairos.industrial.data.sync

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

// Pruebas unitarias con Kotest para la politica de reintentos y backoff
class RetryPolicySpec : StringSpec({

    "la politica define 3 reintentos con backoff acordado de 5s, 15s y 30s" {
        RetryPolicy.BACKOFF_SECONDS shouldBe listOf(5L, 15L, 30L)
        RetryPolicy.MAX_AUTOMATIC_RETRIES shouldBe 3
    }

    "delaySecondsFor entrega el tiempo correcto segun el numero de intento" {
        RetryPolicy.delaySecondsFor(0) shouldBe 5L
        RetryPolicy.delaySecondsFor(1) shouldBe 15L
        RetryPolicy.delaySecondsFor(2) shouldBe 30L
    }

    "delaySecondsFor entrega null si se sobrepasan los reintentos o el indice es negativo" {
        RetryPolicy.delaySecondsFor(3) shouldBe null
        RetryPolicy.delaySecondsFor(5) shouldBe null
        RetryPolicy.delaySecondsFor(-1) shouldBe null
    }
})
