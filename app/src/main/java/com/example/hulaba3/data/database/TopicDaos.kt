package com.example.hulaba3.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicCategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: TopicCategory): Long

    @Update
    suspend fun updateCategory(category: TopicCategory)

    @Delete
    suspend fun deleteCategory(category: TopicCategory)

    @Query("SELECT * FROM topic_categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: Long): TopicCategory?

    @Query("SELECT * FROM topic_categories WHERE isActive = 1 ORDER BY orderIndex, name")
    suspend fun getActiveCategories(): List<TopicCategory>

    @Query("SELECT * FROM topic_categories WHERE isActive = 1 ORDER BY orderIndex, name")
    fun getActiveCategoriesFlow(): Flow<List<TopicCategory>>

    @Query("SELECT * FROM topic_categories WHERE parentCategoryId = :parentId AND isActive = 1 ORDER BY orderIndex, name")
    suspend fun getSubcategories(parentId: Long?): List<TopicCategory>

    @Query("SELECT * FROM topic_categories WHERE name = :name LIMIT 1")
    suspend fun getCategoryByName(name: String): TopicCategory?

    @Query("UPDATE topic_categories SET orderIndex = :orderIndex WHERE id = :categoryId")
    suspend fun updateCategoryOrder(categoryId: Long, orderIndex: Int)

    @Query("UPDATE topic_categories SET isActive = 0 WHERE id = :categoryId")
    suspend fun deactivateCategory(categoryId: Long)

    @Query("DELETE FROM topic_categories")
    suspend fun deleteAllCategories()
}

@Dao
interface ConceptDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConcept(concept: Concept): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConcepts(concepts: List<Concept>)

    @Update
    suspend fun updateConcept(concept: Concept)

    @Delete
    suspend fun deleteConcept(concept: Concept)

    @Query("SELECT * FROM concepts WHERE id = :conceptId")
    suspend fun getConceptById(conceptId: Long): Concept?

    @Query("SELECT * FROM concepts WHERE topicId = :topicId ORDER BY orderIndex, title")
    suspend fun getConceptsByTopic(topicId: String): List<Concept>

    @Query("SELECT * FROM concepts WHERE topicId = :topicId ORDER BY orderIndex, title")
    fun getConceptsByTopicFlow(topicId: String): Flow<List<Concept>>

    @Query("SELECT * FROM concepts WHERE topicId = :topicId AND difficulty = :difficulty ORDER BY orderIndex, title")
    suspend fun getConceptsByDifficulty(topicId: String, difficulty: String): List<Concept>

    @Query("SELECT * FROM concepts WHERE topicId = :topicId ORDER BY orderIndex, title LIMIT :limit")
    suspend fun getConceptsByTopicLimited(topicId: String, limit: Int): List<Concept>

    @Query("SELECT * FROM concepts WHERE topicId = :topicId AND title LIKE '%' || :searchQuery || '%' ORDER BY title")
    suspend fun searchConceptsInTopic(topicId: String, searchQuery: String): List<Concept>

    @Query("UPDATE concepts SET orderIndex = :orderIndex WHERE id = :conceptId")
    suspend fun updateConceptOrder(conceptId: Long, orderIndex: Int)

    @Query("DELETE FROM concepts WHERE topicId = :topicId")
    suspend fun deleteConceptsByTopic(topicId: String)

    @Query("DELETE FROM concepts")
    suspend fun deleteAllConcepts()
}

@Dao
interface StudyMaterialDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: StudyMaterial): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterials(materials: List<StudyMaterial>)

    @Update
    suspend fun updateMaterial(material: StudyMaterial)

    @Delete
    suspend fun deleteMaterial(material: StudyMaterial)

    @Query("SELECT * FROM study_materials WHERE id = :materialId")
    suspend fun getMaterialById(materialId: Long): StudyMaterial?

    @Query("SELECT * FROM study_materials WHERE topicId = :topicId ORDER BY isPrimary DESC, orderIndex, title")
    suspend fun getMaterialsByTopic(topicId: String): List<StudyMaterial>

    @Query("SELECT * FROM study_materials WHERE topicId = :topicId ORDER BY isPrimary DESC, orderIndex, title")
    fun getMaterialsByTopicFlow(topicId: String): Flow<List<StudyMaterial>>

    @Query("SELECT * FROM study_materials WHERE topicId = :topicId AND isPrimary = 1 LIMIT 1")
    suspend fun getPrimaryMaterialByTopic(topicId: String): StudyMaterial?

    @Query("SELECT * FROM study_materials WHERE topicId = :topicId AND fileType = :fileType ORDER BY orderIndex, title")
    suspend fun getMaterialsByType(topicId: String, fileType: String): List<StudyMaterial>

    @Query("UPDATE study_materials SET isPrimary = 0 WHERE topicId = :topicId")
    suspend fun clearPrimaryMaterials(topicId: String)

    @Query("UPDATE study_materials SET isPrimary = 1 WHERE id = :materialId")
    suspend fun setAsPrimary(materialId: Long)

    @Query("DELETE FROM study_materials WHERE topicId = :topicId")
    suspend fun deleteMaterialsByTopic(topicId: String)

    @Query("DELETE FROM study_materials")
    suspend fun deleteAllMaterials()
}

