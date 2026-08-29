package com.example.data.model

import android.net.Uri

enum class AppVisualTheme(
    val id: String,
    val title: String,
    val bgHex: Long,
    val cardHex: Long,
    val primaryHex: Long,
    val secondaryHex: Long,
    val textPrimaryHex: Long,
    val textSecondaryHex: Long,
    val borderHex: Long,
    val isDark: Boolean
) {
    VIBRANT_LIGHT("vibrant_light", "Vibrant Sky", 0xFFF7F9FF, 0xFFFFFFFF, 0xFF2563EB, 0xFF7C3AED, 0xFF0F172A, 0xFF64748B, 0xFFE2E8F0, false),
    MIDNIGHT_OLED("midnight_oled", "Midnight OLED", 0xFF070B14, 0xFF0F172A, 0xFF6366F1, 0xFF06B6D4, 0xFFF8FAFC, 0xFF94A3B8, 0xFF1E293B, true),
    WARM_SEPIA("warm_sepia", "Paper Sepia", 0xFFFAF4EB, 0xFFFFFDF9, 0xFFD97706, 0xFFB45309, 0xFF3D2E1E, 0xFF786551, 0xFFEAE0D0, false),
    AURORA_MINT("aurora_mint", "Aurora Mint", 0xFF041C15, 0xFF0B3026, 0xFF10B981, 0xFF06B6D4, 0xFFECFDF5, 0xFF6EE7B7, 0xFF134E3F, true),
    SUNSET_ROSE("sunset_rose", "Sunset Rose", 0xFFFFF5F7, 0xFFFFFFFF, 0xFFEC4899, 0xFFF43F5E, 0xFF28101E, 0xFF835A6E, 0xFFFCE7F3, false)
}

enum class ReaderTheme(
    val id: String,
    val title: String,
    val bgHex: Long,
    val surfaceHex: Long,
    val textHex: Long,
    val accentHex: Long,
    val isDark: Boolean
) {
    CYBER_DARK("cyber_dark", "Cyber Dark", 0xFF070B14, 0xFF0F172A, 0xFFF1F5F9, 0xFF6366F1, true),
    WARM_SEPIA("warm_sepia", "Paper Sepia", 0xFFFBF0D9, 0xFFF4E5C7, 0xFF4A3728, 0xFFB45309, false),
    DEEP_OLED("deep_oled", "Deep OLED", 0xFF000000, 0xFF121212, 0xFFE2E8F0, 0xFF06B6D4, true),
    AURORA_EMERALD("aurora", "Aurora Emerald", 0xFF061A14, 0xFF0B2E24, 0xFFD1FAE5, 0xFF10B981, true),
    ANIME_DUSK("anime_dusk", "Anime Dusk", 0xFF181024, 0xFF271B3B, 0xFFFCE7F3, 0xFFEC4899, true),
    LIGHT_MINIMAL("light_minimal", "Pure Minimal", 0xFFF8FAFC, 0xFFFFFFFF, 0xFF0F172A, 0xFF4F46E5, false)
}

enum class ReadingMode {
    FLIP_3D, // Realistic 3D Interactive Flip Book with curl and perspective tilt
    FLOW_SCROLL // Vertical smooth continuous stream
}

enum class NoteCategory(val title: String, val colorHex: Long, val iconName: String) {
    CORE_CONCEPT("Key Concept", 0xFF6366F1, "Star"),
    EXAM_PREP("Exam Must-Know", 0xFFEF4444, "PriorityHigh"),
    FORMULA("Formula / Law", 0xFF06B6D4, "Functions"),
    FLASHCARD("Flashcard Q&A", 0xFFA855F7, "Psychology"),
    DOUBT("Revision Doubt", 0xFFF59E0B, "HelpOutline")
}

data class StudyNote(
    val id: String = java.util.UUID.randomUUID().toString(),
    val documentId: String,
    val pageNumber: Int,
    val text: String,
    val highlightedSnippet: String = "",
    val category: NoteCategory = NoteCategory.CORE_CONCEPT,
    val timestamp: Long = System.currentTimeMillis(),
    val flashcardAnswer: String = "" // if category == FLASHCARD
)

data class FlashcardItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val documentId: String,
    val documentTitle: String,
    val question: String,
    val answer: String,
    val subject: String,
    val pageNumber: Int = 1,
    var confidenceScore: Int = 0 // 0 = New, 1 = Hard, 2 = Good, 3 = Mastered
)

data class StudyFolder(
    val id: String,
    val name: String,
    val description: String,
    val colorHex: Long,
    val iconName: String,
    val docCount: Int = 0
)

data class StudyDocument(
    val id: String,
    val title: String,
    val subject: String,
    val folderId: String = "default",
    val uriString: String? = null,
    val isBundledSample: Boolean = false,
    val isRealPdf: Boolean = false,
    val sampleType: String? = null, // physics, math, chem, ai, history
    val totalPages: Int = 12,
    val lastReadPage: Int = 1,
    val lastReadTimestamp: Long = System.currentTimeMillis(),
    val totalReadingMinutes: Int = 24,
    val isFavorite: Boolean = false,
    val tags: List<String> = emptyList(),
    val notesCount: Int = 0,
    val bookmarks: List<Int> = listOf(1),
    val aiSummaryPoints: List<String> = emptyList(),
    val estimatedReadTimeMinutes: Int = 15
) {
    val completionPercentage: Float
        get() = if (totalPages > 0) (lastReadPage.toFloat() / totalPages.toFloat()).coerceIn(0f, 1f) else 0f
}

enum class AmbientSound(val title: String, val subtitle: String, val colorHex: Long) {
    LOFI_BEATS("Lo-Fi Study Beats", "Chill rhythmic alpha waves", 0xFF6366F1),
    RAIN_ON_ROOF("Gentle Rain & Thunder", "Deep binaural brown noise", 0xFF06B6D4),
    DEEP_SPACE("Cosmic Theta Pulse", "432Hz memory enhancement", 0xFFA855F7),
    WHITE_NOISE("Pure White Noise", "Instant distraction shield", 0xFF10B981),
    SILENT("Silent Study", "Absolute tranquility", 0xFF64748B)
}

enum class PomodoroStage(val title: String, val colorHex: Long) {
    FOCUS("Focus Time", 0xFF2563EB),
    SHORT_BREAK("Short Break", 0xFF10B981),
    LONG_BREAK("Long Break", 0xFF7C3AED)
}

data class PomodoroSession(
    val focusDurationMinutes: Int = 25,
    val shortBreakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
    val roundsBeforeLongBreak: Int = 4,
    val currentRound: Int = 1,
    val modeMinutes: Int = 25,
    val remainingSeconds: Int = 25 * 60,
    val isRunning: Boolean = false,
    val stage: PomodoroStage = PomodoroStage.FOCUS,
    val completedSessionsToday: Int = 3,
    val streakDays: Int = 7,
    val activeAmbientSound: AmbientSound = AmbientSound.LOFI_BEATS,
    val ambientVolume: Float = 0.7f,
    val autoAdvanceCycles: Boolean = true
) {
    val isBreak: Boolean get() = stage != PomodoroStage.FOCUS
}

