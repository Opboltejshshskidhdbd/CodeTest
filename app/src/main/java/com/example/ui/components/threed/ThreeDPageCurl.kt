package com.example.ui.components.threed

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * 3D Interactive Page Turn & Curl Component.
 * Supports realistic 3D Y-axis page flipping around the spine,
 * dynamic spine shadow rendering, and finger drag gesture.
 */
@Composable
fun ThreeDPageFlipContainer(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (page: Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val flipAngle = remember { Animatable(0f) }
    var dragDirection by remember { mutableFloatStateOf(0f) } // -1 for next, +1 for prev
    var isFlipping by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(currentPage, totalPages) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        isFlipping = true
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        dragDirection = if (dragAmount < 0) -1f else 1f
                        val progressDelta = (dragAmount / size.width) * 180f
                        val current = flipAngle.value + progressDelta
                        val clamped = when {
                            currentPage == totalPages && current < 0 -> current.coerceAtLeast(-30f)
                            currentPage == 1 && current > 0 -> current.coerceAtMost(30f)
                            else -> current.coerceIn(-180f, 180f)
                        }
                        coroutineScope.launch {
                            flipAngle.snapTo(clamped)
                        }
                    },
                    onDragEnd = {
                        isFlipping = false
                        val currentVal = flipAngle.value
                        coroutineScope.launch {
                            if (currentVal < -45f && currentPage < totalPages) {
                                // Turn to next page
                                flipAngle.animateTo(-180f, tween(260, easing = FastOutSlowInEasing))
                                onPageChange(currentPage + 1)
                                flipAngle.snapTo(0f)
                            } else if (currentVal > 45f && currentPage > 1) {
                                // Turn to previous page
                                flipAngle.animateTo(180f, tween(260, easing = FastOutSlowInEasing))
                                onPageChange(currentPage - 1)
                                flipAngle.snapTo(0f)
                            } else {
                                // Snap back
                                flipAngle.animateTo(0f, spring(dampingRatio = Spring.DampingRatioLowBouncy))
                            }
                        }
                    },
                    onDragCancel = {
                        isFlipping = false
                        coroutineScope.launch {
                            flipAngle.animateTo(0f, spring(dampingRatio = Spring.DampingRatioLowBouncy))
                        }
                    }
                )
            }
    ) {
        val angle = flipAngle.value

        // Underneath Page (Next or Previous being revealed)
        val underPage = when {
            angle < 0 && currentPage < totalPages -> currentPage + 1
            angle > 0 && currentPage > 1 -> currentPage - 1
            else -> currentPage
        }

        // Render underneath page
        if (underPage != currentPage) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        // Subtle depth zoom on background page
                        val underProgress = (abs(angle) / 180f).coerceIn(0f, 1f)
                        scaleX = 0.95f + 0.05f * underProgress
                        scaleY = 0.95f + 0.05f * underProgress
                    }
            ) {
                content(underPage)
                // Underneath shadow casting
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Color.Black.copy(
                                alpha = (1f - (abs(angle) / 180f)) * 0.35f
                            )
                        )
                )
            }
        }

        // Active Flipping Page
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = angle
                    cameraDistance = 12f * density
                    transformOrigin = if (angle <= 0) {
                        TransformOrigin(0f, 0.5f) // Left spine for turning forward
                    } else {
                        TransformOrigin(1f, 0.5f) // Right spine for turning backward
                    }
                    shadowElevation = if (abs(angle) > 2f) 16.dp.toPx() else 0f
                }
                .drawWithContent {
                    drawContent()
                    val absAng = abs(angle)
                    if (absAng > 0.5f) {
                        // Dynamic 3D Spine & Curl Shadow gradient
                        val shadowIntensity = (absAng / 90f).coerceIn(0f, 0.55f)
                        val spineWidth = size.width * 0.15f

                        // Spine shadow
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = shadowIntensity * 0.8f),
                                    Color.Transparent
                                ),
                                startX = 0f,
                                endX = spineWidth
                            ),
                            size = Size(spineWidth, size.height)
                        )

                        // Flip curl light & shadow gradient across page
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = shadowIntensity * 0.25f),
                                    Color.Black.copy(alpha = shadowIntensity * 0.6f)
                                ),
                                startX = size.width * 0.3f,
                                endX = size.width
                            )
                        )
                    }
                }
        ) {
            content(currentPage)
        }
    }
}
