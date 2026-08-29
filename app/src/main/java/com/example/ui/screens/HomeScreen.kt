package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudyDocument
import com.example.ui.components.threed.ThreeDFolderCard
import com.example.ui.components.threed.ThreeDStudyParticleField
import com.example.ui.components.threed.ThreeDStudyPolyhedron
import com.example.ui.components.threed.threeDTilt
import com.example.ui.theme.VibrantAmber
import com.example.ui.theme.VibrantAmberBg
import com.example.ui.theme.VibrantBg
import com.example.ui.theme.VibrantBlue
import com.example.ui.theme.VibrantBlueLight
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantBorderActive
import com.example.ui.theme.VibrantDarkSlate
import com.example.ui.theme.VibrantEmerald
import com.example.ui.theme.VibrantEmeraldBg
import com.example.ui.theme.VibrantIndigo
import com.example.ui.theme.VibrantOrange
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantPurple
import com.example.ui.theme.VibrantSurface
import com.example.ui.theme.VibrantTextDark
import com.example.ui.theme.VibrantTextMuted
import com.example.ui.theme.VibrantTextSubtle
import com.example.ui.theme.VibrantViolet
import com.example.viewmodel.ScreenDestination
import com.example.viewmodel.StudyPdfViewModel
import com.example.viewmodel.StudyUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: StudyUiState,
    viewModel: StudyPdfViewModel,
    modifier: Modifier = Modifier
) {
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.importPdfFromUri(uri, "Imported PDF")
        }
    }

    val filteredDocs = remember(uiState.documents, uiState.searchQuery, uiState.selectedFolderId, uiState.filterFavoritesOnly) {
        uiState.documents.filter { doc ->
            val matchesQuery = uiState.searchQuery.isEmpty() ||
                    doc.title.contains(uiState.searchQuery, ignoreCase = true) ||
                    doc.subject.contains(uiState.searchQuery, ignoreCase = true) ||
                    doc.tags.any { it.contains(uiState.searchQuery, ignoreCase = true) }
            val matchesFolder = uiState.selectedFolderId == null || doc.folderId == uiState.selectedFolderId
            val matchesFav = !uiState.filterFavoritesOnly || doc.isFavorite
            matchesQuery && matchesFolder && matchesFav
        }
    }

    val activeResumeDoc = remember(uiState.documents) {
        uiState.documents.maxByOrNull { it.lastReadTimestamp } ?: uiState.documents.firstOrNull()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VibrantBg)
    ) {
        // Subtle ambient floating particles
        ThreeDStudyParticleField(modifier = Modifier.fillMaxSize(), particleCount = 20)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Vibrant Palette Top Header Bar
            item {
                VibrantHeaderSection()
            }

            // Next Up / Active Study Vibrant Hero Card
            item {
                VibrantNextUpHeroCard(
                    document = activeResumeDoc,
                    onOpen = {
                        if (activeResumeDoc != null) {
                            viewModel.selectDocument(activeResumeDoc, openReader = true)
                        } else {
                            viewModel.setShowCreateDocDialog(true)
                        }
                    }
                )
            }

            // 2x2 Vibrant Quick Feature Action Cards Grid
            item {
                VibrantFeatureGrid(
                    documentsCount = uiState.documents.size,
                    flashcardsCount = uiState.flashcards.size,
                    pomodoroMins = uiState.pomodoro.modeMinutes,
                    foldersCount = uiState.folders.size,
                    onNavigate = { viewModel.navigateTo(it) }
                )
            }

            // Weekly Focus Streak Dark Accent Card
            item {
                VibrantWeeklyStreakCard(
                    streakDays = uiState.studyStreakDays,
                    studyMinutes = uiState.totalStudyMinutesToday,
                    goalMinutes = uiState.dailyGoalMinutes
                )
            }

            // Search & Favorites Filter Bar
            item {
                SearchAndFilterBar(
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                    filterFavorites = uiState.filterFavoritesOnly,
                    onToggleFavorites = { viewModel.toggleFavoritesFilter() }
                )
            }

            // 3D Subject Collections Row
            item {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Study Collections",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = VibrantTextDark
                        )
                        Text(
                            text = "View All",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = VibrantBlue,
                            modifier = Modifier
                                .clickable { viewModel.navigateTo(ScreenDestination.FOLDERS) }
                                .padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            AllFilterPill(
                                isSelected = uiState.selectedFolderId == null,
                                onClick = { viewModel.setSelectedFolder(null) }
                            )
                        }

                        items(uiState.folders) { folder ->
                            ThreeDFolderCard(
                                folder = folder,
                                isSelected = uiState.selectedFolderId == folder.id,
                                onClick = {
                                    if (uiState.selectedFolderId == folder.id) {
                                        viewModel.setSelectedFolder(null)
                                    } else {
                                        viewModel.setSelectedFolder(folder.id)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Study Documents Library Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Study Documents",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = VibrantTextDark
                        )
                        Text(
                            text = "${filteredDocs.size} available guides",
                            fontSize = 12.sp,
                            color = VibrantTextMuted
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { pdfPickerLauncher.launch(arrayOf("application/pdf")) },
                            modifier = Modifier
                                .size(40.dp)
                                .shadow(4.dp, CircleShape, spotColor = Color(0x1A2563EB))
                                .clip(CircleShape)
                                .background(VibrantBlueLight)
                                .border(1.dp, Color(0xFFDBEAFE), CircleShape)
                                .testTag("import_pdf_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.UploadFile,
                                contentDescription = "Import PDF",
                                tint = VibrantBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.setShowCreateDocDialog(true) },
                            modifier = Modifier
                                .size(40.dp)
                                .shadow(4.dp, CircleShape, spotColor = Color(0x332563EB))
                                .clip(CircleShape)
                                .background(VibrantBlue)
                                .testTag("create_doc_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "New Document",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            // Documents List / Empty state
            if (filteredDocs.isEmpty()) {
                item {
                    EmptyDocumentsPlaceholder(
                        onImport = { pdfPickerLauncher.launch(arrayOf("application/pdf")) },
                        onCreate = { viewModel.setShowCreateDocDialog(true) }
                    )
                }
            } else {
                items(filteredDocs) { doc ->
                    StudyDocument3DListItem(
                        document = doc,
                        onOpen = { viewModel.selectDocument(doc, openReader = true) },
                        onToggleFavorite = { viewModel.toggleFavorite(doc.id) },
                        onInfo = {
                            viewModel.selectDocument(doc, openReader = false)
                            viewModel.setShowDocInfoDialog(true)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun VibrantHeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp),
                spotColor = Color(0x0A000000)
            )
            .clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "GOOD MORNING",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = VibrantBlue,
                    letterSpacing = 1.6.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Study Hub",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0F172A),
                    letterSpacing = (-0.5).sp
                )
            }

            // Vibrant Avatar Badge
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = Color(0x403B82F6))
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF3B82F6), Color(0xFF4F46E5))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "3D",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun VibrantNextUpHeroCard(
    document: StudyDocument?,
    onOpen: () -> Unit
) {
    val completionPercent = document?.completionPercentage ?: 0.65f
    val title = document?.title ?: "Physics:\nQuantum World"
    val subject = document?.subject ?: "Quantum Mechanics"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Gradient Glow Underlay
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(horizontal = 6.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0x406366F1),
                            Color(0x33A855F7),
                            Color(0x2AEC4899)
                        )
                    )
                )
        )

        // Main Gradient Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .threeDTilt(
                    maxRotationDegrees = 10f,
                    scaleOnTouch = 1.02f,
                    onClick = onOpen
                )
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = Color(0x334F46E5)
                )
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF4F46E5),
                            Color(0xFF6D28D9),
                            Color(0xFF7C3AED)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = Color(0x4DFFFFFF),
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(22.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0x33FFFFFF))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "NEXT UP",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            lineHeight = 24.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = subject,
                            fontSize = 12.sp,
                            color = Color(0xFFDDD6FE),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Floating 3D Polyhedron / Rocket Core
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0x1AFFFFFF))
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        ThreeDStudyPolyhedron(
                            sizeDp = 64.dp,
                            primaryColor = Color(0xFF818CF8),
                            secondaryColor = Color(0xFF38BDF8),
                            accentColor = Color(0xFFF472B6),
                            isInteractive = true
                        )
                    }
                }

                // Progress Bar & Percentage
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0x33FFFFFF))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(completionPercent)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White)
                                .shadow(4.dp, RoundedCornerShape(4.dp), spotColor = Color.White)
                        )
                    }

                    Text(
                        text = "${(completionPercent * 100).toInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun VibrantFeatureGrid(
    documentsCount: Int,
    flashcardsCount: Int,
    pomodoroMins: Int,
    foldersCount: Int,
    onNavigate: (ScreenDestination) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            VibrantQuickCard(
                title = "Library",
                subtitle = "$documentsCount New PDF",
                icon = Icons.Default.MenuBook,
                iconBg = VibrantAmberBg,
                iconTint = VibrantAmber,
                modifier = Modifier.weight(1f),
                onClick = { onNavigate(ScreenDestination.READER) },
                testTag = "quick_card_library"
            )

            VibrantQuickCard(
                title = "Flashcards",
                subtitle = "Daily Goal: $flashcardsCount",
                icon = Icons.Default.Psychology,
                iconBg = VibrantEmeraldBg,
                iconTint = VibrantEmerald,
                modifier = Modifier.weight(1f),
                onClick = { onNavigate(ScreenDestination.FLASHCARDS) },
                testTag = "quick_card_flashcards"
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            VibrantQuickCard(
                title = "Focus Room",
                subtitle = "${pomodoroMins}m Timer",
                icon = Icons.Default.Timer,
                iconBg = Color(0xFFFAF5FF),
                iconTint = VibrantPurple,
                modifier = Modifier.weight(1f),
                onClick = { onNavigate(ScreenDestination.POMODORO) },
                testTag = "quick_card_focus"
            )

            VibrantQuickCard(
                title = "Collections",
                subtitle = "$foldersCount Subjects",
                icon = Icons.Default.Folder,
                iconBg = VibrantBlueLight,
                iconTint = VibrantBlue,
                modifier = Modifier.weight(1f),
                onClick = { onNavigate(ScreenDestination.FOLDERS) },
                testTag = "quick_card_folders"
            )
        }
    }
}

