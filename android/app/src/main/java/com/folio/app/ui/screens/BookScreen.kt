package com.folio.app.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.folio.app.data.Books
import com.folio.app.data.Users
import com.folio.app.data.VoiceNote
import com.folio.app.ui.components.BookCover
import com.folio.app.ui.components.RatingStars
import com.folio.app.ui.components.SectionLabel
import com.folio.app.ui.components.SoftChip
import com.folio.app.ui.components.UserAvatar
import com.folio.app.ui.components.VoicePill
import com.folio.app.ui.map.NeuralMapSection
import com.folio.app.ui.theme.Clay
import kotlin.math.abs
import kotlin.math.sin
import kotlinx.coroutines.delay

@Composable
fun BookDetailScreen(
    bookId: Int,
    onBack: () -> Unit,
    onOpenBook: (Int) -> Unit,
    onRecord: () -> Unit,
) {
    val book = Books[bookId]
    var synopsisOpen by remember(bookId) { mutableStateOf(false) }
    val averageRating = 3.7f + (bookId % 14) / 10f

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconChip("←", onClick = onBack)
            Spacer(Modifier.weight(1f))
            IconChip("🎙", onClick = onRecord)
            Spacer(Modifier.width(8.dp))
            IconChip("＋", onClick = {})
        }
        Column(Modifier.verticalScroll(rememberScrollState())) {
            Row(
                Modifier.padding(horizontal = 20.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                BookCover(book, Modifier.width(88.dp).height(130.dp), titleSize = 13.sp)
                Column(Modifier.padding(start = 18.dp)) {
                    Text(book.title, style = MaterialTheme.typography.headlineSmall)
                    Text(
                        book.author,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                    Text(
                        book.year.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    )
                    Row(
                        Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        book.genres.forEach { genre ->
                            Text(
                                genre,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                            )
                        }
                    }
                }
            }

            // Synopsis — collapsible after 3 lines.
            Column(Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                Text(
                    book.synopsis,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = if (synopsisOpen) Int.MAX_VALUE else 3,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    if (synopsisOpen) "Read less" else "Read more",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Clay,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clickable { synopsisOpen = !synopsisOpen },
                )
            }

            // Community rating with breakdown.
            Row(
                Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        String.format("%.1f", averageRating),
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 40.sp,
                    )
                    RatingStars(averageRating.toInt(), size = 13.sp)
                    Text(
                        "${214 + bookId * 37} ratings",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    )
                }
                Column(
                    Modifier.padding(start = 20.dp).weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    val distribution = listOf(64, 22, 9, 3, 2)
                    distribution.forEachIndexed { i, v ->
                        val pct = maxOf(2, (v + bookId * 3) % 70)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "${5 - i}★",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.width(22.dp),
                            )
                            Box(
                                Modifier
                                    .weight(1f)
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                            ) {
                                Box(
                                    Modifier
                                        .fillMaxWidth(pct / 100f)
                                        .height(5.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(MaterialTheme.colorScheme.secondary),
                                )
                            }
                        }
                    }
                }
            }

            // Voice notes — sorted by chapter, then book-level.
            SectionLabel("Voice notes")
            val voiceNotes = listOf(
                "priya" to VoiceNote("1:12", "Ch. ${2 + bookId % 7}"),
                "sam" to VoiceNote("0:51", "Ch. ${1 + bookId % 4}"),
                "noor" to VoiceNote("0:33"),
            )
            voiceNotes.forEach { (userId, note) ->
                Row(
                    Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    UserAvatar(Users.getValue(userId), size = 30.dp)
                    VoicePill(note, Modifier.padding(start = 10.dp))
                }
            }
            Box(
                Modifier
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(50))
                    .clickable { onRecord() }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "🎙  Add your voice note",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Comments with replies.
            SectionLabel("Conversation")
            CommentRow("maya", "The last forty pages of this rearranged my furniture. Anyone else read it in one sitting?", "2h", 8)
            CommentRow("theo", "One sitting, one pot of tea, zero emotional preparation.", "1h", 3, reply = true)

            // Neural book map — full-bleed dark canvas (≥70% screen height).
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Connected books", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.weight(1f))
                Text(
                    "${com.folio.app.data.connectionsFor(bookId).size} connections",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                )
            }
            NeuralMapSection(book, onOpenBook)
        }
    }
}

@Composable
private fun CommentRow(userId: String, text: String, ago: String, likes: Int, reply: Boolean = false) {
    Row(
        Modifier.padding(
            start = if (reply) 56.dp else 20.dp,
            end = 20.dp,
            top = if (reply) 4.dp else 10.dp,
            bottom = 4.dp,
        ),
    ) {
        UserAvatar(Users.getValue(userId), size = 30.dp)
        Column(Modifier.padding(start = 10.dp)) {
            Text(Users.getValue(userId).name, style = MaterialTheme.typography.titleMedium, fontSize = 13.sp)
            Text(
                text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp),
            )
            Row(
                Modifier.padding(top = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text(ago, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                Text("Reply", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                Text("🤍 $likes", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
            }
        }
    }
}

/** Minimal recording screen: waveform animation, timer, visibility choice, stop. */
@Composable
fun RecordScreen(bookId: Int, onDone: () -> Unit) {
    val book = Books[bookId]
    var seconds by remember { mutableIntStateOf(0) }
    var visibility by remember { mutableStateOf("Friends") }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            seconds++
        }
    }

    val transition = rememberInfiniteTransition(label = "rec")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Restart),
        label = "phase",
    )
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "pulse",
    )

    Column(
        Modifier.fillMaxSize().padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Recording voice note", style = MaterialTheme.typography.headlineSmall)
        Text(
            "${book.title} · ${book.author}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 8.dp),
        )
        Row(
            Modifier.height(120.dp).padding(top = 30.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            for (i in 0 until 26) {
                val base = 12f + abs(sin(i * 1.3f)) * 70f
                val h = base * (0.5f + 0.5f * abs(sin(phase + i * 0.7f)))
                Box(
                    Modifier
                        .width(4.dp)
                        .height(h.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(MaterialTheme.colorScheme.tertiary),
                )
            }
        }
        Text(
            "${seconds / 60}:${(seconds % 60).toString().padStart(2, '0')}",
            style = MaterialTheme.typography.headlineMedium,
            fontSize = 38.sp,
            modifier = Modifier.padding(top = 10.dp),
        )
        Row(
            Modifier.padding(top = 30.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            listOf("🔒 Private", "👥 Friends", "🌍 Public").forEach { option ->
                val key = option.substringAfter(" ")
                SoftChip(option, selected = visibility == key) { visibility = key }
            }
        }
        Box(
            Modifier
                .padding(top = 38.dp)
                .size(84.dp)
                .graphicsLayer { scaleX = pulse; scaleY = pulse }
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary)
                .clickable { onDone() },
            contentAlignment = Alignment.Center,
        ) {
            Text("■", fontSize = 24.sp, color = MaterialTheme.colorScheme.onSecondary)
        }
        Text(
            "Cancel",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 20.dp).clickable { onDone() },
        )
    }
}
