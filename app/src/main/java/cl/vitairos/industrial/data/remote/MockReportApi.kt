package cl.vitairos.industrial.data.remote

import cl.vitairos.industrial.data.remote.dto.ReportUploadRequest
import cl.vitairos.industrial.data.remote.dto.ReportUploadResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * Conexión completa con la API Mock en la nube de Vercel con soporte
 * para reintentos de red y modo de simulación en proceso.
 */
class MockReportApi : ReportRemoteDataSource {

    companion object {
        // Switch para activar/desactivar simulación de fallo y probar los reintentos (5s, 15s, 30s)
        var simulateFailure: Boolean = false

        // Endpoint de la API Mock alojada en Vercel
        var vercelApiUrl: String = "https://vitairos-api.vercel.app/api/reports"

        // Conectar a Vercel Cloud (true) o usar Mock puramente local en memoria (false)
        var useVercelCloud: Boolean = true

        // Estado descriptivo de la última conexión para auditoría en la UI
        var lastConnectionStatus: String = "API Mock Vercel configurada y lista"

        // Indica si la última sincronización fue procesada exitosamente en la nube
        var lastSyncInCloud: Boolean = false
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    override suspend fun upload(request: ReportUploadRequest): ReportUploadResponse = withContext(Dispatchers.IO) {
        if (simulateFailure) {
            lastConnectionStatus = "Fallo simulado activado para probar reintentos exponenciales (Backoff)"
            lastSyncInCloud = false
            throw IllegalStateException("Fallo simulado de conexión con servidor VITAIROS (Backoff activado)")
        }

        if (useVercelCloud) {
            try {
                lastConnectionStatus = "Despachando a Vercel: $vercelApiUrl..."
                val url = URL(vercelApiUrl)
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = 5000
                    readTimeout = 5000
                    doOutput = true
                    doInput = true
                    setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    setRequestProperty("Accept", "application/json")
                    setRequestProperty("User-Agent", "VITAIROS-Industrial-Android/0.1.0")
                }

                // Serializamos el reporte a JSON
                val requestBody = json.encodeToString(ReportUploadRequest.serializer(), request)
                OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { writer ->
                    writer.write(requestBody)
                    writer.flush()
                }

                val responseCode = connection.responseCode
                if (responseCode in 200..299) {
                    val responseBody = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                    lastConnectionStatus = "Conexión Vercel Cloud exitosa (HTTP $responseCode)"
                    lastSyncInCloud = true

                    return@withContext try {
                        json.decodeFromString<ReportUploadResponse>(responseBody)
                    } catch (e: Exception) {
                        ReportUploadResponse(
                            receivedId = request.reportId,
                            status = "SYNCED",
                            message = "Reporte recibido y persistido en API Mock Vercel (HTTP $responseCode)",
                            simulated = false,
                        )
                    }
                } else {
                    lastConnectionStatus = "Error respuesta Vercel (HTTP $responseCode)"
                    lastSyncInCloud = false
                    throw IllegalStateException("API Vercel respondió con código de error HTTP $responseCode")
                }
            } catch (e: Exception) {
                // Si la URL remota no responde o hay problemas de red en terreno, registramos el estado
                lastConnectionStatus = "Aviso red: ${e.message ?: "Sin respuesta de Vercel"}. Reintentando con política de backoff..."
                lastSyncInCloud = false
                throw e
            }
        } else {
            // Modo simulación local en proceso con latencia de red controlada (1.2s)
            delay(1200)
            lastConnectionStatus = "Procesado vía Mock Local en memoria (Simulación)"
            lastSyncInCloud = false

            return@withContext ReportUploadResponse(
                receivedId = request.reportId,
                status = "SYNCED",
                message = "Reporte recibido y validado en servidor mock local VITAIROS",
                simulated = true,
            )
        }
    }
}
