package cl.vitairos.industrial.domain.repository

import cl.vitairos.industrial.domain.model.User
import kotlinx.coroutines.flow.Flow

/** Login simulado con usuarios ficticios (sin credenciales reales). */
interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun signIn(username: String, password: String): Result<User>
    suspend fun signOut()
}
