package com.example.hulaba3.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyGroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: StudyGroup)

    @Update
    suspend fun updateGroup(group: StudyGroup)

    @Delete
    suspend fun deleteGroup(group: StudyGroup)

    @Query("SELECT * FROM study_groups WHERE id = :groupId")
    suspend fun getGroupById(groupId: String): StudyGroup?

    @Query("SELECT * FROM study_groups WHERE id = :groupId")
    fun getGroupByIdFlow(groupId: String): Flow<StudyGroup?>

    @Query("SELECT * FROM study_groups WHERE isActive = 1 ORDER BY lastActivityAt DESC")
    suspend fun getActiveGroups(): List<StudyGroup>

    @Query("SELECT * FROM study_groups WHERE isActive = 1 ORDER BY lastActivityAt DESC")
    fun getActiveGroupsFlow(): Flow<List<StudyGroup>>

    @Query("SELECT * FROM study_groups WHERE category = :category AND isActive = 1 ORDER BY lastActivityAt DESC")
    suspend fun getGroupsByCategory(category: String): List<StudyGroup>

    @Query("SELECT * FROM study_groups WHERE languageLevel = :level AND isActive = 1 ORDER BY lastActivityAt DESC")
    suspend fun getGroupsByLanguageLevel(level: String): List<StudyGroup>

    @Query("SELECT * FROM study_groups WHERE isPrivate = 0 AND isActive = 1 ORDER BY lastActivityAt DESC LIMIT :limit")
    suspend fun getPublicGroups(limit: Int = 50): List<StudyGroup>

    @Query("SELECT * FROM study_groups WHERE createdBy = :userId ORDER BY createdAt DESC")
    suspend fun getGroupsCreatedByUser(userId: String): List<StudyGroup>

    @Query("SELECT * FROM study_groups WHERE name LIKE '%' || :searchQuery || '%' AND isActive = 1 ORDER BY lastActivityAt DESC")
    suspend fun searchGroups(searchQuery: String): List<StudyGroup>

    @Query("UPDATE study_groups SET memberCount = memberCount + 1, lastActivityAt = :activityTime WHERE id = :groupId")
    suspend fun incrementMemberCount(groupId: String, activityTime: Long)

    @Query("UPDATE study_groups SET memberCount = memberCount - 1 WHERE id = :groupId")
    suspend fun decrementMemberCount(groupId: String)

    @Query("UPDATE study_groups SET lastActivityAt = :activityTime WHERE id = :groupId")
    suspend fun updateLastActivity(groupId: String, activityTime: Long)

    @Query("DELETE FROM study_groups")
    suspend fun deleteAllGroups()
}

@Dao
interface StudyGroupMemberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: StudyGroupMember): Long

    @Update
    suspend fun updateMember(member: StudyGroupMember)

    @Delete
    suspend fun deleteMember(member: StudyGroupMember)

    @Query("SELECT * FROM study_group_members WHERE id = :memberId")
    suspend fun getMemberById(memberId: Long): StudyGroupMember?

    @Query("SELECT * FROM study_group_members WHERE groupId = :groupId AND userId = :userId")
    suspend fun getMember(groupId: String, userId: String): StudyGroupMember?

    @Query("SELECT * FROM study_group_members WHERE groupId = :groupId AND isActive = 1 ORDER BY joinedAt DESC")
    suspend fun getActiveMembers(groupId: String): List<StudyGroupMember>

    @Query("SELECT * FROM study_group_members WHERE groupId = :groupId AND isActive = 1 ORDER BY joinedAt DESC")
    fun getActiveMembersFlow(groupId: String): Flow<List<StudyGroupMember>>

    @Query("SELECT * FROM study_group_members WHERE userId = :userId AND isActive = 1 ORDER BY joinedAt DESC")
    suspend fun getUserActiveMemberships(userId: String): List<StudyGroupMember>

    @Query("SELECT * FROM study_group_members WHERE userId = :userId AND role = 'admin' AND isActive = 1")
    suspend fun getUserAdminGroups(userId: String): List<StudyGroupMember>

    @Query("UPDATE study_group_members SET role = :role WHERE id = :memberId")
    suspend fun updateMemberRole(memberId: Long, role: String)

    @Query("UPDATE study_group_members SET isActive = 0 WHERE id = :memberId")
    suspend fun deactivateMember(memberId: Long)

    @Query("UPDATE study_group_members SET contributionsCount = contributionsCount + 1 WHERE id = :memberId")
    suspend fun incrementContributions(memberId: Long)

    @Query("UPDATE study_group_members SET reputationPoints = reputationPoints + :points WHERE id = :memberId")
    suspend fun addReputationPoints(memberId: Long, points: Int)

    @Query("SELECT COUNT(*) FROM study_group_members WHERE groupId = :groupId AND isActive = 1")
    suspend fun getActiveMemberCount(groupId: String): Int

    @Query("DELETE FROM study_group_members WHERE groupId = :groupId")
    suspend fun deleteAllMembersByGroup(groupId: String)

    @Query("DELETE FROM study_group_members")
    suspend fun deleteAllMembers()
}