@Dao
interface UserTopicProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: UserTopicProgress): Long

    @Update
    suspend fun updateProgress(progress: UserTopicProgress)

    @Query("SELECT * FROM user_topic_progress WHERE id = :progressId")
    suspend fun getProgressById(progressId: Long): UserTopicProgress?

    @Query("SELECT * FROM user_topic_progress WHERE userId = :userId AND topicId = :topicId")
    suspend fun getProgress(userId: String, topicId: String): UserTopicProgress?

    @Query("SELECT * FROM user_topic_progress WHERE userId = :userId AND topicId = :topicId")
    fun getProgressFlow(userId: String, topicId: String): Flow<UserTopicProgress?>

    @Query("""
        SELECT 
            t.id AS topic_id,
            t.title AS topic_title,
            t.description AS topic_description,
            t.categoryId AS topic_categoryId,
            t.difficultyLevel AS topic_difficultyLevel,
            t.estimatedStudyTimeMinutes AS topic_estimatedStudyTimeMinutes,
            t.totalConceptsCount AS topic_totalConceptsCount,
            t.coverImageUrl AS topic_coverImageUrl,
            t.createdBy AS topic_createdBy,
            t.isPublic AS topic_isPublic,
            t.isOfficial AS topic_isOfficial,
            t.language AS topic_language,
            t.tags AS topic_tags,
            t.prerequisites AS topic_prerequisites,
            t.createdAt AS topic_createdAt,
            t.updatedAt AS topic_updatedAt,
            utp.id AS progress_id,
            utp.userId AS progress_userId,
            utp.topicId AS progress_topicId,
            utp.status AS progress_status,
            utp.progressPercentage AS progress_progressPercentage,
            utp.masteredConceptsCount AS progress_masteredConceptsCount,
            utp.totalConceptsCount AS progress_totalConceptsCount,
            utp.lastStudiedAt AS progress_lastStudiedAt,
            utp.nextReviewAt AS progress_nextReviewAt,
            utp.startedAt AS progress_startedAt,
            utp.completedAt AS progress_completedAt,
            utp.totalStudyTimeMinutes AS progress_totalStudyTimeMinutes,
            utp.averageSessionDurationMinutes AS progress_averageSessionDurationMinutes,
            utp.confidenceLevel AS progress_confidenceLevel,
            utp.notes AS progress_notes,
            utp.isFavorite AS progress_isFavorite,
            utp.createdAt AS progress_createdAt,
            utp.updatedAt AS progress_updatedAt
        FROM topics t
        INNER JOIN user_topic_progress utp ON t.id = utp.topicId
        WHERE utp.userId = :userId AND utp.status = :status
        ORDER BY utp.lastStudiedAt DESC
    """)
    suspend fun getTopicsByStatus(userId: String, status: String): List<TopicWithProgress>

    @Query("""
        SELECT 
            t.id AS topic_id,
            t.title AS topic_title,
            t.description AS topic_description,
            t.categoryId AS topic_categoryId,
            t.difficultyLevel AS topic_difficultyLevel,
            t.estimatedStudyTimeMinutes AS topic_estimatedStudyTimeMinutes,
            t.totalConceptsCount AS topic_totalConceptsCount,
            t.coverImageUrl AS topic_coverImageUrl,
            t.createdBy AS topic_createdBy,
            t.isPublic AS topic_isPublic,
            t.isOfficial AS topic_isOfficial,
            t.language AS topic_language,
            t.tags AS topic_tags,
            t.prerequisites AS topic_prerequisites,
            t.createdAt AS topic_createdAt,
            t.updatedAt AS topic_updatedAt,
            utp.id AS progress_id,
            utp.userId AS progress_userId,
            utp.topicId AS progress_topicId,
            utp.status AS progress_status,
            utp.progressPercentage AS progress_progressPercentage,
            utp.masteredConceptsCount AS progress_masteredConceptsCount,
            utp.totalConceptsCount AS progress_totalConceptsCount,
            utp.lastStudiedAt AS progress_lastStudiedAt,
            utp.nextReviewAt AS progress_nextReviewAt,
            utp.startedAt AS progress_startedAt,
            utp.completedAt AS progress_completedAt,
            utp.totalStudyTimeMinutes AS progress_totalStudyTimeMinutes,
            utp.averageSessionDurationMinutes AS progress_averageSessionDurationMinutes,
            utp.confidenceLevel AS progress_confidenceLevel,
            utp.notes AS progress_notes,
            utp.isFavorite AS progress_isFavorite,
            utp.createdAt AS progress_createdAt,
            utp.updatedAt AS progress_updatedAt
        FROM topics t
        INNER JOIN user_topic_progress utp ON t.id = utp.topicId
        WHERE utp.userId = :userId AND utp.nextReviewAt <= :currentTime
        ORDER BY utp.nextReviewAt ASC
    """)
    suspend fun getTopicsForReview(userId: String, currentTime: Long): List<TopicWithProgress>

    @Query("""
        SELECT 
            t.id AS topic_id,
            t.title AS topic_title,
            t.description AS topic_description,
            t.categoryId AS topic_categoryId,
            t.difficultyLevel AS topic_difficultyLevel,
            t.estimatedStudyTimeMinutes AS topic_estimatedStudyTimeMinutes,
            t.totalConceptsCount AS topic_totalConceptsCount,
            t.coverImageUrl AS topic_coverImageUrl,
            t.createdBy AS topic_createdBy,
            t.isPublic AS topic_isPublic,
            t.isOfficial AS topic_isOfficial,
            t.language AS topic_language,
            t.tags AS topic_tags,
            t.prerequisites AS topic_prerequisites,
            t.createdAt AS topic_createdAt,
            t.updatedAt AS topic_updatedAt,
            utp.id AS progress_id,
            utp.userId AS progress_userId,
            utp.topicId AS progress_topicId,
            utp.status AS progress_status,
            utp.progressPercentage AS progress_progressPercentage,
            utp.masteredConceptsCount AS progress_masteredConceptsCount,
            utp.totalConceptsCount AS progress_totalConceptsCount,
            utp.lastStudiedAt AS progress_lastStudiedAt,
            utp.nextReviewAt AS progress_nextReviewAt,
            utp.startedAt AS progress_startedAt,
            utp.completedAt AS progress_completedAt,
            utp.totalStudyTimeMinutes AS progress_totalStudyTimeMinutes,
            utp.averageSessionDurationMinutes AS progress_averageSessionDurationMinutes,
            utp.confidenceLevel AS progress_confidenceLevel,
            utp.notes AS progress_notes,
            utp.isFavorite AS progress_isFavorite,
            utp.createdAt AS progress_createdAt,
            utp.updatedAt AS progress_updatedAt
        FROM topics t
        INNER JOIN user_topic_progress utp ON t.id = utp.topicId
        WHERE utp.userId = :userId AND utp.isFavorite = 1
        ORDER BY utp.lastStudiedAt DESC
    """)
    suspend fun getFavoriteTopics(userId: String): List<TopicWithProgress>

    @Query("""
        SELECT 
            t.id AS topic_id,
            t.title AS topic_title,
            t.description AS topic_description,
            t.categoryId AS topic_categoryId,
            t.difficultyLevel AS topic_difficultyLevel,
            t.estimatedStudyTimeMinutes AS topic_estimatedStudyTimeMinutes,
            t.totalConceptsCount AS topic_totalConceptsCount,
            t.coverImageUrl AS topic_coverImageUrl,
            t.createdBy AS topic_createdBy,
            t.isPublic AS topic_isPublic,
            t.isOfficial AS topic_isOfficial,
            t.language AS topic_language,
            t.tags AS topic_tags,
            t.prerequisites AS topic_prerequisites,
            t.createdAt AS topic_createdAt,
            t.updatedAt AS topic_updatedAt,
            utp.id AS progress_id,
            utp.userId AS progress_userId,
            utp.topicId AS progress_topicId,
            utp.status AS progress_status,
            utp.progressPercentage AS progress_progressPercentage,
            utp.masteredConceptsCount AS progress_masteredConceptsCount,
            utp.totalConceptsCount AS progress_totalConceptsCount,
            utp.lastStudiedAt AS progress_lastStudiedAt,
            utp.nextReviewAt AS progress_nextReviewAt,
            utp.startedAt AS progress_startedAt,
            utp.completedAt AS progress_completedAt,
            utp.totalStudyTimeMinutes AS progress_totalStudyTimeMinutes,
            utp.averageSessionDurationMinutes AS progress_averageSessionDurationMinutes,
            utp.confidenceLevel AS progress_confidenceLevel,
            utp.notes AS progress_notes,
            utp.isFavorite AS progress_isFavorite,
            utp.createdAt AS progress_createdAt,
            utp.updatedAt AS progress_updatedAt
        FROM topics t
        INNER JOIN user_topic_progress utp ON t.id = utp.topicId
        INNER JOIN topic_categories tc ON t.categoryId = tc.id
        WHERE utp.userId = :userId AND tc.name = :categoryName
        ORDER BY utp.lastStudiedAt DESC
    """)
    suspend fun getTopicsByCategory(userId: String, categoryName: String): List<TopicWithProgress>

    @Query("""
        SELECT 
            t.id AS topic_id,
            t.title AS topic_title,
            t.description AS topic_description,
            t.categoryId AS topic_categoryId,
            t.difficultyLevel AS topic_difficultyLevel,
            t.estimatedStudyTimeMinutes AS topic_estimatedStudyTimeMinutes,
            t.totalConceptsCount AS topic_totalConceptsCount,
            t.coverImageUrl AS topic_coverImageUrl,
            t.createdBy AS topic_createdBy,
            t.isPublic AS topic_isPublic,
            t.isOfficial AS topic_isOfficial,
            t.language AS topic_language,
            t.tags AS topic_tags,
            t.prerequisites AS topic_prerequisites,
            t.createdAt AS topic_createdAt,
            t.updatedAt AS topic_updatedAt,
            utp.id AS progress_id,
            utp.userId AS progress_userId,
            utp.topicId AS progress_topicId,
            utp.status AS progress_status,
            utp.progressPercentage AS progress_progressPercentage,
            utp.masteredConceptsCount AS progress_masteredConceptsCount,
            utp.totalConceptsCount AS progress_totalConceptsCount,
            utp.lastStudiedAt AS progress_lastStudiedAt,
            utp.nextReviewAt AS progress_nextReviewAt,
            utp.startedAt AS progress_startedAt,
            utp.completedAt AS progress_completedAt,
            utp.totalStudyTimeMinutes AS progress_totalStudyTimeMinutes,
            utp.averageSessionDurationMinutes AS progress_averageSessionDurationMinutes,
            utp.confidenceLevel AS progress_confidenceLevel,
            utp.notes AS progress_notes,
            utp.isFavorite AS progress_isFavorite,
            utp.createdAt AS progress_createdAt,
            utp.updatedAt AS progress_updatedAt
        FROM topics t
        INNER JOIN user_topic_progress utp ON t.id = utp.topicId
        WHERE utp.userId = :userId AND t.difficultyLevel = :difficulty
        ORDER BY utp.lastStudiedAt DESC
    """)
    suspend fun getTopicsByDifficulty(userId: String, difficulty: String): List<TopicWithProgress>

    @Query("""
        SELECT 
            COUNT(*) as totalTopics,
            SUM(CASE WHEN status = 'not_started' THEN 1 ELSE 0 END) as notStarted,
            SUM(CASE WHEN status = 'in_progress' THEN 1 ELSE 0 END) as inProgress,
            SUM(CASE WHEN status = 'completed' THEN 1 ELSE 0 END) as completed,
            SUM(CASE WHEN status = 'mastered' THEN 1 ELSE 0 END) as mastered,
            AVG(progressPercentage) as avgProgress,
            SUM(totalStudyTimeMinutes) as totalStudyTime
        FROM user_topic_progress
        WHERE userId = :userId
    """)
    suspend fun getTopicStats(userId: String): TopicStats?

    @Query("UPDATE user_topic_progress SET status = :status, updatedAt = :updatedAt WHERE id = :progressId")
    suspend fun updateTopicStatus(progressId: Long, status: String, updatedAt: Long)

    @Query("UPDATE user_topic_progress SET progressPercentage = :progress, updatedAt = :updatedAt WHERE id = :progressId")
    suspend fun updateProgressPercentage(progressId: Long, progress: Float, updatedAt: Long)

    @Query("UPDATE user_topic_progress SET masteredConceptsCount = :count, updatedAt = :updatedAt WHERE id = :progressId")
    suspend fun updateMasteredConceptsCount(progressId: Long, count: Int, updatedAt: Long)

    @Query("UPDATE user_topic_progress SET lastStudiedAt = :studiedAt, updatedAt = :updatedAt WHERE id = :progressId")
    suspend fun updateLastStudiedAt(progressId: Long, studiedAt: Long?, updatedAt: Long)

    @Query("UPDATE user_topic_progress SET nextReviewAt = :reviewAt, updatedAt = :updatedAt WHERE id = :progressId")
    suspend fun updateNextReviewAt(progressId: Long, reviewAt: Long?, updatedAt: Long)

    @Query("UPDATE user_topic_progress SET completedAt = :completedAt, updatedAt = :updatedAt WHERE id = :progressId")
    suspend fun markAsCompleted(progressId: Long, completedAt: Long, updatedAt: Long)

    @Query("UPDATE user_topic_progress SET isFavorite = :isFavorite, updatedAt = :updatedAt WHERE id = :progressId")
    suspend fun updateFavoriteStatus(progressId: Long, isFavorite: Boolean, updatedAt: Long)

    @Query("UPDATE user_topic_progress SET totalStudyTimeMinutes = totalStudyTimeMinutes + :minutes, updatedAt = :updatedAt WHERE id = :progressId")
    suspend fun addStudyTime(progressId: Long, minutes: Int, updatedAt: Long)

    @Query("UPDATE user_topic_progress SET notes = :notes, updatedAt = :updatedAt WHERE id = :progressId")
    suspend fun updateNotes(progressId: Long, notes: String?, updatedAt: Long)

    @Query("DELETE FROM user_topic_progress WHERE userId = :userId")
    suspend fun deleteAllUserProgress(userId: String)

    @Query("DELETE FROM user_topic_progress WHERE id = :progressId")
    suspend fun deleteProgress(progressId: Long)
}

