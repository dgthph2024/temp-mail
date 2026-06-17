package com.tempmail.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tempmail.app.ui.screens.HomeScreen
import com.tempmail.app.ui.screens.InboxScreen
import com.tempmail.app.ui.screens.MessageDetailScreen
import com.tempmail.app.ui.theme.TempMailTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TempMailTheme {
                AppContent()
            }
        }
    }
}

enum class Screen {
    HOME, INBOX, DETAIL
}

@Composable
fun AppContent(
    viewModel: MainViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var currentScreen by remember { mutableStateOf(Screen.HOME) }

    when (currentScreen) {
        Screen.HOME -> {
            HomeScreen(
                state = state,
                onCreateEmail = { viewModel.createEmail() },
                onGoToInbox = {
                    viewModel.refreshMessages()
                    currentScreen = Screen.INBOX
                },
                onClearError = { viewModel.clearError() }
            )
        }

        Screen.INBOX -> {
            InboxScreen(
                state = state,
                onRefresh = { viewModel.refreshMessages() },
                onMessageClick = { message ->
                    viewModel.selectMessage(message)
                    currentScreen = Screen.DETAIL
                },
                onBack = { currentScreen = Screen.HOME }
            )
        }

        Screen.DETAIL -> {
            state.selectedMessage?.let { message ->
                MessageDetailScreen(
                    message = message,
                    onBack = {
                        viewModel.clearSelection()
                        currentScreen = Screen.INBOX
                    }
                )
            }
        }
    }
}
