package com.eduquestia.frontend_mobile.ui.screens.rewards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.eduquestia.frontend_mobile.data.local.TokenManager
import com.eduquestia.frontend_mobile.data.model.Logro
import com.eduquestia.frontend_mobile.data.model.PerfilGamificado
import com.eduquestia.frontend_mobile.data.repository.GamificacionRepository
import com.eduquestia.frontend_mobile.ui.components.BottomNavBar
import com.eduquestia.frontend_mobile.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen(
    navController: NavController? = null
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val gamificacionRepository = remember { GamificacionRepository() }
    val scope = rememberCoroutineScope()

    var perfilGamificado by remember { mutableStateOf<PerfilGamificado?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Cargar datos al iniciar
    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            error = null

            val userId = tokenManager.getUserId()
            if (userId != null) {
                gamificacionRepository.obtenerPerfilGamificado(userId)
                    .onSuccess { perfilGamificado = it }
                    .onFailure { error = it.message }
            } else {
                error = "No se pudo obtener el ID del usuario"
            }

            isLoading = false
        }
    }

    val logrosObtenidos = perfilGamificado?.logros?.filter { it.obtenido } ?: emptyList()
    val logrosPendientes = perfilGamificado?.logros?.filter { !it.obtenido } ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "EduQuest Logo",
                            tint = EduQuestBlue
                        )
                        Text(
                            text = "EduQuest",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Notificaciones */ }) {
                        Icon(Icons.Default.Notifications, "Notificaciones", tint = TextPrimary)
                    }
                    IconButton(onClick = { /* TODO: Menú */ }) {
                        Icon(Icons.Default.Menu, "Menú", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        },
        containerColor = BackgroundGray,
        bottomBar = { navController?.let { BottomNavBar(it) } }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Column {
                    Text(
                        text = "Recompensas",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Desbloquea logros completando misiones y subiendo de nivel",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            }

            // Stats
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    RewardStatCard(
                        icon = Icons.Default.EmojiEvents,
                        value = "${logrosObtenidos.size}",
                        label = "Obtenidos",
                        color = AccentOrange,
                        modifier = Modifier.weight(1f)
                    )
                    RewardStatCard(
                        icon = Icons.Default.Lock,
                        value = "${logrosPendientes.size}",
                        label = "Por desbloquear",
                        color = TextLight,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Estado de carga
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = EduQuestBlue)
                    }
                }
            }

            // Error
            error?.let { errorMessage ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = AccentRed.copy(alpha = 0.1f))
                    ) {
                        Text(
                            text = errorMessage,
                            color = AccentRed,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            // Logros obtenidos
            if (logrosObtenidos.isNotEmpty()) {
                item {
                    Text(
                        text = "Logros Obtenidos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                items(logrosObtenidos) { logro ->
                    LogroCard(logro = logro, obtenido = true)
                }
            }

            // Logros pendientes
            if (logrosPendientes.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Por Desbloquear",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                items(logrosPendientes) { logro ->
                    LogroCard(logro = logro, obtenido = false)
                }
            }

            // Sin logros
            if (!isLoading && perfilGamificado?.logros?.isEmpty() == true) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BackgroundWhite)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Sin logros",
                                tint = TextLight,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Los logros estarán disponibles pronto",
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RewardStatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun LogroCard(
    logro: Logro,
    obtenido: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (obtenido) AccentOrange.copy(alpha = 0.1f) else BackgroundWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icono
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if (obtenido) AccentOrange else BackgroundGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (obtenido) Icons.Default.EmojiEvents else Icons.Default.Lock,
                    contentDescription = logro.nombre,
                    tint = if (obtenido) Color.White else TextLight,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = logro.nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (obtenido) TextPrimary else TextSecondary
                )
                Text(
                    text = logro.descripcion,
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                if (!obtenido) {
                    Text(
                        text = "${logro.puntosRequeridos} puntos requeridos",
                        fontSize = 12.sp,
                        color = TextLight
                    )
                }
            }

            // Indicador
            if (obtenido) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Obtenido",
                    tint = AccentGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
