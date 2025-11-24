package com.eduquestia.frontend_mobile.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Courses : Screen("courses")
    object Missions : Screen("missions")
    object Rewards : Screen("rewards")
    object Ranking : Screen("ranking")
    object Chat : Screen("chat")
    object Profile : Screen("profile")
}

