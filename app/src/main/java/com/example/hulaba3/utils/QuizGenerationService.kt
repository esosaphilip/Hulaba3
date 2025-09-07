package com.example.hulaba3.utils


import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.hulaba3.data.database.*
import com.example.hulaba3.data.repository.QuestionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuizGenerationService(
    private val context: Context,
    private val pdfExtractor: PdfTextExtractor,
    private val geminiService: GeminiApiService,
    private val questionRepository: QuestionRepository
) {

    suspend fun generateQuestionsFromPdf(
        topic: Topic,
        questionCount: Int = 5,
        difficulty: String = "medium"
    ): Result<Int> = withContext(Dispatchers.IO) {
        try {
            Log.d("QuizGeneration", "Starting question generation for topic: ${topic.title}")

            // Step 1: Extract text from PDF
            val pdfUri = Uri.parse(topic.pdfUri ?: return@withContext Result.failure(
                Exception("No PDF URI found for topic")
            ))

            val textResult = pdfExtractor.extractTextFromPdf(pdfUri)
            val extractedText = textResult.getOrElse {
                return@withContext Result.failure(it)
            }

            if (!pdfExtractor.validatePdfContent(extractedText)) {
                return@withContext Result.failure(
                    Exception("Insufficient content in PDF for question generation")
                )
            }

            // Step 2: Generate questions using AI
            val questionsResult = geminiService.generateQuestionsFromText(
                extractedText, questionCount, difficulty
            )

            val aiQuestions = questionsResult.getOrElse {
                return@withContext Result.failure(it)
            }

            // Step 3: Convert to database entities and save
            var savedQuestions = 0
            aiQuestions.forEach { aiQuestion ->
                val question = Question(
                    topicId = topic.id,
                    questionText = aiQuestion.question,
                    difficulty = aiQuestion.difficulty,
                    nextReviewTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000) // 1 day from now
                )

                val answers = aiQuestion.answers.mapIndexed { index, answerText ->
                    Answer(
                        questionId = 0, // Will be set when inserted
                        answerText = answerText,
                        isCorrect = index == aiQuestion.correctAnswerIndex,
                        orderIndex = index
                    )
                }

                questionRepository.insertQuestionWithAnswers(question, answers)
                savedQuestions++
            }

            Log.d("QuizGeneration", "Successfully generated and saved $savedQuestions questions")
            Result.success(savedQuestions)

        } catch (e: Exception) {
            Log.e("QuizGeneration", "Error in quiz generation: ${e.localizedMessage}")
            Result.failure(e)
        }
    }

    suspend fun regenerateQuestionsForTopic(topicId: String, questionCount: Int = 5): Result<Int> {
        return try {
            // Delete existing questions
            questionRepository.deleteQuestionsByTopic(topicId)

            // Note: This requires the topic to be passed or fetched
            // For now, return a placeholder
            Result.failure(Exception("Topic regeneration not implemented yet"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
