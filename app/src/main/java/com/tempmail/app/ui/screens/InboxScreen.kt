package com.tempmail.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tempmail.app.AppState
import com.tempmail.app.data.model.Message
import com.tempmail.app.ui.components.EmptyState
import com.tempmail.app.ui.components.LoadingIndicator
import com.tempmail.app.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(
    state: AppState,
    onRefresh: () -> Unit,
    onMessageClick: (Message) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Inbox",
                            fontWeight = FontWeight.Bold
                        )
                        if (state.email.isNotBlank()) {
                            Text(
                                text = state.email,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Làm mới")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading) {
                LoadingIndicator()
            } else if (state.messages.isEmpty()) {
                EmptyState(
                    message = "Chưa có email nào.\nHãy gửi mail đến địa chỉ này nhé!",
                    icon = "📭"
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.messages, key = { it.id }) { message ->
                        MessageCard(
                            message = message,
                            onClick = { onMessageClick(message) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageCard(
    message: Message,
    onClick: () -> Unit
) {
    val hasCode = message.body_text?.contains(Regex("\\b\\d{4,8}\\b")) ?: false

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // From + time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = message.from ?: "Unknown",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                }
                Text(
                    text = formatTime(message.created_at),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Subject
            Text(
                text = message.subject ?: "(Không có tiêu đề)",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Preview
            if (!message.body_text.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.body_text,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = TextSecondary
                )
            }

            // Code badge
            if (hasCode) {
                Spacer(modifier = Modifier.height(8.dp))
                val codes = extractCodes(message.body_text ?: "")
                codes.forEach { code ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "Mã: $code",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

private fun extractCodes(text: String): List<String> {
    val regex = Regex("\\b\\d{4,8}\\b")
    return regex.findAll(text).map { it.value }.toList().distinct()
}

private fun formatTime(timeStr: String?): String {
    if (timeStr.isNullOrBlank()) return ""
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(timeStr) ?: return timeStr
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        outputFormat.format(date)
    } catch (e: Exception) {
        timeStr.take(5)
    }
}
