package com.healthconnect.app.utils

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await

object ReportScanner {
    private val recognizer by lazy { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }
    suspend fun extractTextFromImageUri(context: Context, uri: Uri): Result<String> = runCatching {
        val image = InputImage.fromFilePath(context, uri)
        recognizer.process(image).await().text
    }
}
