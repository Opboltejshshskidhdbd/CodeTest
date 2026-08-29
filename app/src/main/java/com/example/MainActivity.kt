package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CreateStudyDocDialog
import com.example.ui.components.DocumentInfoDialog
import com.example.ui.screens.FlashcardsScreen
import com.example.ui.screens.FoldersScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PdfViewerScreen
import com.example.ui.screens.PomodoroScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.ScreenDestination
import com.example.viewmodel.StudyPdfViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: StudyPdfViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                StudyPdfApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun StudyPdfApp(viewModel: StudyPdfViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (uiState.currentScreen != ScreenDestination.READER) {
                StudyBottomNavBar(
                    currentScreen = uiState.currentScreen,
                    onNavigate = { viewModel.navigateTo(it) }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (uiState.currentScreen != ScreenDestination.READER) innerPadding.calculateBottomPadding() else 0.dp)
        ) {
            AnimatedContent(
                targetState = uiState.currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenNavigation"
            ) { destination ->
                when (destination) {
                    ScreenDestination.HOME -> HomeScreen(uiState = uiState, viewModel = viewModel)
                    ScreenDestination.READER -> PdfViewerScreen(uiState = uiState, viewModel = viewModel)
                    ScreenDestination.POMODORO -> PomodoroScreen(uiState = uiState, viewModel = viewModel)
                    ScreenDestination.FLASHCARDS -> FlashcardsScreen(uiState = uiState, viewModel = viewModel)
                    ScreenDestination.FOLDERS -> FoldersScreen(uiState = uiState, viewModel = viewModel)
                }
            }

            // Dialogs
            if (uiState.showCreateDocDialog) {
                CreateStudyDocDialog(
                    folders = uiState.folders,
                    onCreate = { title, subject, folderId, tags ->
                        viewModel.createStudyDocument(title, subject, folderId, tags)
                    },
                    onDismiss = { viewModel.setShowCreateDocDialog(false) }
                )
            }

            if (uiState.showDocInfoDialog && uiState.activeDocument != null) {
                DocumentInfoDialog(
                    document = uiState.activeDocument!!,
                    onDismiss = { viewModel.setShowDocInfoDialog(false) }
                )
            }
        }
    }
}

@Composable
private fun StudyBottomNavBar(
    currentScreen: ScreenDestination,
    onNavigate: (ScreenDestination) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = Color(0x1A4F46E5),
                    ambientColor = Color(0x0F000000)
                )
                .clip(RoundedCornerShape(32.dp))
                .background(Color.White)
                .border(
                    1.dp,
                    Color(0xFFF1F5F9),
                    RoundedCornerShape(32.dp)
                )
                .padding(horizontal = 6.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StudyNavItem(
                    icon = Icons.Default.Home,
                    label = "Hub",
                    isSelected = currentScreen == ScreenDestination.HOME,
                    onClick = { onNavigate(ScreenDestination.HOME) },
                    testTag = "nav_home"
                )
                StudyNavItem(
                    icon = Icons.Default.MenuBook,
                    label = "3D Reader",
                    isSelected = currentScreen == ScreenDestination.READER,
                    onClick = { onNavigate(ScreenDestination.READER) },
                    testTag = "nav_reader"
                )
                StudyNavItem(
                    icon = Icons.Default.Timer,
                    label = "Focus",
                    isSelected = currentScreen == ScreenDestination.POMODORO,
                    onClick = { onNavigate(ScreenDestination.POMODORO) },
                    testTag = "nav_pomodoro"
                )
                StudyNavItem(
                    icon = Icons.Default.Psychology,
                    label = "3D Cards",
                    isSelected = currentScreen == ScreenDestination.FLASHCARDS,
                    onClick = { onNavigate(ScreenDestination.FLASHCARDS) },
                    testTag = "nav_flashcards"
                )
                StudyNavItem(
                    icon = Icons.Default.Folder,
                    label = "Folders",
                    isSelected = currentScreen == ScreenDestination.FOLDERS,
                    onClick = { onNavigate(ScreenDestination.FOLDERS) },
                    testTag = "nav_folders"
                )
            }
        }
    }
}

@Composable
private fun StudyNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val activeColor = Color(0xFF2563EB)
    val inactiveColor = Color(0xFF94A3B8)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) Color(0xFFEFF6FF) else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(width = 36.dp, height = 24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) Color(0xFFDBEAFE) else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isSelected) activeColor else inactiveColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isSelected) activeColor else inactiveColor,
                letterSpacing = (-0.2).sp
            )
        }
    }
}
