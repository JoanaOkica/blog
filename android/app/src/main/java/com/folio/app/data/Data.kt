package com.folio.app.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color

data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val year: Int,
    val genres: List<String>,
    val c1: Color,
    val c2: Color,
    val synopsis: String,
)

data class User(val id: String, val name: String, val initials: String, val color: Color)

data class VoiceNote(val duration: String, val chapter: String? = null)

data class FeedPost(
    val userId: String,
    val bookId: Int,
    val rating: Int,
    val ago: String,
    val friendsOnly: Boolean,
    val text: String,
    val voice: VoiceNote?,
    val reactions: Map<String, Int>,
    val comments: Int,
)

enum class ConnectionType(val label: String, val tint: Color) {
    THEME("Theme", Color(0xFFA99EC9)),
    STYLE("Writing style", Color(0xFF94C2A0)),
    READERS("Reader overlap", Color(0xFFE0A877)),
}

data class Connection(val to: Int, val strength: Float, val type: ConnectionType, val reason: String)

data class Folder(
    val name: String,
    val books: SnapshotStateList<Int>,
    var visibility: String,
)

data class Community(
    val name: String,
    val description: String,
    val c1: Color,
    val c2: Color,
    val members: Int,
    val inviteOnly: Boolean,
    val shelf: List<Int>,
)

data class Message(
    val fromMe: Boolean,
    val at: String,
    val text: String? = null,
    val bookId: Int? = null,
    val voice: VoiceNote? = null,
)

data class Chat(
    val userId: String,
    val time: String,
    var unread: Boolean,
    val messages: SnapshotStateList<Message>,
)

val Books = listOf(
    Book(0, "The Lantern Tide", "Mara Ellison", 2021, listOf("Literary", "Coastal"), Color(0xFF3E5C66), Color(0xFF7FA8A4),
        "On a fading island off the Atlantic coast, a lighthouse keeper's daughter inherits a logbook that seems to record storms before they happen. As the village prepares for one last winter, she must decide whether knowing the future is a gift or a quiet kind of grief. A slow, luminous novel about tides, memory, and the people who stay."),
    Book(1, "Salt & Cedar", "June Okafor", 2019, listOf("Romance", "Slow burn"), Color(0xFF9C6B4F), Color(0xFFD9A47E),
        "A carpenter restoring a derelict seaside chapel and the botanist cataloguing the dune behind it keep borrowing each other's tools, then each other's evenings. A warm, unhurried romance about building things that last — told over four seasons and one stubborn rose garden."),
    Book(2, "The Glass Orchard", "T. R. Vance", 2023, listOf("Mystery", "Gothic"), Color(0xFF4E4668), Color(0xFF8B80A8),
        "When archivist Nell Prior returns to her family's greenhouse estate, she finds every pane etched with initials that match a fifty-year-old missing persons list. The deeper she digs into the orchard's records, the more the house seems to be keeping its own. Atmospheric, precise, and quietly chilling."),
    Book(3, "Smallwood", "Petra Lindqvist", 2018, listOf("Literary", "Family"), Color(0xFF5C6B4E), Color(0xFF9BAD86),
        "Three generations of the Hale family run the last general store in a logging town that the highway forgot. Told in interleaving voices across sixty years, Smallwood is a tender study of inheritance — of debts, recipes, grudges, and the forest that outlasts them all."),
    Book(4, "A Theory of Birds", "Anil Deshpande", 2022, listOf("Sci-fi", "Speculative"), Color(0xFF2F4858), Color(0xFF6E9AAF),
        "In a near-future where migratory birds have begun flying new, impossible routes, ornithologist Ira Menon discovers the patterns spell coordinates. What waits at the convergence point will change how humanity understands navigation, language, and grief. Cerebral and unexpectedly moving."),
    Book(5, "Night Swimming", "Carmen Reyes", 2020, listOf("Literary", "Coming of age"), Color(0xFF1F3A5F), Color(0xFF5C7FA8),
        "Over one humid August, four teenagers sneak into the municipal pool after closing and tell each other everything except the truth. Twenty years later, one of them writes it all down. A novel about the versions of ourselves we leave in the water."),
    Book(6, "The Cartographer's Daughter", "H. G. Bellamy", 2017, listOf("Historical", "Adventure"), Color(0xFF7A5C3E), Color(0xFFC2A878),
        "Lisbon, 1755. After the earthquake destroys her father's map archive, Inês Almeida sets out to redraw the city from memory — and uncovers the route to a colony that every official chart was paid to forget. Sweeping, meticulous historical fiction."),
    Book(7, "Driftless", "Noah Tran", 2024, listOf("Literary", "Nature"), Color(0xFF566246), Color(0xFFA4B494),
        "A burnt-out cartographer takes a season-long job surveying the unglaciated hills of Wisconsin and finds a landscape that refuses straight lines. Part fieldwork journal, part love letter to slowness, Driftless is the rare novel that lowers your heart rate."),
    Book(8, "The Quiet Hours", "Imogen Hale", 2021, listOf("Mystery", "Cozy"), Color(0xFF6B5B73), Color(0xFFA893B0),
        "Night librarian Edie Foss has catalogued every sound the old library makes — which is how she knows the footsteps on the third floor don't belong. A gentle locked-room mystery steeped in tea, marginalia, and the politics of a very small town."),
    Book(9, "Honey & Rust", "S. K. Adeyemi", 2023, listOf("Family", "Saga"), Color(0xFF8C5A2E), Color(0xFFD9A441),
        "When the Adekunle family's beekeeping estate is split between two heirs — one who stayed, one who left — the bees themselves seem to take sides. A rich, humming saga about sweetness, corrosion, and what we owe the places that made us."),
    Book(10, "Borrowed Light", "Felix Marsh", 2016, listOf("Sci-fi", "Quiet"), Color(0xFF2E3A4E), Color(0xFF7E8FA8),
        "The last lamplighter on a generation ship walks the same three kilometres of corridor every night-cycle, keeping the old filament lamps alive for a crew that no longer notices them. Then one lamp starts flickering in Morse. Small, perfect, and devastating."),
    Book(11, "The Understory", "Lena Brandt", 2022, listOf("Nature", "Essays"), Color(0xFF3D5240), Color(0xFF7E9276),
        "Twelve essays written from the floor of twelve forests, on fungal networks, deadwood, patience, and the slow conversations trees hold underground. Brandt writes like moss grows — quietly, and then all at once you're covered."),
)

