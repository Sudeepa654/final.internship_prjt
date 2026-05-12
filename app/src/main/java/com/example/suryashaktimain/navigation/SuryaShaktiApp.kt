package com.example.suryashaktimain.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.suryashaktimain.data.local.UserEntity
import com.example.suryashaktimain.ui.auth.ForgotPasswordScreen
import com.example.suryashaktimain.ui.auth.LoginScreen
import com.example.suryashaktimain.ui.auth.RegisterScreen
import com.example.suryashaktimain.ui.dashboard.DashboardScreen
import com.example.suryashaktimain.ui.logs.AddLogScreen
import com.example.suryashaktimain.ui.profile.ProfileScreen
import com.example.suryashaktimain.ui.reports.ReportsScreen
import com.example.suryashaktimain.ui.theme.SolarBlack
import com.example.suryashaktimain.ui.theme.SolarMuted
import com.example.suryashaktimain.ui.theme.SolarPanel
import com.example.suryashaktimain.ui.theme.SolarText
import com.example.suryashaktimain.ui.theme.SolarYellow
import com.example.suryashaktimain.viewmodel.AuthViewModel
import com.example.suryashaktimain.viewmodel.EnergyViewModel

private data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem(MainRoutes.DASHBOARD, "Dashboard", Icons.Default.Dashboard),
    BottomNavItem(MainRoutes.ADD_LOG, "Add Log", Icons.Default.AddCircle),
    BottomNavItem(MainRoutes.REPORTS, "Reports", Icons.Default.Assessment),
    BottomNavItem(MainRoutes.PROFILE, "Profile", Icons.Default.Person)
)

@Composable
fun SuryaShaktiApp(
    authViewModel: AuthViewModel,
    energyViewModel: EnergyViewModel
) {
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(authState.currentUser?.id) {
        energyViewModel.setActiveUser(authState.currentUser)
    }

    if (authState.currentUser == null) {
        AuthNavHost(authViewModel = authViewModel)
    } else {
        MainNavHost(
            authViewModel = authViewModel,
            energyViewModel = energyViewModel
        )
    }
}

@Composable
private fun AuthNavHost(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = AuthRoutes.LOGIN
    ) {
        composable(AuthRoutes.LOGIN) {
            LoginScreen(
                uiState = authState,
                onLogin = authViewModel::login,
                onRegisterClick = { navController.navigate(AuthRoutes.REGISTER) },
                onForgotPasswordClick = { navController.navigate(AuthRoutes.FORGOT_PASSWORD) },
                onMessageShown = authViewModel::clearMessages
            )
        }
        composable(AuthRoutes.REGISTER) {
            RegisterScreen(
                uiState = authState,
                onRegister = authViewModel::register,
                onBackToLogin = { navController.popBackStack() },
                onMessageShown = authViewModel::clearMessages
            )
        }
        composable(AuthRoutes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                uiState = authState,
                onResetPassword = authViewModel::resetPassword,
                onBackToLogin = { navController.popBackStack() },
                onMessageShown = authViewModel::clearMessages
            )
        }
    }
}

@Composable
private fun MainNavHost(
    authViewModel: AuthViewModel,
    energyViewModel: EnergyViewModel
) {
    val navController = rememberNavController()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    val user = authState.currentUser ?: return

    Scaffold(
        containerColor = SolarBlack,
        bottomBar = { MainBottomBar(navController = navController) }
    ) { innerPadding ->
        MainContent(
            navController = navController,
            innerPadding = innerPadding,
            authViewModel = authViewModel,
            energyViewModel = energyViewModel,
            user = user
        )
    }
}

@Composable
private fun MainContent(
    navController: NavHostController,
    innerPadding: PaddingValues,
    authViewModel: AuthViewModel,
    energyViewModel: EnergyViewModel,
    user: UserEntity
) {
    NavHost(
        navController = navController,
        startDestination = MainRoutes.DASHBOARD,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(MainRoutes.DASHBOARD) {
            DashboardScreen(
                userName = user.name,
                energyViewModel = energyViewModel,
                onAddLogClick = { navController.navigate(MainRoutes.ADD_LOG) }
            )
        }
        composable(MainRoutes.ADD_LOG) {
            AddLogScreen(
                user = user,
                energyViewModel = energyViewModel
            )
        }
        composable(MainRoutes.REPORTS) {
            ReportsScreen(energyViewModel = energyViewModel)
        }
        composable(MainRoutes.PROFILE) {
            ProfileScreen(
                authViewModel = authViewModel,
                energyViewModel = energyViewModel
            )
        }
    }
}

@Composable
private fun MainBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(containerColor = SolarPanel, contentColor = SolarText) {
        bottomNavItems.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SolarBlack,
                    selectedTextColor = SolarYellow,
                    indicatorColor = SolarYellow,
                    unselectedIconColor = SolarMuted,
                    unselectedTextColor = SolarMuted
                )
            )
        }
    }
}

