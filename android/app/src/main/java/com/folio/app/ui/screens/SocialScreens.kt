package com.folio.app.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.folio.app.data.Books
import com.folio.app.data.Chat
import com.folio.app.data.Chats
import com.folio.app.data.Communities
import com.folio.app.data.Community
import com.folio.app.data.Message
import com.folio.app.data.Users
import com.folio.app.ui.components.BookCover
import com.folio.app.ui.components.SoftChip
import com.folio.app.ui.components.UserAvatar
import com.folio.app.ui.components.VoicePill
import com.folio.app.ui.theme.BookSerif

/* ============================== COMMUNITIES ============================== */

@Composable
fun CommunitiesScreen(onOpenBook: (Int) -> Unit) {
    var openIndex by remember { mutableStateOf<Int?>(null) }
    var showCreate by remember { mutableStateOf(false) }

    val index = openIndex
    if (index != null && index < Communities.size) {
        CommunityDetail(Communities[index], onBack = { openIndex = null }, onOpenBook = onOpenBook)
        return
    }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Communities", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.weight(1f))
            IconChip("＋", onClick = { showCreate = true })
        }
        LazyColumn(
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            items(Communities) { community ->
                CommunityCard(community) {
                    openIndex = Communities.indexOf(community)
                }
            }
        }
    }

    if (showCreate) {
        var name by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var inviteOnly by remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = { showCreate = false },
            title = { Text("Start a community", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = name, onValueChange = { name = it },
                        placeholder = { Text("e.g. Slow Sunday Readers") }, singleLine = true,
                    )
                    OutlinedTextField(
                        value = description, onValueChange = { description = it },
                        placeholder = { Text("What's it about?") },
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SoftChip("🌿 Open", selected = !inviteOnly) { inviteOnly = false }
                        SoftChip("✉ Invite-only", selected = inviteOnly) { inviteOnly = true }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    Communities.add(
                        0,
                        Community(
                            name.ifBlank { "Untitled circle" },
                            description.ifBlank { "A brand new corner for readers." },
                            Color(0xFF7E9276), Color(0xFFA4B494), 1, inviteOnly, listOf(0, 7),
                        )
                    )
                    showCreate = false
                }) { Text("Create") }
            },
            dismissButton = { TextButton(onClick = { showCreate = false }) { Text("Cancel") } },
        )
    }
}

@Composable
private fun CommunityCard(community: Community, onClick: () -> Unit) {
    Column(
        Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
            .clickable { onClick() },
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(84.dp)
                .background(Brush.linearGradient(listOf(community.c1, community.c2))),
            contentAlignment = Alignment.BottomStart,
        ) {
            Text(
                community.name,
                fontFamily = BookSerif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            )
        }
        Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                community.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                Modifier.padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    if (community.inviteOnly) "INVITE-ONLY" else "OPEN",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                        .padding(horizontal = 7.dp, vertical = 3.dp),
                )
                Text(
                    "${community.members} members",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                )
                Spacer(Modifier.weight(1f))
                Text(
                    "${community.shelf.size} on shared shelf",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                )
            }
        }
    }
}

