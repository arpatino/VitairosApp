package cl.vitairos.industrial.ui.screens.login

import cl.vitairos.industrial.domain.model.User
import cl.vitairos.industrial.domain.model.UserRole
import cl.vitairos.industrial.domain.repository.AuthRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
// Pruebas unitarias con Kotest para la logica del ViewModel de inicio de sesion
class LoginViewModelSpec : FunSpec({

    val testDispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    test("onUsernameChange y onPasswordChange actualizan el estado y limpian errores") {
        val authRepo = mockk<AuthRepository>()
        every { authRepo.currentUser } returns flowOf(null)
        val vm = LoginViewModel(authRepo)

        vm.onUsernameChange("supervisor_norte")
        vm.uiState.value.username shouldBe "supervisor_norte"
        vm.uiState.value.error shouldBe null

        vm.onPasswordChange("password123")
        vm.uiState.value.password shouldBe "password123"
        vm.uiState.value.error shouldBe null
    }

    test("selectRole conmuta el rol y autocompleta el nombre sugerido") {
        val authRepo = mockk<AuthRepository>()
        every { authRepo.currentUser } returns flowOf(null)
        val vm = LoginViewModel(authRepo)

        vm.selectRole(UserRole.SUPERVISOR)
        vm.uiState.value.selectedRole shouldBe UserRole.SUPERVISOR
        vm.uiState.value.username shouldBe "supervisor"

        vm.selectRole(UserRole.OPERATOR)
        vm.uiState.value.selectedRole shouldBe UserRole.OPERATOR
        vm.uiState.value.username shouldBe "operador"
    }

    test("signIn con usuario en blanco genera mensaje de error descriptivo") {
        val authRepo = mockk<AuthRepository>()
        every { authRepo.currentUser } returns flowOf(null)
        val vm = LoginViewModel(authRepo)

        vm.onUsernameChange("")
        vm.signIn()

        vm.uiState.value.error shouldBe "El identificador de usuario no puede estar vacío."
        vm.uiState.value.isLoading shouldBe false
    }

    test("signIn exitoso autentica al usuario y actualiza el estado") {
        val authRepo = mockk<AuthRepository>()
        every { authRepo.currentUser } returns flowOf(null)
        coEvery { authRepo.signIn(any(), any()) } returns Result.success(User("u-operador", "Operador", UserRole.OPERATOR))
        val vm = LoginViewModel(authRepo)

        vm.onUsernameChange("operador")
        vm.onPasswordChange("123456")
        vm.signIn()
        testDispatcher.scheduler.advanceUntilIdle()

        vm.uiState.value.isLoading shouldBe false
        vm.uiState.value.user?.id shouldBe "u-operador"
        vm.uiState.value.error shouldBe null
    }
})
