package com.eduquestia.frontend_mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eduquestia.frontend_mobile.ui.navigation.Screen
import com.eduquestia.frontend_mobile.ui.theme.*

data class DrawerMenuItem(
    val icon: ImageVector,
    val title: String,
    val route: String,
    val isLogout: Boolean = false
)

val drawerMenuItems = listOf(
    DrawerMenuItem(Icons.Default.Home, "Inicio", Screen.Home.route),
    DrawerMenuItem(Icons.Default.MenuBook, "Cursos", Screen.Courses.route),
    DrawerMenuItem(Icons.Default.Assignment, "Misiones", Screen.Missions.route),
    DrawerMenuItem(Icons.Default.EmojiEvents, "Recompensas", Screen.Rewards.route),
    DrawerMenuItem(Icons.Default.Leaderboard, "Ranking", Screen.Ranking.route),
    DrawerMenuItem(Icons.Default.SmartToy, "Chat IA", Screen.Chat.route),
    DrawerMenuItem(Icons.Default.Person, "Perfil", Screen.Profile.route),
    DrawerMenuItem(Icons.AutoMirrored.Filled.ExitToApp, "Cerrar sesión", "", isLogout = true)
)

@Composable
fun AppDrawer(
    currentRoute: String,
    userName: String = "Usuario",
    userEmail: String = "",
    onMenuItemClick: (DrawerMenuItem) -> Unit,
    onClose: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = BackgroundWhite
    ) {
        Column(
            modifier = Modifier.fillMaxHeight()
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EduQuestBlue)
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "Menú",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        IconButton(
                            onClick = onClose,
                            colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Menu Items
            drawerMenuItems.forEach { item ->
                val isSelected = currentRoute == item.route

                DrawerItem(
                    icon = item.icon,
                    title = item.title,
                    isSelected = isSelected,
                    isLogout = item.isLogout,
                    onClick = { onMenuItemClick(item) }
                )
            }
        }
    }
}

@Composable
fun DrawerItem(
    icon: ImageVector,
    title: String,
    isSelected: Boolean,
    isLogout: Boolean = false,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> EduQuestBlue.copy(alpha = 0.1f)
        else -> Color.Transparent
    }

    val contentColor = when {
        isLogout -> AccentRed
        isSelected -> EduQuestBlue
        else -> TextPrimary
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = contentColor
        )
    }
}







