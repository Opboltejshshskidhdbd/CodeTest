package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FlashcardItem
import com.example.ui.components.threed.ThreeDStudyParticleField
import com.example.ui.components.threed.threeDTilt
import com.example.ui.theme.VibrantBg
import com.example.ui.theme.VibrantBlue
import com.example.ui.theme.VibrantBlueLight
import com.example.ui.theme.VibrantEmerald
import com.example.ui.theme.VibrantEmeraldBg
import com.example.ui.theme.VibrantOrange
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantPurple
import com.example.ui.theme.VibrantPurpleBg
import com.example.ui.theme.VibrantTextDark
import com.example.ui.theme.VibrantTextMuted
import com.example.viewmodel.ScreenDestination
import com.example.viewmodel.StudyPdfViewModel
import com.example.viewmodel.StudyUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsScreen(
    uiState: StudyUiState,
    viewModel: StudyPdfViewModel,
    modifier: Modifier = Modifier
) {
    var selectedSubject by remember { mutableStateOf<String?>(null) }
    var currentCardIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var showAddCardDialog by remember { mutableStateOf(false) }

    val filteredCards = remember(uiState.flashcards, selectedSubject) {
        if (selectedSubject == null) uiState.flashcards
        else uiState.flashcards.filter { it.subject == selectedSubject }
    }

    val safeIndex = if (filteredCards.isNotEmpty()) currentCardIndex.coerceIn(0, filteredCards.size - 1) else 0
    val activeCard = filteredCards.getOrNull(safeIndex)

    val subjects = remember(uiState.flashcards) {
        uiState.flashcards.map { it.subject }.distinct()
    }

    // 3D Flip angle animation
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "flashcardFlip"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VibrantBg)
    ) {
        ThreeDStudyParticleField(modifier = Modifier.fillMaxSize(), particleCount = 16)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(ScreenDestination.HOME) },
                    modifier = Modifier
                        .size(44.dp)
                        .shadow(2.dp, CircleShape, spotColor = Color(0x0A000000))
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                        .testTag("flashcards_back_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF0F172A))
                }

                Text(
                    text = "3D Flashcard Deck",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = VibrantTextDark
                )

                IconButton(
                    onClick = { showAddCardDialog = true },
                    modifier = Modifier
                        .size(44.dp)
                        .shadow(4.dp, CircleShape, spotColor = Color(0x332563EB))
                        .clip(CircleShape)
                        .background(VibrantBlue)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Card", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Subject Filter Row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    SubjectPill(
                        title = "All Decks (${uiState.flashcards.size})",
                        isSelected = selectedSubject == null,
                        onClick = {
                            selectedSubject = null
                            currentCardIndex = 0
                            isFlipped = false
                        }
                    )
                }
                items(subjects) { subj ->
                    val count = uiState.flashcards.count { it.subject == subj }
                    SubjectPill(
                        title = "$subj ($count)",
                        isSelected = selectedSubject == subj,
                        onClick = {
                            selectedSubject = subj
                            currentCardIndex = 0
                            isFlipped = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredCards.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No flashcards in this deck yet.\nTap + to create one!",
                        color = VibrantTextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            } else if (activeCard != null) {
                // Deck Progress Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Card ${safeIndex + 1} of ${filteredCards.size}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = VibrantBlue
                    )
                    Text(
                        text = activeCard.subject,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = VibrantTextMuted
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { (safeIndex + 1).toFloat() / filteredCards.size.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = VibrantBlue,
                    trackColor = Color(0xFFE2E8F0)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 3D Interactive Flip Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .threeDTilt(
                            maxRotationDegrees = 12f,
                            scaleOnTouch = 1.02f,
                            onClick = { isFlipped = !isFlipped }
                        )
                        .graphicsLayer {
                            rotationY = rotation
                            cameraDistance = 14f * density
                        }
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(28.dp),
                            spotColor = if (rotation <= 90f) Color(0x1A2563EB) else Color(0x1A10B981)
                        )
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color.White)
                        .border(
                            1.5.dp,
                            if (rotation <= 90f) Color(0xFFDBEAFE) else Color(0xFFD1FAE5),
                            RoundedCornerShape(28.dp)
                        )
                        .padding(24.dp)
                        .testTag("flashcard_flip_card"),
                    contentAlignment = Alignment.Center
                ) {
                    if (rotation <= 90f) {
                        // FRONT VIEW - Question
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(VibrantBlueLight)
                                    .padding(horizontal = 12.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "QUESTION",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = VibrantBlue
                                )
                            }

                            Text(
                                text = activeCard.question,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = VibrantTextDark,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Flip, contentDescription = null, tint = VibrantBlue, modifier = Modifier.size(16.dp))
                                Text(text = "Tap to Reveal Answer", fontSize = 11.sp, color = VibrantBlue, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // BACK VIEW - Answer (inversed graphicsLayer to cancel mirror)
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer { rotationY = 180f }
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(VibrantEmeraldBg)
                                    .padding(horizontal = 12.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "ANSWER / KEY CONCEPT",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = VibrantEmerald
                                )
                            }

                            Text(
                                text = activeCard.answer,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = VibrantTextDark,
                                textAlign = TextAlign.Center,
                                lineHeight = 24.sp,
                                modifier = Modifier.padding(vertical = 14.dp)
                            )

                            Text(text = "Rate your recall below:", fontSize = 11.sp, color = VibrantTextMuted, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Mastery / Recall Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.updateFlashcardScore(activeCard.id, 1)
                            if (safeIndex < filteredCards.size - 1) {
                                currentCardIndex = safeIndex + 1
                                isFlipped = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Hard (1)", fontSize = 12.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            viewModel.updateFlashcardScore(activeCard.id, 2)
                            if (safeIndex < filteredCards.size - 1) {
                                currentCardIndex = safeIndex + 1
                                isFlipped = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEF3C7)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Good (2)", fontSize = 12.sp, color = Color(0xFFD97706), fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            viewModel.updateFlashcardScore(activeCard.id, 3)
                            if (safeIndex < filteredCards.size - 1) {
                                currentCardIndex = safeIndex + 1
                                isFlipped = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD1FAE5)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6EE7B7)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Mastered", fontSize = 12.sp, color = Color(0xFF059669), fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Prev / Next Card Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (safeIndex > 0) {
                                currentCardIndex = safeIndex - 1
                                isFlipped = false
                            }
                        },
                        enabled = safeIndex > 0
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Prev", tint = if (safeIndex > 0) Color(0xFF0F172A) else Color(0xFFCBD5E1))
                    }

                    Text(
                        text = "Card ${safeIndex + 1} / ${filteredCards.size}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = VibrantTextMuted
                    )

                    IconButton(
                        onClick = {
                            if (safeIndex < filteredCards.size - 1) {
                                currentCardIndex = safeIndex + 1
                                isFlipped = false
                            }
                        },
                        enabled = safeIndex < filteredCards.size - 1
                    ) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = if (safeIndex < filteredCards.size - 1) Color(0xFF0F172A) else Color(0xFFCBD5E1))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Add Flashcard Dialog
        if (showAddCardDialog) {
            AddFlashcardBottomSheet(
                subjects = subjects,
                onAdd = { q, a, s ->
                    viewModel.addCustomFlashcard(q, a, s)
                    showAddCardDialog = false
                },
                onDismiss = { showAddCardDialog = false }
            )
        }
    }
}

@Composable
private fun SubjectPill(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .shadow(if (isSelected) 4.dp else 1.dp, RoundedCornerShape(14.dp), spotColor = if (isSelected) Color(0x332563EB) else Color(0x0A000000))
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) VibrantBlue else Color.White)
            .border(
                1.dp,
                if (isSelected) VibrantBlue else Color(0xFFE2E8F0),
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isSelected) Color.White else Color(0xFF475569)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddFlashcardBottomSheet(
    subjects: List<String>,
    onAdd: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var question by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf(subjects.firstOrNull() ?: "General Study") }

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
                text = "Create 3D Flashcard",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = VibrantTextDark
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = question,
                onValueChange = { question = it },
                label = { Text("Question or Concept Prompt") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VibrantBlue,
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = answer,
                onValueChange = { answer = it },
                label = { Text("Answer or Explanation") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VibrantBlue,
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (question.isNotBlank() && answer.isNotBlank()) {
                        onAdd(question.trim(), answer.trim(), selectedSubject)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color(0x332563EB)),
                colors = ButtonDefaults.buttonColors(containerColor = VibrantBlue),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Add to 3D Deck", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

