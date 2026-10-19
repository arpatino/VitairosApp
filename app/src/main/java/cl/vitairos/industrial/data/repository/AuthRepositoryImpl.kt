package cl.vitairos.industrial.data.repository

import cl.vitairos.industrial.data.local.prefs.SessionDataStore
import cl.vitairos.industrial.domain.model.User
import cl.vitairos.industrial.domain.model.UserRole
import cl.vitairos.industrial.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Usuarios ficticios de demostracion. No son credenciales reales de VITAIROS. */
class AuthRepositoryImpl(private val session: SessionDataStore) : AuthRepository {
    private val demoUsers = listOf(
        User("u-operador", "Operador Demo", UserRole.OPERATOR),
        User("u-supervisor", "Supervisor Demo", UserRole.SUPERVISOR),
    )

    override val currentUser: Flow<User?> =
        session.userId.map { id -> demoUsers.firstOrNull { it.id == id } }

    override suspend fun signIn(username: String, password: String): Result<User> {
        val user = when {
            username.contains("super", ignoreCase = true) -> demoUsers.first { it.role == UserRole.SUPERVISOR }
            else -> demoUsers.first { it.role == UserRole.OPERATOR }
        }
        session.setUserId(user.id)
        return Result.success(user)
    }

    override suspend fun signOut() = session.clear()
}