val Users = mapOf(
    "me" to User("me", "Joana", "JO", Color(0xFF7E9276)),
    "maya" to User("maya", "Maya Chen", "MC", Color(0xFFC8825F)),
    "theo" to User("theo", "Theo Park", "TP", Color(0xFF9A8FB8)),
    "priya" to User("priya", "Priya Nair", "PN", Color(0xFF5C7FA8)),
    "sam" to User("sam", "Sam Whitfield", "SW", Color(0xFF8C5A2E)),
    "noor" to User("noor", "Noor Haddad", "NH", Color(0xFF566246)),
)

val FeedPosts = listOf(
    FeedPost("maya", 7, 5, "2h", true,
        "Finished this on the porch over two slow mornings and I genuinely feel recalibrated. Tran writes hills the way other people write love interests. The chapter on contour lines undid me a little.",
        VoiceNote("0:47"), mapOf("🤍" to 12, "🌿" to 5), 3),
    FeedPost("theo", 2, 4, "5h", true,
        "Did NOT see the greenhouse reveal coming. Vance plays completely fair with the clues too — I went back and they were all there, hiding in the pruning records.",
        null, mapOf("🤍" to 8, "😮" to 7), 5),
    FeedPost("priya", 10, 5, "8h", true,
        "A 180-page book that I will be thinking about for 180 years. The flickering lamp chapter is the best thing I've read all year.",
        VoiceNote("1:12", "Ch. 9"), mapOf("🤍" to 21, "✨" to 9), 8),
    FeedPost("sam", 9, 4, "1d", false,
        "Reading this while eating toast with honey felt like a 4D experience. The sibling chapters alternate perspectives and it works so well.",
        null, mapOf("🤍" to 6, "🍯" to 4), 2),
    FeedPost("noor", 0, 5, "1d", true,
        "The logbook entries between chapters are little prose poems. Read the last one out loud to an empty kitchen, no regrets.",
        VoiceNote("0:33"), mapOf("🤍" to 14), 4),
    FeedPost("maya", 4, 4, "2d", false,
        "Bird people, drop everything. The middle section drags a touch but the ending pays off every single page of patience.",
        null, mapOf("🤍" to 9, "🐦" to 11), 6),
)