@Dao
interface GroupDiscussionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiscussion(discussion: GroupDiscussion)

    @Update
    suspend fun updateDiscussion(discussion: GroupDiscussion)

    @Delete
    suspend fun deleteDiscussion(discussion: GroupDiscussion)

    @Query("SELECT * FROM group_discussions WHERE id = :discussionId")
    suspend fun getDiscussionById(discussionId: String): GroupDiscussion?

    @Query("SELECT * FROM group_discussions WHERE id = :discussionId")
    fun getDiscussionByIdFlow(discussionId: String): Flow<GroupDiscussion?>

    @Query("SELECT * FROM group_discussions WHERE groupId = :groupId ORDER BY isPinned DESC, (lastReplyAt IS NULL) ASC, lastReplyAt DESC, createdAt DESC")
    suspend fun getDiscussionsByGroup(groupId: String): List<GroupDiscussion>

    @Query("SELECT * FROM group_discussions WHERE groupId = :groupId ORDER BY isPinned DESC, (lastReplyAt IS NULL) ASC, lastReplyAt DESC, createdAt DESC")
    fun getDiscussionsByGroupFlow(groupId: String): Flow<List<GroupDiscussion>>

    @Query("SELECT * FROM group_discussions WHERE authorId = :userId ORDER BY createdAt DESC")
    suspend fun getDiscussionsByUser(userId: String): List<GroupDiscussion>

    @Query("SELECT * FROM group_discussions WHERE discussionType = :type AND groupId = :groupId ORDER BY createdAt DESC")
    suspend fun getDiscussionsByType(groupId: String, type: String): List<GroupDiscussion>

    @Query("SELECT * FROM group_discussions WHERE isPinned = 1 AND groupId = :groupId ORDER BY createdAt DESC")
    suspend fun getPinnedDiscussions(groupId: String): List<GroupDiscussion>

    @Query("SELECT * FROM group_discussions WHERE title LIKE '%' || :searchQuery || '%' OR content LIKE '%' || :searchQuery || '%' ORDER BY createdAt DESC")
    suspend fun searchDiscussions(searchQuery: String): List<GroupDiscussion>

    @Query("UPDATE group_discussions SET replyCount = replyCount + 1, lastReplyAt = :replyTime WHERE id = :discussionId")
    suspend fun incrementReplyCount(discussionId: String, replyTime: Long)

    @Query("UPDATE group_discussions SET likeCount = likeCount + 1 WHERE id = :discussionId")
    suspend fun incrementLikeCount(discussionId: String)

    @Query("UPDATE group_discussions SET likeCount = likeCount - 1 WHERE id = :discussionId AND likeCount > 0")
    suspend fun decrementLikeCount(discussionId: String)

    @Query("UPDATE group_discussions SET isPinned = :isPinned WHERE id = :discussionId")
    suspend fun updatePinnedStatus(discussionId: String, isPinned: Boolean)

    @Query("UPDATE group_discussions SET isLocked = :isLocked WHERE id = :discussionId")
    suspend fun updateLockedStatus(discussionId: String, isLocked: Boolean)

    @Query("DELETE FROM group_discussions WHERE groupId = :groupId")
    suspend fun deleteAllDiscussionsByGroup(groupId: String)

    @Query("DELETE FROM group_discussions")
    suspend fun deleteAllDiscussions()
}

