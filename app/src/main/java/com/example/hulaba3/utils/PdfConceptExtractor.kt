package com.example.hulaba3.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.hulaba3.data.database.Concept
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.util.regex.Pattern

/**
 * AI-powered PDF analysis and concept extraction service
 * Uses Google's Generative AI to extract technical concepts from PDF documents
 */
class PdfConceptExtractor(
    private val context: Context,
    private val apiKey: String
) {
    
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )
    
    data class ExtractedConcept(
        val title: String,
        val definition: String,
        val useCase: String,
        val codeExample: String? = null,
        val germanTranslation: String,
        val germanDefinition: String,
        val germanUseCase: String,
        val pageNumber: Int? = null,
        val confidenceScore: Float,
        val category: String
    )
    
    data class ExtractionResult(
        val concepts: List<ExtractedConcept>,
        val totalPages: Int,
        val processingTime: Long,
        val success: Boolean,
        val errorMessage: String? = null
    )
    
    /**
     * Extract technical concepts from a PDF document
     */
    suspend fun extractConceptsFromPdf(
        pdfUri: Uri,
        maxConcepts: Int = 20,
        minConfidence: Float = 0.7f
    ): ExtractionResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        
        try {
            // Extract text from PDF
            val pdfText = extractTextFromPdf(pdfUri)
            if (pdfText.isBlank()) {
                return@withContext ExtractionResult(
                    concepts = emptyList(),
                    totalPages = 0,
                    processingTime = System.currentTimeMillis() - startTime,
                    success = false,
                    errorMessage = "No text content found in PDF"
                )
            }
            
            // Use AI to extract concepts
            val concepts = extractConceptsWithAI(pdfText, maxConcepts, minConfidence)
            
            ExtractionResult(
                concepts = concepts,
                totalPages = estimatePageCount(pdfText),
                processingTime = System.currentTimeMillis() - startTime,
                success = true
            )
            
        } catch (e: Exception) {
            Log.e("PdfConceptExtractor", "Failed to extract concepts from PDF", e)
            ExtractionResult(
                concepts = emptyList(),
                totalPages = 0,
                processingTime = System.currentTimeMillis() - startTime,
                success = false,
                errorMessage = e.message ?: "Unknown error occurred"
            )
        }
    }
    
    /**
     * Extract text content from PDF using basic text extraction
     */
    private suspend fun extractTextFromPdf(pdfUri: Uri): String = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(pdfUri)?.use { inputStream ->
                // For now, we'll use a basic text extraction approach
                // In a production app, you might want to use a more sophisticated PDF parser
                val content = inputStream.bufferedReader().readText()
                
                // Clean up the text - remove excessive whitespace and special characters
                content.replace(Regex("\\s+"), " ")
                    .trim()
            } ?: ""
        } catch (e: Exception) {
            Log.e("PdfConceptExtractor", "Failed to extract text from PDF", e)
            ""
        }
    }
    
    /**
     * Use AI to extract technical concepts from the text
     */
    private suspend fun extractConceptsWithAI(
        text: String,
        maxConcepts: Int,
        minConfidence: Float
    ): List<ExtractedConcept> {
        try {
            val prompt = createExtractionPrompt(text, maxConcepts)
            
            val response = generativeModel.generateContent(
                content {
                    text(prompt)
                }
            )
            
            return parseAIResponse(response.text ?: "", minConfidence)
            
        } catch (e: Exception) {
            Log.e("PdfConceptExtractor", "AI extraction failed", e)
            return extractConceptsWithHeuristics(text, maxConcepts)
        }
    }
    
    /**
     * Create a prompt for AI concept extraction
     */
    private fun createExtractionPrompt(text: String, maxConcepts: Int): String {
        return """
            Analyze this technical document and extract the most important technical concepts.
            
            Document content:
            $text
            
            Please extract up to $maxConcepts technical concepts from this document. For each concept, provide:
            
            1. **Title**: A clear, concise title for the concept
            2. **Definition**: A detailed explanation of what this concept is
            3. **Use Case**: A practical example of how this concept is used
            4. **Code Example**: If applicable, a simple code example (optional)
            5. **German Translation**: The German translation of the concept title
            6. **German Definition**: The definition in German
            7. **German Use Case**: The use case description in German
            8. **Confidence Score**: A score from 0.0 to 1.0 indicating how confident you are that this is an important technical concept
            9. **Category**: One of: "programming", "algorithms", "data_structures", "web_development", "mobile_development", "machine_learning", "databases", "networking", "security", "other"
            
            Format your response as JSON with the following structure:
            {
                "concepts": [
                    {
                        "title": "Concept Name",
                        "definition": "Detailed definition...",
                        "useCase": "Practical example...",
                        "codeExample": "Optional code example...",
                        "germanTranslation": "Konzept Name",
                        "germanDefinition": "Detaillierte Definition...",
                        "germanUseCase": "Praktisches Beispiel...",
                        "confidenceScore": 0.85,
                        "category": "programming"
                    }
                ]
            }
            
            Focus on concepts that would be valuable for someone learning programming or software development.
        """.trimIndent()
    }
    
    /**
     * Parse the AI response and extract concepts
     */
    private fun parseAIResponse(responseText: String, minConfidence: Float): List<ExtractedConcept> {
        val concepts = mutableListOf<ExtractedConcept>()
        
        try {
            // Simple JSON parsing (in production, use a proper JSON parser)
            val jsonPattern = Pattern.compile(
                "\\{[^}]*\"title\"\\s*:\\s*\"([^\"]*)\"[^}]*\"definition\"\\s*:\\s*\"([^\"]*)\"[^}]*\"useCase\"\\s*:\\s*\"([^\"]*)\"[^}]*\"germanTranslation\"\\s*:\\s*\"([^\"]*)\"[^}]*\"germanDefinition\"\\s*:\\s*\"([^\"]*)\"[^}]*\"germanUseCase\"\\s*:\\s*\"([^\"]*)\"[^}]*\"confidenceScore\"\\s*:\\s*([0-9.]+)[^}]*\"category\"\\s*:\\s*\"([^\"]*)\"[^}]*\\}",
                Pattern.DOTALL
            )
            
            val matcher = jsonPattern.matcher(responseText)
            while (matcher.find() && concepts.size < 20) {
                val confidence = matcher.group(8)?.toFloatOrNull() ?: 0.5f
                if (confidence >= minConfidence) {
                    concepts.add(
                        ExtractedConcept(
                            title = matcher.group(1) ?: "Unknown Concept",
                            definition = matcher.group(2) ?: "No definition provided",
                            useCase = matcher.group(3) ?: "No use case provided",
                            codeExample = null, // Will be added separately if needed
                            germanTranslation = matcher.group(4) ?: "Unbekanntes Konzept",
                            germanDefinition = matcher.group(5) ?: "Keine Definition verfügbar",
                            germanUseCase = matcher.group(6) ?: "Kein Anwendungsfall verfügbar",
                            pageNumber = null,
                            confidenceScore = confidence,
                            category = matcher.group(7) ?: "other"
                        )
                    )
                }
            }
            
        } catch (e: Exception) {
            Log.e("PdfConceptExtractor", "Failed to parse AI response", e)
        }
        
        return concepts
    }
    
    /**
     * Fallback heuristic-based concept extraction
     */
    private fun extractConceptsWithHeuristics(text: String, maxConcepts: Int): List<ExtractedConcept> {
        val concepts = mutableListOf<ExtractedConcept>()
        
        // Common programming keywords and concepts
        val programmingConcepts = mapOf(
            "function" to "A reusable block of code that performs a specific task",
            "variable" to "A storage location with an associated name that contains data",
            "array" to "A collection of elements stored at contiguous memory locations",
            "object" to "An instance of a class containing data and methods",
            "class" to "A blueprint for creating objects with specific properties and methods",
            "loop" to "A control structure that repeats a block of code multiple times",
            "condition" to "A boolean expression that determines program flow",
            "algorithm" to "A step-by-step procedure for solving a problem",
            "data structure" to "A way of organizing and storing data for efficient access",
            "API" to "Application Programming Interface - a set of rules for software interaction"
        )
        
        // German translations
        val germanTranslations = mapOf(
            "function" to "Funktion",
            "variable" to "Variable",
            "array" to "Array",
            "object" to "Objekt",
            "class" to "Klasse",
            "loop" to "Schleife",
            "condition" to "Bedingung",
            "algorithm" to "Algorithmus",
            "data structure" to "Datenstruktur",
            "API" to "API"
        )
        
        // Extract based on keyword frequency
        val wordFrequency = text.lowercase()
            .split(Regex("\\W+"))
            .groupingBy { it }
            .eachCount()
        
        programmingConcepts.forEach { (keyword, definition) ->
            val frequency = wordFrequency[keyword.lowercase()] ?: 0
            if (frequency > 2 && concepts.size < maxConcepts) {
                concepts.add(
                    ExtractedConcept(
                        title = keyword.replaceFirstChar { it.uppercase() },
                        definition = definition,
                        useCase = "Used extensively in programming for organizing and controlling code execution",
                        codeExample = null,
                        germanTranslation = germanTranslations[keyword] ?: keyword,
                        germanDefinition = "Eine ${keyword.replaceFirstChar { it.uppercase() }} in der Programmierung",
                        germanUseCase = "Wird in der Programmierung verwendet, um Code zu organisieren und auszuführen",
                        pageNumber = null,
                        confidenceScore = 0.7f,
                        category = "programming"
                    )
                )
            }
        }
        
        return concepts
    }
    
    /**
     * Estimate page count based on text length
     */
    private fun estimatePageCount(text: String): Int {
        // Rough estimate: ~300 words per page, ~5 characters per word
        val estimatedWords = text.length / 5
        return (estimatedWords / 300).coerceAtLeast(1)
    }
    
    /**
     * Process extracted concepts and save them to the database
     */
    suspend fun saveExtractedConcepts(
        concepts: List<ExtractedConcept>,
        topicId: String,
        userId: String
    ): List<Long> = withContext(Dispatchers.IO) {
        val conceptIds = mutableListOf<Long>()
        
        concepts.forEach { extractedConcept ->
            try {
                // Create Concept entity
                val concept = Concept(
                    topicId = topicId,
                    title = extractedConcept.title,
                    definition = extractedConcept.definition,
                    useCase = extractedConcept.useCase,
                    codeExample = extractedConcept.codeExample,
                    germanTranslation = extractedConcept.germanTranslation,
                    germanDefinition = extractedConcept.germanDefinition,
                    germanUseCase = extractedConcept.germanUseCase,
                    sourcePageNumber = extractedConcept.pageNumber,
                    difficulty = "medium",
                    tags = "${extractedConcept.category}, extracted, ai",
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                
                // Save to database (this would be done through repository)
                // For now, we'll just return the concept structure
                conceptIds.add(System.currentTimeMillis()) // Placeholder ID
                
            } catch (e: Exception) {
                Log.e("PdfConceptExtractor", "Failed to save concept: ${extractedConcept.title}", e)
            }
        }
        
        conceptIds
    }
}