package com.example.physicshub.util

import android.content.Context
import android.net.Uri
import com.example.physicshub.data.model.FileType

object FileValidation {

    private const val MAX_FILE_SIZE_MB = 10
    private const val MAX_FILE_SIZE_BYTES = MAX_FILE_SIZE_MB * 1024 * 1024

    private val allowedMimeTypes = setOf(
        "application/pdf",
        "image/jpeg",
        "image/png"
    )

    fun isValidFile(context: Context, uri: Uri): Boolean {
        val resolver = context.contentResolver
        val mimeType = resolver.getType(uri) ?: return false
        if (mimeType !in allowedMimeTypes) return false

        val size = resolver.openFileDescriptor(uri, "r")?.statSize ?: return false
        return size <= MAX_FILE_SIZE_BYTES
    }

    fun getFileType(mime: String): FileType =
        when (mime) {
            "application/pdf" -> FileType.PDF
            "image/jpeg", "image/png" -> FileType.IMAGE
            else -> throw IllegalArgumentException("Unsupported mime type")
        }
}