@Dao
interface DiscussionReplyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReply(reply: DiscussionReply)

    @Update
    suspend fun updateReply(reply: DiscussionReply)

    @Delete
    suspend fun deleteReply(reply: DiscussionReply)

    @Query("SELECT * FROM discussion_replies WHERE id = :replyId")
    suspend fun getReplyById(replyId: String): DiscussionReply?

    @Query("SELECT * FROM discussion_replies WHERE discussionId = :discussionId AND parentReplyId IS NULL ORDER BY createdAt ASC")
    suspend fun getTopLevelReplies(discussionId: String): List<DiscussionReply>

    @Query("SELECT * FROM discussion_replies WHERE discussionId = :discussionId AND parentReplyId IS NULL ORDER BY createdAt ASC")
    fun getTopLevelRepliesFlow(discussionId: String): Flow<List<DiscussionReply>>

    @Query("SELECT * FROM discussion_replies WHERE parentReplyId = :parentReplyId ORDER BY createdAt ASC")
    suspend fun getChildReplies(parentReplyId: String): List<DiscussionReply>

    @Query("SELECT * FROM discussion_replies WHERE authorId = :userId ORDER BY createdAt DESC")
    suspend fun getRepliesByUser(userId: String): List<DiscussionReply>

    @Query("SELECT * FROM discussion_replies WHERE discussionId = :discussionId ORDER BY createdAt ASC")
    suspend fun getAllRepliesByDiscussion(discussionId: String): List<DiscussionReply>

    @Query("SELECT * FROM discussion_replies WHERE isAcceptedAnswer = 1 AND discussionId = :discussionId")
    suspend fun getAcceptedAnswer(discussionId: String): DiscussionReply?

    @Query("UPDATE discussion_replies SET likeCount = likeCount + 1 WHERE id = :replyId")
    suspend fun incrementLikeCount(replyId: String)

    @Query("UPDATE discussion_replies SET likeCount = likeCount - 1 WHERE id = :replyId AND likeCount > 0")
    suspend fun decrementLikeCount(replyId: String)

    @Query("UPDATE discussion_replies SET isAcceptedAnswer = 0 WHERE discussionId = :discussionId")
    suspend fun clearAcceptedAnswers(discussionId: String)

    @Query("UPDATE discussion_replies SET isAcceptedAnswer = 1 WHERE id = :replyId")
    suspend fun markAsAcceptedAnswer(replyId: String)

    @Query("DELETE FROM discussion_replies WHERE discussionId = :discussionId")
    suspend fun deleteAllRepliesByDiscussion(discussionId: String)

    @Query("DELETE FROM discussion_replies")
    suspend fun deleteAllReplies()
}

@Dao
interface SharedCollectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: SharedCollection)

    @Update
    suspend fun updateCollection(collection: SharedCollection)

    @Delete
    suspend fun deleteCollection(collection: SharedCollection)

    @Query("SELECT * FROM shared_collections WHERE id = :collectionId")
    suspend fun getCollectionById(collectionId: String): SharedCollection?

    @Query("SELECT * FROM shared_collections WHERE id = :collectionId")
    fun getCollectionByIdFlow(collectionId: String): Flow<SharedCollection?>

    @Query("SELECT * FROM shared_collections WHERE isPublic = 1 ORDER BY downloadCount DESC, createdAt DESC")
    suspend fun getPublicCollections(): List<SharedCollection>

    @Query("SELECT * FROM shared_collections WHERE isPublic = 1 ORDER BY downloadCount DESC, createdAt DESC")
    fun getPublicCollectionsFlow(): Flow<List<SharedCollection>>

    @Query("SELECT * FROM shared_collections WHERE createdBy = :userId ORDER BY createdAt DESC")
    suspend fun getCollectionsByUser(userId: String): List<SharedCollection>

    @Query("SELECT * FROM shared_collections WHERE collectionType = :type AND isPublic = 1 ORDER BY downloadCount DESC")
    suspend fun getCollectionsByType(type: String): List<SharedCollection>

    @Query("SELECT * FROM shared_collections WHERE languageLevel = :level AND isPublic = 1 ORDER BY downloadCount DESC")
    suspend fun getCollectionsByLanguageLevel(level: String): List<SharedCollection>

    @Query("SELECT * FROM shared_collections WHERE title LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%' AND isPublic = 1 ORDER BY downloadCount DESC")
    suspend fun searchPublicCollections(searchQuery: String): List<SharedCollection>

    @Query("SELECT * FROM shared_collections WHERE tags LIKE '%' || :tag || '%' AND isPublic = 1 ORDER BY downloadCount DESC")
    suspend fun getCollectionsByTag(tag: String): List<SharedCollection>

    @Query("SELECT * FROM shared_collections ORDER BY ratingAverage DESC, ratingCount DESC LIMIT :limit")
    suspend fun getTopRatedCollections(limit: Int = 20): List<SharedCollection>

    @Query("UPDATE shared_collections SET downloadCount = downloadCount + 1 WHERE id = :collectionId")
    suspend fun incrementDownloadCount(collectionId: String)

    @Query("UPDATE shared_collections SET viewCount = viewCount + 1 WHERE id = :collectionId")
    suspend fun incrementViewCount(collectionId: String)

    @Query("UPDATE shared_collections SET likeCount = likeCount + 1 WHERE id = :collectionId")
    suspend fun incrementLikeCount(collectionId: String)

    @Query("UPDATE shared_collections SET ratingAverage = :newRating, ratingCount = ratingCount + 1 WHERE id = :collectionId")
    suspend fun updateRating(collectionId: String, newRating: Float)

    @Query("UPDATE shared_collections SET itemCount = :itemCount WHERE id = :collectionId")
    suspend fun updateItemCount(collectionId: String, itemCount: Int)

    @Query("DELETE FROM shared_collections")
    suspend fun deleteAllCollections()
}

