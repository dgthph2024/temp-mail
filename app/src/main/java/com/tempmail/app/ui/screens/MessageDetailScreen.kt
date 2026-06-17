package com.tempmail.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tempmail.app.data.model.Message
import com.tempmail.app.ui.components.OTPCodeCard
import com.tempmail.app.ui.theme.CodeTextColor
import com.tempmail.app.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageDetailScreen(
    message: Message,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val codes = extractAllCodes(message)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Chi tiết thư",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header info
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailRow("Từ", message.from ?: "N/A")
                    Spacer(modifier = Modifier.height(6.dp))
                    DetailRow("Đến", message.to ?: "N/A")
                    Spacer(modifier = Modifier.height(6.dp))
                    DetailRow("Tiêu đề", message.subject ?: "(Không có)")
                }
            }

            // Codes section
            if (codes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "📋 Mã xác thực",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                codes.forEach { code ->
                    OTPCodeCard(code = code)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Body content
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "📄 Nội dung thư",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val bodyText = message.body_text ?: message.body_html?.let { html ->
                        // Simple HTML tag stripping
                        html.replace(Regex("<[^>]*>"), "")
                            .replace("&amp;", "&")
                            .replace("&lt;", "<")
                            .replace("&gt;", ">")
                            .replace("&nbsp;", " ")
                            .replace("&quot;", "\"")
                    } ?: "(Không có nội dung)"

                    if (bodyText.isBlank()) {
                        Text(
                            "(Không có nội dung)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    } else {
                        Text(
                            text = highlightCodes(bodyText),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                lineHeight = 22.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

private fun extractAllCodes(message: Message): List<String> {
    val text = (message.body_text ?: "") + "\n" + (message.subject ?: "")
    val regex = Regex("\\b\\d{4,8}\\b")
    return regex.findAll(text).map { it.value }.toList().distinct()
}

private fun highlightCodes(text: String) = buildAnnotatedString {
    val regex = Regex("\\b\\d{4,8}\\b")
    var lastIndex = 0
    regex.findAll(text).forEach { match ->
        append(text.substring(lastIndex, match.range.first))
        withStyle(
            SpanStyle(
                color = CodeTextColor,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                fontSize = 16.sp,
                letterSpacing = 2.sp
            )
        ) {
            append(match.value)
        }
        lastIndex = match.range.last + 1
    }
    append(text.substring(lastIndex))
}
