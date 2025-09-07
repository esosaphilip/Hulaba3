package com.example.hulaba3.data.repository


import com.example.hulaba3.data.database.*

class QuestionRepository(
    private val questionDao: QuestionDao,
    private val answerDao: AnswerDao
) {
    suspend fun insertQuestionWithAnswers(question: Question, answers: List<Answer>): Long {
        val questionId = questionDao.insertQuestion(question)
        val answersWithQuestionId = answers.map { it.copy(questionId = questionId) }
        answerDao.insertAnswers(answersWithQuestionId)
        return questionId
    }

    suspend fun insertMultipleQuestionsWithAnswers(questionsWithAnswers: List<Pair<Question, List<Answer>>>) {
        questionsWithAnswers.forEach { (question, answers) ->
            insertQuestionWithAnswers(question, answers)
        }
    }

    suspend fun getQuestionsByTopic(topicId: String): List<Question> =
        questionDao.getQuestionsByTopic(topicId)

    suspend fun getQuestionWithAnswers(questionId: Long): QuestionWithAnswers? {
        val question = questionDao.getQuestionById(questionId) ?: return null
        val answers = answerDao.getAnswersByQuestion(questionId)
        return QuestionWithAnswers(question, answers)
    }

    suspend fun getQuestionsWithAnswersByTopic(topicId: String): List<QuestionWithAnswers> {
        val questions = questionDao.getQuestionsByTopic(topicId)
        return questions.map { question ->
            val answers = answerDao.getAnswersByQuestion(question.id)
            QuestionWithAnswers(question, answers)
        }
    }

    suspend fun getQuestionsForReview(topicId: String, limit: Int = 10): List<QuestionWithAnswers> {
        val currentTime = System.currentTimeMillis()
        val questions = questionDao.getQuestionsForReview(topicId, currentTime, limit)
        return questions.map { question ->
            val answers = answerDao.getAnswersByQuestion(question.id)
            QuestionWithAnswers(question, answers)
        }
    }

    suspend fun updateQuestion(question: Question) = questionDao.updateQuestion(question)

    suspend fun deleteQuestionsByTopic(topicId: String) {
        questionDao.deleteQuestionsByTopic(topicId)
    }

    suspend fun getQuestionCountByTopic(topicId: String): Int =
        questionDao.getQuestionCountByTopic(topicId)

    suspend fun getCorrectAnswer(questionId: Long): Answer? =
        answerDao.getCorrectAnswer(questionId)
}
