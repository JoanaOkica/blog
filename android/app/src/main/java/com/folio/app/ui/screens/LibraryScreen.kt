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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.folio.app.data.Books
import com.folio.app.data.Folder
import com.folio.app.data.Folders
import com.folio.app.data.Shelves
import com.folio.app.ui.components.BookCover
import com.folio.app.ui.components.EmptyState
import com.folio.app.ui.components.SectionLabel

@Composable
fun LibraryScreen(onOpenBook: (Int) -> Unit) {
    var openFolderIndex by remember { mutableStateOf<Int?>(null) }
    var showNewFolderDialog by remember { mutableStateOf(false) }

    val folderIndex = openFolderIndex
    if (folderIndex != null && folderIndex < Folders.size) {
        FolderDetail(
            folder = Folders[folderIndex],
            onBack = { openFolderIndex = null },
            onOpenBook = onOpenBook,
        )
        return
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text(
            "Library",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
        )
        SectionLabel("Currently reading")
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(Shelves.currentlyReading) { id ->
                BookCover(
                    Books[id],
                    Modifier.width(88.dp).height(130.dp),
                    titleSize = 13.sp,
                    onClick = { onOpenBook(id) },
                )
            }
        }
        SectionLabel("Folders")
        Folders.forEachIndexed { index, folder ->
            FolderRow(folder) { openFolderIndex = index }
        }
        // New folder
        Box(
            Modifier
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                .clickable { showNewFolderDialog = true }
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "＋  New folder",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            )
        }
        Spacer(Modifier.height(24.dp))
    }

    if (showNewFolderDialog) {
        var name by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showNewFolderDialog = false },
            title = { Text("Name your folder", style = MaterialTheme.typography.titleLarge) },
            text = {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("e.g. Beach reads, To gift…") },
                    singleLine = true,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (name.isNotBlank()) {
                        Folders.add(Folder(name.trim(), mutableStateListOf(), "Private"))
                    }
                    showNewFolderDialog = false
                }) { Text("Create") }
            },
            dismissButton = {
                TextButton(onClick = { showNewFolderDialog = false }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun FolderRow(folder: Folder, onClick: () -> Unit) {
    Row(
        Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Preview mosaic of up to 3 covers.
        Row {
            folder.books.take(3).forEachIndexed { i, bookId ->
                BookCover(
                    Books[bookId],
                    Modifier
                        .offset(x = (-20 * i).dp)
                        .width(42.dp)
                        .height(62.dp),
                    titleSize = 8.sp,
                )
            }
        }
        val overlapCorrection = (20 * (folder.books.take(3).size - 1).coerceAtLeast(0)).dp
        Column(Modifier.weight(1f).padding(start = 12.dp).offset(x = -overlapCorrection)) {
            Text(folder.name, style = MaterialTheme.typography.titleMedium)
            Text(
                folder.visibility,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            )
        }
        if (folder.books.size > 3) {
            Text(
                "+${folder.books.size - 3}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(horizontal = 9.dp, vertical = 4.dp),
            )
        }
        Text(
            "›",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Composable
private fun FolderDetail(folder: Folder, onBack: () -> Unit, onOpenBook: (Int) -> Unit) {
    var editMode by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconChip("←", onClick = onBack)
            Text(
                folder.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 12.dp),
            )
            Spacer(Modifier.weight(1f))
            IconChip(if (editMode) "✓" else "✎", onClick = { editMode = !editMode })
            Spacer(Modifier.width(8.dp))
            // Cycle visibility: public → friends only → private.
            IconChip("⋯", onClick = {
                folder.visibility = when (folder.visibility) {
                    "Public" -> "Friends only"
                    "Friends only" -> "Private"
                    else -> "Public"
                }
            })
        }
        if (folder.books.isEmpty()) {
            EmptyState("🕯", "This folder is waiting.", "Add a few books to give it a soul.")
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            itemsIndexed(folder.books) { index, bookId ->
                Box {
                    Column {
                        BookCover(
                            Books[bookId],
                            Modifier.fillMaxWidth().height(160.dp),
                            titleSize = 13.sp,
                            onClick = { if (!editMode) onOpenBook(bookId) },
                        )
                        Text(
                            Books[bookId].title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 6.dp),
                        )
                    }
                    if (editMode) {
                        // Remove pulls the book out of the folder — never deletes it.
                        Box(
                            Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 6.dp, y = (-6).dp)
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondary)
                                .clickable { folder.books.removeAt(index) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("✕", fontSize = 10.sp, color = Color.White)
                        }
                    }
                }
            }
            item {
                // "Add book" tile — last item in every folder grid.
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                        .clickable {
                            val candidates = Books.map { it.id }.filter { it !in folder.books }
                            if (candidates.isNotEmpty()) folder.books.add(candidates.random())
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("＋", fontSize = 22.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                        Text("Add book", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}
