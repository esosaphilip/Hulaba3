package com.example.hulaba3.utils


import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class GeminiApiService {
    private val apiKey = "API_KEY" // TODO: Move to BuildConfig or secure storage
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent"

    data class QuizQuestion(
        val question: String,
        val answers: List<String>,
        val correctAnswerIndex: Int,
        val difficulty: Int = 1
    )

    suspend fun generateQuestionsFromText(
        text: String,
        questionCount: Int = 5,
        difficulty: String = "medium"
    ): Result<List<QuizQuestion>> = withContext(Dispatchers.IO) {
        try {
            val prompt = buildPrompt(text, questionCount, difficulty)
            val response = makeApiRequest(prompt)
            parseQuizResponse(response)
        } catch (e: Exception) {
            Log.e("GeminiApiService", "Error generating questions: ${e.localizedMessage}")
            Result.failure(e)
        }
    }

    private fun buildPrompt(text: String, questionCount: Int, difficulty: String): String {
        return """
            Based on the following text, generate exactly $questionCount multiple-choice questions at $difficulty difficulty level.
            
            Text: "$text"
            
            Requirements:
            1. Each question should have exactly 4 answer choices (A, B, C, D)
            2. Only one answer should be correct
            3. Questions should test comprehension and key concepts from the text
            4. Avoid questions that are too obvious or too obscure
            5. Make incorrect answers plausible but clearly wrong
            
            Format your response as a JSON array with this exact structure:
            [
                {
                    "question": "Question text here?",
                    "answers": ["Option A", "Option B", "Option C", "Option D"],
                    "correctIndex": 0,
                    "difficulty": 2
                }
            ]
            
            Important: Return ONLY the JSON array, no additional text or explanation.
        """.trimIndent()
    }

    private suspend fun makeApiRequest(prompt: String): String = withContext(Dispatchers.IO) {
        val url = URL("$baseUrl?key=$apiKey")
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val requestBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
            }

            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(requestBody.toString())
                writer.flush()
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                val errorStream = connection.errorStream?.bufferedReader()?.use { it.readText() }
                Log.e("GeminiApiService", "API Error $responseCode: $errorStream")
                throw Exception("API request failed with code $responseCode: $errorStream")
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun parseQuizResponse(response: String): Result<List<QuizQuestion>> {
        return try {
            Log.d("GeminiApiService", "Raw API response: $response")

            val responseJson = JSONObject(response)
            val candidates = responseJson.getJSONArray("candidates")
            val content = candidates.getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")

            // Extract JSON from the response text (might have additional formatting)
            val jsonStart = content.indexOf("[")
            val jsonEnd = content.lastIndexOf("]") + 1
            val jsonContent = content.substring(jsonStart, jsonEnd)

            val questionsArray = JSONArray(jsonContent)
            val questions = mutableListOf<QuizQuestion>()

            for (i in 0 until questionsArray.length()) {
                val questionObj = questionsArray.getJSONObject(i)
                val answersArray = questionObj.getJSONArray("answers")
                val answers = mutableListOf<String>()

                for (j in 0 until answersArray.length()) {
                    answers.add(answersArray.getString(j))
                }

                questions.add(
                    QuizQuestion(
                        question = questionObj.getString("question"),
                        answers = answers,
                        correctAnswerIndex = questionObj.getInt("correctIndex"),
                        difficulty = questionObj.optInt("difficulty", 2)
                    )
                )
            }

            Log.d("GeminiApiService", "Successfully parsed ${questions.size} questions")
            Result.success(questions)

        } catch (e: Exception) {
            Log.e("GeminiApiService", "Error parsing quiz response: ${e.localizedMessage}")
            Result.failure(Exception("Failed to parse AI response: ${e.localizedMessage}"))
        }
    }
}