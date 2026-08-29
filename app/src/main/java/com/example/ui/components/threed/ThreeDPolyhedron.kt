package com.example.ui.components.threed

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * 3D Point in coordinate space (x, y, z)
 */
data class Point3D(val x: Float, val y: Float, val z: Float)

/**
 * 3D Rotating Study Crystal / Icosahedron Core with dynamic vertex projection,
 * glow highlights, and interactive touch spin gestures.
 */
@Composable
fun ThreeDStudyPolyhedron(
    modifier: Modifier = Modifier,
    sizeDp: Dp = 120.dp,
    primaryColor: Color = Color(0xFF6366F1),
    secondaryColor: Color = Color(0xFF06B6D4),
    accentColor: Color = Color(0xFFA855F7),
    isInteractive: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "3DPolyhedronRotation")
    val autoAngleY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "autoAngleY"
    )
    val autoAngleX by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "autoAngleX"
    )

    var manualAngleX by remember { mutableFloatStateOf(0f) }
    var manualAngleY by remember { mutableFloatStateOf(0f) }

    // Golden ratio for icosahedron vertices
    val phi = (1f + sqrt(5f)) / 2f
    val baseVertices = remember {
        listOf(
            Point3D(-1f, phi, 0f),
            Point3D(1f, phi, 0f),
            Point3D(-1f, -phi, 0f),
            Point3D(1f, -phi, 0f),
            Point3D(0f, -1f, phi),
            Point3D(0f, 1f, phi),
            Point3D(0f, -1f, -phi),
            Point3D(0f, 1f, -phi),
            Point3D(phi, 0f, -1f),
            Point3D(phi, 0f, 1f),
            Point3D(-phi, 0f, -1f),
            Point3D(-phi, 0f, 1f)
        ).map { p ->
            // Normalize to unit sphere
            val len = sqrt(p.x * p.x + p.y * p.y + p.z * p.z)
            Point3D(p.x / len, p.y / len, p.z / len)
        }
    }

    val faces = remember {
        listOf(
            Triple(0, 11, 5), Triple(0, 5, 1), Triple(0, 1, 7), Triple(0, 7, 10), Triple(0, 10, 11),
            Triple(1, 5, 9), Triple(5, 11, 4), Triple(11, 10, 2), Triple(10, 7, 6), Triple(7, 1, 8),
            Triple(3, 9, 4), Triple(3, 4, 2), Triple(3, 2, 6), Triple(3, 6, 8), Triple(3, 8, 9),
            Triple(4, 9, 5), Triple(2, 4, 11), Triple(6, 2, 10), Triple(8, 6, 7), Triple(9, 8, 1)
        )
    }

    Box(
        modifier = modifier
            .size(sizeDp)
            .then(
                if (isInteractive) {
                    Modifier.pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            manualAngleY += dragAmount.x * 0.6f
                            manualAngleX -= dragAmount.y * 0.6f
                        }
                    }
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val totalAngleX = (autoAngleX + manualAngleX) * (PI.toFloat() / 180f)
            val totalAngleY = (autoAngleY + manualAngleY) * (PI.toFloat() / 180f)

            val centerX = size.width / 2f
            val centerY = size.height / 2f
            val radius = size.minDimension * 0.38f
            val cameraDist = 3.5f

            // Rotate vertices in 3D
            val transformed = baseVertices.map { v ->
                // Rotate Y
                val cosY = cos(totalAngleY)
                val sinY = sin(totalAngleY)
                val x1 = v.x * cosY + v.z * sinY
                val y1 = v.y
                val z1 = -v.x * sinY + v.z * cosY

                // Rotate X
                val cosX = cos(totalAngleX)
                val sinX = sin(totalAngleX)
                val x2 = x1
                val y2 = y1 * cosX - z1 * sinX
                val z2 = y1 * sinX + z1 * cosX

                // Perspective projection
                val perspective = cameraDist / (cameraDist + z2)
                val projX = centerX + x2 * radius * perspective
                val projY = centerY + y2 * radius * perspective

                ProjectedVertex(projX, projY, z2, perspective)
            }

            // Draw outer ambient 3D energy ring
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.25f),
                        secondaryColor.copy(alpha = 0.1f),
                        Color.Transparent
                    ),
                    center = Offset(centerX, centerY),
                    radius = radius * 1.5f
                ),
                radius = radius * 1.5f
            )

            // Render faces sorted by depth (Painter's algorithm)
            val sortedFaces = faces.map { face ->
                val v0 = transformed[face.first]
                val v1 = transformed[face.second]
                val v2 = transformed[face.third]
                val avgZ = (v0.z + v1.z + v2.z) / 3f
                FaceRenderData(face, v0, v1, v2, avgZ)
            }.sortedBy { it.avgZ }

            // Light vector for diffuse shading
            val lightX = 0.5f
            val lightY = -0.7f
            val lightZ = 1f
            val lightLen = sqrt(lightX * lightX + lightY * lightY + lightZ * lightZ)

            sortedFaces.forEach { faceData ->
                val (face, v0, v1, v2, avgZ) = faceData

                // Normal vector calculation
                val ax = v1.x - v0.x
                val ay = v1.y - v0.y
                val bx = v2.x - v0.x
                val by = v2.y - v0.y
                val normalZ = ax * by - ay * bx

                // Back-face culling for solid 3D crystal look
                if (normalZ > 0) {
                    val path = Path().apply {
                        moveTo(v0.x, v0.y)
                        lineTo(v1.x, v1.y)
                        lineTo(v2.x, v2.y)
                        close()
                    }

                    // Shading factor based on depth and normal
                    val depthFactor = ((avgZ + 1f) / 2f).coerceIn(0.2f, 1f)
                    val faceColor = when {
                        avgZ > 0.3f -> accentColor.copy(alpha = 0.35f * depthFactor)
                        avgZ > -0.2f -> primaryColor.copy(alpha = 0.28f * depthFactor)
                        else -> secondaryColor.copy(alpha = 0.18f * depthFactor)
                    }

                    drawPath(path, faceColor, style = Fill)
                    drawPath(
                        path,
                        Brush.linearGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.8f * depthFactor),
                                secondaryColor.copy(alpha = 0.7f * depthFactor)
                            ),
                            start = Offset(v0.x, v0.y),
                            end = Offset(v2.x, v2.y)
                        ),
                        style = Stroke(width = 1.6.dp.toPx())
                    )
                }
            }

            // Draw glowing vertex nodes
            transformed.forEach { v ->
                val nodeAlpha = ((v.z + 1f) / 2f).coerceIn(0.3f, 1f)
                val nodeRadius = (2.8f * v.perspective).coerceIn(1.5f, 4.5f).dp.toPx()

                drawCircle(
                    color = Color.White.copy(alpha = 0.9f * nodeAlpha),
                    radius = nodeRadius * 0.6f,
                    center = Offset(v.x, v.y)
                )
                drawCircle(
                    color = accentColor.copy(alpha = 0.5f * nodeAlpha),
                    radius = nodeRadius,
                    center = Offset(v.x, v.y)
                )
            }
        }
    }
}

private data class ProjectedVertex(
    val x: Float,
    val y: Float,
    val z: Float,
    val perspective: Float
)

private data class FaceRenderData(
    val face: Triple<Int, Int, Int>,
    val v0: ProjectedVertex,
    val v1: ProjectedVertex,
    val v2: ProjectedVertex,
    val avgZ: Float
)