@Dao
interface CollectionItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: CollectionItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<CollectionItem>)

    @Update
    suspend fun updateItem(item: CollectionItem)

    @Delete
    suspend fun deleteItem(item: CollectionItem)

    @Query("SELECT * FROM collection_items WHERE id = :itemId")
    suspend fun getItemById(itemId: Long): CollectionItem?

    @Query("SELECT * FROM collection_items WHERE collectionId = :collectionId ORDER BY orderIndex")
    suspend fun getItemsByCollection(collectionId: String): List<CollectionItem>

    @Query("SELECT * FROM collection_items WHERE collectionId = :collectionId ORDER BY orderIndex")
    fun getItemsByCollectionFlow(collectionId: String): Flow<List<CollectionItem>>

    @Query("SELECT * FROM collection_items WHERE vocabularyId = :vocabularyId")
    suspend fun getItemsByVocabulary(vocabularyId: Long): List<CollectionItem>

    @Query("SELECT * FROM collection_items WHERE conceptId = :conceptId")
    suspend fun getItemsByConcept(conceptId: Long): List<CollectionItem>

    @Query("SELECT * FROM collection_items WHERE topicId = :topicId")
    suspend fun getItemsByTopic(topicId: String): List<CollectionItem>

    @Query("SELECT COUNT(*) FROM collection_items WHERE collectionId = :collectionId")
    suspend fun getItemCount(collectionId: String): Int

    @Query("UPDATE collection_items SET orderIndex = :orderIndex WHERE id = :itemId")
    suspend fun updateItemOrder(itemId: Long, orderIndex: Int)

    @Query("DELETE FROM collection_items WHERE collectionId = :collectionId")
    suspend fun deleteItemsByCollection(collectionId: String)

    @Query("DELETE FROM collection_items")
    suspend fun deleteAllItems()
}

@Dao
interface CollectionDownloadDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: CollectionDownload): Long

    @Update
    suspend fun updateDownload(download: CollectionDownload)

    @Query("SELECT * FROM collection_downloads WHERE id = :downloadId")
    suspend fun getDownloadById(downloadId: Long): CollectionDownload?

    @Query("SELECT * FROM collection_downloads WHERE collectionId = :collectionId AND userId = :userId")
    suspend fun getDownload(collectionId: String, userId: String): CollectionDownload?

    @Query("SELECT * FROM collection_downloads WHERE userId = :userId AND isActive = 1 ORDER BY lastUsedAt DESC")
    suspend fun getActiveDownloads(userId: String): List<CollectionDownload>

    @Query("SELECT * FROM collection_downloads WHERE userId = :userId ORDER BY downloadedAt DESC")
    suspend fun getAllDownloads(userId: String): List<CollectionDownload>

    @Query("SELECT * FROM collection_downloads WHERE collectionId = :collectionId ORDER BY downloadedAt DESC")
    suspend fun getDownloadsByCollection(collectionId: String): List<CollectionDownload>

    @Query("UPDATE collection_downloads SET lastUsedAt = :lastUsedAt, usageCount = usageCount + 1 WHERE id = :downloadId")
    suspend fun updateLastUsed(downloadId: Long, lastUsedAt: Long)

    @Query("UPDATE collection_downloads SET isActive = 0 WHERE id = :downloadId")
    suspend fun deactivateDownload(downloadId: Long)

    @Query("UPDATE collection_downloads SET userRating = :rating, userReview = :review WHERE id = :downloadId")
    suspend fun updateUserRating(downloadId: Long, rating: Int?, review: String?)

    @Query("SELECT COUNT(*) FROM collection_downloads WHERE collectionId = :collectionId")
    suspend fun getDownloadCount(collectionId: String): Int

    @Query("DELETE FROM collection_downloads WHERE userId = :userId")
    suspend fun deleteAllDownloadsByUser(userId: String)

    @Query("DELETE FROM collection_downloads")
    suspend fun deleteAllDownloads()
}

