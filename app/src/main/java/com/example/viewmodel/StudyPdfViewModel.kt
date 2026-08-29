package com.example.viewmodel

import android.app.Application
import android.net.Uri
import android.speech.tts.TextToSpeech
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AmbientSound
import com.example.data.model.AppVisualTheme
import com.example.data.model.FlashcardItem
import com.example.data.model.NoteCategory
import com.example.data.model.PomodoroSession
import com.example.data.model.PomodoroStage
import com.example.data.model.ReaderTheme
import com.example.data.model.ReadingMode
import com.example.data.model.StudyDocument
import com.example.data.model.StudyFolder
import com.example.data.model.StudyNote
import com.example.data.pdf.PdfRendererManager
import com.example.data.sample.PageStudyContent
import com.example.data.sample.SampleStudyData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

enum class ScreenDestination {
    HOME,
    READER,
    POMODORO,
    FLASHCARDS,
    FOLDERS
}

data class StudyUiState(
    val currentScreen: ScreenDestination = ScreenDestination.HOME,
    val appTheme: AppVisualTheme = AppVisualTheme.VIBRANT_LIGHT,
    val documents: List<StudyDocument> = SampleStudyData.sampleDocuments,
    val folders: List<StudyFolder> = SampleStudyData.sampleFolders,
    val notes: List<StudyNote> = SampleStudyData.sampleNotes,
    val flashcards: List<FlashcardItem> = SampleStudyData.sampleFlashcards,
    
    // Filtering & Search
    val searchQuery: String = "",
    val selectedFolderId: String? = null,
    val filterFavoritesOnly: Boolean = false,
    
    // Active Reader State
    val activeDocument: StudyDocument? = null,
    val currentPage: Int = 1,
    val readingMode: ReadingMode = ReadingMode.FLIP_3D,
    val readerTheme: ReaderTheme = ReaderTheme.CYBER_DARK,
    val isTtsSpeaking: Boolean = false,
    val ttsSpeed: Float = 1.0f,
    val isNotesDrawerOpen: Boolean = false,
    val isSummaryDeckOpen: Boolean = false,
    val renderedPageBitmap: ImageBitmap? = null,
    val currentStudyContent: PageStudyContent? = null,
    val isThumbnailBarVisible: Boolean = true,
    
    // Pomodoro Focus Session
    val pomodoro: PomodoroSession = PomodoroSession(),
    
    // Quick Add & Customization Dialogs
    val showAddFolderDialog: Boolean = false,
    val showAddNoteDialog: Boolean = false,
    val showCreateDocDialog: Boolean = false,
    val showDocInfoDialog: Boolean = false,
    val showCustomTimerDialog: Boolean = false,
    val showThemeDialog: Boolean = false,
    
    // Daily Study Metrics
    val totalStudyMinutesToday: Int = 78,
    val dailyGoalMinutes: Int = 120,
    val studyStreakDays: Int = 14
)

class StudyPdfViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val _uiState = MutableStateFlow(StudyUiState())
    val uiState: StateFlow<StudyUiState> = _uiState.asStateFlow()

    private val pdfManager = PdfRendererManager(application)
    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false
    private var pomodoroJob: Job? = null

    init {
        tts = TextToSpeech(application, this)
        // Pre-select first document as preview
        selectDocument(SampleStudyData.sampleDocuments[0], openReader = false)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            isTtsInitialized = true
        }
    }

    fun navigateTo(destination: ScreenDestination) {
        _uiState.update { it.copy(currentScreen = destination) }
    }

    fun selectDocument(document: StudyDocument, openReader: Boolean = true) {
        viewModelScope.launch {
            val totalPages = pdfManager.openDocument(document.uriString, document.sampleType)
            val updatedDoc = document.copy(totalPages = totalPages.coerceAtLeast(1))
            val initialPage = updatedDoc.lastReadPage.coerceIn(1, updatedDoc.totalPages)

            _uiState.update {
                it.copy(
                    activeDocument = updatedDoc,
                    currentPage = initialPage,
                    currentStudyContent = SampleStudyData.getDocumentPageContent(updatedDoc.sampleType, initialPage),
                    currentScreen = if (openReader) ScreenDestination.READER else it.currentScreen
                )
            }
            loadPageBitmap(initialPage)
        }
    }

    fun setPage(pageNumber: Int) {
        val doc = _uiState.value.activeDocument ?: return
        val clamped = pageNumber.coerceIn(1, doc.totalPages)
        if (clamped == _uiState.value.currentPage && _uiState.value.renderedPageBitmap != null) return

        _uiState.update {
            it.copy(
                currentPage = clamped,
                currentStudyContent = SampleStudyData.getDocumentPageContent(doc.sampleType, clamped),
                activeDocument = doc.copy(lastReadPage = clamped, lastReadTimestamp = System.currentTimeMillis())
            )
        }
        loadPageBitmap(clamped)
        if (_uiState.value.isTtsSpeaking) {
            speakCurrentPage()
        }
    }

    private fun loadPageBitmap(pageNumber: Int) {
        viewModelScope.launch {
            val bitmap = pdfManager.renderPage(pageNumber - 1)
            _uiState.update { it.copy(renderedPageBitmap = bitmap) }
        }
    }

    fun toggleReadingMode() {
        _uiState.update {
            it.copy(
                readingMode = if (it.readingMode == ReadingMode.FLIP_3D) ReadingMode.FLOW_SCROLL else ReadingMode.FLIP_3D
            )
        }
    }

    fun setReaderTheme(theme: ReaderTheme) {
        _uiState.update { it.copy(readerTheme = theme) }
    }

    fun toggleBookmarkCurrentPage() {
        val doc = _uiState.value.activeDocument ?: return
        val current = _uiState.value.currentPage
        val updatedBookmarks = if (doc.bookmarks.contains(current)) {
            doc.bookmarks.filter { it != current }
        } else {
            (doc.bookmarks + current).sorted()
        }
        val updatedDoc = doc.copy(bookmarks = updatedBookmarks)

        _uiState.update { state ->
            state.copy(
                activeDocument = updatedDoc,
                documents = state.documents.map { if (it.id == doc.id) updatedDoc else it }
            )
        }
    }

    fun toggleFavorite(docId: String) {
        _uiState.update { state ->
            val updated = state.documents.map {
                if (it.id == docId) it.copy(isFavorite = !it.isFavorite) else it
            }
            state.copy(
                documents = updated,
                activeDocument = if (state.activeDocument?.id == docId) {
                    state.activeDocument.copy(isFavorite = !state.activeDocument.isFavorite)
                } else state.activeDocument
            )
        }
    }

    fun deleteDocument(documentId: String) {
        _uiState.update { state ->
            val remaining = state.documents.filter { it.id != documentId }
            state.copy(
                documents = remaining,
                activeDocument = if (state.activeDocument?.id == documentId) remaining.firstOrNull() else state.activeDocument
            )
        }
    }

    fun setAppTheme(theme: AppVisualTheme) {
        _uiState.update { it.copy(appTheme = theme, showThemeDialog = false) }
    }

    fun setShowThemeDialog(show: Boolean) {
        _uiState.update { it.copy(showThemeDialog = show) }
    }

    fun setShowCustomTimerDialog(show: Boolean) {
        _uiState.update { it.copy(showCustomTimerDialog = show) }
    }

    fun toggleTts() {
        if (_uiState.value.isTtsSpeaking) {
            tts?.stop()
            _uiState.update { it.copy(isTtsSpeaking = false) }
        } else {
            speakCurrentPage()
        }
    }

    private fun speakCurrentPage() {
        val content = _uiState.value.currentStudyContent ?: return
        if (!isTtsInitialized) return

        val textToSpeak = StringBuilder()
            .append(content.title).append(". ")
            .append(content.subtitle).append(". ")
        content.sections.forEach {
            textToSpeak.append(it.header).append(". ").append(it.body).append(". ")
        }
        textToSpeak.append("Formula: ").append(content.keyFormula)

        tts?.setSpeechRate(_uiState.value.ttsSpeed)
        tts?.speak(textToSpeak.toString(), TextToSpeech.QUEUE_FLUSH, null, "study_tts")
        _uiState.update { it.copy(isTtsSpeaking = true) }
    }

    fun setTtsSpeed(speed: Float) {
        _uiState.update { it.copy(ttsSpeed = speed) }
        if (_uiState.value.isTtsSpeaking) {
            speakCurrentPage()
        }
    }

    fun toggleNotesDrawer() {
        _uiState.update { it.copy(isNotesDrawerOpen = !it.isNotesDrawerOpen) }
    }

    fun toggleSummaryDeck() {
        _uiState.update { it.copy(isSummaryDeckOpen = !it.isSummaryDeckOpen) }
    }

    fun addStudyNote(text: String, category: NoteCategory, snippet: String = "") {
        val doc = _uiState.value.activeDocument ?: return
        val newNote = StudyNote(
            documentId = doc.id,
            pageNumber = _uiState.value.currentPage,
            text = text,
            highlightedSnippet = snippet,
            category = category
        )
        _uiState.update { state ->
            val updatedNotes = state.notes + newNote
            val updatedDoc = doc.copy(notesCount = doc.notesCount + 1)
            state.copy(
                notes = updatedNotes,
                activeDocument = updatedDoc,
                documents = state.documents.map { if (it.id == doc.id) updatedDoc else it },
                showAddNoteDialog = false
            )
        }
    }

    fun deleteStudyNote(noteId: String) {
        _uiState.update { state ->
            state.copy(notes = state.notes.filter { it.id != noteId })
        }
    }

    fun updateFlashcardScore(cardId: String, score: Int) {
        _uiState.update { state ->
            state.copy(
                flashcards = state.flashcards.map {
                    if (it.id == cardId) it.copy(confidenceScore = score) else it
                }
            )
        }
    }

    fun addCustomFlashcard(question: String, answer: String, subject: String) {
        val newCard = FlashcardItem(
            documentId = _uiState.value.activeDocument?.id ?: "custom",
            documentTitle = _uiState.value.activeDocument?.title ?: "Custom Deck",
            question = question,
            answer = answer,
            subject = subject,
            pageNumber = _uiState.value.currentPage
        )
        _uiState.update { it.copy(flashcards = it.flashcards + newCard) }
    }

    fun importPdfFromUri(uri: Uri, defaultName: String? = null) {
        viewModelScope.launch {
            val fileName = pdfManager.getFileNameFromUri(uri)
            val title = if (fileName.isNotBlank() && !fileName.equals("Document.pdf", ignoreCase = true)) {
                fileName.replace(".pdf", "", ignoreCase = true)
            } else {
                defaultName ?: "Imported PDF"
            }
            val totalPages = pdfManager.openDocument(uri.toString(), null)
            val newDoc = StudyDocument(
                id = UUID.randomUUID().toString(),
                title = title,
                subject = "My PDF Material",
                folderId = _uiState.value.selectedFolderId ?: "default",
                uriString = uri.toString(),
                isBundledSample = false,
                isRealPdf = true,
                totalPages = totalPages.coerceAtLeast(1),
                lastReadPage = 1,
                tags = listOf("PDF", "Imported", "Notes"),
                aiSummaryPoints = listOf(
                    "Original PDF document imported from storage.",
                    "Full 3D page flip and high-clarity rendered view enabled."
                )
            )
            _uiState.update {
                it.copy(
                    documents = listOf(newDoc) + it.documents,
                    showCreateDocDialog = false
                )
            }
            selectDocument(newDoc, openReader = true)
        }
    }

    fun createStudyDocument(title: String, subject: String, folderId: String, tags: List<String>) {
        val newDoc = StudyDocument(
            id = UUID.randomUUID().toString(),
            title = title,
            subject = subject,
            folderId = folderId,
            isBundledSample = true,
            sampleType = when (subject.lowercase()) {
                "physics" -> "physics"
                "chemistry" -> "chem"
                "calculus", "math" -> "math"
                "computer science", "ai" -> "ai"
                else -> "history"
            },
            totalPages = 8,
            tags = tags,
            aiSummaryPoints = listOf(
                "Key Principle: Master fundamentals through active recall.",
                "Summary: Generated customized study guide with page bookmarks and formula highlights."
            )
        )
        _uiState.update {
            it.copy(
                documents = listOf(newDoc) + it.documents,
                showCreateDocDialog = false
            )
        }
        selectDocument(newDoc, openReader = true)
    }

    fun createFolder(name: String, description: String, colorHex: Long, iconName: String) {
        val newFolder = StudyFolder(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description,
            colorHex = colorHex,
            iconName = iconName,
            docCount = 0
        )
        _uiState.update {
            it.copy(
                folders = it.folders + newFolder,
                showAddFolderDialog = false
            )
        }
    }

    fun deleteFolder(folderId: String) {
        _uiState.update { state ->
            state.copy(
                folders = state.folders.filter { it.id != folderId },
                selectedFolderId = if (state.selectedFolderId == folderId) null else state.selectedFolderId
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setSelectedFolder(folderId: String?) {
        _uiState.update { it.copy(selectedFolderId = folderId) }
    }

    fun toggleFavoritesFilter() {
        _uiState.update { it.copy(filterFavoritesOnly = !it.filterFavoritesOnly) }
    }

    fun setShowAddFolderDialog(show: Boolean) {
        _uiState.update { it.copy(showAddFolderDialog = show) }
    }

    fun setShowAddNoteDialog(show: Boolean) {
        _uiState.update { it.copy(showAddNoteDialog = show) }
    }

    fun setShowCreateDocDialog(show: Boolean) {
        _uiState.update { it.copy(showCreateDocDialog = show) }
    }

    fun setShowDocInfoDialog(show: Boolean) {
        _uiState.update { it.copy(showDocInfoDialog = show) }
    }

    // Pomodoro Timer Controls & Customization
    fun setPomodoroMode(minutes: Int) {
        pomodoroJob?.cancel()
        val stage = if (minutes <= 5) PomodoroStage.SHORT_BREAK else if (minutes <= 15) PomodoroStage.LONG_BREAK else PomodoroStage.FOCUS
        _uiState.update {
            it.copy(
                pomodoro = it.pomodoro.copy(
                    modeMinutes = minutes,
                    remainingSeconds = minutes * 60,
                    isRunning = false,
                    stage = stage
                )
            )
        }
    }

    fun updatePomodoroSettings(
        focusMinutes: Int,
        shortBreakMinutes: Int,
        longBreakMinutes: Int,
        rounds: Int,
        autoAdvance: Boolean
    ) {
        pomodoroJob?.cancel()
        _uiState.update {
            val updated = it.pomodoro.copy(
                focusDurationMinutes = focusMinutes,
                shortBreakMinutes = shortBreakMinutes,
                longBreakMinutes = longBreakMinutes,
                roundsBeforeLongBreak = rounds,
                autoAdvanceCycles = autoAdvance,
                modeMinutes = focusMinutes,
                remainingSeconds = focusMinutes * 60,
                stage = PomodoroStage.FOCUS,
                isRunning = false
            )
            it.copy(pomodoro = updated, showCustomTimerDialog = false)
        }
    }

    fun adjustPomodoroTime(secondsDelta: Int) {
        _uiState.update {
            val newSeconds = (it.pomodoro.remainingSeconds + secondsDelta).coerceIn(10, 180 * 60)
            it.copy(
                pomodoro = it.pomodoro.copy(
                    remainingSeconds = newSeconds,
                    modeMinutes = (newSeconds / 60).coerceAtLeast(1)
                )
            )
        }
    }

    fun skipPomodoroStage() {
        pomodoroJob?.cancel()
        _uiState.update {
            val p = it.pomodoro
            val nextStage: PomodoroStage
            val nextMinutes: Int
            var nextRound = p.currentRound

            if (p.stage == PomodoroStage.FOCUS) {
                if (p.currentRound >= p.roundsBeforeLongBreak) {
                    nextStage = PomodoroStage.LONG_BREAK
                    nextMinutes = p.longBreakMinutes
                    nextRound = 1
                } else {
                    nextStage = PomodoroStage.SHORT_BREAK
                    nextMinutes = p.shortBreakMinutes
                }
            } else {
                nextStage = PomodoroStage.FOCUS
                nextMinutes = p.focusDurationMinutes
                if (p.stage == PomodoroStage.SHORT_BREAK) {
                    nextRound = p.currentRound + 1
                }
            }

            it.copy(
                pomodoro = p.copy(
                    stage = nextStage,
                    modeMinutes = nextMinutes,
                    remainingSeconds = nextMinutes * 60,
                    currentRound = nextRound,
                    isRunning = false
                )
            )
        }
    }

    fun togglePomodoroTimer() {
        val current = _uiState.value.pomodoro
        if (current.isRunning) {
            pomodoroJob?.cancel()
            _uiState.update { it.copy(pomodoro = it.pomodoro.copy(isRunning = false)) }
        } else {
            _uiState.update { it.copy(pomodoro = it.pomodoro.copy(isRunning = true)) }
            pomodoroJob = viewModelScope.launch {
                while (_uiState.value.pomodoro.remainingSeconds > 0 && _uiState.value.pomodoro.isRunning) {
                    delay(1000)
                    _uiState.update {
                        val remaining = (it.pomodoro.remainingSeconds - 1).coerceAtLeast(0)
                        it.copy(
                            pomodoro = it.pomodoro.copy(remainingSeconds = remaining),
                            totalStudyMinutesToday = if (!it.pomodoro.isBreak && remaining % 60 == 0) it.totalStudyMinutesToday + 1 else it.totalStudyMinutesToday
                        )
                    }
                }
                if (_uiState.value.pomodoro.remainingSeconds <= 0) {
                    // Session Completed
                    val p = _uiState.value.pomodoro
                    val completed = if (!p.isBreak) p.completedSessionsToday + 1 else p.completedSessionsToday
                    _uiState.update {
                        it.copy(
                            pomodoro = it.pomodoro.copy(
                                isRunning = false,
                                completedSessionsToday = completed
                            )
                        )
                    }
                    if (p.autoAdvanceCycles) {
                        skipPomodoroStage()
                    }
                }
            }
        }
    }

    fun resetPomodoro() {
        pomodoroJob?.cancel()
        _uiState.update {
            it.copy(
                pomodoro = it.pomodoro.copy(
                    remainingSeconds = it.pomodoro.modeMinutes * 60,
                    isRunning = false
                )
            )
        }
    }

    fun setAmbientSound(sound: AmbientSound) {
        _uiState.update { it.copy(pomodoro = it.pomodoro.copy(activeAmbientSound = sound)) }
    }

    override fun onCleared() {
        super.onCleared()
        pomodoroJob?.cancel()
        tts?.stop()
        tts?.shutdown()
        pdfManager.closeCurrent()
    }
}
