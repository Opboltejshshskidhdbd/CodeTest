package com.example.ui.screens

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudyFolder
import com.example.ui.components.threed.ThreeDFolderCard
import com.example.ui.components.threed.ThreeDStudyParticleField
import com.example.ui.theme.VibrantBg
import com.example.ui.theme.VibrantBlue
import com.example.ui.theme.VibrantDarkSlate
import com.example.ui.theme.VibrantEmerald
import com.example.ui.theme.VibrantOrange
import com.example.ui.theme.VibrantPink
import com.example.ui.theme.VibrantPurple
import com.example.ui.theme.VibrantTextDark
import com.example.ui.theme.VibrantTextMuted
import com.example.viewmodel.ScreenDestination
import com.example.viewmodel.StudyPdfViewModel
import com.example.viewmodel.StudyUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoldersScreen(
    uiState: StudyUiState,
    viewModel: StudyPdfViewModel,
    modifier: Modifier = Modifier
) {
    var showCreateFolder by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VibrantBg)
    ) {
        ThreeDStudyParticleField(modifier = Modifier.fillMaxSize(), particleCount = 14)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
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
                        .testTag("folders_back_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF0F172A))
                }

                Text(
                    text = "Study Collections",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = VibrantTextDark
                )

                IconButton(
                    onClick = { showCreateFolder = true },
                    modifier = Modifier
                        .size(44.dp)
                        .shadow(4.dp, CircleShape, spotColor = Color(0x332563EB))
                        .clip(CircleShape)
                        .background(VibrantBlue)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Folder", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3D Folder Cards Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 100.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.folders) { folder ->
                    ThreeDFolderCard(
                        folder = folder,
                        isSelected = uiState.selectedFolderId == folder.id,
                        onClick = {
                            viewModel.setSelectedFolder(folder.id)
                            viewModel.navigateTo(ScreenDestination.HOME)
                        }
                    )
                }
            }
        }

        if (showCreateFolder) {
            CreateFolderBottomSheet(
                onCreate = { name, desc, color, icon ->
                    viewModel.createFolder(name, desc, color, icon)
                    showCreateFolder = false
                },
                onDismiss = { showCreateFolder = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateFolderBottomSheet(
    onCreate: (String, String, Long, String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var selectedColor by remember { mutableLongStateOf(0xFF2563EB) }
    var selectedIcon by remember { mutableStateOf("Folder") }

    val colors = listOf(
        0xFF2563EB, // Blue
        0xFF7C3AED, // Violet
        0xFFEC4899, // Pink
        0xFF059669, // Emerald
        0xFFF97316, // Orange
        0xFF0EA5E9  // Sky
    )

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
                text = "New Study Collection",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = VibrantTextDark
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Collection Name (e.g. Quantum Physics)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VibrantBlue,
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = desc,
                onValueChange = { desc = it },
                label = { Text("Short Description or Semester") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VibrantBlue,
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Theme Color", fontSize = 12.sp, color = VibrantTextMuted, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                colors.forEach { c ->
                    val isSel = (c == selectedColor)
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(c))
                            .border(if (isSel) 3.dp else 0.dp, Color(0xFF0F172A), CircleShape)
                            .clickable { selectedColor = c }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreate(name.trim(), desc.trim(), selectedColor, selectedIcon)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color(0x332563EB)),
                colors = ButtonDefaults.buttonColors(containerColor = VibrantBlue),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Create Collection", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

