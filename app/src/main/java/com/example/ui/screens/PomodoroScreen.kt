package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AmbientSound
import com.example.ui.components.threed.ThreeDStudyParticleField
import com.example.ui.components.threed.threeDTilt
import com.example.ui.theme.VibrantBg
import com.example.ui.theme.VibrantBlue
import com.example.ui.theme.VibrantBlueLight
import com.example.ui.theme.VibrantDarkSlate
import com.example.ui.theme.VibrantEmerald
import com.example.ui.theme.VibrantEmeraldBg
import com.example.ui.theme.VibrantOrange
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantPurple
import com.example.ui.theme.VibrantTextDark
import com.example.ui.theme.VibrantTextMuted
import com.example.viewmodel.ScreenDestination
import com.example.viewmodel.StudyPdfViewModel
import com.example.viewmodel.StudyUiState

@Composable
fun PomodoroScreen(
    uiState: StudyUiState,
    viewModel: StudyPdfViewModel,
    modifier: Modifier = Modifier
) {
    val pomodoro = uiState.pomodoro
    val totalSeconds = pomodoro.modeMinutes * 60
    val progress = if (totalSeconds > 0) {
        (totalSeconds - pomodoro.remainingSeconds).toFloat() / totalSeconds.toFloat()
    } else 0f

    val minutes = pomodoro.remainingSeconds / 60
    val seconds = pomodoro.remainingSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VibrantBg)
    ) {
        // Subtle ambient floating particles
        ThreeDStudyParticleField(modifier = Modifier.fillMaxSize(), particleCount = 18)

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
                        .testTag("pomodoro_back_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF0F172A))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFFFBEB))
                        .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = VibrantOrange,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${uiState.studyStreakDays} Day Streak",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB45309)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (pomodoro.isBreak) "Recharge & Rest" else "3D Focus Room",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = VibrantTextDark
            )
            Text(
                text = "Binaural soundscape & active deep study timer",
                style = MaterialTheme.typography.bodySmall,
                color = VibrantTextMuted
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Mode Selector Pills
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(20.dp), spotColor = Color(0x0A000000))
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
                    .padding(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        Pair(25, "25m Study"),
                        Pair(5, "5m Break"),
                        Pair(50, "50m Deep"),
                        Pair(15, "15m Rest")
                    ).forEach { (modeMins, title) ->
                        val isCur = (pomodoro.modeMinutes == modeMins)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isCur) VibrantBlue else Color.Transparent
                                )
                                .clickable { viewModel.setPomodoroMode(modeMins) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = if (isCur) FontWeight.Bold else FontWeight.SemiBold,
                                color = if (isCur) Color.White else Color(0xFF64748B)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3D Glowing Animated Focus Ring
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .threeDTilt(maxRotationDegrees = 12f, scaleOnTouch = 1.04f),
                contentAlignment = Alignment.Center
            ) {
                GlowingFocusRingCanvas(
                    progress = progress,
                    isRunning = pomodoro.isRunning,
                    isBreak = pomodoro.isBreak
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = timeFormatted,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = VibrantTextDark,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                    Text(
                        text = if (pomodoro.isRunning) (if (pomodoro.isBreak) "BREAK TIME" else "FOCUSING") else "PAUSED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (pomodoro.isRunning) VibrantBlue else Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Timer Controls (Start/Pause, Reset)
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.resetPomodoro() },
                    modifier = Modifier
                        .size(54.dp)
                        .shadow(4.dp, CircleShape, spotColor = Color(0x10000000))
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Button(
                    onClick = { viewModel.togglePomodoroTimer() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (pomodoro.isRunning) Color(0xFFEF4444) else VibrantBlue
                    ),
                    shape = RoundedCornerShape(24.dp),
                    contentPadding = PaddingValues(horizontal = 36.dp, vertical = 14.dp),
                    modifier = Modifier
                        .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = if (pomodoro.isRunning) Color(0x33EF4444) else Color(0x332563EB))
                        .testTag("pomodoro_toggle_button")
                ) {
                    Icon(
                        imageVector = if (pomodoro.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (pomodoro.isRunning) "PAUSE FOCUS" else "START FOCUS",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Ambient Soundscape Selector
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(24.dp), spotColor = Color(0x0A000000))
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(VibrantBlueLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Headphones,
                                contentDescription = null,
                                tint = VibrantBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Ambient Soundscape",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = VibrantTextDark
                        )
                    }

                    // Sound active indicator
                    if (pomodoro.activeAmbientSound != AmbientSound.SILENT) {
                        AnimatedSoundVisualizer(color = Color(pomodoro.activeAmbientSound.colorHex))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(AmbientSound.values()) { sound ->
                        val isSel = (pomodoro.activeAmbientSound == sound)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSel) Color(sound.colorHex).copy(alpha = 0.15f) else Color(0xFFF8FAFC))
                                .border(
                                    1.dp,
                                    if (isSel) Color(sound.colorHex) else Color(0xFFE2E8F0),
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { viewModel.setAmbientSound(sound) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Column {
                                Text(
                                    text = sound.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color(sound.colorHex) else Color(0xFF334155)
                                )
                                Text(
                                    text = sound.subtitle,
                                    fontSize = 9.sp,
                                    color = if (isSel) Color(sound.colorHex) else Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GlowingFocusRingCanvas(
    progress: Float,
    isRunning: Boolean,
    isBreak: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "FocusRingPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension * 0.42f
        val strokeWidth = 14.dp.toPx()

        // Background Track
        drawCircle(
            color = Color(0xFFE2E8F0),
            radius = radius,
            center = center,
            style = Stroke(width = strokeWidth)
        )

        // Outer Glow
        if (isRunning) {
            val glowColor = if (isBreak) Color(0xFF10B981) else Color(0xFF2563EB)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(glowColor.copy(alpha = 0.2f * pulseAlpha), Color.Transparent),
                    center = center,
                    radius = radius * 1.35f
                ),
                radius = radius * 1.35f,
                center = center
            )
        }

        // Active Progress Arc
        val sweepAngle = progress * 360f
        val activeBrush = if (isBreak) {
            Brush.sweepGradient(listOf(Color(0xFF10B981), Color(0xFF06B6D4), Color(0xFF10B981)))
        } else {
            Brush.sweepGradient(listOf(Color(0xFF2563EB), Color(0xFF4F46E5), Color(0xFF7C3AED), Color(0xFF2563EB)))
        }

        drawArc(
            brush = activeBrush,
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            size = Size(radius * 2f, radius * 2f),
            topLeft = Offset(center.x - radius, center.y - radius)
        )
    }
}

@Composable
private fun AnimatedSoundVisualizer(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "SoundBars")
    val h1 by infiniteTransition.animateFloat(initialValue = 4f, targetValue = 14f, animationSpec = infiniteRepeatable(tween(250), RepeatMode.Reverse), label = "h1")
    val h2 by infiniteTransition.animateFloat(initialValue = 12f, targetValue = 5f, animationSpec = infiniteRepeatable(tween(350), RepeatMode.Reverse), label = "h2")
    val h3 by infiniteTransition.animateFloat(initialValue = 7f, targetValue = 16f, animationSpec = infiniteRepeatable(tween(300), RepeatMode.Reverse), label = "h3")

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.height(16.dp)
    ) {
        Box(modifier = Modifier.width(2.5.dp).height(h1.dp).clip(CircleShape).background(color))
        Box(modifier = Modifier.width(2.5.dp).height(h2.dp).clip(CircleShape).background(color))
        Box(modifier = Modifier.width(2.5.dp).height(h3.dp).clip(CircleShape).background(color))
    }
}
