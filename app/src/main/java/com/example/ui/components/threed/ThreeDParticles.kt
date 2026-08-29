package com.example.ui.components.threed

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.sin
import kotlin.random.Random

private data class StudyParticle(
    val initialX: Float, // 0..1
    val initialY: Float, // 0..1
    val radius: Float,
    val speed: Float,
    val colorIndex: Int,
    val oscillationPhase: Float
)

/**
 * Ambient 3D floating particle and energy mesh background for study focus.
 */
@Composable
fun ThreeDStudyParticleField(
    modifier: Modifier = Modifier,
    particleCount: Int = 36
) {
    val infiniteTransition = rememberInfiniteTransition(label = "StudyParticles")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleTime"
    )

    val colors = remember {
        listOf(
            Color(0xFF2563EB), // Vibrant Blue
            Color(0xFF7C3AED), // Vibrant Purple
            Color(0xFFEC4899), // Vibrant Pink
            Color(0xFF059669), // Vibrant Emerald
            Color(0xFFF97316)  // Vibrant Orange
        )
    }

    val particles = remember {
        val rand = Random(42)
        List(particleCount) {
            StudyParticle(
                initialX = rand.nextFloat(),
                initialY = rand.nextFloat(),
                radius = rand.nextFloat() * 3.5f + 1.5f,
                speed = rand.nextFloat() * 0.4f + 0.2f,
                colorIndex = rand.nextInt(colors.size),
                oscillationPhase = rand.nextFloat() * 6.28f
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Top-right and bottom-left ambient neon glow orbs
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x256366F1), Color.Transparent),
                center = Offset(w * 0.85f, h * 0.15f),
                radius = w * 0.55f
            ),
            radius = w * 0.55f
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x2006B6D4), Color.Transparent),
                center = Offset(w * 0.15f, h * 0.85f),
                radius = w * 0.5f
            ),
            radius = w * 0.5f
        )

        // Draw individual floating particles with sine-wave drift
        particles.forEach { p ->
            val curY = ((p.initialY - (time * p.speed)) % 1f + 1f) % 1f
            val curX = p.initialX + (sin(time * 6.28f * 2f + p.oscillationPhase) * 0.04f)

            val px = curX * w
            val py = curY * h
            val baseColor = colors[p.colorIndex]
            val alpha = (0.25f + 0.45f * sin(time * 6.28f + p.oscillationPhase)).coerceIn(0.1f, 0.7f)

            // Outer soft glow
            drawCircle(
                color = baseColor.copy(alpha = alpha * 0.35f),
                radius = p.radius * 2.8f,
                center = Offset(px, py)
            )
            // Inner bright core
            drawCircle(
                color = baseColor.copy(alpha = alpha),
                radius = p.radius,
                center = Offset(px, py)
            )
        }
    }
}
