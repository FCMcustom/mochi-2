package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

data class ChatMessage(
    val sender: String, // "AI" or "USER"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocraticChatDialog(
    experimentContext: String,
    studentHypothesis: String,
    onDismiss: () -> Unit,
    onSendMessage: suspend (String) -> String
) {
    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    sender = "AI",
                    text = "Xin chào! Thầy là **Trợ lý phòng Lab Socratic GDPT 2018**.\n\nTrong lúc làm thí nghiệm, nếu gặp hiện tượng bất ngờ hoặc chưa rõ bản chất, em hãy hỏi thầy nhé. Thầy sẽ gợi mở để em tự tìm ra chân lý khoa học!"
                )
            )
        )
    }
    var inputText by remember { mutableStateOf("") }
    var isThinking by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0C1929),
        contentColor = Color.White,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF00E5FF).copy(alpha = 0.2f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Socratic",
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Trợ lý phòng Lab Socratic",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF80D8FF)
                        )
                        Text(
                            text = "Phương pháp gợi mở • Chuẩn GDPT 2018",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Đóng", tint = Color.LightGray)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Suggestion chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SuggestionChip(
                    onClick = { inputText = "Tại sao Cu lại không phản ứng với dung dịch HCl?" },
                    label = { Text("Vì sao Cu không tan?", fontSize = 11.sp) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = Color(0xFF162B45),
                        labelColor = Color(0xFF80D8FF)
                    )
                )
                SuggestionChip(
                    onClick = { inputText = "Bản chất vi mô của sự truyền electron trong phản ứng Zn + HCl là gì?" },
                    label = { Text("Truyền electron vi mô?", fontSize = 11.sp) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = Color(0xFF162B45),
                        labelColor = Color(0xFF80D8FF)
                    )
                )
            }

            HorizontalDivider(color = Color(0xFF1E3A5F), thickness = 0.5.dp)

            // Message list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages) { msg ->
                    val isAi = msg.sender == "AI"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isAi) Arrangement.Start else Arrangement.End
                    ) {
                        Surface(
                            shape = RoundedCornerShape(
                                topStart = 14.dp,
                                topEnd = 14.dp,
                                bottomStart = if (isAi) 2.dp else 14.dp,
                                bottomEnd = if (isAi) 14.dp else 2.dp
                            ),
                            color = if (isAi) Color(0xFF132840) else Color(0xFF006699),
                            border = if (isAi) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3E62)) else null,
                            modifier = Modifier.widthIn(max = 300.dp)
                        ) {
                            Text(
                                text = msg.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                modifier = Modifier.padding(12.dp),
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                if (isThinking) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color(0xFF00E5FF),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Thầy Socratic đang tư duy phản biện...",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF80D8FF),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Input Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Hỏi thầy Socratic về phản ứng...", color = Color.Gray, fontSize = 13.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF0A1522),
                        unfocusedContainerColor = Color(0xFF0A1522),
                        focusedBorderColor = Color(0xFF00E5FF),
                        unfocusedBorderColor = Color(0xFF1E3A5F),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        val text = inputText.trim()
                        if (text.isNotBlank() && !isThinking) {
                            val userMsg = ChatMessage(sender = "USER", text = text)
                            messages = messages + userMsg
                            inputText = ""
                            isThinking = true
                            coroutineScope.launch {
                                val reply = onSendMessage(text)
                                messages = messages + ChatMessage(sender = "AI", text = reply)
                                isThinking = false
                            }
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF00E5FF), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Gửi câu hỏi",
                        tint = Color(0xFF002244)
                    )
                }
            }
        }
    }
}