@Dao
interface ChallengeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(challenge: Challenge)

    @Update
    suspend fun updateChallenge(challenge: Challenge)

    @Delete
    suspend fun deleteChallenge(challenge: Challenge)

    @Query("SELECT * FROM challenges WHERE id = :challengeId")
    suspend fun getChallengeById(challengeId: String): Challenge?

    @Query("SELECT * FROM challenges WHERE id = :challengeId")
    fun getChallengeByIdFlow(challengeId: String): Flow<Challenge?>

    @Query("SELECT * FROM challenges WHERE isActive = 1 AND startDate <= :currentTime AND endDate >= :currentTime ORDER BY startDate ASC")
    suspend fun getActiveChallenges(currentTime: Long): List<Challenge>

    @Query("SELECT * FROM challenges WHERE isActive = 1 AND startDate <= :currentTime AND endDate >= :currentTime ORDER BY startDate ASC")
    fun getActiveChallengesFlow(currentTime: Long): Flow<List<Challenge>>

    @Query("SELECT * FROM challenges WHERE isPublic = 1 ORDER BY startDate DESC")
    suspend fun getPublicChallenges(): List<Challenge>

    @Query("SELECT * FROM challenges WHERE challengeType = :type AND isActive = 1 ORDER BY startDate DESC")
    suspend fun getChallengesByType(type: String): List<Challenge>

    @Query("SELECT * FROM challenges WHERE difficulty = :difficulty AND isActive = 1 ORDER BY startDate DESC")
    suspend fun getChallengesByDifficulty(difficulty: String): List<Challenge>

    @Query("SELECT * FROM challenges WHERE createdBy = :userId ORDER BY createdAt DESC")
    suspend fun getChallengesCreatedByUser(userId: String): List<Challenge>

    @Query("SELECT * FROM challenges WHERE title LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%' ORDER BY startDate DESC")
    suspend fun searchChallenges(searchQuery: String): List<Challenge>

    @Query("UPDATE challenges SET participantCount = participantCount + 1 WHERE id = :challengeId")
    suspend fun incrementParticipantCount(challengeId: String)

    @Query("UPDATE challenges SET participantCount = participantCount - 1 WHERE id = :challengeId AND participantCount > 0")
    suspend fun decrementParticipantCount(challengeId: String)

    @Query("UPDATE challenges SET completionRate = :completionRate WHERE id = :challengeId")
    suspend fun updateCompletionRate(challengeId: String, completionRate: Float)

    @Query("DELETE FROM challenges")
    suspend fun deleteAllChallenges()
}

@Dao
interface ChallengeParticipantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipant(participant: ChallengeParticipant): Long

    @Update
    suspend fun updateParticipant(participant: ChallengeParticipant)

    @Query("SELECT * FROM challenge_participants WHERE id = :participantId")
    suspend fun getParticipantById(participantId: Long): ChallengeParticipant?

    @Query("SELECT * FROM challenge_participants WHERE challengeId = :challengeId AND userId = :userId")
    suspend fun getParticipant(challengeId: String, userId: String): ChallengeParticipant?

    @Query("SELECT * FROM challenge_participants WHERE challengeId = :challengeId ORDER BY score DESC, progress DESC")
    suspend fun getParticipantsByChallenge(challengeId: String): List<ChallengeParticipant>

    @Query("SELECT * FROM challenge_participants WHERE challengeId = :challengeId ORDER BY score DESC, progress DESC")
    fun getParticipantsByChallengeFlow(challengeId: String): Flow<List<ChallengeParticipant>>

    @Query("SELECT * FROM challenge_participants WHERE userId = :userId ORDER BY joinedAt DESC")
    suspend fun getUserParticipations(userId: String): List<ChallengeParticipant>

    @Query("SELECT * FROM challenge_participants WHERE userId = :userId AND status = 'active' ORDER BY joinedAt DESC")
    suspend fun getUserActiveParticipations(userId: String): List<ChallengeParticipant>

    @Query("SELECT * FROM challenge_participants WHERE userId = :userId AND status = 'completed' ORDER BY completedAt DESC")
    suspend fun getUserCompletedParticipations(userId: String): List<ChallengeParticipant>

    @Query("SELECT * FROM challenge_participants WHERE challengeId = :challengeId AND status = 'completed' ORDER BY completedAt ASC")
    suspend fun getCompletedParticipants(challengeId: String): List<ChallengeParticipant>

    @Query("UPDATE challenge_participants SET status = :status, lastActivityAt = :updatedAt WHERE id = :participantId")
    suspend fun updateParticipantStatus(participantId: Long, status: String, updatedAt: Long)

    @Query("UPDATE challenge_participants SET progress = :progress, lastActivityAt = :updatedAt WHERE id = :participantId")
    suspend fun updateProgress(participantId: Long, progress: Float, updatedAt: Long)

    @Query("UPDATE challenge_participants SET score = :score, lastActivityAt = :updatedAt WHERE id = :participantId")
    suspend fun updateScore(participantId: Long, score: Int, updatedAt: Long)

    @Query("UPDATE challenge_participants SET streakDays = :streakDays, lastActivityAt = :updatedAt WHERE id = :participantId")
    suspend fun updateStreakDays(participantId: Long, streakDays: Int, updatedAt: Long)

    @Query("UPDATE challenge_participants SET currentDay = :currentDay, lastActivityAt = :updatedAt WHERE id = :participantId")
    suspend fun updateCurrentDay(participantId: Long, currentDay: Int, updatedAt: Long)

    @Query("UPDATE challenge_participants SET rank = :rank, lastActivityAt = :updatedAt WHERE id = :participantId")
    suspend fun updateRank(participantId: Long, rank: Int, updatedAt: Long)

    @Query("UPDATE challenge_participants SET completedAt = :completedAt, status = 'completed', lastActivityAt = :updatedAt WHERE id = :participantId")
    suspend fun markAsCompleted(participantId: Long, completedAt: Long, updatedAt: Long)

    @Query("UPDATE challenge_participants SET droppedAt = :droppedAt, status = 'dropped', lastActivityAt = :updatedAt WHERE id = :participantId")
    suspend fun markAsDropped(participantId: Long, droppedAt: Long, updatedAt: Long)

    @Query("SELECT COUNT(*) FROM challenge_participants WHERE challengeId = :challengeId")
    suspend fun getParticipantCount(challengeId: String): Int

    @Query("SELECT COUNT(*) FROM challenge_participants WHERE challengeId = :challengeId AND status = 'completed'")
    suspend fun getCompletedCount(challengeId: String): Int

    @Query("DELETE FROM challenge_participants WHERE challengeId = :challengeId")
    suspend fun deleteAllParticipantsByChallenge(challengeId: String)

    @Query("DELETE FROM challenge_participants")
    suspend fun deleteAllParticipants()
}