object Shelves {
    val currentlyReading = listOf(7, 2)
    val read = listOf(0, 5, 10, 11, 9)
    val wantToRead = listOf(4, 6, 8, 1, 3)
}

val Folders: SnapshotStateList<Folder> = mutableStateListOf(
    Folder("Beach reads", mutableStateListOf(1, 5, 9), "Public"),
    Folder("Quiet sci-fi", mutableStateListOf(4, 10), "Friends only"),
    Folder("To gift", mutableStateListOf(7, 11, 8, 3), "Private"),
)

val Communities: SnapshotStateList<Community> = mutableStateListOf(
    Community("Slow Fiction Society",
        "For books that take their time. We read one quiet novel a month and nobody is ever behind.",
        Color(0xFF566246), Color(0xFFA4B494), 284, false, listOf(7, 3, 11, 0)),
    Community("Gothic Greenhouse",
        "Mysteries with atmosphere. Currently halfway through The Glass Orchard — spoilers walled until Sunday.",
        Color(0xFF4E4668), Color(0xFF8B80A8), 97, true, listOf(2, 8)),
    Community("Night Shift Readers",
        "For everyone who reads when the house finally goes quiet. Voice notes encouraged, volume low.",
        Color(0xFF2E3A4E), Color(0xFF7E8FA8), 151, false, listOf(10, 5, 4)),
)

val Chats: SnapshotStateList<Chat> = mutableStateListOf(
    Chat("maya", "2:14 pm", true, mutableStateListOf(
        Message(false, "1:58 pm", text = "ok I need you to read Driftless immediately"),
        Message(false, "1:58 pm", bookId = 7),
        Message(true, "2:03 pm", text = "adding it right now, your porch review sold me"),
        Message(false, "2:14 pm", voice = VoiceNote("0:21")),
    )),
    Chat("theo", "11:40 am", false, mutableStateListOf(
        Message(true, "11:02 am", text = "no spoilers but how far are you in Glass Orchard"),
        Message(false, "11:38 am", text = "the pruning records chapter 👀"),
        Message(false, "11:40 am", text = "say NOTHING"),
    )),
    Chat("priya", "yesterday", true, mutableStateListOf(
        Message(false, "9:12 pm", text = "borrowed light book club when??"),
        Message(false, "9:13 pm", voice = VoiceNote("1:02")),
    )),
)

val OnboardingGenres = listOf(
    "Literary fiction", "Cozy mystery", "Slow sci-fi", "Romance", "Nature writing",
    "Historical", "Essays", "Gothic", "Family sagas", "Poetry",
)

// Curated bookish banner palette: deep purples, forest greens, terracotta, navy, charcoal.
val BannerSwatches = listOf(
    Color(0xFF4E4668), Color(0xFF3D5240), Color(0xFF9C6B4F),
    Color(0xFF2E3A4E), Color(0xFF3A3733), Color(0xFF6B5B73), Color(0xFF566246),
)

private val ThemeReasons = listOf(
    "Both circle the question of what we owe the places that made us.",
    "Shared preoccupation with memory held inside landscapes.",
    "Each treats solitude as a setting, not a problem to be fixed.",
)
private val StyleReasons = listOf(
    "Same unhurried, close-third prose that rewards slow reading.",
    "Both build chapters like rooms — small, complete, lamplit.",
    "Spare sentences, long silences, devastating final lines.",
)
private val ReaderReasons = listOf(
    "68% of readers who loved one rated the other 5 stars.",
    "Frequently shelved together by readers in quiet-fiction circles.",
    "Readers who voice-note one almost always voice-note the other.",
)

fun reasonsFor(type: ConnectionType): List<String> = when (type) {
    ConnectionType.THEME -> ThemeReasons
    ConnectionType.STYLE -> StyleReasons
    ConnectionType.READERS -> ReaderReasons
}

/** Deterministic pseudo-random connections so every book has a stable constellation. */
fun connectionsFor(id: Int): List<Connection> {
    val types = ConnectionType.entries
    val out = mutableListOf<Connection>()
    for (k in 1..7) {
        val target = (id * 7 + k * 5) % Books.size
        if (target == id) continue
        val type = types[(id + k) % 3]
        out += Connection(
            to = target,
            strength = 0.35f + ((id * 13 + k * 29) % 60) / 100f,
            type = type,
            reason = reasonsFor(type)[(id + k) % 3],
        )
    }
    return out.take(6)
}
