package com.folio.app.ui.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.folio.app.data.Book
import com.folio.app.data.Books
import com.folio.app.data.Connection
import com.folio.app.data.ConnectionType
import com.folio.app.data.connectionsFor
import com.folio.app.ui.theme.BookSerif
import com.folio.app.ui.theme.MapBackground
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class MapNode(
    val book: Book,
    val pos: Offset,
    val radius: Float,
    val phase: Float,
    val conn: Connection?, // null for the center node
)

private class Particle(var t: Float, val toIndex: Int, val speed: Float, val color: Color)

/**
 * The living constellation at the bottom of every book detail page.
 * Breathing nodes (per-node sine phase), pulsing center ring, particles
 * travelling along edges, filter chips, drag-to-pan, pinch-to-zoom, and a
 * slide-up card that lets you hop to any connected book's own map.
 */
@Composable
fun NeuralMapSection(book: Book, onOpenBook: (Int) -> Unit) {
    val density = LocalDensity.current
    val connections = remember(book.id) { connectionsFor(book.id) }
    var filter by remember(book.id) { mutableStateOf<ConnectionType?>(null) }
    var selected by remember(book.id) { mutableStateOf<MapNode?>(null) }
    var scale by remember(book.id) { mutableFloatStateOf(1f) }
    var pan by remember(book.id) { mutableStateOf(Offset.Zero) }
    var time by remember { mutableFloatStateOf(0f) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    val particles = remember(book.id) { mutableStateListOf<Particle>() }
    val intro = remember(book.id) { Animatable(0f) }

    // Smoothly fade nodes when switching filters — no hard cuts.
    val nodeAlphas = connections.map { c ->
        animateFloatAsState(
            targetValue = if (filter == null || c.type == filter) 1f else 0.12f,
            animationSpec = tween(400),
            label = "nodeAlpha",
        ).value
    }

    val nodes = remember(book.id, canvasSize) {
        if (canvasSize == Size.Zero) emptyList()
        else buildNodes(book, connections, canvasSize, density)
    }

    LaunchedEffect(book.id) { intro.animateTo(1f, tween(1400)) }

    // Frame clock: drives breathing, pulsing, and particle motion.
    LaunchedEffect(book.id) {
        val start = withFrameNanos { it }
        while (true) {
            withFrameNanos { now -> time = (now - start) / 1_000_000_000f }
            val iterator = particles.listIterator()
            while (iterator.hasNext()) {
                val p = iterator.next()
                p.t += p.speed
                if (p.t >= 1f) iterator.remove()
            }
            // Particles spawn randomly to keep the canvas alive even with no interaction.
            if (particles.size < 36 && connections.isNotEmpty() && Random.nextFloat() < 0.07f) {
                val toIndex = 1 + Random.nextInt(connections.size)
                particles.add(
                    Particle(
                        t = 0f,
                        toIndex = toIndex,
                        speed = 0.0035f + Random.nextFloat() * 0.004f,
                        color = Books[connections[toIndex - 1].to].c2,
                    )
                )
            }
        }
    }

    val textMeasurer = rememberTextMeasurer()

    Box(
        Modifier
            .fillMaxWidth()
            .height(620.dp)
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(MapBackground)
    ) {
        Canvas(
            Modifier
                .fillMaxSize()
                .onSizeChanged { canvasSize = Size(it.width.toFloat(), it.height.toFloat()) }
                .pointerInput(book.id) {
                    detectTapGestures { tap ->
                        if (nodes.isEmpty()) return@detectTapGestures
                        val pivot = Offset(size.width / 2f, size.height / 2f)
                        val world = (tap - pan - pivot) / scale + pivot
                        val hit = nodes.lastOrNull { n ->
                            (world - n.pos).getDistance() < n.radius + 26f
                        }
                        selected = if (hit?.conn != null) hit else null
                    }
                }
                .pointerInput(book.id) {
                    detectTransformGestures { _, panDelta, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.5f, 2.4f)
                        pan += panDelta
                    }
                }
        ) {
            // Faint twinkling starfield.
            for (i in 0 until 46) {
                val sx = (i * 97.3f) % size.width
                val sy = (i * 61.7f) % size.height
                val twinkle = 0.10f + 0.08f * ((1f + sin(time * 0.7f + i)) / 2f)
                drawRect(
                    Color(0xFFCFD2FF).copy(alpha = twinkle),
                    topLeft = Offset(sx, sy),
                    size = Size(2.2f, 2.2f),
                )
            }
            if (nodes.isEmpty()) return@Canvas
            val pivot = Offset(size.width / 2f, size.height / 2f)
            withTransform({
                translate(pan.x, pan.y)
                scale(scale, scale, pivot)
            }) {
                val center = nodes[0]
                fun alphaFor(index: Int): Float =
                    if (index == 0) 1f else nodeAlphas.getOrElse(index - 1) { 1f }

                // Edges: thin and low-opacity by default; bright when selected.
                nodes.forEachIndexed { i, n ->
                    if (i == 0) return@forEachIndexed
                    val a = alphaFor(i) * intro.value
                    val isSelected = selected === n
                    drawLine(
                        color = if (isSelected) n.book.c2 else Color(0xFFB4B4DC),
                        start = center.pos,
                        end = n.pos,
                        strokeWidth = if (isSelected) 4f else 1.6f,
                        alpha = (if (isSelected) 0.7f else 0.13f) * a,
                    )
                }

                // Particles — readers moving between books.
                particles.forEach { p ->
                    val target = nodes.getOrNull(p.toIndex) ?: return@forEach
                    val a = alphaFor(p.toIndex)
                    if (a < 0.5f) return@forEach
                    val pos = center.pos + (target.pos - center.pos) * p.t
                    val pa = (sin(p.t * PI.toFloat()) * 0.9f).coerceIn(0f, 1f)
                    drawCircle(p.color, radius = 3.5f, center = pos, alpha = pa)
                    drawCircle(p.color, radius = 8f, center = pos, alpha = pa * 0.25f)
                }

                // Nodes: each breathes on its own phase so they never sync.
                nodes.forEachIndexed { i, n ->
                    val a = (alphaFor(i) * intro.value).coerceIn(0f, 1f)
                    if (a <= 0.01f) return@forEachIndexed
                    val breathe = 1f + 0.06f * sin(time * 1.4f + n.phase)
                    val r = n.radius * breathe * (0.4f + 0.6f * intro.value)
                    // glow halo
                    drawCircle(
                        Brush.radialGradient(
                            listOf(n.book.c2.copy(alpha = 0.4f * a), Color.Transparent),
                            center = n.pos, radius = r * 2.4f,
                        ),
                        radius = r * 2.4f, center = n.pos,
                    )
                    // pulsing ring on the center node and the selected node
                    if (i == 0 || selected === n) {
                        val ringR = r + 14f + 8f * sin(time * 2f + n.phase)
                        drawCircle(
                            n.book.c2, radius = ringR, center = n.pos,
                            alpha = 0.5f * a, style = Stroke(width = 2.5f),
                        )
                    }
                    // body
                    drawCircle(
                        Brush.radialGradient(
                            listOf(n.book.c2, n.book.c1),
                            center = n.pos - Offset(r * 0.3f, r * 0.35f),
                            radius = r * 1.8f,
                        ),
                        radius = r, center = n.pos, alpha = a,
                    )
                    drawCircle(
                        Color.White.copy(alpha = 0.35f * a),
                        radius = r, center = n.pos, style = Stroke(width = 2f),
                    )
                    // label
                    if (a > 0.5f) {
                        val label = n.book.title.let { if (it.length > 18) it.take(17) + "…" else it }
                        val layout = textMeasurer.measure(
                            label,
                            TextStyle(
                                fontSize = if (i == 0) 13.sp else 11.sp,
                                fontWeight = if (i == 0) FontWeight.SemiBold else FontWeight.Medium,
                                color = Color(0xFFEBE9F5).copy(alpha = 0.85f * a),
                            ),
                        )
                        drawText(
                            layout,
                            topLeft = Offset(n.pos.x - layout.size.width / 2f, n.pos.y + r + 12f),
                        )
                    }
                }
            }
        }

        // Filter chips: isolate by connection type, animated — no hard cuts.
        Row(
            Modifier
                .align(Alignment.TopStart)
                .padding(14.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MapChip("All", filter == null) { filter = null; selected = null }
            ConnectionType.entries.forEach { t ->
                MapChip(t.label, filter == t) { filter = t; selected = null }
            }
        }

        Text(
            "drag to wander · pinch to zoom · tap a star",
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.32f),
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 14.dp),
        )

        // Slide-up card for the tapped node.
        var lastCard by remember { mutableStateOf<MapNode?>(null) }
        if (selected != null) lastCard = selected
        AnimatedVisibility(
            visible = selected != null,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter).padding(12.dp),
        ) {
            val node = lastCard
            val conn = node?.conn
            if (node != null && conn != null) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xE61C1B26))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(22.dp))
                        .padding(18.dp),
                ) {
                    Text(
                        node.book.title,
                        fontFamily = BookSerif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = Color(0xFFEDEAF5),
                    )
                    Text(
                        "${node.book.author} · ${node.book.year}",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.55f),
                    )
                    Row(
                        Modifier.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            conn.type.label.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = conn.type.tint,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(conn.type.tint.copy(alpha = 0.15f))
                                .border(1.dp, conn.type.tint.copy(alpha = 0.35f), RoundedCornerShape(50))
                                .padding(horizontal = 9.dp, vertical = 3.dp),
                        )
                        Text(
                            "${(conn.strength * 100).toInt()}% LINKED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color.White.copy(alpha = 0.08f))
                                .padding(horizontal = 9.dp, vertical = 3.dp),
                        )
                    }
                    Text(
                        conn.reason,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = Color.White.copy(alpha = 0.78f),
                    )
                    Button(
                        onClick = {
                            val id = node.book.id
                            selected = null
                            onOpenBook(id)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEDE9DF),
                            contentColor = Color(0xFF16151C),
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                    ) {
                        Text("View book →", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun MapChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        label,
        fontSize = 12.sp,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
        color = if (selected) Color(0xFF16151C) else Color.White.copy(alpha = 0.65f),
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) Color.White.copy(alpha = 0.92f) else Color.White.copy(alpha = 0.06f))
            .border(1.dp, Color.White.copy(alpha = if (selected) 0f else 0.16f), RoundedCornerShape(50))
            .clickable { onClick() }
            .padding(horizontal = 15.dp, vertical = 8.dp),
    )
}

private fun buildNodes(
    book: Book,
    connections: List<Connection>,
    size: Size,
    density: Density,
): List<MapNode> = with(density) {
    val cx = size.width / 2f
    val cy = size.height / 2f - 30.dp.toPx()
    val out = mutableListOf(MapNode(book, Offset(cx, cy), 30.dp.toPx(), 0f, null))
    connections.forEachIndexed { i, c ->
        val angle = Math.toRadians(
            -90.0 + i * (360.0 / connections.size) + (book.id * 23) % 40
        ).toFloat()
        val orbit = (105 + (i % 2) * 62 + ((book.id + i) % 3) * 18).dp.toPx()
        out += MapNode(
            book = Books[c.to],
            pos = Offset(cx + cos(angle) * orbit, cy + sin(angle) * orbit),
            radius = (13f + c.strength * 15f).dp.toPx(),
            phase = i * 1.13f + book.id,
            conn = c,
        )
    }
    out
}
