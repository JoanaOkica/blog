package com.folio.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.folio.app.ui.screens.BookDetailScreen
import com.folio.app.ui.screens.CommunitiesScreen
import com.folio.app.ui.screens.DiscoverScreen
import com.folio.app.ui.screens.FeedScreen
import com.folio.app.ui.screens.LibraryScreen
import com.folio.app.ui.screens.MessagesScreen
import com.folio.app.ui.screens.OnboardingScreen
import com.folio.app.ui.screens.ProfileScreen
import com.folio.app.ui.screens.RecordScreen

private data class Tab(val route: String, val glyph: String, val label: String)

private val Tabs = listOf(
    Tab("feed", "⌂", "Home"),
    Tab("discover", "⌕", "Discover"),
    Tab("library", "▤", "Library"),
    Tab("communities", "◉", "Clubs"),
    Tab("messages", "✉", "Chats"),
)

@Composable
fun FolioApp(onToggleTheme: () -> Unit) {
    val navController = rememberNavController()
    var onboarded by rememberSaveable { mutableStateOf(false) }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in Tabs.map { it.route }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    Tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.route,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo("feed") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Text(tab.glyph, fontSize = 19.sp) },
                            label = { Text(tab.label, fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            ),
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (onboarded) "feed" else "onboarding",
            modifier = Modifier.fillMaxSize().padding(innerPadding),
        ) {
            composable("onboarding") {
                OnboardingScreen(onFinish = {
                    onboarded = true
                    navController.navigate("feed") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                })
            }
            composable("feed") {
                FeedScreen(
                    onOpenBook = { navController.navigate("book/$it") },
                    onOpenProfile = { navController.navigate("profile") },
                    onToggleTheme = onToggleTheme,
                )
            }
            composable("discover") {
                DiscoverScreen(onOpenBook = { navController.navigate("book/$it") })
            }
            composable("library") {
                LibraryScreen(onOpenBook = { navController.navigate("book/$it") })
            }
            composable("communities") {
                CommunitiesScreen(onOpenBook = { navController.navigate("book/$it") })
            }
            composable("messages") {
                MessagesScreen(onOpenBook = { navController.navigate("book/$it") })
            }
            composable("profile") {
                ProfileScreen(
                    onBack = { navController.popBackStack() },
                    onOpenBook = { navController.navigate("book/$it") },
                )
            }
            composable(
                "book/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType }),
            ) { entry ->
                val id = entry.arguments?.getInt("id") ?: 0
                BookDetailScreen(
                    bookId = id,
                    onBack = { navController.popBackStack() },
                    // Infinite exploration: each connected book opens its own map.
                    onOpenBook = { navController.navigate("book/$it") },
                    onRecord = { navController.navigate("record/$id") },
                )
            }
            composable(
                "record/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType }),
            ) { entry ->
                RecordScreen(
                    bookId = entry.arguments?.getInt("id") ?: 0,
                    onDone = { navController.popBackStack() },
                )
            }
        }
    }
}