@Dao
interface UserConceptProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: UserConceptProgress): Long

    @Update
    suspend fun updateProgress(progress: UserConceptProgress)

    @Query("SELECT * FROM user_concept_progress WHERE id = :progressId")
    suspend fun getProgressById(progressId: Long): UserConceptProgress?

    @Query("SELECT * FROM user_concept_progress WHERE userId = :userId AND conceptId = :conceptId")
    suspend fun getProgress(userId: String, conceptId: Long): UserConceptProgress?

    @Query("SELECT * FROM user_concept_progress WHERE userId = :userId AND conceptId = :conceptId")
    fun getProgressFlow(userId: String, conceptId: Long): Flow<UserConceptProgress?>

    @Query("""
        SELECT 
            c.id AS concept_id,
            c.topicId AS concept_topicId,
            c.title AS concept_title,
            c.description AS concept_description,
            c.definition AS concept_definition,
            c.useCase AS concept_useCase,
            c.visualDiagramUrl AS concept_visualDiagramUrl,
            c.codeExample AS concept_codeExample,
            c.germanTranslation AS concept_germanTranslation,
            c.germanDefinition AS concept_germanDefinition,
            c.germanUseCase AS concept_germanUseCase,
            c.sourcePageNumber AS concept_sourcePageNumber,
            c.orderIndex AS concept_orderIndex,
            c.difficulty AS concept_difficulty,
            c.estimatedStudyTimeMinutes AS concept_estimatedStudyTimeMinutes,
            c.relatedConceptIds AS concept_relatedConceptIds,
            c.tags AS concept_tags,
            c.createdAt AS concept_createdAt,
            c.updatedAt AS concept_updatedAt,
            ucp.id AS progress_id,
            ucp.userId AS progress_userId,
            ucp.conceptId AS progress_conceptId,
            ucp.topicId AS progress_topicId,
            ucp.status AS progress_status,
            ucp.confidenceLevel AS progress_confidenceLevel,
            ucp.lastReviewedAt AS progress_lastReviewedAt,
            ucp.nextReviewAt AS progress_nextReviewAt,
            ucp.reviewCount AS progress_reviewCount,
            ucp.correctReviewsCount AS progress_correctReviewsCount,
            ucp.timeSpentMinutes AS progress_timeSpentMinutes,
            ucp.notes AS progress_notes,
            ucp.isFlagged AS progress_isFlagged,
            ucp.createdAt AS progress_createdAt,
            ucp.updatedAt AS progress_updatedAt
        FROM concepts c
        INNER JOIN user_concept_progress ucp ON c.id = ucp.conceptId
        WHERE ucp.userId = :userId AND ucp.topicId = :topicId AND ucp.status = :status
        ORDER BY c.orderIndex, c.title
    """)
    suspend fun getConceptsByStatus(userId: String, topicId: String, status: String): List<ConceptWithProgress>

    @Query("""
        SELECT 
            c.id AS concept_id,
            c.topicId AS concept_topicId,
            c.title AS concept_title,
            c.description AS concept_description,
            c.definition AS concept_definition,
            c.useCase AS concept_useCase,
            c.visualDiagramUrl AS concept_visualDiagramUrl,
            c.codeExample AS concept_codeExample,
            c.germanTranslation AS concept_germanTranslation,
            c.germanDefinition AS concept_germanDefinition,
            c.germanUseCase AS concept_germanUseCase,
            c.sourcePageNumber AS concept_sourcePageNumber,
            c.orderIndex AS concept_orderIndex,
            c.difficulty AS concept_difficulty,
            c.estimatedStudyTimeMinutes AS concept_estimatedStudyTimeMinutes,
            c.relatedConceptIds AS concept_relatedConceptIds,
            c.tags AS concept_tags,
            c.createdAt AS concept_createdAt,
            c.updatedAt AS concept_updatedAt,
            ucp.id AS progress_id,
            ucp.userId AS progress_userId,
            ucp.conceptId AS progress_conceptId,
            ucp.topicId AS progress_topicId,
            ucp.status AS progress_status,
            ucp.confidenceLevel AS progress_confidenceLevel,
            ucp.lastReviewedAt AS progress_lastReviewedAt,
            ucp.nextReviewAt AS progress_nextReviewAt,
            ucp.reviewCount AS progress_reviewCount,
            ucp.correctReviewsCount AS progress_correctReviewsCount,
            ucp.timeSpentMinutes AS progress_timeSpentMinutes,
            ucp.notes AS progress_notes,
            ucp.isFlagged AS progress_isFlagged,
            ucp.createdAt AS progress_createdAt,
            ucp.updatedAt AS progress_updatedAt
        FROM concepts c
        INNER JOIN user_concept_progress ucp ON c.id = ucp.conceptId
        WHERE ucp.userId = :userId AND ucp.topicId = :topicId AND ucp.nextReviewAt <= :currentTime
        ORDER BY c.orderIndex, c.title
    """)
    suspend fun getConceptsForReview(userId: String, topicId: String, currentTime: Long): List<ConceptWithProgress>

    @Query("""
        SELECT 
            c.id AS concept_id,
            c.topicId AS concept_topicId,
            c.title AS concept_title,
            c.description AS concept_description,
            c.definition AS concept_definition,
            c.useCase AS concept_useCase,
            c.visualDiagramUrl AS concept_visualDiagramUrl,
            c.codeExample AS concept_codeExample,
            c.germanTranslation AS concept_germanTranslation,
            c.germanDefinition AS concept_germanDefinition,
            c.germanUseCase AS concept_germanUseCase,
            c.sourcePageNumber AS concept_sourcePageNumber,
            c.orderIndex AS concept_orderIndex,
            c.difficulty AS concept_difficulty,
            c.estimatedStudyTimeMinutes AS concept_estimatedStudyTimeMinutes,
            c.relatedConceptIds AS concept_relatedConceptIds,
            c.tags AS concept_tags,
            c.createdAt AS concept_createdAt,
            c.updatedAt AS concept_updatedAt,
            ucp.id AS progress_id,
            ucp.userId AS progress_userId,
            ucp.conceptId AS progress_conceptId,
            ucp.topicId AS progress_topicId,
            ucp.status AS progress_status,
            ucp.confidenceLevel AS progress_confidenceLevel,
            ucp.lastReviewedAt AS progress_lastReviewedAt,
            ucp.nextReviewAt AS progress_nextReviewAt,
            ucp.reviewCount AS progress_reviewCount,
            ucp.correctReviewsCount AS progress_correctReviewsCount,
            ucp.timeSpentMinutes AS progress_timeSpentMinutes,
            ucp.notes AS progress_notes,
            ucp.isFlagged AS progress_isFlagged,
            ucp.createdAt AS progress_createdAt,
            ucp.updatedAt AS progress_updatedAt
        FROM concepts c
        INNER JOIN user_concept_progress ucp ON c.id = ucp.conceptId
        WHERE ucp.userId = :userId AND ucp.topicId = :topicId AND ucp.isFlagged = 1
        ORDER BY c.orderIndex, c.title
    """)
    suspend fun getFlaggedConcepts(userId: String, topicId: String): List<ConceptWithProgress>

    @Query("""
        SELECT 
            COUNT(*) as totalConcepts,
            SUM(CASE WHEN status = 'new' THEN 1 ELSE 0 END) as newConcepts,
            SUM(CASE WHEN status = 'learning' THEN 1 ELSE 0 END) as learningConcepts,
            SUM(CASE WHEN status = 'reviewing' THEN 1 ELSE 0 END) as reviewingConcepts,
            SUM(CASE WHEN status = 'mastered' THEN 1 ELSE 0 END) as masteredConcepts,
            AVG(confidenceLevel) as avgConfidence,
            SUM(timeSpentMinutes) as totalStudyTime
        FROM user_concept_progress
        WHERE userId = :userId AND topicId = :topicId
    """)
    suspend fun getConceptStats(userId: String, topicId: String): ConceptStats?

    @Query("UPDATE user_concept_progress SET status = :status, updatedAt = :updatedAt WHERE id = :progressId")
    suspend fun updateConceptStatus(progressId: Long, status: String, updatedAt: Long)

    @Query("UPDATE user_concept_progress SET confidenceLevel = :confidence, updatedAt = :updatedAt WHERE id = :progressId")
    suspend fun updateConfidenceLevel(progressId: Long, confidence: Int, updatedAt: Long)

    @Query("UPDATE user_concept_progress SET lastReviewedAt = :reviewedAt, nextReviewAt = :nextReviewAt, updatedAt = :updatedAt WHERE id = :progressId")
    suspend fun updateReviewSchedule(progressId: Long, reviewedAt: Long?, nextReviewAt: Long?, updatedAt: Long)

    @Query("UPDATE user_concept_progress SET isFlagged = :isFlagged, updatedAt = :updatedAt WHERE id = :progressId")
    suspend fun updateFlaggedStatus(progressId: Long, isFlagged: Boolean, updatedAt: Long)

    @Query("UPDATE user_concept_progress SET notes = :notes, updatedAt = :updatedAt WHERE id = :progressId")
    suspend fun updateNotes(progressId: Long, notes: String?, updatedAt: Long)

    @Query("UPDATE user_concept_progress SET timeSpentMinutes = timeSpentMinutes + :minutes, updatedAt = :updatedAt WHERE id = :progressId")
    suspend fun addStudyTime(progressId: Long, minutes: Int, updatedAt: Long)

    @Query("UPDATE user_concept_progress SET reviewCount = reviewCount + 1, updatedAt = :updatedAt WHERE id = :progressId")
    suspend fun incrementReviewCount(progressId: Long, updatedAt: Long)

    @Query("UPDATE user_concept_progress SET correctReviewsCount = correctReviewsCount + 1, updatedAt = :updatedAt WHERE id = :progressId")
    suspend fun incrementCorrectReviewCount(progressId: Long, updatedAt: Long)

    @Query("DELETE FROM user_concept_progress WHERE userId = :userId")
    suspend fun deleteAllUserProgress(userId: String)

    @Query("DELETE FROM user_concept_progress WHERE id = :progressId")
    suspend fun deleteProgress(progressId: Long)
}

