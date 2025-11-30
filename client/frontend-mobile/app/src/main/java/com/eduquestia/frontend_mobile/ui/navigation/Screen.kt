package com.eduquestia.frontend_mobile.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Courses : Screen("courses")
    object Missions : Screen("missions")
    object MissionDetail : Screen("mission_detail/{misionId}") {
        fun createRoute(misionId: String) = "mission_detail/$misionId"
    }
    object Ranking : Screen("ranking")
    object Chat : Screen("chat")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object Shop : Screen("shop")
}
