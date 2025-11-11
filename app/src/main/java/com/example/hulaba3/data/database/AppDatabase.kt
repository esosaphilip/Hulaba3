import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.hulaba3.data.database.Answer
import com.example.hulaba3.data.database.AnswerDao
import com.example.hulaba3.data.database.Question
import com.example.hulaba3.data.database.QuestionAttempt
import com.example.hulaba3.data.database.QuestionAttemptDao
import com.example.hulaba3.data.database.QuestionDao
import com.example.hulaba3.data.database.StudySession
import com.example.hulaba3.data.database.StudySessionDao
import com.example.hulaba3.data.database.Topic
import com.example.hulaba3.data.database.TopicDao
import com.example.hulaba3.data.database.Word
import com.example.hulaba3.data.database.WordDao
import com.example.hulaba3.utils.UriTypeConverter

@Database(
    entities = [
        Word::class,
        Topic::class,
        Question::class,
        Answer::class,
        StudySession::class,
        QuestionAttempt::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(UriTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun topicDao(): TopicDao
    abstract fun questionDao(): QuestionDao
    abstract fun answerDao(): AnswerDao
    abstract fun studySessionDao(): StudySessionDao
    abstract fun questionAttemptDao(): QuestionAttemptDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migration from version 3 to 4 (adds new quiz tables)
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create questions table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `questions` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `topicId` TEXT NOT NULL,
                        `questionText` TEXT NOT NULL,
                        `difficulty` INTEGER NOT NULL DEFAULT 1,
                        `createdAt` INTEGER NOT NULL,
                        `lastReviewed` INTEGER,
                        `reviewCount` INTEGER NOT NULL DEFAULT 0,
                        `nextReviewTime` INTEGER NOT NULL DEFAULT 0,
                        `easeFactor` REAL NOT NULL DEFAULT 2.5,
                        `correctStreak` INTEGER NOT NULL DEFAULT 0,
                        `incorrectCount` INTEGER NOT NULL DEFAULT 0,
                        FOREIGN KEY(`topicId`) REFERENCES `topics`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """)

                // Create answers table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `answers` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `questionId` INTEGER NOT NULL,
                        `answerText` TEXT NOT NULL,
                        `isCorrect` INTEGER NOT NULL,
                        `orderIndex` INTEGER NOT NULL,
                        FOREIGN KEY(`questionId`) REFERENCES `questions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """)

                // Create study_sessions table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `study_sessions` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `topicId` TEXT NOT NULL,
                        `startTime` INTEGER NOT NULL,
                        `endTime` INTEGER,
                        `totalQuestions` INTEGER NOT NULL DEFAULT 0,
                        `correctAnswers` INTEGER NOT NULL DEFAULT 0,
                        `completionPercentage` REAL NOT NULL DEFAULT 0.0,
                        `averageResponseTime` INTEGER NOT NULL DEFAULT 0,
                        `sessionType` TEXT NOT NULL DEFAULT 'QUIZ'
                    )
                """)

                // Create question_attempts table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `question_attempts` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `sessionId` INTEGER NOT NULL,
                        `questionId` INTEGER NOT NULL,
                        `selectedAnswerId` INTEGER,
                        `isCorrect` INTEGER NOT NULL DEFAULT 0,
                        `responseTime` INTEGER NOT NULL DEFAULT 0,
                        `attemptedAt` INTEGER NOT NULL,
                        FOREIGN KEY(`sessionId`) REFERENCES `study_sessions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                        FOREIGN KEY(`questionId`) REFERENCES `questions`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """)

                // Create indices for better performance
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_questions_topicId` ON `questions` (`topicId`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_answers_questionId` ON `answers` (`questionId`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_question_attempts_sessionId` ON `question_attempts` (`sessionId`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_question_attempts_questionId` ON `question_attempts` (`questionId`)")
            }
        }

        // Migration from version 2 to 3 (existing)
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE topics ADD COLUMN lastReviewed INTEGER DEFAULT NULL")
                database.execSQL("ALTER TABLE topics ADD COLUMN nextReviewTime INTEGER NOT NULL DEFAULT 0")
            }
        }

        // Migration from version 4 to 5 (adds savedForLater and isFavorite columns to words)
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE words ADD COLUMN savedForLater INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE words ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hulaba3_database"
                )
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    // IMPORTANT: Keep user data by avoiding destructive migrations.
                    // If you add new versions, ensure proper Migration objects are provided.
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