@Dao
interface ConceptReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ConceptReview): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ConceptReview>)

    @Update
    suspend fun updateReview(review: ConceptReview)

    @Query("SELECT * FROM concept_reviews WHERE id = :reviewId")
    suspend fun getReviewById(reviewId: Long): ConceptReview?

    @Query("""
        SELECT * FROM concept_reviews
        WHERE userId = :userId AND conceptId = :conceptId AND topicId = :topicId
        ORDER BY reviewedAt DESC
    """)
    suspend fun getReviewsByConcept(userId: String, conceptId: Long, topicId: String): List<ConceptReview>

    @Query("""
        SELECT * FROM concept_reviews
        WHERE userId = :userId AND conceptId = :conceptId AND topicId = :topicId
        ORDER BY reviewedAt DESC
        LIMIT :limit
    """)
    suspend fun getRecentReviewsByConcept(userId: String, conceptId: Long, topicId: String, limit: Int = 10): List<ConceptReview>

    @Query("""
        SELECT * FROM concept_reviews
        WHERE sessionId = :sessionId
        ORDER BY reviewedAt DESC
    """)
    suspend fun getReviewsBySession(sessionId: Long): List<ConceptReview>

    @Query("""
        SELECT AVG(difficultyRating) as avgDifficulty,
               AVG(confidenceAfter - confidenceBefore) as avgConfidenceGain,
               AVG(timeSpentSeconds) as avgTimeSpent,
               COUNT(*) as totalReviews
        FROM concept_reviews
        WHERE userId = :userId AND topicId = :topicId
    """)
    suspend fun getReviewStats(userId: String, topicId: String): ConceptReviewStats?

    @Query("DELETE FROM concept_reviews WHERE userId = :userId")
    suspend fun deleteAllUserReviews(userId: String)

    @Query("DELETE FROM concept_reviews WHERE id = :reviewId")
    suspend fun deleteReview(reviewId: Long)
}

