package cl.vitairos.industrial.data.local.prefs

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.sessionStore by preferencesDataStore(name = "session")

/** Sesion simulada: guarda solo el id del usuario ficticio, jamas contrasenas ni tokens. */
class SessionDataStore(private val context: Context) {
    private val userIdKey = stringPreferencesKey("user_id")

    val userId: Flow<String?> = context.sessionStore.data.map { it[userIdKey] }

    suspend fun setUserId(id: String) {
        context.sessionStore.edit { it[userIdKey] = id }
    }

    suspend fun clear() {
        context.sessionStore.edit { it.clear() }
    }
}
