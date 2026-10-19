package cl.vitairos.industrial.data.local.files

import android.content.Context
import java.io.File

/** Adjuntos en almacenamiento privado (filesDir); nunca en carpetas publicas. */
class PrivateFileStore(context: Context) {
    private val root = File(context.filesDir, "attachments").apply { mkdirs() }

    fun fileFor(relativePath: String): File = File(root, relativePath)

    fun delete(relativePath: String): Boolean = fileFor(relativePath).delete()
}