@Dao
interface UserNoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: UserNote): Long

    @Update
    suspend fun updateNote(note: UserNote)

    @Delete
    suspend fun deleteNote(note: UserNote)

    @Query("SELECT * FROM user_notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: Long): UserNote?

    @Query("SELECT * FROM user_notes WHERE userId = :userId ORDER BY updatedAt DESC")
    suspend fun getNotesByUser(userId: String): List<UserNote>

    @Query("SELECT * FROM user_notes WHERE userId = :userId AND topicId = :topicId ORDER BY updatedAt DESC")
    suspend fun getNotesByTopic(userId: String, topicId: String): List<UserNote>

    @Query("SELECT * FROM user_notes WHERE userId = :userId AND conceptId = :conceptId ORDER BY updatedAt DESC")
    suspend fun getNotesByConcept(userId: String, conceptId: Long): List<UserNote>

    @Query("SELECT * FROM user_notes WHERE userId = :userId AND isPublic = 1 ORDER BY updatedAt DESC")
    suspend fun getPublicNotes(userId: String): List<UserNote>

    @Query("SELECT * FROM user_notes WHERE userId = :userId AND (title LIKE '%' || :searchQuery || '%' OR content LIKE '%' || :searchQuery || '%') ORDER BY updatedAt DESC")
    suspend fun searchNotes(userId: String, searchQuery: String): List<UserNote>

    @Query("UPDATE user_notes SET isPublic = :isPublic, updatedAt = :updatedAt WHERE id = :noteId")
    suspend fun updateNoteVisibility(noteId: Long, isPublic: Boolean, updatedAt: Long)

    @Query("DELETE FROM user_notes WHERE userId = :userId")
    suspend fun deleteAllUserNotes(userId: String)
}

