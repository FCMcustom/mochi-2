package com.example.ui.lab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.ChatMessage
import com.example.ai.SocraticUiState

/**
 * Panel Socratic Assistant tích hợp trực tiếp vào VirtualLabScreen.
 * Hiển thị bong bóng hội thoại, trạng thái streaming và ô nhập câu hỏi.
 */
@Composable
fun SocraticPanel(
    uiState: SocraticUiState,
    messages: List<ChatMessage>,
    onSendMessage: (String) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = true
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll khi có tin nhắn mới
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0C1929))
            .border(1.dp, Color(0xFF1E3A5F), RoundedCornerShape(16.dp))
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar với animation khi streaming
                val isThinking = uiState is SocraticUiState.Loading || uiState is SocraticUiState.Streaming
                val infiniteTransition = rememberInfiniteTransition(label = "avatar_pulse")
                val avatarAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(800, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "avatar"
                )

                Box(contentAlignment = Alignment.Center) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF00E5FF).copy(alpha = if (isThinking) avatarAlpha else 0.2f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Socratic",
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    if (isThinking) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(36.dp)
                                .align(Alignment.Center),
                            color = Color(0xFF00E5FF),
                            strokeWidth = 2.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Trợ lý Socratic",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF80D8FF)
                    )
                    Text(
                        text = when (uiState) {
                            is SocraticUiState.Idle -> "Sẵn sàng hỗ trợ"
                            is SocraticUiState.Loading -> "Đang suy nghĩ..."
                            is SocraticUiState.Streaming -> "Đang trả lời..."
                            is SocraticUiState.Error -> "Gặp lỗi"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp,
                        color = when (uiState) {
                            is SocraticUiState.Loading, is SocraticUiState.Streaming -> Color(0xFF00E5FF)
                            is SocraticUiState.Error -> Color(0xFFFF8A80)
                            else -> Color.LightGray
                        }
                    )
                }
            }

            Row {
                // Nút xóa lịch sử
                IconButton(
                    onClick = onClearHistory,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Xóa lịch sử",
                        tint = Color.LightGray,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Nút thu gọn/mở rộng
                if (!isExpanded) {
                    IconButton(
                        onClick = { /* toggle expand */ },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExpandLess,
                            contentDescription = "Thu gọn",
                            tint = Color.LightGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = Color(0xFF1E3A5F), thickness = 0.5.dp)

        // Nội dung chat
        if (isExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp, max = 280.dp)
            ) {
                if (messages.isEmpty()) {
                    // Trạng thái trống
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = Color(0xFF1E3A5F),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Hãy chọn hóa chất và trộn chúng,\nTrợ lý Socratic sẽ gợi mở câu hỏi!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(messages, key = { it.timestamp }) { msg ->
                            ChatBubble(msg = msg, isStreaming = false)
                        }

                        // Streaming text đang hiển thị
                        if (uiState is SocraticUiState.Streaming) {
                            item {
                                StreamingBubble(partialText = uiState.partialText)
                            }
                        }

                        // Loading indicator
                        if (uiState is SocraticUiState.Loading) {
                            item {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        color = Color(0xFF00E5FF),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Trợ lý đang suy nghĩ...",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.sp,
                                        color = Color(0xFF80D8FF)
                                    )
                                }
                            }
                        }

                        // Error message
                        if (uiState is SocraticUiState.Error) {
                            item {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF2E1218),
                                    border = BorderStroke(1.dp, Color(0xFFFF5252))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Error,
                                            contentDescription = "Lỗi",
                                            tint = Color(0xFFFF5252),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = uiState.message,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontSize = 11.sp,
                                            color = Color(0xFFFF8A80)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFF1E3A5F), thickness = 0.5.dp)

            // Gợi ý nhanh (Suggestion chips)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val suggestions = listOf(
                    "Tại sao lại như vậy?" to "Tại sao lại như vậy?",
                    "Giải thích vi mô?" to "Giải thích bản chất vi mô của phản ứng này được không?"
                )
                suggestions.forEach { (label, _) ->
                    SuggestionChip(
                        onClick = { inputText = _ },
                        label = { Text(label, fontSize = 10.sp) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = Color(0xFF162B45),
                            labelColor = Color(0xFF80D8FF)
                        ),
                        modifier = Modifier.height(28.dp)
                    )
                }
            }

            // Input bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Hỏi thầy Socratic...", color = Color.Gray, fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF0A1522),
                        unfocusedContainerColor = Color(0xFF0A1522),
                        focusedBorderColor = Color(0xFF00E5FF),
                        unfocusedBorderColor = Color(0xFF1E3A5F),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                val canSend = inputText.isNotBlank() && uiState !is SocraticUiState.Loading
                IconButton(
                    onClick = {
                        if (canSend) {
                            onSendMessage(inputText.trim())
                            inputText = ""
                        }
                    },
                    enabled = canSend,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (canSend) Color(0xFF00E5FF) else Color(0xFF1E3A5F),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Gửi câu hỏi",
                        tint = if (canSend) Color(0xFF002244) else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(
    msg: ChatMessage,
    isStreaming: Boolean,
    modifier: Modifier = Modifier
) {
    val isUser = msg.sender == "user"
    val isAi = msg.sender == "assistant"

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            // Avatar AI
            Surface(
                shape = CircleShape,
                color = Color(0xFF00E5FF).copy(alpha = 0.2f),
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier.padding(4.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = if (isUser) 14.dp else 2.dp,
                bottomEnd = if (isUser) 2.dp else 14.dp
            ),
            color = when {
                isUser -> Color(0xFF006699)
                else -> Color(0xFF132840)
            },
            border = if (isAi) BorderStroke(1.dp, Color(0xFF1E3E62)) else null,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = msg.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    lineHeight = 19.sp
                )

                if (!isUser) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Trợ lý Socratic",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        color = Color(0xFF00E5FF).copy(alpha = 0.6f)
                    )
                }
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(6.dp))
            // Avatar user
            Surface(
                shape = CircleShape,
                color = Color(0xFF006699),
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

@Composable
private fun StreamingBubble(partialText: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFF00E5FF).copy(alpha = 0.2f),
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color(0xFF00E5FF),
                modifier = Modifier.padding(4.dp)
            )
        }
        Spacer(modifier = Modifier.width(6.dp))

        Surface(
            shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp, bottomStart = 2.dp, bottomEnd = 14.dp),
            color = Color(0xFF132840),
            border = BorderStroke(1.dp, Color(0xFF1E3E62)),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = partialText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    lineHeight = 19.sp
                )
                // Cursor nhấp nháy
                val infiniteTransition = rememberInfiniteTransition(label = "cursor")
                val cursorAlpha by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(500),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "cursor_alpha"
                )
                Text(
                    text = "▌",
                    color = Color(0xFF00E5FF).copy(alpha = cursorAlpha),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
