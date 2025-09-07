package com.example.hulaba3.data.database


// Study session with detailed results
data class StudySessionWithResults(
    val session: StudySession,
    val attempts: List<QuestionAttempt>,
    val questionsWithAnswers: List<QuestionWithAnswers>
)