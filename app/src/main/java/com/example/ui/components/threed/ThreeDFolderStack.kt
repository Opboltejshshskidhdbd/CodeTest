package com.example.ui.components.threed

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudyFolder

/**
 * 3D Isometric Layered Folder Card with stacked paper depth effect.
 */
@Composable
fun ThreeDFolderCard(
    folder: StudyFolder,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedElev by animateFloatAsState(
        targetValue = if (isPressed) 2f else if (isSelected) 12f else 6f,
        animationSpec = spring(),
        label = "elevation"
    )

    val folderColor = Color(folder.colorHex)

    val icon: ImageVector = when (folder.iconName) {
        "Bolt" -> Icons.Default.Bolt
        "Science" -> Icons.Default.Science
        "Functions" -> Icons.Default.Functions
        "Memory" -> Icons.Default.Memory
        "MenuBook" -> Icons.Default.MenuBook
        else -> Icons.Default.Folder
    }

    Box(
        modifier = modifier
            .width(160.dp)
            .height(140.dp)
            .threeDTilt(
                maxRotationDegrees = 14f,
                scaleOnTouch = 1.05f,
                onClick = onClick
            )
    ) {
        // Bottom stacked layer (3D depth illusion)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = 6.dp, y = 6.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(folderColor.copy(alpha = 0.18f))
        )

        // Middle stacked layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = 3.dp, y = 3.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(folderColor.copy(alpha = 0.32f))
        )

        // Main Front Card
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(elevation = animatedElev.dp, shape = RoundedCornerShape(20.dp), spotColor = folderColor.copy(alpha = 0.25f))
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) folderColor else Color(0xFFE2E8F0),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Glowing Icon Container
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(folderColor.copy(alpha = 0.12f))
                            .border(1.dp, folderColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = folder.name,
                            tint = folderColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Doc count badge
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(folderColor.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${folder.docCount} docs",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = folderColor
                        )
                    }
                }

                Column {
                    Text(
                        text = folder.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = folder.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
