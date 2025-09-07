package com.example.hulaba3.data.repository

import com.example.hulaba3.data.database.QuestionAttempt
import com.example.hulaba3.data.database.QuestionAttemptDao
import com.example.hulaba3.data.database.StudySession
import com.example.hulaba3.data.database.StudySessionDao
import com.example.hulaba3.data.database.StudySessionWithResults


class StudySessionRepository(
    private val studySessionDao: StudySessionDao,
    private val questionAttemptDao: QuestionAttemptDao
) {
    suspend fun insertStudySession(session: StudySession): Long =
        studySessionDao.insertStudySession(session)

    suspend fun insertQuestionAttempt(attempt: QuestionAttempt): Long =
        questionAttemptDao.insertQuestionAttempt(attempt)

    suspend fun updateStudySession(session: StudySession) =
        studySessionDao.updateStudySession(session)

    suspend fun getSessionsByTopic(topicId: String): List<StudySession> =
        studySessionDao.getSessionsByTopic(topicId)

    suspend fun getSessionById(sessionId: Long): StudySession? =
        studySessionDao.getSessionById(sessionId)

    suspend fun getSessionWithResults(sessionId: Long): StudySessionWithResults? {
        val session = studySessionDao.getSessionById(sessionId) ?: return null
        val attempts = questionAttemptDao.getAttemptsBySession(sessionId)
        return StudySessionWithResults(session, attempts, emptyList())
    }

    suspend fun getRecentSessions(): List<StudySession> =
        studySessionDao.getRecentSessions()

    suspend fun getAverageScoreByTopic(topicId: String): Float? =
        studySessionDao.getAverageScoreByTopic(topicId)

    suspend fun getCompletedSessionCount(topicId: String): Int =
        studySessionDao.getCompletedSessionCount(topicId)
}
