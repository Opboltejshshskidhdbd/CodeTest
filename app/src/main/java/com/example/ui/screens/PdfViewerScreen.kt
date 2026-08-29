package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NoteCategory
import com.example.data.model.ReaderTheme
import com.example.data.model.ReadingMode
import com.example.data.sample.PageStudyContent
import com.example.data.sample.SampleStudyData
import com.example.ui.components.threed.ThreeDPageFlipContainer
import com.example.ui.components.threed.ThreeDStudyParticleField
import com.example.ui.components.threed.threeDTilt
import com.example.viewmodel.ScreenDestination
import com.example.viewmodel.StudyPdfViewModel
import com.example.viewmodel.StudyUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(
    uiState: StudyUiState,
    viewModel: StudyPdfViewModel,
    modifier: Modifier = Modifier
) {
    val activeDoc = uiState.activeDocument ?: return
    val currentTheme = uiState.readerTheme
    val themeBg = Color(currentTheme.bgHex)
    val themeSurface = Color(currentTheme.surfaceHex)
    val themeText = Color(currentTheme.textHex)
    val themeAccent = Color(currentTheme.accentHex)

    var showThemeMenu by remember { mutableStateOf(false) }
    var isControlsVisible by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(themeBg)
    ) {
        // Subtle ambient 3D particles in dark themes
        if (currentTheme.isDark) {
            ThreeDStudyParticleField(modifier = Modifier.fillMaxSize(), particleCount = 18)
        }

        // Main Page View (3D Flip Book or Continuous Scroll)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = if (isControlsVisible) 76.dp else 16.dp,
                    bottom = if (isControlsVisible) 110.dp else 24.dp
                )
                .clickable { isControlsVisible = !isControlsVisible }
        ) {
            if (uiState.readingMode == ReadingMode.FLIP_3D) {
                // 3D Flip Book mode with realistic curl & drag gesture
                ThreeDPageFlipContainer(
                    currentPage = uiState.currentPage,
                    totalPages = activeDoc.totalPages,
                    onPageChange = { viewModel.setPage(it) },
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    StudyPageCardContent(
                        pageNumber = page,
                        sampleType = activeDoc.sampleType,
                        theme = currentTheme,
                        renderedBitmap = if (page == uiState.currentPage) uiState.renderedPageBitmap else null,
                        notesForPage = uiState.notes.filter { it.documentId == activeDoc.id && it.pageNumber == page }
                    )
                }
            } else {
                // Smooth Continuous Flow Scroll mode
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(activeDoc.totalPages) { idx ->
                        val pageNum = idx + 1
                        StudyPageCardContent(
                            pageNumber = pageNum,
                            sampleType = activeDoc.sampleType,
                            theme = currentTheme,
                            renderedBitmap = if (pageNum == uiState.currentPage) uiState.renderedPageBitmap else null,
                            notesForPage = uiState.notes.filter { it.documentId == activeDoc.id && it.pageNumber == pageNum }
                        )
                    }
                }
            }
        }

        // Top Scrim & Control Bar
        AnimatedVisibility(
            visible = isControlsVisible,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it },
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            TopViewerControlBar(
                title = activeDoc.title,
                currentPage = uiState.currentPage,
                totalPages = activeDoc.totalPages,
                readingMode = uiState.readingMode,
                isBookmarked = activeDoc.bookmarks.contains(uiState.currentPage),
                isTtsSpeaking = uiState.isTtsSpeaking,
                onBack = { viewModel.navigateTo(ScreenDestination.HOME) },
                onToggleReadingMode = { viewModel.toggleReadingMode() },
                onToggleBookmark = { viewModel.toggleBookmarkCurrentPage() },
                onToggleTts = { viewModel.toggleTts() },
                onOpenThemes = { showThemeMenu = true },
                onOpenNotes = { viewModel.toggleNotesDrawer() },
                onOpenSummary = { viewModel.toggleSummaryDeck() }
            )
        }

        // Bottom Controls Bar (3D Thumbnails Carousel + Quick Slider)
        AnimatedVisibility(
            visible = isControlsVisible,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            BottomViewerControlBar(
                currentPage = uiState.currentPage,
                totalPages = activeDoc.totalPages,
                onPageSelected = { viewModel.setPage(it) },
                theme = currentTheme
            )
        }

        // Floating TTS Assistant Audio Bar
        if (uiState.isTtsSpeaking) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp)
            ) {
                TtsActiveAudioPill(
                    currentSpeed = uiState.ttsSpeed,
                    onSpeedChange = { viewModel.setTtsSpeed(it) },
                    onStop = { viewModel.toggleTts() }
                )
            }
        }

        // Theme Switcher Dialog / Sheet
        if (showThemeMenu) {
            ThemeSwitcherBottomSheet(
                currentTheme = currentTheme,
                onThemeSelected = {
                    viewModel.setReaderTheme(it)
                    showThemeMenu = false
                },
                onDismiss = { showThemeMenu = false }
            )
        }

        // Notes & Annotations Drawer / Sheet
        if (uiState.isNotesDrawerOpen) {
            StudyNotesBottomSheet(
                notes = uiState.notes.filter { it.documentId == activeDoc.id },
                currentPage = uiState.currentPage,
                onAddNote = { text, cat -> viewModel.addStudyNote(text, cat) },
                onDeleteNote = { viewModel.deleteStudyNote(it) },
                onDismiss = { viewModel.toggleNotesDrawer() }
            )
        }

        // AI Key Takeaways / Summary Sheet
        if (uiState.isSummaryDeckOpen) {
            AiSummaryBottomSheet(
                document = activeDoc,
                onGenerateFlashcard = { q, a ->
                    viewModel.addCustomFlashcard(q, a, activeDoc.subject)
                },
                onDismiss = { viewModel.toggleSummaryDeck() }
            )
        }
    }
}

