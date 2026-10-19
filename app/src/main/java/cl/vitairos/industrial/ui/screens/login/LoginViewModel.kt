package cl.vitairos.industrial.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.vitairos.industrial.domain.model.User
import cl.vitairos.industrial.domain.model.UserRole
import cl.vitairos.industrial.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val username: String = "operador",
    val password: String = "123456",
    val selectedRole: UserRole = UserRole.OPERATOR,
    val isLoading: Boolean = false,
    val error: String? = null,
    val user: User? = null,
)

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _uiState.update { it.copy(user = user) }
            }
        }
    }

    fun onUsernameChange(username: String) {
        _uiState.update { it.copy(username = username, error = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    fun selectRole(role: UserRole) {
        _uiState.update {
            it.copy(
                selectedRole = role,
                username = if (role == UserRole.OPERATOR) "operador" else "supervisor",
                error = null,
            )
        }
    }

    fun signIn() {
        val current = _uiState.value
        if (current.username.isBlank()) {
            _uiState.update { it.copy(error = "El identificador de usuario no puede estar vacío.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.signIn(current.username, current.password)
            result.onSuccess { user ->
                _uiState.update { it.copy(isLoading = false, user = user) }
            }.onFailure { ex ->
                _uiState.update { it.copy(isLoading = false, error = ex.message ?: "Error al autenticar.") }
            }
        }
    }

    companion object {
        fun factory(authRepository: AuthRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    LoginViewModel(authRepository) as T
            }
    }
}

