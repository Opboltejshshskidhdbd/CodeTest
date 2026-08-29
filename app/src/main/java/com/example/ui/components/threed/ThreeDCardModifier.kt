package com.example.ui.components.threed

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * 3D Interactive Card Modifier that responds to touch drag gestures
 * with realistic 3D perspective tilt (rotationX, rotationY), specular sheen reflection,
 * scale depth pop, and spring physics rebound.
 */
fun Modifier.threeDTilt(
    maxRotationDegrees: Float = 16f,
    scaleOnTouch: Float = 1.04f,
    showSpecularSheen: Boolean = true,
    sheenColor: Color = Color(0x33FFFFFF),
    onClick: (() -> Unit)? = null
): Modifier = composed {
    val coroutineScope = rememberCoroutineScope()
    val rotationX = remember { Animatable(0f) }
    val rotationY = remember { Animatable(0f) }
    val scale = remember { Animatable(1f) }
    var touchNormalizedX by remember { mutableStateOf(0.5f) }
    var touchNormalizedY by remember { mutableStateOf(0.5f) }
    var isTouching by remember { mutableStateOf(false) }

    this
        .graphicsLayer {
            this.rotationX = rotationX.value
            this.rotationY = rotationY.value
            this.scaleX = scale.value
            this.scaleY = scale.value
            this.cameraDistance = 14f * density
            this.shadowElevation = if (isTouching) 24.dp.toPx() else 8.dp.toPx()
        }
        .drawWithContent {
            drawContent()
            if (showSpecularSheen && isTouching) {
                // Dynamic specular sheen following the light source opposite to tilt
                val sheenCenterX = size.width * touchNormalizedX
                val sheenCenterY = size.height * touchNormalizedY
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            sheenColor,
                            sheenColor.copy(alpha = sheenColor.alpha * 0.4f),
                            Color.Transparent
                        ),
                        center = Offset(sheenCenterX, sheenCenterY),
                        radius = size.maxDimension * 0.7f
                    )
                )
            }
        }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = { offset ->
                    isTouching = true
                    touchNormalizedX = (offset.x / size.width).coerceIn(0f, 1f)
                    touchNormalizedY = (offset.y / size.height).coerceIn(0f, 1f)

                    val targetRotY = (touchNormalizedX - 0.5f) * (maxRotationDegrees * 2)
                    val targetRotX = -(touchNormalizedY - 0.5f) * (maxRotationDegrees * 2)

                    coroutineScope.launch {
                        scale.animateTo(scaleOnTouch, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                    }
                    coroutineScope.launch {
                        rotationX.animateTo(targetRotX, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                    }
                    coroutineScope.launch {
                        rotationY.animateTo(targetRotY, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                    }

                    val released = tryAwaitRelease()
                    isTouching = false

                    coroutineScope.launch {
                        scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
                    }
                    coroutineScope.launch {
                        rotationX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
                    }
                    coroutineScope.launch {
                        rotationY.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
                    }

                    if (released && onClick != null) {
                        onClick()
                    }
                }
            )
        }
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { offset ->
                    isTouching = true
                    touchNormalizedX = (offset.x / size.width).coerceIn(0f, 1f)
                    touchNormalizedY = (offset.y / size.height).coerceIn(0f, 1f)
                },
                onDrag = { change, _ ->
                    change.consume()
                    touchNormalizedX = (change.position.x / size.width).coerceIn(0f, 1f)
                    touchNormalizedY = (change.position.y / size.height).coerceIn(0f, 1f)

                    val targetRotY = (touchNormalizedX - 0.5f) * (maxRotationDegrees * 2)
                    val targetRotX = -(touchNormalizedY - 0.5f) * (maxRotationDegrees * 2)

                    coroutineScope.launch {
                        rotationX.snapTo(targetRotX)
                        rotationY.snapTo(targetRotY)
                    }
                },
                onDragEnd = {
                    isTouching = false
                    coroutineScope.launch {
                        scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                        rotationX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                        rotationY.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                    }
                },
                onDragCancel = {
                    isTouching = false
                    coroutineScope.launch {
                        scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                        rotationX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                        rotationY.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                    }
                }
            )
        }
}