@Composable
private fun VibrantQuickCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Box(
        modifier = modifier
            .threeDTilt(
                maxRotationDegrees = 8f,
                scaleOnTouch = 1.03f,
                onClick = onClick
            )
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(28.dp),
                spotColor = Color(0x0D000000)
            )
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(28.dp))
            .padding(16.dp)
            .testTag(testTag)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun VibrantWeeklyStreakCard(
    streakDays: Int,
    studyMinutes: Int,
    goalMinutes: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 10.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(32.dp),
                spotColor = Color(0x260F172A)
            )
            .clip(RoundedCornerShape(32.dp))
            .background(VibrantDarkSlate)
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "WEEKLY STREAK",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$streakDays",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = VibrantOrange
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Days",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFCBD5E1),
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }
            }

            // Activity Streak Bar Indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                val heights = listOf(24.dp, 32.dp, 40.dp, 20.dp, 16.dp)
                val isActive = listOf(true, true, true, false, false)

                heights.zip(isActive).forEach { (h, active) ->
                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .height(h)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (active) VibrantOrange else Color(0xFF334155))
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchAndFilterBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filterFavorites: Boolean,
    onToggleFavorites: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Search topics, formulas, notes...", color = Color(0xFF94A3B8), fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = VibrantBlue
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color(0xFF94A3B8))
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = VibrantBlue,
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedTextColor = Color(0xFF0F172A),
                unfocusedTextColor = Color(0xFF0F172A)
            ),
            modifier = Modifier
                .weight(1f)
                .shadow(2.dp, RoundedCornerShape(20.dp), spotColor = Color(0x0A000000))
                .testTag("search_text_field")
        )

        // Favorites filter button
        Box(
            modifier = Modifier
                .size(52.dp)
                .shadow(2.dp, RoundedCornerShape(20.dp), spotColor = Color(0x0A000000))
                .clip(RoundedCornerShape(20.dp))
                .background(if (filterFavorites) Color(0xFFFCE7F3) else Color.White)
                .border(
                    1.dp,
                    if (filterFavorites) Color(0xFFF472B6) else Color(0xFFE2E8F0),
                    RoundedCornerShape(20.dp)
                )
                .clickable { onToggleFavorites() }
                .testTag("favorites_filter_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (filterFavorites) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorites Filter",
                tint = if (filterFavorites) VibrantPink else Color(0xFF94A3B8)
            )
        }
    }
}

