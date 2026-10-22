package cl.vitairos.industrial.data.local.files

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import cl.vitairos.industrial.domain.model.Attachment
import cl.vitairos.industrial.domain.model.AttachmentKind
import cl.vitairos.industrial.domain.validation.AttachmentRules
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/** Adjuntos en almacenamiento privado (filesDir); nunca en carpetas publicas. */
class PrivateFileStore(context: Context) {
    private val appContext = context.applicationContext
    private val root = File(context.filesDir, "attachments").apply { mkdirs() }

    fun fileFor(relativePath: String): File = File(root, relativePath)

    fun delete(relativePath: String): Boolean = fileFor(relativePath).delete()

    // Crea un archivo vacio en la carpeta privada para que la camara guarde la foto ahi
    fun createTempCameraFile(): Pair<String, Uri> {
        val fileName = "CAM_${System.currentTimeMillis()}.jpg"
        val file = fileFor(fileName)
        val uri = FileProvider.getUriForFile(
            appContext,
            "${appContext.packageName}.fileprovider",
            file
        )
        return Pair(fileName, uri)
    }

    // Procesa la foto tomada con la camara: la comprime al 80% y la valida
    fun processCameraPhoto(
        reportId: String,
        fileName: String,
        currentCount: Int
    ): Pair<Attachment?, String?> {
        val file = fileFor(fileName)
        if (!file.exists() || file.length() <= 0L) {
            return Pair(null, "No se pudo capturar la fotografía.")
        }

        // Comprimimos la imagen al 80% en JPEG para que ocupe menos espacio
        compressImageFile(file)

        val sizeBytes = file.length()
        val violation = AttachmentRules.validate("image/jpeg", sizeBytes, currentCount)
        if (violation != null) {
            file.delete()
            return Pair(null, mapViolationMessage(violation))
        }

        val attachment = Attachment(
            id = "ATT-" + UUID.randomUUID().toString().take(8).uppercase(),
            reportId = reportId,
            kind = AttachmentKind.IMAGE,
            mimeType = "image/jpeg",
            fileName = fileName,
            relativePath = fileName,
            sizeBytes = sizeBytes,
            sha256 = computeSha256(file)
        )
        return Pair(attachment, null)
    }

    // Copia un archivo elegido desde el selector (imagen o PDF) al almacenamiento privado
    fun saveFromPickerUri(
        reportId: String,
        uri: Uri,
        currentCount: Int
    ): Pair<Attachment?, String?> {
        val resolver = appContext.contentResolver
        val mimeType = resolver.getType(uri)

        // Obtenemos nombre y tamano del archivo seleccionado usando un Cursor basico
        var originalName = "archivo_${System.currentTimeMillis()}"
        var originalSize = 0L

        val cursor = resolver.query(uri, null, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (nameIndex != -1) {
                    originalName = cursor.getString(nameIndex) ?: originalName
                }
                if (sizeIndex != -1) {
                    originalSize = cursor.getLong(sizeIndex)
                }
            }
            cursor.close()
        }

        // Validamos formato, tamano maximo (10 MB) y limite de 5 archivos
        val violation = AttachmentRules.validate(mimeType, originalSize, currentCount)
        if (violation != null) {
            return Pair(null, mapViolationMessage(violation))
        }

        return try {
            val isPdf = mimeType == "application/pdf"
            val extension = if (isPdf) ".pdf" else ".jpg"
            val savedFileName = "DOC_${System.currentTimeMillis()}$extension"
            val destFile = fileFor(savedFileName)

            if (isPdf) {
                // Si es PDF lo copiamos directamente a la carpeta privada
                val inputStream = resolver.openInputStream(uri)
                val outputStream = FileOutputStream(destFile)
                if (inputStream != null) {
                    inputStream.copyTo(outputStream)
                    inputStream.close()
                }
                outputStream.close()
            } else {
                // Si es imagen (JPG o PNG) la decodificamos y la guardamos comprimida al 80%
                val inputStream = resolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (bitmap == null) {
                    return Pair(null, "No se pudo leer la imagen seleccionada.")
                }

                val scaledBitmap = resizeBitmapIfNeeded(bitmap, 1600)
                val outputStream = FileOutputStream(destFile)
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                outputStream.flush()
                outputStream.close()
            }

            val finalMime = if (isPdf) "application/pdf" else "image/jpeg"
            val finalKind = if (isPdf) AttachmentKind.PDF else AttachmentKind.IMAGE

            val attachment = Attachment(
                id = "ATT-" + UUID.randomUUID().toString().take(8).uppercase(),
                reportId = reportId,
                kind = finalKind,
                mimeType = finalMime,
                fileName = originalName,
                relativePath = savedFileName,
                sizeBytes = destFile.length(),
                sha256 = computeSha256(destFile)
            )
            Pair(attachment, null)
        } catch (e: Exception) {
            Pair(null, "Error al guardar el archivo adjunto.")
        }
    }

    // Funcion auxiliar para comprimir una foto que ya esta en un archivo
    private fun compressImageFile(file: File) {
        try {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath) ?: return
            val scaledBitmap = resizeBitmapIfNeeded(bitmap, 1600)
            val outputStream = FileOutputStream(file)
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: Exception) {
            // Si falla la compresion dejamos el archivo original
        }
    }

    // Si la foto es demasiado grande, la achicamos manteniendo la proporcion para no gastar memoria
    private fun resizeBitmapIfNeeded(bitmap: Bitmap, maxSide: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxSide && height <= maxSide) {
            return bitmap
        }
        val ratio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int
        if (width > height) {
            newWidth = maxSide
            newHeight = (maxSide / ratio).toInt()
        } else {
            newHeight = maxSide
            newWidth = (maxSide * ratio).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    // Traduce los errores de AttachmentRules a mensajes claros para el usuario
    private fun mapViolationMessage(violation: AttachmentRules.Violation): String {
        return when (violation) {
            AttachmentRules.Violation.EmptyFile -> "El archivo está vacío (0 bytes)."
            AttachmentRules.Violation.UnsupportedFormat -> "Formato no permitido. Solo se aceptan JPG, PNG o PDF."
            AttachmentRules.Violation.FileTooLarge -> "El archivo supera el tamaño máximo permitido de 10 MB."
            AttachmentRules.Violation.TooManyAttachments -> "Se alcanzó el máximo de 5 archivos adjuntos por reporte."
        }
    }

    // Calcula el hash criptografico SHA-256 del archivo para trazabilidad y anti-duplicados
    fun computeSha256(file: File): String {
        return try {
            val digest = java.security.MessageDigest.getInstance("SHA-256")
            file.inputStream().use { input ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    digest.update(buffer, 0, bytesRead)
                }
            }
            digest.digest().joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            "SHA256_UNAVAILABLE"
        }
    }
}