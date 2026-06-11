package com.folio.app.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.folio.app.data.Books
import com.folio.app.data.OnboardingGenres
import com.folio.app.data.Users
import com.folio.app.ui.components.BookCover
import com.folio.app.ui.components.UserAvatar

/**
 * Warm, unhurried onboarding. Every screen is skippable — users can finish
 * setup later from their profile. No dark patterns, ever.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    var step by remember { mutableIntStateOf(0) }
    val selectedGenres = remember { mutableStateOf(setOf<String>()) }
    val selectedBooks = remember { mutableStateOf(setOf<Int>()) }
    val followed = remember { listOf(false, false, false, false).toMutableStateList() }

    Crossfade(targetState = step, label = "onboarding") { current ->
        Column(Modifier.fillMaxSize().padding(28.dp)) {
            when (current) {
                0 -> {
                    Spacer(Modifier.weight(0.7f))
                    Box(
                        Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(150.dp)
                            .clip(RoundedCornerShape(48.dp))
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.tertiaryContainer,
                                        MaterialTheme.colorScheme.primaryContainer,
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("📚", fontSize = 56.sp)
                    }
                    Text(
                        "Your books.\nYour voice.\nYour people.",
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 30.sp,
                        lineHeight = 36.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 34.dp),
                    )
                    Text(
                        "Folio is a quiet corner of the internet for the books you love — track them, talk about them, and wander to your next one.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    )
                    Spacer(Modifier.weight(1f))
                    StepFooter(0, "Get started", onNext = { step = 1 }, onSkip = onFinish)
                }
                1 -> {
                    Text("What do you love to read?", style = MaterialTheme.typography.headlineSmall, fontSize = 26.sp)
                    Text(
                        "Pick a few — this gently shapes your discovery map. You can always change these later.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 10.dp),
                    )
                    FlowRow(
                        Modifier.padding(top = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(9.dp),
                        verticalArrangement = Arrangement.spacedBy(9.dp),
                    ) {
                        OnboardingGenres.forEach { genre ->
                            val selected = genre in selectedGenres.value
                            Text(
                                genre,
                                fontSize = 13.sp,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        if (selected) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surface
                                    )
                                    .border(
                                        1.dp,
                                        if (selected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.outline,
                                        RoundedCornerShape(50),
                                    )
                                    .clickable {
                                        selectedGenres.value =
                                            if (selected) selectedGenres.value - genre
                                            else selectedGenres.value + genre
                                    }
                                    .padding(horizontal = 17.dp, vertical = 10.dp),
                            )
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    StepFooter(1, "Continue", onNext = { step = 2 }, onSkip = { step = 2 })
                }
                2 -> {
                    Text("Add a few books you've read", style = MaterialTheme.typography.headlineSmall, fontSize = 26.sp)
                    Text(
                        "Three to five is plenty. They'll seed your shelf and light up your map.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 10.dp),
                    )
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(vertical = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        items(Books.take(9)) { book ->
                            val selected = book.id in selectedBooks.value
                            Box {
                                BookCover(
                                    book,
                                    Modifier
                                        .fillMaxWidth()
                                        .height(150.dp)
                                        .then(
                                            if (selected) Modifier.border(
                                                2.5.dp,
                                                MaterialTheme.colorScheme.primary,
                                                RoundedCornerShape(10.dp),
                                            ) else Modifier
                                        ),
                                    titleSize = 12.sp,
                                    onClick = {
                                        selectedBooks.value =
                                            if (selected) selectedBooks.value - book.id
                                            else selectedBooks.value + book.id
                                    },
                                )
                                if (selected) {
                                    Box(
                                        Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text("✓", fontSize = 11.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                    StepFooter(2, "Continue", onNext = { step = 3 }, onSkip = { step = 3 })
                }
                else -> {
                    Text("Find your people", style = MaterialTheme.typography.headlineSmall, fontSize = 26.sp)
                    Text(
                        "Totally optional. Folio never reads your contacts without asking — and never asks twice.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 10.dp),
                    )
                    Column(Modifier.padding(top = 18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf("maya", "theo", "priya", "sam").forEachIndexed { i, userId ->
                            val user = Users.getValue(userId)
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                                    .padding(horizontal = 14.dp, vertical = 11.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                UserAvatar(user)
                                Text(
                                    user.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.weight(1f).padding(start = 12.dp),
                                )
                                Text(
                                    if (followed[i]) "Following ✓" else "Follow",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (followed[i]) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    else MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(
                                            if (followed[i]) MaterialTheme.colorScheme.surfaceVariant
                                            else MaterialTheme.colorScheme.primaryContainer
                                        )
                                        .clickable { followed[i] = !followed[i] }
                                        .padding(horizontal = 16.dp, vertical = 7.dp),
                                )
                            }
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    StepFooter(3, "Step inside", onNext = onFinish, onSkip = onFinish, skipLabel = "Maybe later")
                }
            }
        }
    }
}

@Composable
private fun StepFooter(
    step: Int,
    nextLabel: String,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    skipLabel: String = "Skip for now",
) {
    Column(Modifier.fillMaxWidth().padding(top = 20.dp)) {
        Row(
            Modifier.align(Alignment.CenterHorizontally).padding(bottom = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            for (i in 0 until 4) {
                Box(
                    Modifier
                        .width(if (i == step) 20.dp else 7.dp)
                        .height(7.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (i == step) MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.outline
                        ),
                )
            }
        }
        Button(
            onClick = onNext,
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth().height(54.dp),
        ) {
            Text(nextLabel, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }
        Text(
            skipLabel,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 10.dp)
                .clickable { onSkip() },
        )
    }
}