@Dao
interface NativeSpeakerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNativeSpeaker(speaker: NativeSpeaker)

    @Update
    suspend fun updateNativeSpeaker(speaker: NativeSpeaker)

    @Query("SELECT * FROM native_speakers WHERE id = :speakerId")
    suspend fun getNativeSpeakerById(speakerId: String): NativeSpeaker?

    @Query("SELECT * FROM native_speakers WHERE userId = :userId")
    suspend fun getNativeSpeakerByUserId(userId: String): NativeSpeaker?

    @Query("SELECT * FROM native_speakers WHERE isAvailable = 1 ORDER BY ratingAverage DESC, sessionCount DESC")
    suspend fun getAvailableNativeSpeakers(): List<NativeSpeaker>

    @Query("SELECT * FROM native_speakers WHERE isAvailable = 1 ORDER BY ratingAverage DESC, sessionCount DESC")
    fun getAvailableNativeSpeakersFlow(): Flow<List<NativeSpeaker>>

    @Query("SELECT * FROM native_speakers WHERE language = :language AND isAvailable = 1 ORDER BY ratingAverage DESC, sessionCount DESC")
    suspend fun getNativeSpeakersByLanguage(language: String): List<NativeSpeaker>

    @Query("SELECT * FROM native_speakers WHERE dialect = :dialect AND isAvailable = 1 ORDER BY ratingAverage DESC, sessionCount DESC")
    suspend fun getNativeSpeakersByDialect(dialect: String): List<NativeSpeaker>

    @Query("SELECT * FROM native_speakers WHERE specialties LIKE '%' || :specialty || '%' AND isAvailable = 1 ORDER BY ratingAverage DESC, sessionCount DESC")
    suspend fun getNativeSpeakersBySpecialty(specialty: String): List<NativeSpeaker>

    @Query("SELECT * FROM native_speakers WHERE hourlyRate IS NULL AND isAvailable = 1 ORDER BY ratingAverage DESC, sessionCount DESC")
    suspend fun getFreeNativeSpeakers(): List<NativeSpeaker>

    @Query("SELECT * FROM native_speakers WHERE hourlyRate IS NOT NULL AND isAvailable = 1 ORDER BY hourlyRate ASC, ratingAverage DESC")
    suspend fun getPaidNativeSpeakers(): List<NativeSpeaker>

    @Query("SELECT * FROM native_speakers WHERE ratingAverage >= :minRating AND isAvailable = 1 ORDER BY ratingAverage DESC, sessionCount DESC")
    suspend fun getHighlyRatedNativeSpeakers(minRating: Float = 4.0f): List<NativeSpeaker>

    @Query("UPDATE native_speakers SET isAvailable = :isAvailable WHERE id = :speakerId")
    suspend fun updateAvailability(speakerId: String, isAvailable: Boolean)

    @Query("UPDATE native_speakers SET ratingAverage = :newRating, ratingCount = ratingCount + 1 WHERE id = :speakerId")
    suspend fun updateRating(speakerId: String, newRating: Float)

    @Query("UPDATE native_speakers SET sessionCount = sessionCount + 1 WHERE id = :speakerId")
    suspend fun incrementSessionCount(speakerId: String)

    @Query("UPDATE native_speakers SET responseTimeHours = :responseTime WHERE id = :speakerId")
    suspend fun updateResponseTime(speakerId: String, responseTime: Float)

    @Query("DELETE FROM native_speakers WHERE userId = :userId")
    suspend fun deleteNativeSpeakerByUserId(userId: String)

    @Query("DELETE FROM native_speakers")
    suspend fun deleteAllNativeSpeakers()
}

