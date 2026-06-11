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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.folio.app.data.Books
import com.folio.app.data.FeedPost
import com.folio.app.data.FeedPosts
import com.folio.app.data.Users
import com.folio.app.ui.components.BookCover
import com.folio.app.ui.components.RatingStars
import com.folio.app.ui.components.SoftChip
import com.folio.app.ui.components.UserAvatar
import com.folio.app.ui.components.VoicePill

@Composable
fun FeedScreen(
    onOpenBook: (Int) -> Unit,
    onOpenProfile: () -> Unit,
    onToggleTheme: () -> Unit,
) {
    var friendsOnly by remember { mutableStateOf(true) }
    val posts = if (friendsOnly) FeedPosts.filter { it.friendsOnly } else FeedPosts

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Folio", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.weight(1f))
            IconChip("◐", onClick = onToggleTheme)
            Spacer(Modifier.width(10.dp))
            UserAvatar(Users.getValue("me"), onClick = onOpenProfile)
        }
        Row(
            Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SoftChip("Friends", selected = friendsOnly) { friendsOnly = true }
            SoftChip("Everyone", selected = !friendsOnly) { friendsOnly = false }
        }
        LazyColumn(
            contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(posts) { post -> FeedCard(post, onOpenBook) }
        }
    }
}

@Composable
fun IconChip(glyph: String, onClick: () -> Unit) {
    Box(
        Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(glyph, fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun FeedCard(post: FeedPost, onOpenBook: (Int) -> Unit) {
    val user = Users.getValue(post.userId)
    val book = Books[post.bookId]
    var rating by remember { mutableIntStateOf(post.rating) }
    var expanded by remember { mutableStateOf(false) }

    Column(
        Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            UserAvatar(user)
            Column(Modifier.weight(1f).padding(start = 10.dp)) {
                Text(user.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    "finished a book · ${post.ago} ago",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                )
            }
        }
        Row(Modifier.padding(top = 12.dp)) {
            BookCover(
                book,
                Modifier.width(86.dp).height(126.dp),
                titleSize = 14.sp,
                onClick = { onOpenBook(book.id) },
            )
            Column(Modifier.padding(start = 14.dp).weight(1f)) {
                Text(
                    book.title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.clickable { onOpenBook(book.id) },
                )
                Text(
                    book.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                RatingStars(
                    rating,
                    Modifier.padding(vertical = 6.dp),
                    onRate = { rating = it },
                )
                Text(
                    post.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = if (expanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable { expanded = !expanded },
                )
            }
        }
        if (post.voice != null) {
            VoicePill(post.voice, Modifier.padding(top = 10.dp))
        }
        Row(
            Modifier.padding(top = 13.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            post.reactions.forEach { (emoji, count) -> ReactionChip(emoji, count) }
            SoftChip("💬 ${post.comments}") {}
        }
    }
}

@Composable
private fun ReactionChip(emoji: String, baseCount: Int) {
    var hit by remember { mutableStateOf(false) }
    SoftChip(
        "$emoji ${baseCount + if (hit) 1 else 0}",
        selected = hit,
        onClick = { hit = !hit },
    )
}

@Composable
fun DiscoverScreen(onOpenBook: (Int) -> Unit) {
    var query by remember { mutableStateOf("") }
    val results = Books.filter {
        query.isBlank() || it.title.contains(query, ignoreCase = true) ||
            it.author.contains(query, ignoreCase = true)
    }
    Column(Modifier.fillMaxSize()) {
        Text(
            "Discover",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
        )
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search titles, authors…") },
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            ),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        )
        if (results.isEmpty()) {
            com.folio.app.ui.components.EmptyState(
                "🔍", "Nothing yet.",
                "Try an author, or wander the map from any book you love.",
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(results) { book ->
                    Column {
                        BookCover(
                            book,
                            Modifier.fillMaxWidth().height(160.dp),
                            titleSize = 13.sp,
                            onClick = { onOpenBook(book.id) },
                        )
                        Text(
                            book.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 6.dp),
                        )
                    }
                }
            }
        }
    }
}
