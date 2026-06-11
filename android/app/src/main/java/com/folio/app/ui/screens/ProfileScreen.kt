package com.folio.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.folio.app.data.BannerSwatches
import com.folio.app.data.Books
import com.folio.app.data.Shelves
import com.folio.app.ui.components.BookCover
import com.folio.app.ui.components.SectionLabel
import com.folio.app.ui.theme.Sage

private val DefaultBannerStart = Color(0xFF4E4668)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onBack: () -> Unit, onOpenBook: (Int) -> Unit) {
    var bannerColor by remember { mutableStateOf(DefaultBannerStart) }
    var bannerUri by remember { mutableStateOf<Uri?>(null) }
    var showPicker by remember { mutableStateOf(false) }

    // Native photo picker via GetContent — fills the banner with a center-crop.
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            bannerUri = uri
            showPicker = false
        }
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        // Banner — color swatch or photo, avatar pinned bottom-left with white ring.
        Box(Modifier.fillMaxWidth().height(170.dp)) {
            Box(
                Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(bannerColor, bannerColor.copy(red = (bannerColor.red + 0.15f).coerceAtMost(1f)))
                        )
                    ),
            ) {
                if (bannerUri != null) {
                    AsyncImage(
                        model = bannerUri,
                        contentDescription = "Profile banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                Box(
                    Modifier
                        .align(Alignment.TopStart)
                        .padding(14.dp)
                        .size(40.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black.copy(alpha = 0.35f))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("←", color = Color.White, fontSize = 18.sp)
                }
                Text(
                    "✎ Edit profile",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.Black.copy(alpha = 0.4f))
                        .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(50))
                        .clickable { showPicker = true }
                        .padding(horizontal = 15.dp, vertical = 7.dp),
                )
            }
            // Avatar overlapping the banner edge, white border ring.
            Box(
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 20.dp)
                    .offset(y = 34.dp)
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(Sage)
                    .clickable { photoPicker.launch("image/*") },
                contentAlignment = Alignment.Center,
            ) {
                Text("JO", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 26.sp)
            }
        }
        Spacer(Modifier.height(44.dp))
        Text(
            "Joana",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Text(
            "Reading slowly on purpose. Voice-noting every book that makes me put the kettle on. 🌿",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
        )
        Text(
            "128 followers · 97 following",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
        )

        // Stats.
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            StatCard("14", "This year", Modifier.weight(1f))
            StatCard("${Shelves.read.size + 38}", "All-time", Modifier.weight(1f))
            StatCard("4.2", "Avg rating", Modifier.weight(1f))
        }

        SectionLabel("Favourite genres")
        Row(
            Modifier.padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            listOf("Literary", "Nature", "Quiet sci-fi", "Cozy").forEach { genre ->
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

        ShelfRail("Currently reading", Shelves.currentlyReading, onOpenBook)
        ShelfRail("Read", Shelves.read, onOpenBook)
        ShelfRail("Want to read", Shelves.wantToRead, onOpenBook)
        Spacer(Modifier.height(24.dp))
    }

    if (showPicker) {
        ModalBottomSheet(onDismissRequest = { showPicker = false }) {
            Column(Modifier.padding(horizontal = 20.dp).padding(bottom = 30.dp)) {
                Text("Make it yours", style = MaterialTheme.typography.titleLarge)
                Text(
                    "Pick a banner color, or use a photo from your camera roll.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    BannerSwatches.take(5).forEach { swatch ->
                        Box(
                            Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(swatch)
                                .border(
                                    if (bannerColor == swatch && bannerUri == null) 3.dp else 0.dp,
                                    MaterialTheme.colorScheme.secondary,
                                    CircleShape,
                                )
                                .clickable {
                                    bannerColor = swatch
                                    bannerUri = null
                                },
                        )
                    }
                    Box(
                        Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { photoPicker.launch("image/*") },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("📷", fontSize = 17.sp)
                    }
                }
                Text(
                    "Reset to default",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .clickable {
                            bannerColor = DefaultBannerStart
                            bannerUri = null
                            showPicker = false
                        },
                )
            }
        }
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
            .padding(vertical = 13.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, style = MaterialTheme.typography.headlineSmall, fontSize = 21.sp)
        Text(
            label.uppercase(),
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.6.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        )
    }
}

@Composable
private fun ShelfRail(title: String, bookIds: List<Int>, onOpenBook: (Int) -> Unit) {
    SectionLabel(title)
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(bookIds) { id ->
            BookCover(
                Books[id],
                Modifier.width(88.dp).height(130.dp),
                titleSize = 13.sp,
                onClick = { onOpenBook(id) },
            )
        }
    }
}
