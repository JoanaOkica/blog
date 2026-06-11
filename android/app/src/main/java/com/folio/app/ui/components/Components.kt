package com.folio.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.folio.app.data.Book
import com.folio.app.data.User
import com.folio.app.data.VoiceNote
import com.folio.app.ui.theme.BookSerif
import com.folio.app.ui.theme.Gold
import kotlin.math.abs
import kotlin.math.sin

/** A book cover rendered from the book's two palette colors — no image assets needed. */
@Composable
fun BookCover(
    book: Book,
    modifier: Modifier = Modifier,
    titleSize: TextUnit = 14.sp,
    onClick: (() -> Unit)? = null,
) {
    val base = if (onClick != null) modifier.clip(RoundedCornerShape(10.dp)).clickable { onClick() }
    else modifier.clip(RoundedCornerShape(10.dp))
    Box(
        base
            .background(Brush.linearGradient(listOf(book.c1, book.c2)))
            .drawBehind {
                // spine highlight
                drawLine(
                    Color.White.copy(alpha = 0.25f),
                    Offset(12f, 0f), Offset(12f, size.height), strokeWidth = 3f
                )
            }
            .padding(horizontal = 9.dp, vertical = 9.dp)
    ) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Text(
                book.title,
                fontFamily = BookSerif,
                fontWeight = FontWeight.SemiBold,
                fontSize = titleSize,
                lineHeight = titleSize * 1.15,
                color = Color.White.copy(alpha = 0.95f),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                book.author,
                fontSize = titleSize * 0.62,
                color = Color.White.copy(alpha = 0.8f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun UserAvatar(user: User, size: Dp = 38.dp, onClick: (() -> Unit)? = null) {
    val base = if (onClick != null) Modifier.clickable { onClick() } else Modifier
    Box(
        base.size(size).clip(CircleShape).background(user.color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            user.initials,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = (size.value * 0.36f).sp,
        )
    }
}

@Composable
fun RatingStars(
    rating: Int,
    modifier: Modifier = Modifier,
    size: TextUnit = 16.sp,
    onRate: ((Int) -> Unit)? = null,
) {
    Row(modifier) {
        for (i in 1..5) {
            val starModifier =
                if (onRate != null) Modifier.clickable { onRate(i) }.padding(end = 2.dp)
                else Modifier.padding(end = 2.dp)
            Text(
                "★",
                fontSize = size,
                color = if (i <= rating) Gold else MaterialTheme.colorScheme.outline,
                modifier = starModifier,
            )
        }
    }
}

/**
 * A playable voice-note pill: avatar-sized play button, waveform, duration —
 * with an optional chapter badge. Playback is simulated with a waveform pulse.
 */
@Composable
fun VoicePill(note: VoiceNote, modifier: Modifier = Modifier) {
    var playing by remember { mutableStateOf(false) }
    val barHeights = remember { List(18) { 6f + abs(sin(it * 1.7f)) * 14f } }
    val transition = rememberInfiniteTransition(label = "wave")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(1100, easing = LinearEasing), RepeatMode.Restart),
        label = "phase",
    )
    Row(
        modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .clickable { playing = !playing }
            .padding(start = 6.dp, end = 14.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            Modifier.size(26.dp).clip(CircleShape).background(MaterialTheme.colorScheme.tertiary),
            contentAlignment = Alignment.Center,
        ) {
            Text(if (playing) "❚❚" else "▶", color = Color.White, fontSize = 9.sp)
        }
        if (note.chapter != null) {
            Text(
                note.chapter,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.tertiary)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            )
        }
        Row(
            Modifier.height(22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            barHeights.forEachIndexed { i, h ->
                val hh = if (playing) h * (0.6f + 0.55f * abs(sin(phase + i * 0.8f))) else h
                Box(
                    Modifier
                        .width(2.5.dp)
                        .height(hh.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.tertiary)
                )
            }
        }
        Text(
            note.duration,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.tertiary,
        )
    }
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        modifier = modifier.padding(horizontal = 20.dp, vertical = 12.dp),
    )
}

/** Inviting empty states — illustrated, friendly copy. Never harsh. */
@Composable
fun EmptyState(emoji: String, title: String, body: String, modifier: Modifier = Modifier) {
    Column(
        modifier.padding(horizontal = 40.dp, vertical = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(emoji, fontSize = 44.sp)
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 12.dp),
        )
        Text(
            body,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

/** A small rounded chip-style button used for reactions and soft actions. */
@Composable
fun SoftChip(
    text: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: () -> Unit = {},
) {
    Box(
        modifier
            .clip(RoundedCornerShape(50))
            .background(
                if (selected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp),
    ) {
        Text(
            text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
