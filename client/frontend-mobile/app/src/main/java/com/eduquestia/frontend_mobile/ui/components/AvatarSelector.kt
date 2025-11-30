package com.eduquestia.frontend_mobile.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.eduquestia.frontend_mobile.data.model.AvatarTipo
import com.eduquestia.frontend_mobile.ui.theme.*

/**
 * Componente para mostrar el avatar seleccionado del usuario
 */
@Composable
fun UserAvatar(
    avatarTipo: AvatarTipo,
    size: Int = 80,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(EduQuestPurple.copy(alpha = 0.1f))
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            )
            .border(3.dp, EduQuestPurple, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = avatarTipo.drawableRes),
            contentDescription = avatarTipo.displayName,
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            contentScale = ContentScale.Fit
        )
    }
}

/**
 * Diálogo para seleccionar un avatar
 */
@Composable
fun AvatarSelectorDialog(
    currentAvatar: AvatarTipo,
    onDismiss: () -> Unit,
    onAvatarSelected: (AvatarTipo) -> Unit
) {
    var selectedAvatar by remember { mutableStateOf(currentAvatar) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundWhite)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Selecciona tu Avatar",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Preview del avatar seleccionado
                UserAvatar(
                    avatarTipo = selectedAvatar,
                    size = 100
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = selectedAvatar.displayName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = EduQuestPurple
                )

                Text(
                    text = selectedAvatar.descripcion,
                    fontSize = 14.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Grid de avatares
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.height(200.dp)
                ) {
                    items(AvatarTipo.entries.toList()) { avatar ->
                        AvatarOption(
                            avatar = avatar,
                            isSelected = avatar == selectedAvatar,
                            onClick = { selectedAvatar = avatar }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón confirmar
                Button(
                    onClick = { onAvatarSelected(selectedAvatar) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EduQuestBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Confirmar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun AvatarOption(
    avatar: AvatarTipo,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) EduQuestPurple.copy(alpha = 0.2f)
                else BackgroundGray
            )
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) EduQuestPurple else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = avatar.drawableRes),
            contentDescription = avatar.displayName,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )

        // Check si está seleccionado
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(24.dp)
                    .background(EduQuestPurple, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Seleccionado",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Componente de estadísticas del usuario estilo RPG
 */
@Composable
fun UserStatsCard(
    nivel: Int,
    xpActual: Int,
    xpParaSiguiente: Int,
    eduCoins: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Nivel y XP
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nivel
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(EduQuestPurple, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$nivel",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Column {
                        Text(
                            text = "Nivel $nivel",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Estudiante",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                // EduCoins
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .background(AccentOrange.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(AccentOrange, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "E",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = "$eduCoins",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentOrange
                    )
                }
            }

            // Barra de XP
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Experiencia",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "$xpActual / $xpParaSiguiente XP",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                LinearProgressIndicator(
                    progress = { (xpActual.toFloat() / xpParaSiguiente.toFloat()).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = EduQuestBlue,
                    trackColor = BackgroundGray
                )
            }
        }
    }
}

