package com.example.hulaba3.data.database

import com.example.hulaba3.data.database.SpeakingPracticeType
import com.example.hulaba3.data.database.SpeakingPracticeDifficulty
import com.example.hulaba3.data.database.SpeakingPracticeExerciseType
import com.example.hulaba3.data.database.SpeakingPracticeExerciseDifficulty

import android.net.Uri
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.hulaba3.data.database.NotificationType
import com.example.hulaba3.data.database.NotificationPriority
import java.util.*

class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

class UriTypeConverter {
    @TypeConverter
    fun fromUri(uri: Uri?): String? {
        return uri?.toString()
    }

    @TypeConverter
    fun toUri(uriString: String?): Uri? {
        return uriString?.let { Uri.parse(it) }
    }
}

class JsonConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: String?): List<String>? {
        if (value == null) return null
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun toStringList(list: List<String>?): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromIntList(value: String?): List<Int>? {
        if (value == null) return null
        val listType = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun toIntList(list: List<Int>?): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromLongList(value: String?): List<Long>? {
        if (value == null) return null
        val listType = object : TypeToken<List<Long>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun toLongList(list: List<Long>?): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromMap(value: String?): Map<String, Any>? {
        if (value == null) return null
        val mapType = object : TypeToken<Map<String, Any>>() {}.type
        return gson.fromJson(value, mapType)
    }

    @TypeConverter
    fun toMap(map: Map<String, Any>?): String? {
        return gson.toJson(map)
    }

    @TypeConverter
    fun fromStringMap(value: String?): Map<String, String>? {
        if (value == null) return null
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(value, mapType)
    }

    @TypeConverter
    fun toStringMap(map: Map<String, String>?): String? {
        return gson.toJson(map)
    }
}

class TimeConverter {
    @TypeConverter
    fun fromMinutes(minutes: Int?): Date? {
        return minutes?.let { 
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, it / 60)
            calendar.set(Calendar.MINUTE, it % 60)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.time
        }
    }

    @TypeConverter
    fun toMinutes(date: Date?): Int? {
        return date?.let {
            val calendar = Calendar.getInstance()
            calendar.time = it
            calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
        }
    }
}

class BooleanConverter {
    @TypeConverter
    fun fromInt(value: Int?): Boolean? {
        return value?.let { it == 1 }
    }

    @TypeConverter
    fun toInt(boolean: Boolean?): Int? {
        return boolean?.let { if (it) 1 else 0 }
    }
}

class ColorConverter {
    @TypeConverter
    fun fromColorInt(color: Int?): String? {
        return color?.let { String.format("#%06X", (0xFFFFFF and it)) }
    }