@Composable
private fun CommunityDetail(community: Community, onBack: () -> Unit, onOpenBook: (Int) -> Unit) {
    var tab by remember { mutableStateOf(0) }
    val chatMessages = remember {
        mutableListOf(
            "anyone else reading by lamp light right now or is that just me" to false,
            "chapter 6 is so good I had to put the book DOWN" to false,
            "putting the kettle on and catching up tonight 🍵" to true,
        ).let { androidx.compose.runtime.mutableStateListOf(*it.toTypedArray()) }
    }
    var draft by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconChip("←", onClick = onBack)
            Text(
                community.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 12.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf("Feed", "Shelf", "Chat").forEachIndexed { i, label ->
                SoftChip(label, selected = tab == i) { tab = i }
            }
        }
        when (tab) {
            0 -> LazyColumn(contentPadding = PaddingValues(vertical = 10.dp)) {
                item {
                    Column(
                        Modifier
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                            .padding(16.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            UserAvatar(Users.getValue("noor"), size = 32.dp)
                            Column(Modifier.padding(start = 10.dp)) {
                                Text("Noor Haddad", style = MaterialTheme.typography.titleMedium, fontSize = 13.sp)
                                Text(
                                    "pinned by moderators · 3h ago",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                )
                            }
                        }
                        Text(
                            "📌 This month's read is ${Books[community.shelf.first()].title}. " +
                                "We discuss the first half on Sunday — voice notes welcome, spoilers gently fenced.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 10.dp),
                        )
                    }
                }
                item {
                    Column(
                        Modifier
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                            .padding(16.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            UserAvatar(Users.getValue("sam"), size = 32.dp)
                            Column(Modifier.padding(start = 10.dp)) {
                                Text("Sam Whitfield", style = MaterialTheme.typography.titleMedium, fontSize = 13.sp)
                                Text(
                                    "shared a voice note · 6h ago",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                )
                            }
                        }
                        VoicePill(com.folio.app.data.VoiceNote("0:58"), Modifier.padding(top = 10.dp))
                    }
                }
            }
            1 -> LazyColumn(contentPadding = PaddingValues(16.dp)) {
                item {
                    Text(
                        "Shared shelf — anyone can add",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        community.shelf.take(3).forEach { id ->
                            BookCover(
                                Books[id],
                                Modifier.weight(1f).height(160.dp),
                                titleSize = 13.sp,
                                onClick = { onOpenBook(id) },
                            )
                        }
                    }
                }
            }
            else -> Column(Modifier.fillMaxSize()) {
                LazyColumn(
                    Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(chatMessages) { (text, fromMe) -> ChatBubble(text, fromMe) }
                }
                Row(
                    Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = draft,
                        onValueChange = { draft = it },
                        placeholder = { Text("Message the club…") },
                        singleLine = true,
                        shape = RoundedCornerShape(50),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        ),
                        modifier = Modifier.weight(1f),
                    )
                    Box(
                        Modifier
                            .padding(start = 8.dp)
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable {
                                if (draft.isNotBlank()) {
                                    chatMessages.add(draft.trim() to true)
                                    draft = ""
                                }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("➤", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(text: String, fromMe: Boolean) {
    Box(Modifier.fillMaxWidth()) {
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (fromMe) Color.White else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .align(if (fromMe) Alignment.CenterEnd else Alignment.CenterStart)
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        18.dp, 18.dp,
                        if (fromMe) 6.dp else 18.dp,
                        if (fromMe) 18.dp else 6.dp,
                    )
                )
                .background(
                    if (fromMe) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surface
                )
                .border(
                    1.dp,
                    if (fromMe) Color.Transparent else MaterialTheme.colorScheme.outline,
                    RoundedCornerShape(
                        18.dp, 18.dp,
                        if (fromMe) 6.dp else 18.dp,
                        if (fromMe) 18.dp else 6.dp,
                    ),
                )
                .padding(horizontal = 14.dp, vertical = 10.dp),
        )
    }
}

/* ============================== MESSAGES ============================== */

@Composable
fun MessagesScreen(onOpenBook: (Int) -> Unit) {
    var openChatIndex by remember { mutableStateOf<Int?>(null) }

    val index = openChatIndex
    if (index != null && index < Chats.size) {
        ChatThread(Chats[index], onBack = { openChatIndex = null }, onOpenBook = onOpenBook)
        return
    }

    Column(Modifier.fillMaxSize()) {
        Text(
            "Messages",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
        )
        LazyColumn {
            items(Chats) { chat ->
                val user = Users.getValue(chat.userId)
                val last = chat.messages.last()
                val preview = when {
                    last.bookId != null -> "📕 ${Books[last.bookId].title}"
                    last.voice != null -> "🎙 Voice note · ${last.voice.duration}"
                    else -> last.text.orEmpty()
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            chat.unread = false
                            openChatIndex = Chats.indexOf(chat)
                        }
                        .padding(horizontal = 20.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    UserAvatar(user)
                    Column(Modifier.weight(1f).padding(start = 13.dp)) {
                        Text(user.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            preview,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            chat.time,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        )
                        if (chat.unread) {
                            Box(
                                Modifier
                                    .padding(top = 6.dp)
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondary),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatThread(chat: Chat, onBack: () -> Unit, onOpenBook: (Int) -> Unit) {
    val user = Users.getValue(chat.userId)
    var draft by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(chat.messages.size) {
        if (chat.messages.isNotEmpty()) listState.scrollToItem(chat.messages.size - 1)
    }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconChip("←", onClick = onBack)
            UserAvatar(user, size = 30.dp, onClick = null)
            Text(
                user.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 10.dp),
            )
        }
        LazyColumn(
            Modifier.weight(1f),
            state = listState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(chat.messages) { message -> MessageRow(message, onOpenBook) }
        }
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                placeholder = { Text("Message…") },
                singleLine = true,
                shape = RoundedCornerShape(50),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                ),
                modifier = Modifier.weight(1f),
            )
            Box(
                Modifier
                    .padding(start = 8.dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        if (draft.isNotBlank()) {
                            chat.messages.add(Message(fromMe = true, at = "now", text = draft.trim()))
                            draft = ""
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text("➤", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun MessageRow(message: Message, onOpenBook: (Int) -> Unit) {
    Box(Modifier.fillMaxWidth()) {
        val alignment = if (message.fromMe) Alignment.CenterEnd else Alignment.CenterStart
        when {
            // Book card share — tappable to open the book.
            message.bookId != null -> {
                val book = Books[message.bookId]
                Row(
                    Modifier
                        .align(alignment)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                        .clickable { onOpenBook(book.id) }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BookCover(book, Modifier.width(42.dp).height(62.dp), titleSize = 8.sp)
                    Column(Modifier.padding(start = 10.dp)) {
                        Text(book.title, fontFamily = BookSerif, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text(
                            "${book.author} · ★★★★★",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        )
                    }
                }
            }
            // Voice note share — playable pill in the thread.
            message.voice != null -> {
                Box(Modifier.align(alignment)) { VoicePill(message.voice) }
            }
            else -> {
                ChatBubble(message.text.orEmpty(), message.fromMe)
            }
        }
    }
}
