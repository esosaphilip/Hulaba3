package com.example.hulaba3.utils


import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

class PdfTextExtractor(private val context: Context) {

    suspend fun extractTextFromPdf(pdfUri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(pdfUri)
            inputStream?.use { stream ->
                // For now, return a placeholder. We'll implement actual PDF extraction
                // after adding the iText dependency
                val extractedText = extractTextUsingSimpleMethod(stream)
                if (extractedText.isNotBlank()) {
                    Log.d("PdfTextExtractor", "Successfully extracted ${extractedText.length} characters")
                    Result.success(extractedText)
                } else {
                    Log.w("PdfTextExtractor", "No text extracted from PDF")
                    Result.failure(Exception("No text content found in PDF"))
                }
            } ?: run {
                Log.e("PdfTextExtractor", "Could not open PDF stream")
                Result.failure(Exception("Could not access PDF file"))
            }
        } catch (e: Exception) {
            Log.e("PdfTextExtractor", "Error extracting PDF text: ${e.localizedMessage}")
            Result.failure(e)
        }
    }

    private fun extractTextUsingSimpleMethod(inputStream: InputStream): String {
        // Placeholder implementation - will be replaced with iText
        return try {
            val buffer = ByteArray(1024)
            val content = StringBuilder()
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                // This is just reading raw bytes - not actual PDF parsing
                // Will be replaced with proper PDF text extraction
                content.append(String(buffer, 0, bytesRead))
            }

            // For development/testing, return a sample text if no real extraction
            if (content.toString().length < 100) {
                "Sample PDF content for testing AI question generation. This text discusses various topics including technology, science, and learning methodologies that can be used to create educational content."
            } else {
                content.toString()
            }
        } catch (e: Exception) {
            Log.w("PdfTextExtractor", "Fallback text extraction failed: ${e.localizedMessage}")
            "Sample content for AI question generation testing."
        }
    }

    fun validatePdfContent(extractedText: String): Boolean {
        return extractedText.trim().length >= 50 // Minimum content length for meaningful questions
    }
}