    @TypeConverter
    fun toColorInt(colorHex: String?): Int? {
        return colorHex?.let {
            try {
                android.graphics.Color.parseColor(it)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}

class DifficultyConverter {
    @TypeConverter
    fun fromDifficultyLevel(level: String?): String? {
        return level?.lowercase()
    }

    @TypeConverter
    fun toDifficultyLevel(level: String?): String? {
        return when (level?.lowercase()) {
            "a1", "a2", "b1", "b2", "c1", "c2" -> level.uppercase()
            "beginner", "intermediate", "advanced", "expert" -> level.lowercase()
            "easy", "medium", "hard" -> level.lowercase()
            else -> level
        }
    }
}

class LanguageLevelConverter {
    @TypeConverter
    fun fromLanguageLevel(level: String?): String? {
        return level?.uppercase()
    }

    @TypeConverter
    fun toLanguageLevel(level: String?): String? {
        return when (level?.uppercase()) {
            "A1", "A2", "B1", "B2", "C1", "C2" -> level
            else -> "A1" // Default to A1 if invalid
        }
    }
}

class ConnectionTypeConverter {
    @TypeConverter
    fun fromConnectionType(type: String?): String? {
        return type?.lowercase()
    }

    @TypeConverter
    fun toConnectionType(type: String?): String? {
        return when (type?.lowercase()) {
            "friend", "study_buddy", "mentor", "student" -> type.lowercase()
            else -> "friend" // Default to friend if invalid
        }
    }
}

class ChallengeTypeConverter {
    @TypeConverter
    fun fromChallengeType(type: String?): String? {
        return type?.lowercase()
    }

    @TypeConverter
    fun toChallengeType(type: String?): String? {
        return when (type?.lowercase()) {
            "speaking", "streak", "vocabulary", "mixed", "topic_review", "pronunciation" -> type.lowercase()
            else -> "mixed" // Default to mixed if invalid
        }
    }
}

class SessionTypeConverter {
    @TypeConverter
    fun fromSessionType(type: String?): String? {
        return type?.lowercase()
    }

    @TypeConverter
    fun toSessionType(type: String?): String? {
        return when (type?.lowercase()) {
            "conversation", "correction", "teaching", "practice", "interview_prep" -> type.lowercase()
            else -> "conversation" // Default to conversation if invalid
        }
    }
}

class DiscussionTypeConverter {
    @TypeConverter
    fun fromDiscussionType(type: String?): String? {
        return type?.lowercase()
    }

    @TypeConverter
    fun toDiscussionType(type: String?): String? {
        return when (type?.lowercase()) {
            "general", "question", "announcement", "resource", "help" -> type.lowercase()
            else -> "general" // Default to general if invalid
        }
    }
}

class CollectionTypeConverter {
    @TypeConverter
    fun fromCollectionType(type: String?): String? {
        return type?.lowercase()
    }

    @TypeConverter
    fun toCollectionType(type: String?): String? {
        return when (type?.lowercase()) {
            "vocabulary", "topics", "mixed", "german_tech", "interview_prep", "speaking_practice" -> type.lowercase()
            else -> "mixed" // Default to mixed if invalid
        }
    }
}

class FileTypeConverter {
    @TypeConverter
    fun fromFileType(type: String?): String? {
        return type?.lowercase()
    }

    @TypeConverter
    fun toFileType(type: String?): String? {
        return when (type?.lowercase()) {
            "pdf", "image", "video", "audio", "link", "document" -> type.lowercase()
            else -> "pdf" // Default to pdf if invalid
        }
    }
}

class PartOfSpeechConverter {
    @TypeConverter
    fun fromPartOfSpeech(part: String?): String? {
        return part?.lowercase()
    }

    @TypeConverter
    fun toPartOfSpeech(part: String?): String? {
        return when (part?.lowercase()) {
            "noun", "verb", "adjective", "adverb", "pronoun", "preposition", "conjunction", "interjection", "article" -> part.lowercase()
            else -> part
        }
    }
}

class GenderConverter {
    @TypeConverter
    fun fromGender(gender: String?): String? {
        return gender?.lowercase()
    }

    @TypeConverter
    fun toGender(gender: String?): String? {
        return when (gender?.lowercase()) {
            "der", "die", "das", "masculine", "feminine", "neuter" -> gender.lowercase()
            else -> gender
        }
    }
}

class ThemeConverter {
    @TypeConverter
    fun fromTheme(theme: String?): String? {
        return theme?.lowercase()
    }

    @TypeConverter
    fun toTheme(theme: String?): String? {
        return when (theme?.lowercase()) {
            "auto", "light", "dark", "oled" -> theme.lowercase()
            else -> "auto" // Default to auto if invalid
        }
    }
}

class AchievementTypeConverter {
    @TypeConverter
    fun fromAchievementType(type: String?): String? {
        return type?.lowercase()
    }

    @TypeConverter
    fun toAchievementType(type: String?): String? {
        return when (type?.lowercase()) {
            "streak", "words_learned", "topics_completed", "speaking_practice", "study_time", "accuracy", "retention" -> type.lowercase()
            else -> type
        }
    }
}

class GoalTypeConverter {
    @TypeConverter
    fun fromGoalType(type: String?): String? {
        return type?.lowercase()
    }

    @TypeConverter
    fun toGoalType(type: String?): String? {
        return when (type?.lowercase()) {
            "german_level", "topic_completion", "speaking_practice", "vocabulary_mastery", "study_time", "streak_maintenance", "mixed_learning" -> type.lowercase()
            else -> type
        }
    }
}

class NotificationTypeConverter {
    @TypeConverter
    fun fromNotificationType(type: NotificationType?): String? {
        return type?.name
    }

    @TypeConverter
    fun toNotificationType(type: String?): NotificationType? {
        if (type == null) return null
        return try {
            NotificationType.valueOf(type.uppercase())
        } catch (e: IllegalArgumentException) {
            NotificationType.REVIEW_REMINDER
        }
    }
}
class LearningStatusConverter {
    @TypeConverter
    fun fromLearningStatus(status: String?): String? {
        return status?.lowercase()
    }

    @TypeConverter
    fun toLearningStatus(status: String?): String? {
        return when (status?.lowercase()) {
            "new", "learning", "reviewing", "mastered", "not_started", "in_progress", "completed" -> status.lowercase()
            else -> "new" // Default to new if invalid
        }
    }
}

// Removed PriorityConverter, ConfidenceConverter, and DifficultyRatingConverter
// These converters defined Int -> Int conversions that conflicted at the
// database level because multiple converters had identical signatures.
// Int fields do not require type converters; values should be stored directly.

class NotificationPriorityConverter {
    @TypeConverter
    fun fromNotificationPriority(priority: NotificationPriority?): String? {
        return priority?.name
    }

    @TypeConverter
    fun toNotificationPriority(priority: String?): NotificationPriority? {
        if (priority == null) return null
        return try {
            NotificationPriority.valueOf(priority.uppercase())
        } catch (e: IllegalArgumentException) {
            NotificationPriority.MEDIUM
        }
    }
}


class SpeakingPracticeTypeConverter {
    @androidx.room.TypeConverter
    fun fromType(type: SpeakingPracticeType?): String? = type?.name
    @androidx.room.TypeConverter
    fun toType(value: String?): SpeakingPracticeType? = value?.let { SpeakingPracticeType.valueOf(it) }
}

class SpeakingPracticeDifficultyConverter {
    @androidx.room.TypeConverter
    fun fromDifficulty(diff: SpeakingPracticeDifficulty?): String? = diff?.name
    @androidx.room.TypeConverter
    fun toDifficulty(value: String?): SpeakingPracticeDifficulty? = value?.let { SpeakingPracticeDifficulty.valueOf(it) }
}

class SpeakingPracticeExerciseTypeConverter {
    @androidx.room.TypeConverter
    fun fromType(type: SpeakingPracticeExerciseType?): String? = type?.name
    @androidx.room.TypeConverter
    fun toType(value: String?): SpeakingPracticeExerciseType? = value?.let { SpeakingPracticeExerciseType.valueOf(it) }
}

class SpeakingPracticeExerciseDifficultyConverter {
    @androidx.room.TypeConverter
    fun fromDifficulty(diff: SpeakingPracticeExerciseDifficulty?): String? = diff?.name
    @androidx.room.TypeConverter
    fun toDifficulty(value: String?): SpeakingPracticeExerciseDifficulty? = value?.let { SpeakingPracticeExerciseDifficulty.valueOf(it) }
}

// Quiz TypeConverters
class QuizTypeConverter {
    @androidx.room.TypeConverter
    fun fromQuizType(value: QuizType?): String? = value?.name

    @androidx.room.TypeConverter
    fun toQuizType(value: String?): QuizType? = value?.let { QuizType.valueOf(it) }
}

class QuizDifficultyConverter {
    @androidx.room.TypeConverter
    fun fromQuizDifficulty(value: QuizDifficulty?): String? = value?.name

    @androidx.room.TypeConverter
    fun toQuizDifficulty(value: String?): QuizDifficulty? = value?.let { QuizDifficulty.valueOf(it) }
}

class QuestionTypeConverter {
    @androidx.room.TypeConverter
    fun fromQuestionType(value: QuestionType?): String? = value?.name

    @androidx.room.TypeConverter
    fun toQuestionType(value: String?): QuestionType? = value?.let { QuestionType.valueOf(it) }
}

class QuestionDifficultyConverter {
    @androidx.room.TypeConverter
    fun fromQuestionDifficulty(value: QuestionDifficulty?): String? = value?.name

    @androidx.room.TypeConverter
    fun toQuestionDifficulty(value: String?): QuestionDifficulty? = value?.let { QuestionDifficulty.valueOf(it) }
}