@Dao
interface LanguageExchangeSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: LanguageExchangeSession)

    @Update
    suspend fun updateSession(session: LanguageExchangeSession)

    @Query("SELECT * FROM language_exchange_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: String): LanguageExchangeSession?

    @Query("SELECT * FROM language_exchange_sessions WHERE id = :sessionId")
    fun getSessionByIdFlow(sessionId: String): Flow<LanguageExchangeSession?>

    @Query("SELECT * FROM language_exchange_sessions WHERE learnerId = :userId ORDER BY (scheduledFor IS NULL) ASC, scheduledFor DESC, createdAt DESC")
    suspend fun getLearnerSessions(userId: String): List<LanguageExchangeSession>

    @Query("SELECT * FROM language_exchange_sessions WHERE nativeSpeakerId = :userId ORDER BY (scheduledFor IS NULL) ASC, scheduledFor DESC, createdAt DESC")
    suspend fun getNativeSpeakerSessions(userId: String): List<LanguageExchangeSession>

    @Query("SELECT * FROM language_exchange_sessions WHERE (learnerId = :userId OR nativeSpeakerId = :userId) ORDER BY (scheduledFor IS NULL) ASC, scheduledFor DESC, createdAt DESC")
    suspend fun getUserSessions(userId: String): List<LanguageExchangeSession>

    @Query("SELECT * FROM language_exchange_sessions WHERE learnerId = :userId AND status = 'scheduled' ORDER BY scheduledFor ASC")
    suspend fun getScheduledLearnerSessions(userId: String): List<LanguageExchangeSession>

    @Query("SELECT * FROM language_exchange_sessions WHERE nativeSpeakerId = :userId AND status = 'scheduled' ORDER BY scheduledFor ASC")
    suspend fun getScheduledNativeSpeakerSessions(userId: String): List<LanguageExchangeSession>

    @Query("SELECT * FROM language_exchange_sessions WHERE status = 'scheduled' AND scheduledFor IS NOT NULL AND scheduledFor > :currentTime AND scheduledFor <= :maxTime ORDER BY scheduledFor ASC")
    suspend fun getUpcomingSessions(currentTime: Long, maxTime: Long): List<LanguageExchangeSession>

    @Query("SELECT * FROM language_exchange_sessions WHERE status = :status ORDER BY createdAt DESC")
    suspend fun getSessionsByStatus(status: String): List<LanguageExchangeSession>

    @Query("UPDATE language_exchange_sessions SET status = :status WHERE id = :sessionId")
    suspend fun updateSessionStatus(sessionId: String, status: String)

    @Query("UPDATE language_exchange_sessions SET notes = :notes WHERE id = :sessionId")
    suspend fun updateSessionNotes(sessionId: String, notes: String?)

    @Query("UPDATE language_exchange_sessions SET feedbackGiven = 1 WHERE id = :sessionId")
    suspend fun markFeedbackGiven(sessionId: String)

    @Query("UPDATE language_exchange_sessions SET ratingGiven = 1 WHERE id = :sessionId")
    suspend fun markRatingGiven(sessionId: String)

    @Query("UPDATE language_exchange_sessions SET startedAt = :startedAt, status = 'in_progress' WHERE id = :sessionId")
    suspend fun markSessionStarted(sessionId: String, startedAt: Long)

    @Query("UPDATE language_exchange_sessions SET completedAt = :completedAt, status = 'completed' WHERE id = :sessionId")
    suspend fun markSessionCompleted(sessionId: String, completedAt: Long)

    @Query("DELETE FROM language_exchange_sessions WHERE learnerId = :userId OR nativeSpeakerId = :userId")
    suspend fun deleteAllUserSessions(userId: String)

    @Query("DELETE FROM language_exchange_sessions")
    suspend fun deleteAllSessions()
}

