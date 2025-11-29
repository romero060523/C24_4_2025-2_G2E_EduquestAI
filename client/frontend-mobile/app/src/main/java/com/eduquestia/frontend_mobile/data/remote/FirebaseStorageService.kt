package com.eduquestia.frontend_mobile.data.remote

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Estados de subida de archivos
 */
sealed class UploadState {
    data class Loading(val progress: Int) : UploadState()
    data class Success(val downloadUrl: String) : UploadState()
    data class Error(val message: String) : UploadState()
}

/**
 * Tipos de archivo permitidos
 */
enum class TipoArchivo(val carpeta: String, val extensiones: List<String>) {
    IMAGEN("imagenes", listOf("jpg", "jpeg", "png", "gif", "webp")),
    VIDEO("videos", listOf("mp4", "mov", "avi", "mkv")),
    PDF("documentos", listOf("pdf")),
    DOCUMENTO("documentos", listOf("doc", "docx", "txt", "pptx", "xlsx"))
}

/**
 * Servicio para subir archivos a Firebase Storage
 */
class FirebaseStorageService {

    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    init {
        // Autenticación anónima para permitir subidas
        if (auth.currentUser == null) {
            auth.signInAnonymously()
        }
    }

    /**
     * Sube un archivo a Firebase Storage con seguimiento de progreso
     * @param uri URI del archivo local
     * @param misionId ID de la misión para organizar archivos
     * @param estudianteId ID del estudiante
     * @param tipo Tipo de archivo
     * @return Flow con el estado de la subida
     */
    fun subirArchivo(
        uri: Uri,
        misionId: String,
        estudianteId: String,
        tipo: TipoArchivo
    ): Flow<UploadState> = callbackFlow {
        try {
            // Asegurar autenticación
            if (auth.currentUser == null) {
                auth.signInAnonymously().await()
            }

            // Generar nombre único para el archivo
            val nombreArchivo = "${UUID.randomUUID()}_${System.currentTimeMillis()}"
            val extension = obtenerExtension(uri.toString()) ?: when(tipo) {
                TipoArchivo.IMAGEN -> "jpg"
                TipoArchivo.VIDEO -> "mp4"
                TipoArchivo.PDF -> "pdf"
                TipoArchivo.DOCUMENTO -> "pdf"
            }

            // Ruta en Storage: entregas/{misionId}/{estudianteId}/{tipo}/{archivo}
            val path = "entregas/$misionId/$estudianteId/${tipo.carpeta}/$nombreArchivo.$extension"
            val storageRef = storage.reference.child(path)

            // Iniciar subida
            val uploadTask = storageRef.putFile(uri)

            // Escuchar progreso
            uploadTask.addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                trySend(UploadState.Loading(progress))
            }

            // Esperar a que termine la subida
            uploadTask.await()

            // Obtener URL de descarga
            val downloadUrl = storageRef.downloadUrl.await().toString()
            trySend(UploadState.Success(downloadUrl))

        } catch (e: Exception) {
            trySend(UploadState.Error(e.message ?: "Error al subir archivo"))
        }

        awaitClose { }
    }

    /**
     * Sube un archivo y retorna directamente la URL (sin Flow)
     */
    suspend fun subirArchivoSimple(
        uri: Uri,
        misionId: String,
        estudianteId: String,
        tipo: TipoArchivo
    ): Result<String> {
        return try {
            // Asegurar autenticación
            if (auth.currentUser == null) {
                auth.signInAnonymously().await()
            }

            val nombreArchivo = "${UUID.randomUUID()}_${System.currentTimeMillis()}"
            val extension = obtenerExtension(uri.toString()) ?: when(tipo) {
                TipoArchivo.IMAGEN -> "jpg"
                TipoArchivo.VIDEO -> "mp4"
                TipoArchivo.PDF -> "pdf"
                TipoArchivo.DOCUMENTO -> "pdf"
            }

            val path = "entregas/$misionId/$estudianteId/${tipo.carpeta}/$nombreArchivo.$extension"
            val storageRef = storage.reference.child(path)

            // Subir archivo
            storageRef.putFile(uri).await()

            // Obtener URL
            val downloadUrl = storageRef.downloadUrl.await().toString()
            Result.success(downloadUrl)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina un archivo de Storage
     */
    suspend fun eliminarArchivo(url: String): Result<Unit> {
        return try {
            val storageRef = storage.getReferenceFromUrl(url)
            storageRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Detecta el tipo de archivo basado en la extensión
     */
    fun detectarTipoArchivo(uri: Uri): TipoArchivo? {
        val extension = obtenerExtension(uri.toString())?.lowercase() ?: return null

        return when {
            TipoArchivo.IMAGEN.extensiones.contains(extension) -> TipoArchivo.IMAGEN
            TipoArchivo.VIDEO.extensiones.contains(extension) -> TipoArchivo.VIDEO
            TipoArchivo.PDF.extensiones.contains(extension) -> TipoArchivo.PDF
            TipoArchivo.DOCUMENTO.extensiones.contains(extension) -> TipoArchivo.DOCUMENTO
            else -> null
        }
    }

    private fun obtenerExtension(path: String): String? {
        return path.substringAfterLast('.', "").takeIf { it.isNotEmpty() && it.length <= 5 }
    }
}