@Composable
private fun AllFilterPill(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(110.dp)
            .height(140.dp)
            .shadow(
                elevation = if (isSelected) 8.dp else 2.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = if (isSelected) Color(0x332563EB) else Color(0x0D000000)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (isSelected) {
                    Brush.linearGradient(listOf(Color(0xFF2563EB), Color(0xFF4F46E5)))
                } else {
                    Brush.linearGradient(listOf(Color.White, Color.White))
                }
            )
            .border(
                1.dp,
                if (isSelected) Color(0xFF93C5FD) else Color(0xFFE2E8F0),
                RoundedCornerShape(24.dp)
            )
            .clickable { onClick() }
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color(0x33FFFFFF) else VibrantBlueLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "All Folders",
                    tint = if (isSelected) Color.White else VibrantBlue
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "All Subjects",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color(0xFF0F172A)
            )
        }
    }
}

@Composable
private fun StudyDocument3DListItem(
    document: StudyDocument,
    onOpen: () -> Unit,
    onToggleFavorite: () -> Unit,
    onInfo: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .threeDTilt(
                maxRotationDegrees = 6f,
                scaleOnTouch = 1.02f,
                onClick = onOpen
            )
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color(0x0D000000)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(24.dp))
            .padding(16.dp)
            .testTag("doc_item_${document.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Vibrant Pastel Badge
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(VibrantBlueLight)
                    .border(1.dp, Color(0xFFDBEAFE), RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = VibrantBlue,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = document.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (document.bookmarks.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "Bookmarked",
                            tint = VibrantAmber,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFEFF6FF))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = document.subject,
                            fontSize = 10.sp,
                            color = VibrantBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "•",
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1)
                    )
                    Text(
                        text = "${document.totalPages} pages",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                    if (document.notesCount > 0) {
                        Text(
                            text = "• ${document.notesCount} notes",
                            fontSize = 11.sp,
                            color = VibrantPurple,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (document.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (document.isFavorite) VibrantPink else Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onInfo) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyDocumentsPlaceholder(
    onImport: () -> Unit,
    onCreate: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Description,
            contentDescription = null,
            tint = Color(0xFFCBD5E1),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "No study documents found",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF475569)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Import a local PDF or create a new study notes guide to start studying with 3D animations.",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF64748B),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = onImport,
                colors = ButtonDefaults.buttonColors(containerColor = VibrantBlue),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Import PDF")
            }
            Button(
                onClick = onCreate,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Create Study Guide")
            }
        }
    }
}