@Dao
interface MaterialPageProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPageProgress(progress: MaterialPageProgress): Long

    @Update
    suspend fun updatePageProgress(progress: MaterialPageProgress)

    @Query("SELECT * FROM material_page_progress WHERE id = :progressId")
    suspend fun getPageProgressById(progressId: Long): MaterialPageProgress?

    @Query("SELECT * FROM material_page_progress WHERE userId = :userId AND materialId = :materialId AND pageNumber = :pageNumber")
    suspend fun getPageProgress(userId: String, materialId: Long, pageNumber: Int): MaterialPageProgress?

    @Query("SELECT * FROM material_page_progress WHERE userId = :userId AND materialId = :materialId ORDER BY pageNumber")
    suspend fun getMaterialProgress(userId: String, materialId: Long): List<MaterialPageProgress>

    @Query("SELECT * FROM material_page_progress WHERE userId = :userId AND materialId = :materialId AND isRead = 1 ORDER BY pageNumber")
    suspend fun getReadPages(userId: String, materialId: Long): List<MaterialPageProgress>

    @Query("SELECT COUNT(*) FROM material_page_progress WHERE userId = :userId AND materialId = :materialId AND isRead = 1")
    suspend fun getReadPagesCount(userId: String, materialId: Long): Int

    @Query("SELECT COUNT(*) FROM material_page_progress WHERE userId = :userId AND materialId = :materialId")
    suspend fun getTotalPagesCount(userId: String, materialId: Long): Int

    @Query("UPDATE material_page_progress SET isRead = :isRead, lastReadAt = :lastReadAt WHERE id = :progressId")
    suspend fun updateReadStatus(progressId: Long, isRead: Boolean, lastReadAt: Long)

    @Query("UPDATE material_page_progress SET readingProgress = :progress, lastReadAt = :lastReadAt WHERE id = :progressId")
    suspend fun updateReadingProgress(progressId: Long, progress: Float, lastReadAt: Long)

    @Query("UPDATE material_page_progress SET timeSpentSeconds = timeSpentSeconds + :seconds, lastReadAt = :lastReadAt WHERE id = :progressId")
    suspend fun addStudyTime(progressId: Long, seconds: Int, lastReadAt: Long)

    @Query("UPDATE material_page_progress SET notes = :notes, lastReadAt = :lastReadAt WHERE id = :progressId")
    suspend fun updateNotes(progressId: Long, notes: String?, lastReadAt: Long)

    @Query("UPDATE material_page_progress SET highlights = :highlights, lastReadAt = :lastReadAt WHERE id = :progressId")
    suspend fun updateHighlights(progressId: Long, highlights: String?, lastReadAt: Long)

    @Query("DELETE FROM material_page_progress WHERE userId = :userId")
    suspend fun deleteAllUserProgress(userId: String)

    @Query("DELETE FROM material_page_progress WHERE materialId = :materialId")
    suspend fun deleteMaterialProgress(materialId: Long)
}