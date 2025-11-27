package com.eduquestia.frontend_mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.eduquestia.frontend_mobile.ui.navigation.Screen
import com.eduquestia.frontend_mobile.ui.theme.*

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BackgroundWhite,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = Icons.Default.Home,
                label = "Inicio",
                route = Screen.Home.route,
                currentRoute = currentRoute,
                navController = navController
            )

            NavItem(
                icon = Icons.Default.MenuBook,
                label = "Cursos",
                route = Screen.Courses.route,
                currentRoute = currentRoute,
                navController = navController
            )

            NavItem(
                icon = Icons.Default.Flag,
                label = "Misiones",
                route = Screen.Missions.route,
                currentRoute = currentRoute,
                navController = navController
            )

            NavItem(
                icon = Icons.Default.ChatBubble,
                label = "Chat IA",
                route = Screen.Chat.route,
                currentRoute = currentRoute,
                navController = navController
            )

            NavItem(
                icon = Icons.Default.Person,
                label = "Perfil",
                route = Screen.Profile.route,
                currentRoute = currentRoute,
                navController = navController
            )
        }
    }
}

@Composable
fun NavItem(
    icon: ImageVector,
    label: String,
    route: String,
    currentRoute: String?,
    navController: NavController
) {
    val isSelected = currentRoute == route

    Column(
        modifier = Modifier
            .clickable {
                if (currentRoute != route) {
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(EduQuestLightBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = EduQuestBlue,
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = TextSecondary,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isSelected) EduQuestBlue else TextSecondary
        )
    }
}