@Composable
private fun TopViewerControlBar(
    title: String,
    currentPage: Int,
    totalPages: Int,
    readingMode: ReadingMode,
    isBookmarked: Boolean,
    isTtsSpeaking: Boolean,
    onBack: () -> Unit,
    onToggleReadingMode: () -> Unit,
    onToggleBookmark: () -> Unit,
    onToggleTts: () -> Unit,
    onOpenThemes: () -> Unit,
    onOpenNotes: () -> Unit,
    onOpenSummary: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .shadow(8.dp, RoundedCornerShape(22.dp), spotColor = Color(0x1A000000))
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xF5FFFFFF))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(22.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF0F172A)
                    )
                }

                Column(modifier = Modifier.padding(start = 4.dp)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Page $currentPage of $totalPages • ${if (readingMode == ReadingMode.FLIP_3D) "3D Flip Mode" else "Scroll Mode"}",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // 3D Flip vs Scroll Toggle
                IconButton(onClick = onToggleReadingMode) {
                    Icon(
                        imageVector = if (readingMode == ReadingMode.FLIP_3D) Icons.Default.Flip else Icons.Default.Layers,
                        contentDescription = "Toggle 3D Mode",
                        tint = if (readingMode == ReadingMode.FLIP_3D) Color(0xFF2563EB) else Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // TTS Read-Aloud
                IconButton(onClick = onToggleTts) {
                    Icon(
                        imageVector = if (isTtsSpeaking) Icons.Default.Pause else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Read Aloud",
                        tint = if (isTtsSpeaking) Color(0xFFEC4899) else Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // AI Summary Deck
                IconButton(onClick = onOpenSummary) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Summary",
                        tint = Color(0xFF7C3AED),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Bookmark
                IconButton(onClick = onToggleBookmark) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) Color(0xFFF59E0B) else Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Notes
                IconButton(onClick = onOpenNotes) {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = "Study Notes",
                        tint = Color(0xFF059669),
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Theme Switcher
                IconButton(onClick = onOpenThemes) {
                    Icon(
                        imageVector = Icons.Default.ColorLens,
                        contentDescription = "Themes",
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomViewerControlBar(
    currentPage: Int,
    totalPages: Int,
    onPageSelected: (Int) -> Unit,
    theme: ReaderTheme
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(22.dp), spotColor = Color(0x1A000000))
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xF5FFFFFF))
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(22.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Column {
                // Slider and Prev/Next
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { if (currentPage > 1) onPageSelected(currentPage - 1) },
                        enabled = currentPage > 1,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Previous Page",
                            tint = if (currentPage > 1) Color(0xFF0F172A) else Color(0xFFCBD5E1)
                        )
                    }

                    Slider(
                        value = currentPage.toFloat(),
                        onValueChange = { onPageSelected(it.toInt()) },
                        valueRange = 1f..totalPages.toFloat(),
                        steps = (totalPages - 2).coerceAtLeast(0),
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF2563EB),
                            activeTrackColor = Color(0xFF2563EB),
                            inactiveTrackColor = Color(0xFFE2E8F0)
                        )
                    )

                    IconButton(
                        onClick = { if (currentPage < totalPages) onPageSelected(currentPage + 1) },
                        enabled = currentPage < totalPages,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Next Page",
                            tint = if (currentPage < totalPages) Color(0xFF0F172A) else Color(0xFFCBD5E1)
                        )
                    }
                }

                // 3D Angled Thumbnail Strip
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(totalPages) { idx ->
                        val page = idx + 1
                        val isCurrent = (page == currentPage)
                        Box(
                            modifier = Modifier
                                .size(width = 38.dp, height = 48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isCurrent) Color(0xFF2563EB) else Color(0xFFF1F5F9))
                                .border(
                                    1.5.dp,
                                    if (isCurrent) Color(0xFF2563EB) else Color(0xFFE2E8F0),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { onPageSelected(page) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$page",
                                fontSize = 12.sp,
                                fontWeight = if (isCurrent) FontWeight.Black else FontWeight.Normal,
                                color = if (isCurrent) Color.White else Color(0xFF64748B)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudyPageCardContent(
    pageNumber: Int,
    sampleType: String?,
    theme: ReaderTheme,
    renderedBitmap: androidx.compose.ui.graphics.ImageBitmap?,
    notesForPage: List<com.example.data.model.StudyNote>
) {
    val pageContent = remember(sampleType, pageNumber) {
        SampleStudyData.getDocumentPageContent(sampleType, pageNumber)
    }

    val surfaceColor = Color(theme.surfaceHex)
    val textColor = Color(theme.textHex)
    val accentColor = Color(theme.accentHex)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(surfaceColor)
            .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header Page Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = pageContent.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = textColor,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "P. $pageNumber",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = pageContent.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = textColor.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Rendered PDF Page Preview (if available)
            if (renderedBitmap != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0x33000000), RoundedCornerShape(12.dp))
                ) {
                    Image(
                        bitmap = renderedBitmap,
                        contentDescription = "PDF Page $pageNumber",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Structured Study Sections
            pageContent.sections.forEach { section ->
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    Text(
                        text = section.header,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = section.body,
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor.copy(alpha = 0.9f),
                        lineHeight = 20.sp
                    )
                }
            }

            // Key Formula Highlight Box
            if (pageContent.keyFormula.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(accentColor.copy(alpha = 0.12f))
                        .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CORE FORMULA / KEY PRINCIPLE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = accentColor
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = pageContent.keyFormula,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }
            }

            // Notes for this page
            if (notesForPage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Page Annotations (${notesForPage.size}):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981)
                )
                Spacer(modifier = Modifier.height(6.dp))
                notesForPage.forEach { note ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(note.category.colorHex).copy(alpha = 0.15f))
                            .border(1.dp, Color(note.category.colorHex).copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = note.text,
                            fontSize = 12.sp,
                            color = textColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun TtsActiveAudioPill(
    currentSpeed: Float,
    onSpeedChange: (Float) -> Unit,
    onStop: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "TtsEq")
    val bar1 by infiniteTransition.animateFloat(
        initialValue = 4f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(tween(300, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "b1"
    )
    val bar2 by infiniteTransition.animateFloat(
        initialValue = 14f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "b2"
    )
    val bar3 by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(tween(350, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "b3"
    )

    Box(
        modifier = Modifier
            .shadow(12.dp, RoundedCornerShape(24.dp), spotColor = Color(0x33EC4899))
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF0F172A))
            .border(1.5.dp, Color(0xFFEC4899), RoundedCornerShape(24.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Live Equalizer bars
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.height(20.dp)
            ) {
                Box(modifier = Modifier.width(3.dp).height(bar1.dp).clip(CircleShape).background(Color(0xFFEC4899)))
                Box(modifier = Modifier.width(3.dp).height(bar2.dp).clip(CircleShape).background(Color(0xFF7C3AED)))
                Box(modifier = Modifier.width(3.dp).height(bar3.dp).clip(CircleShape).background(Color(0xFF2563EB)))
            }

            Text(
                text = "Reading Aloud",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Speed pills
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(0.75f, 1.0f, 1.25f, 1.5f).forEach { spd ->
                    val isCur = (spd == currentSpeed)
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isCur) Color(0xFFEC4899) else Color(0x33FFFFFF))
                            .clickable { onSpeedChange(spd) }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${spd}x",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            IconButton(onClick = onStop, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Stop",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeSwitcherBottomSheet(
    currentTheme: ReaderTheme,
    onThemeSelected: (ReaderTheme) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Study Canvas Themes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(14.dp))

            ReaderTheme.values().forEach { theme ->
                val isSelected = (theme == currentTheme)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(theme.bgHex))
                        .border(
                            if (isSelected) 2.5.dp else 1.dp,
                            if (isSelected) Color(0xFF2563EB) else Color(0xFFE2E8F0),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { onThemeSelected(theme) }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(theme.accentHex))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = theme.title,
                                fontWeight = FontWeight.Bold,
                                color = Color(theme.textHex)
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color(0xFF2563EB)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudyNotesBottomSheet(
    notes: List<com.example.data.model.StudyNote>,
    currentPage: Int,
    onAddNote: (String, NoteCategory) -> Unit,
    onDeleteNote: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var newNoteText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(NoteCategory.CORE_CONCEPT) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Study Annotations & Notes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Add Note Input
            androidx.compose.material3.OutlinedTextField(
                value = newNoteText,
                onValueChange = { newNoteText = it },
                placeholder = { Text("Write note for Page $currentPage...", color = Color(0xFF64748B)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(NoteCategory.values()) { cat ->
                    val isSel = (cat == selectedCategory)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) Color(cat.colorHex) else Color(0xFFF1F5F9))
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = cat.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) Color.White else Color(0xFF64748B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (newNoteText.isNotBlank()) {
                        onAddNote(newNoteText.trim(), selectedCategory)
                        newNoteText = ""
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Save Note to Page $currentPage", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notes list
            LazyColumn(modifier = Modifier.fillMaxWidth().height(220.dp)) {
                items(notes) { note ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(note.category.colorHex).copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Page ${note.pageNumber} • ${note.category.title}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(note.category.colorHex)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = note.text,
                                    fontSize = 12.sp,
                                    color = Color(0xFF1E293B)
                                )
                            }
                            IconButton(onClick = { onDeleteNote(note.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AiSummaryBottomSheet(
    document: com.example.data.model.StudyDocument,
    onGenerateFlashcard: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFF7C3AED),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI Key Principles & Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            document.aiSummaryPoints.forEachIndexed { idx, point ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFAF5FF))
                        .border(1.dp, Color(0x337C3AED), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "• $point",
                        fontSize = 12.sp,
                        color = Color(0xFF334155),
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onGenerateFlashcard(
                        "Summary Question for ${document.title}",
                        document.aiSummaryPoints.firstOrNull() ?: "Core conceptual breakdown."
                    )
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .shadow(4.dp, RoundedCornerShape(14.dp), spotColor = Color(0x337C3AED)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Convert to 3D Flashcard Deck", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
