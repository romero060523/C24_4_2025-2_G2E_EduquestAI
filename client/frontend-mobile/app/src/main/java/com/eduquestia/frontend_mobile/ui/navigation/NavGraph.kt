package com.eduquestia.frontend_mobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.eduquestia.frontend_mobile.data.local.TokenManager
import com.eduquestia.frontend_mobile.data.repository.AuthRepository
import com.eduquestia.frontend_mobile.ui.screens.chat.ChatScreen
import com.eduquestia.frontend_mobile.ui.screens.courses.CoursesScreen
import com.eduquestia.frontend_mobile.ui.screens.home.HomeScreen
import com.eduquestia.frontend_mobile.ui.screens.login.LoginScreen
import com.eduquestia.frontend_mobile.ui.screens.missions.MissionDetailScreen
import com.eduquestia.frontend_mobile.ui.screens.missions.MissionsScreen
import com.eduquestia.frontend_mobile.ui.screens.profile.ProfileScreen
import com.eduquestia.frontend_mobile.ui.screens.ranking.RankingScreen
import com.eduquestia.frontend_mobile.ui.screens.settings.SettingsScreen
import com.eduquestia.frontend_mobile.ui.screens.shop.ShopScreen
import com.eduquestia.frontend_mobile.ui.viewmodel.AuthViewModel

@Composable
fun NavGraph(navController: NavHostController, startDestination: String = Screen.Login.route) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val authRepository = remember { AuthRepository(tokenManager = tokenManager) }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            val authViewModel: AuthViewModel = viewModel {
                AuthViewModel(authRepository = authRepository)
            }
            LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    viewModel = authViewModel
            )
        }

        composable(Screen.Home.route) { HomeScreen(navController = navController) }

        composable(Screen.Courses.route) { CoursesScreen(navController = navController) }

        composable(Screen.Missions.route) { MissionsScreen(navController = navController) }

        composable(
                route = Screen.MissionDetail.route,
                arguments = listOf(navArgument("misionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val misionId = backStackEntry.arguments?.getString("misionId") ?: ""
            MissionDetailScreen(navController = navController, misionId = misionId)
        }

        composable(Screen.Ranking.route) { RankingScreen(navController = navController) }

        composable(Screen.Chat.route) { ChatScreen(navController = navController) }

        composable(Screen.Profile.route) { ProfileScreen(navController = navController) }

        composable(Screen.Settings.route) { SettingsScreen(navController = navController) }

        composable(Screen.Shop.route) { ShopScreen(navController = navController) }
    }
}