@Dao
interface UserConnectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConnection(connection: UserConnection): Long

    @Update
    suspend fun updateConnection(connection: UserConnection)

    @Delete
    suspend fun deleteConnection(connection: UserConnection)

    @Query("SELECT * FROM user_connections WHERE id = :connectionId")
    suspend fun getConnectionById(connectionId: Long): UserConnection?

    @Query("SELECT * FROM user_connections WHERE userId = :userId AND connectedUserId = :connectedUserId")
    suspend fun getConnection(userId: String, connectedUserId: String): UserConnection?

    @Query("SELECT * FROM user_connections WHERE userId = :userId AND status = 'accepted' ORDER BY (lastInteractionAt IS NULL) ASC, lastInteractionAt DESC, createdAt DESC")
    suspend fun getAcceptedConnections(userId: String): List<UserConnection>

    @Query("SELECT * FROM user_connections WHERE userId = :userId AND status = 'accepted' ORDER BY (lastInteractionAt IS NULL) ASC, lastInteractionAt DESC, createdAt DESC")
    fun getAcceptedConnectionsFlow(userId: String): Flow<List<UserConnection>>

    @Query("SELECT * FROM user_connections WHERE userId = :userId AND status = 'pending' ORDER BY createdAt DESC")
    suspend fun getPendingConnections(userId: String): List<UserConnection>

    @Query("SELECT * FROM user_connections WHERE userId = :userId AND isFavorite = 1 ORDER BY (lastInteractionAt IS NULL) ASC, lastInteractionAt DESC, createdAt DESC")
    suspend fun getFavoriteConnections(userId: String): List<UserConnection>

    @Query("SELECT * FROM user_connections WHERE userId = :userId AND connectionType = :type AND status = 'accepted' ORDER BY (lastInteractionAt IS NULL) ASC, lastInteractionAt DESC, createdAt DESC")
    suspend fun getConnectionsByType(userId: String, type: String): List<UserConnection>

    @Query("SELECT * FROM user_connections WHERE (userId = :userId OR connectedUserId = :userId) AND status = 'accepted' ORDER BY (lastInteractionAt IS NULL) ASC, lastInteractionAt DESC, createdAt DESC")
    suspend fun getAllUserConnections(userId: String): List<UserConnection>

    @Query("UPDATE user_connections SET status = :status WHERE id = :connectionId")
    suspend fun updateConnectionStatus(connectionId: Long, status: String)

    @Query("UPDATE user_connections SET isFavorite = :isFavorite WHERE id = :connectionId")
    suspend fun updateFavoriteStatus(connectionId: Long, isFavorite: Boolean)

    @Query("UPDATE user_connections SET lastInteractionAt = :interactionTime WHERE id = :connectionId")
    suspend fun updateLastInteraction(connectionId: Long, interactionTime: Long)

    @Query("UPDATE user_connections SET sharedCollectionsCount = sharedCollectionsCount + 1 WHERE id = :connectionId")
    suspend fun incrementSharedCollectionsCount(connectionId: Long)

    @Query("UPDATE user_connections SET jointStudySessionsCount = jointStudySessionsCount + 1 WHERE id = :connectionId")
    suspend fun incrementJointStudySessionsCount(connectionId: Long)

    @Query("SELECT COUNT(*) FROM user_connections WHERE userId = :userId AND status = 'accepted'")
    suspend fun getConnectionCount(userId: String): Int

    @Query("DELETE FROM user_connections WHERE userId = :userId OR connectedUserId = :userId")
    suspend fun deleteAllUserConnections(userId: String)

    @Query("DELETE FROM user_connections")
    suspend fun deleteAllConnections()
}

@Dao
interface UserReputationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReputation(reputation: UserReputation)

    @Update
    suspend fun updateReputation(reputation: UserReputation)

    @Query("SELECT * FROM user_reputation WHERE id = :reputationId")
    suspend fun getReputationById(reputationId: String): UserReputation?

    @Query("SELECT * FROM user_reputation WHERE userId = :userId")
    suspend fun getReputationByUserId(userId: String): UserReputation?

    @Query("SELECT * FROM user_reputation WHERE userId = :userId")
    fun getReputationByUserIdFlow(userId: String): Flow<UserReputation?>

    @Query("UPDATE user_reputation SET totalPoints = totalPoints + :points WHERE id = :reputationId")
    suspend fun addPoints(reputationId: String, points: Int)

    @Query("UPDATE user_reputation SET helpfulAnswersCount = helpfulAnswersCount + 1 WHERE id = :reputationId")
    suspend fun incrementHelpfulAnswers(reputationId: String)

    @Query("UPDATE user_reputation SET sharedCollectionsCount = sharedCollectionsCount + 1 WHERE id = :reputationId")
    suspend fun incrementSharedCollections(reputationId: String)

    @Query("UPDATE user_reputation SET collectionDownloadsCount = collectionDownloadsCount + 1 WHERE id = :reputationId")
    suspend fun incrementCollectionDownloads(reputationId: String)

    @Query("UPDATE user_reputation SET studyGroupContributionsCount = studyGroupContributionsCount + 1 WHERE id = :reputationId")
    suspend fun incrementStudyGroupContributions(reputationId: String)

    @Query("UPDATE user_reputation SET nativeSpeakerSessionsCount = nativeSpeakerSessionsCount + 1 WHERE id = :reputationId")
    suspend fun incrementNativeSpeakerSessions(reputationId: String)

    @Query("UPDATE user_reputation SET challengeWinsCount = challengeWinsCount + 1 WHERE id = :reputationId")
    suspend fun incrementChallengeWins(reputationId: String)

    @Query("UPDATE user_reputation SET ratingAverage = :newRating, ratingCount = ratingCount + 1 WHERE id = :reputationId")
    suspend fun updateRating(reputationId: String, newRating: Float)

    @Query("UPDATE user_reputation SET level = :level WHERE id = :reputationId")
    suspend fun updateLevel(reputationId: String, level: String)

    @Query("DELETE FROM user_reputation WHERE userId = :userId")
    suspend fun deleteReputationByUserId(userId: String)

    @Query("DELETE FROM user_reputation")
    suspend fun deleteAllReputations()
}