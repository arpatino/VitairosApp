package cl.vitairos.industrial.data.repository

import cl.vitairos.industrial.data.local.prefs.SessionDataStore
import cl.vitairos.industrial.domain.model.UserRole
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

// Pruebas unitarias con Kotest para la autenticacion y roles de usuario
class AuthRepositorySpec : BehaviorSpec({

    Given("un usuario iniciando sesion como operador") {
        val session = mockk<SessionDataStore>(relaxed = true)
        every { session.userId } returns flowOf("u-operador")
        val authRepo = AuthRepositoryImpl(session)

        When("inicia sesion con nombre de operador") {
            val result = authRepo.signIn("operador", "123456")

            Then("se le asigna el rol OPERATOR y se guarda su id") {
                result.isSuccess shouldBe true
                val user = result.getOrNull()
                user?.id shouldBe "u-operador"
                user?.role shouldBe UserRole.OPERATOR
                coVerify(exactly = 1) { session.setUserId("u-operador") }
            }

            Then("currentUser emite el usuario operador") {
                val current = authRepo.currentUser.first()
                current?.id shouldBe "u-operador"
                current?.role shouldBe UserRole.OPERATOR
            }
        }
    }

    Given("un usuario iniciando sesion como supervisor") {
        val session = mockk<SessionDataStore>(relaxed = true)
        every { session.userId } returns flowOf("u-supervisor")
        val authRepo = AuthRepositoryImpl(session)

        When("el nombre de usuario contiene 'super'") {
            val result = authRepo.signIn("supervisor_faena", "secret")

            Then("se le asigna el rol SUPERVISOR") {
                result.isSuccess shouldBe true
                val user = result.getOrNull()
                user?.id shouldBe "u-supervisor"
                user?.role shouldBe UserRole.SUPERVISOR
                coVerify(exactly = 1) { session.setUserId("u-supervisor") }
            }
        }
    }

    Given("un usuario cerrando sesion") {
        val session = mockk<SessionDataStore>(relaxed = true)
        val authRepo = AuthRepositoryImpl(session)

        When("se llama a signOut") {
            authRepo.signOut()

            Then("se limpian los datos en el session store") {
                coVerify(exactly = 1) { session.clear() }
            }
        }
    }
})
