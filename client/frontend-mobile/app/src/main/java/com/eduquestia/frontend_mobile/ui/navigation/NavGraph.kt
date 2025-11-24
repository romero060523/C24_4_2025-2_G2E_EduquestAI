package com.eduquestia.frontend_mobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.eduquestia.frontend_mobile.ui.screens.chat.ChatScreen
import com.eduquestia.frontend_mobile.ui.screens.courses.CoursesScreen
import com.eduquestia.frontend_mobile.ui.screens.home.HomeScreen
import com.eduquestia.frontend_mobile.ui.screens.login.LoginScreen
import com.eduquestia.frontend_mobile.ui.screens.missions.MissionsScreen
import com.eduquestia.frontend_mobile.ui.screens.profile.ProfileScreen
import com.eduquestia.frontend_mobile.ui.screens.ranking.RankingScreen
import com.eduquestia.frontend_mobile.ui.screens.rewards.RewardsScreen
import com.eduquestia.frontend_mobile.ui.viewmodel.AuthViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            val authViewModel: AuthViewModel = viewModel()
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        
        composable(Screen.Courses.route) {
            CoursesScreen(navController = navController)
        }
        
        composable(Screen.Missions.route) {
            MissionsScreen(navController = navController)
        }
        
        composable(Screen.Rewards.route) {
            RewardsScreen(navController = navController)
        }
        
        composable(Screen.Ranking.route) {
            RankingScreen(navController = navController)
        }
        
        composable(Screen.Chat.route) {
            ChatScreen(navController = navController)
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
    }
}

