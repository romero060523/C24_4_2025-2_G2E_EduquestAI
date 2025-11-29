package com.eduquestia.frontend_mobile.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.eduquestia.frontend_mobile.data.local.TokenManager
import com.eduquestia.frontend_mobile.data.repository.AuthRepository
import com.eduquestia.frontend_mobile.data.repository.GamificacionRepository
import com.eduquestia.frontend_mobile.data.repository.MisionRepository
import com.eduquestia.frontend_mobile.ui.components.AppDrawer
import com.eduquestia.frontend_mobile.ui.components.BottomNavBar
import com.eduquestia.frontend_mobile.ui.components.drawerMenuItems
import com.eduquestia.frontend_mobile.ui.navigation.Screen
import com.eduquestia.frontend_mobile.ui.theme.*
import com.eduquestia.frontend_mobile.ui.viewmodel.AuthViewModel
import com.eduquestia.frontend_mobile.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController? = null
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val viewModel: HomeViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(
                    gamificacionRepository = GamificacionRepository(),
                    misionRepository = MisionRepository(),
                    tokenManager = tokenManager
                ) as T
            }
        }
    )
    val uiState by viewModel.uiState.collectAsState()

    val authRepository = remember { AuthRepository(tokenManager = tokenManager) }
    val authViewModel: AuthViewModel = viewModel {
        AuthViewModel(authRepository = authRepository)
    }

    var userName by remember { mutableStateOf("Usuario") }
    var userEmail by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadHomeData()
        userName = tokenManager.getUserNombre() ?: "Usuario"
        userEmail = tokenManager.getUserEmail() ?: ""
    }

    val currentRoute = Screen.Home.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentRoute = currentRoute,
                userName = userName,
                userEmail = userEmail,
                onMenuItemClick = { item ->
                    scope.launch { drawerState.close() }
                    when {
                        item.isLogout -> {
                            authViewModel.logout()
                            navController?.navigate(Screen.Login.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                        item.route.isNotEmpty() -> {
                            navController?.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                },
                onClose = { scope.launch { drawerState.close() } }
            )
        }
    ) {
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
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notificaciones",
                                tint = TextPrimary
                            )
                        }
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menú",
                                tint = TextPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BackgroundWhite
                    )
                )
            },
        containerColor = BackgroundGray,
        bottomBar = {
            navController?.let { BottomNavBar(it) }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card de Bienvenida con Gradiente
            item {
                WelcomeCard(
                    nombreUsuario = uiState.nombreUsuario,
                    nivel = uiState.perfilGamificado?.nivel ?: 1,
                    puntosTotales = uiState.perfilGamificado?.puntosTotales ?: 0,
                    posicionRanking = uiState.posicionRanking
                )
            }

            // Cards de Estadísticas
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Misiones Activas
                    StatCard(
                        icon = Icons.Default.CheckCircle,
                        title = "Misiones Activas",
                        value = "${uiState.misionesRecientes.size}",
                        subtitle = "${uiState.misionesRecientes.count { !it.completada }} por completar hoy",
                        modifier = Modifier.weight(1f)
                    )

                    // Logros Desbloqueados
                    StatCard(
                        icon = Icons.Default.Star,
                        title = "Logros Desbloqueados",
                        value = "${uiState.perfilGamificado?.logrosObtenidos ?: 0}",
                        subtitle = "${(uiState.perfilGamificado?.logros?.size ?: 0) - (uiState.perfilGamificado?.logrosObtenidos ?: 0)} más para el próximo nivel",
                        modifier = Modifier.weight(1f),
                        valueColor = AccentGreen
                    )

                }
            }

            // Misiones Recientes
            item {
                SectionHeader(
                    title = "Misiones Recientes",
                    actionText = "Ver todas >",
                    onActionClick = { navController?.navigate("missions") }
                )
            }

            items(uiState.misionesRecientes) { mision ->
                MisionCard(
                    mision = mision,
                    onClick = { /* TODO: Navegar a detalle de misión */ }
                )
            }

            // Logros Recientes
            if (uiState.logrosRecientes.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Logros Recientes",
                        actionText = null
                    )
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.logrosRecientes) { logro ->
                            LogroCard(logro = logro)
                        }
                    }
                }
            }

            // Estado de carga
            if (uiState.isLoading) {
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
            uiState.error?.let { error ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = AccentRed.copy(alpha = 0.1f))
                    ) {
                        Text(
                            text = error,
                            color = AccentRed,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
    }
}

@Composable
fun WelcomeCard(
    nombreUsuario: String,
    nivel: Int,
    puntosTotales: Int,
    posicionRanking: Int?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(EduQuestBlue, EduQuestPurple)
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "¡Hola, ${nombreUsuario.split(" ").firstOrNull() ?: "Usuario"}!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text("👋", fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Estás en el nivel $nivel. ¡Sigue así para alcanzar el nivel ${nivel + 1}!",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "XP",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "$puntosTotales XP",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    posicionRanking?.let { posicion ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Ranking",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Puesto #$posicion",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    valueColor: Color = EduQuestBlue
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = valueColor,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )

            Text(
                text = title,
                fontSize = 12.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextLight
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    actionText: String?,
    onActionClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        actionText?.let {
            TextButton(onClick = onActionClick) {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    color = EduQuestBlue,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun MisionCard(
    mision: com.eduquestia.frontend_mobile.data.model.MisionEstudiante,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icono de la misión
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(EduQuestLightBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Assignment,
                    contentDescription = "Misión",
                    tint = EduQuestBlue,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = mision.titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = mision.cursoNombre,
                    fontSize = 14.sp,
                    color = TextSecondary
                )

                // Barra de progreso
                LinearProgressIndicator(
                    progress = { mision.porcentajeCompletado / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = EduQuestBlue,
                    trackColor = BackgroundGray
                )
            }

            // Badge de XP y estado
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = AccentOrange.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "+${mision.puntosRecompensa} XP",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentOrange,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                if (mision.completada) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completada",
                        tint = AccentGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LogroCard(
    logro: com.eduquestia.frontend_mobile.data.model.Logro
) {
    Card(
        modifier = Modifier.width(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (logro.obtenido) AccentGreen.copy(alpha = 0.1f) else BackgroundGray
        ),
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
                imageVector = Icons.Default.Star,
                contentDescription = logro.nombre,
                tint = if (logro.obtenido) AccentOrange else TextLight,
                modifier = Modifier.size(40.dp)
            )

            Text(
                text = logro.nombre,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
