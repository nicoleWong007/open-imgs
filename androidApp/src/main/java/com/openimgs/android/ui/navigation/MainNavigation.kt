package com.openimgs.android.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.openimgs.android.ui.albums.AlbumsScreen
import com.openimgs.android.ui.clean.CleanScreen
import com.openimgs.android.ui.gallery.GalleryScreen
import com.openimgs.android.ui.search.SearchScreen
import com.openimgs.android.ui.settings.SettingsScreen

data class TabItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val tabs = listOf(
    TabItem("gallery", "Photos", Icons.Filled.PhotoLibrary, Icons.Outlined.PhotoLibrary),
    TabItem("albums", "Albums", Icons.Filled.Collections, Icons.Outlined.Collections),
    TabItem("search", "Search", Icons.Filled.Search, Icons.Outlined.Search),
    TabItem("clean", "Clean", Icons.Filled.DeleteSweep, Icons.Outlined.DeleteSweep),
    TabItem("settings", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings),
)

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                if (currentDestination?.hierarchy?.any { it.route == tab.route } == true)
                                    tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.label
                            )
                        },
                        label = { Text(tab.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "gallery",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("gallery") { GalleryScreen() }
            composable("albums") { AlbumsScreen() }
            composable("search") { SearchScreen() }
            composable("clean") { CleanScreen() }
            composable("settings") { SettingsScreen() }
        }
    }
}
