package com.pettrack.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pettrack.app.core.session.AuthState
import com.pettrack.app.ui.RootViewModel
import com.pettrack.app.ui.auth.login.LoginScreen
import com.pettrack.app.ui.auth.register.RegisterScreen
import com.pettrack.app.ui.community.CommunityScreen
import com.pettrack.app.ui.dashboard.DashboardScreen
import com.pettrack.app.ui.pets.list.MyReportsScreen
import com.pettrack.app.ui.pets.report.ReportPetScreen
import com.pettrack.app.ui.petdetail.PetDetailScreen
import com.pettrack.app.ui.profile.ProfileScreen
import com.pettrack.app.ui.notifications.NotificationWatcherViewModel
import com.pettrack.app.ui.notifications.NotificationsScreen
import android.Manifest
import android.os.Build
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

private data class TabItem(val route: String, val label: String, val icon: ImageVector)

private val TABS = listOf(
    TabItem(Routes.COMMUNITY, "Comunidad", Icons.Filled.Place),
    TabItem(Routes.MY_REPORTS, "Mis reportes", Icons.Filled.Pets),
    TabItem(Routes.DASHBOARD, "Dashboard", Icons.Filled.BarChart),
    TabItem(Routes.PROFILE, "Perfil", Icons.Filled.Person),
)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PetTrackNavHost(rootViewModel: RootViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val authState by rootViewModel.authState.collectAsStateWithLifecycle()
    val startDestination = if (authState is AuthState.Authenticated) Routes.COMMUNITY else Routes.LOGIN

    // Notifications: poll for unread badge + fire system notifications for new sightings.
    val notificationWatcher: NotificationWatcherViewModel = hiltViewModel()
    val unreadCount by notificationWatcher.unreadCount.collectAsStateWithLifecycle()
    val notificationsPermission = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !notificationsPermission.status.isGranted) {
                notificationsPermission.launchPermissionRequest()
            }
        } else {
            notificationWatcher.reset()
        }
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = TABS.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    TABS.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.route,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(padding),
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(
                    onLoggedIn = {
                        navController.navigate(Routes.COMMUNITY) { popUpTo(Routes.LOGIN) { inclusive = true } }
                    },
                    onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                )
            }
            composable(Routes.REGISTER) {
                RegisterScreen(
                    onRegistered = {
                        navController.navigate(Routes.COMMUNITY) { popUpTo(Routes.LOGIN) { inclusive = true } }
                    },
                    onBack = { navController.popBackStack() },
                )
            }
            composable(Routes.COMMUNITY) {
                CommunityScreen(
                    onOpenPet = { id -> navController.navigate(Routes.petDetail(id)) },
                    unreadCount = unreadCount,
                    onOpenNotifications = { navController.navigate(Routes.NOTIFICATIONS) },
                )
            }
            composable(Routes.MY_REPORTS) {
                MyReportsScreen(
                    onAddPet = { navController.navigate(Routes.reportPet(null)) },
                    onEditPet = { id -> navController.navigate(Routes.reportPet(id)) },
                )
            }
            composable(Routes.DASHBOARD) { DashboardScreen() }
            composable(Routes.PROFILE) {
                ProfileScreen(
                    onLoggedOut = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
                )
            }
            composable(
                route = Routes.REPORT_PET,
                arguments = listOf(
                    navArgument("petId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                ),
            ) {
                ReportPetScreen(
                    onSaved = { navController.popBackStack() },
                    onBack = { navController.popBackStack() },
                )
            }
            composable(
                route = Routes.PET_DETAIL,
                arguments = listOf(navArgument("petId") { type = NavType.StringType }),
            ) {
                PetDetailScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.NOTIFICATIONS) {
                NotificationsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
