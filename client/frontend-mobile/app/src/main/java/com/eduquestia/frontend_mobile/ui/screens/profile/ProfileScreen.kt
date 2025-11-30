package com.eduquestia.frontend_mobile.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.eduquestia.frontend_mobile.data.local.AvatarManager
import com.eduquestia.frontend_mobile.data.local.TokenManager
import com.eduquestia.frontend_mobile.data.model.AvatarTipo
import com.eduquestia.frontend_mobile.data.repository.GamificacionRepository
import com.eduquestia.frontend_mobile.ui.components.AvatarSelectorDialog
import com.eduquestia.frontend_mobile.ui.components.BottomNavBar
import com.eduquestia.frontend_mobile.ui.theme.*
import com.eduquestia.frontend_mobile.ui.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController? = null) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val avatarManager = remember { AvatarManager(context) }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Estado del avatar
    val selectedAvatar by
            avatarManager.selectedAvatar.collectAsState(initial = AvatarTipo.PERSONAJE_1)
    var showAvatarSelector by remember { mutableStateOf(false) }

    val authRepository = remember {
        com.eduquestia.frontend_mobile.data.repository.AuthRepository(tokenManager = tokenManager)
    }
    val authViewModel: com.eduquestia.frontend_mobile.ui.viewmodel.AuthViewModel = viewModel {
        com.eduquestia.frontend_mobile.ui.viewmodel.AuthViewModel(authRepository = authRepository)
    }

    var userName by remember { mutableStateOf("Usuario") }
    var userEmail by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        userName = tokenManager.getUserNombre() ?: "Usuario"
        userEmail = tokenManager.getUserEmail() ?: ""
    }

    val viewModel: ProfileViewModel =
            viewModel(
                    factory =
                            object : androidx.lifecycle.ViewModelProvider.Factory {
                                @Suppress("UNCHECKED_CAST")
                                override fun <T : androidx.lifecycle.ViewModel> create(
                                        modelClass: Class<T>
                                ): T {
                                    return ProfileViewModel(
                                            gamificacionRepository = GamificacionRepository(),
                                            tokenManager = tokenManager
                                    ) as
                                            T
                                }
                            }
            )

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadProfileData() }

    val currentRoute = com.eduquestia.frontend_mobile.ui.navigation.Screen.Profile.route

    ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                com.eduquestia.frontend_mobile.ui.components.AppDrawer(
                        currentRoute = currentRoute,
                        userName = userName,
                        userEmail = userEmail,
                        onMenuItemClick = { item ->
                            scope.launch { drawerState.close() }
                            when {
                                item.isLogout -> {
                                    authViewModel.logout()
                                    navController?.navigate(
                                            com.eduquestia.frontend_mobile.ui.navigation.Screen
                                                    .Login.route
                                    ) {
                                        popUpTo(
                                                com.eduquestia.frontend_mobile.ui.navigation.Screen
                                                        .Home.route
                                        ) { inclusive = true }
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
                            navigationIcon = {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(
                                            imageVector = Icons.Default.Menu,
                                            contentDescription = "Menú",
                                            tint = TextPrimary
                                    )
                                }
                            },
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
                                IconButton(onClick = { /* TODO: Notificaciones */}) {
                                    Icon(
                                            imageVector = Icons.Default.Notifications,
                                            contentDescription = "Notificaciones",
                                            tint = TextPrimary
                                    )
                                }
                                IconButton(
                                        onClick = {
                                            navController?.navigate(
                                                    com.eduquestia.frontend_mobile.ui.navigation
                                                            .Screen.Settings.route
                                            )
                                        }
                                ) {
                                    Icon(
                                            imageVector = Icons.Default.Settings,
                                            contentDescription = "Configuración",
                                            tint = TextPrimary
                                    )
                                }
                            },
                            colors =
                                    TopAppBarDefaults.topAppBarColors(
                                            containerColor = BackgroundWhite
                                    )
                    )
                },
                containerColor = BackgroundGray,
                bottomBar = { navController?.let { BottomNavBar(it) } }
        ) { paddingValues ->
            LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Título
                item {
                    Column {
                        Text(
                                text = "Mi Perfil",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                        )
                        Text(
                                text = "Gestiona tu información y personaliza tu experiencia",
                                fontSize = 14.sp,
                                color = TextSecondary
                        )
                    }
                }

                // Card de Usuario
                item {
                    UserInfoCard(
                            nombreUsuario = uiState.nombreUsuario,
                            nivel = uiState.perfilGamificado?.nivel ?: 1,
                            puntosTotales = uiState.perfilGamificado?.puntosTotales ?: 0,
                            puntosParaSiguiente = uiState.perfilGamificado?.puntosParaSiguienteNivel
                                            ?: 100,
                            avatarTipo = selectedAvatar,
                            onEditAvatarClick = { showAvatarSelector = true }
                    )
                }

                // Card de Ranking Global
                item {
                    RankingGlobalCard(
                            posicion = uiState.posicionRanking,
                            totalEstudiantes = uiState.rankingGlobal?.totalEstudiantes ?: 0
                    )
                }

                // Sección Estadísticas
                item {
                    Text(
                            text = "Estadísticas",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                    )
                }

                item {
                    StatisticsGrid(
                            cursosCompletados = 3, // TODO: Obtener del backend
                            misionesCompletadas = uiState.perfilGamificado?.misionesCompletadas
                                            ?: 0,
                            horasEstudio = 124 // TODO: Obtener del backend
                    )
                }

                // Información Personal (sin tabs)
                item {
                    PersonalInfoSection(
                            nombreCompleto = uiState.nombreUsuario,
                            email = uiState.email,
                            fechaNacimiento = "15/05/2005", // TODO: Obtener del backend
                            biografia =
                                    "Estudiante apasionada por las matemáticas y la literatura. Me encanta aprender cosas nuevas y compartir conocimientos con mis compañeros.",
                            onSaveClick = { /* TODO: Guardar cambios */}
                    )
                }

                // Estado de carga
                if (uiState.isLoading) {
                    item {
                        Box(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = EduQuestBlue) }
                    }
                }
            }
        }
    }

    // Diálogo de selección de avatar
    if (showAvatarSelector) {
        AvatarSelectorDialog(
                currentAvatar = selectedAvatar,
                onDismiss = { showAvatarSelector = false },
                onAvatarSelected = { avatar ->
                    scope.launch { avatarManager.saveSelectedAvatar(avatar) }
                    showAvatarSelector = false
                }
        )
    }
}

@Composable
fun UserInfoCard(
        nombreUsuario: String,
        nivel: Int,
        puntosTotales: Int,
        puntosParaSiguiente: Int,
        avatarTipo: AvatarTipo,
        onEditAvatarClick: () -> Unit
) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar con imagen RPG - MÁS GRANDE
            Box(
                    modifier =
                            Modifier.size(130.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(EduQuestBlue.copy(alpha = 0.1f))
                                    .border(4.dp, EduQuestBlue, RoundedCornerShape(20.dp))
                                    .clickable(onClick = onEditAvatarClick),
                    contentAlignment = Alignment.Center
            ) {
                Image(
                        painter = painterResource(id = avatarTipo.drawableRes),
                        contentDescription = avatarTipo.displayName,
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                        contentScale = ContentScale.Fit
                )
            }

            // Nombre
            Text(
                    text = nombreUsuario,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
            )

            // Nivel
            Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                        text = "Nivel $nivel",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                )
                Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Nivel",
                        tint = AccentOrange,
                        modifier = Modifier.size(20.dp)
                )
            }

            // Botón Editar Avatar
            TextButton(onClick = onEditAvatarClick) {
                Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Editar avatar")
            }

            // Barra de progreso
            Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                            text = "Progreso al nivel ${nivel + 1}",
                            fontSize = 14.sp,
                            color = TextSecondary
                    )
                    Text(
                            text = "$puntosTotales/$puntosParaSiguiente XP",
                            fontSize = 14.sp,
                            color = TextSecondary
                    )
                }

                val progress =
                        (puntosTotales.toFloat() / puntosParaSiguiente.toFloat()).coerceIn(0f, 1f)
                LinearProgressIndicator(
                        progress = { progress },
                        modifier =
                                Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = EduQuestBlue,
                        trackColor = BackgroundGray
                )
            }
        }
    }
}

@Composable
fun RankingGlobalCard(posicion: Int?, totalEstudiantes: Int) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
                modifier =
                        Modifier.fillMaxWidth()
                                .background(
                                        brush =
                                                Brush.horizontalGradient(
                                                        colors =
                                                                listOf(
                                                                        EduQuestDarkBlue,
                                                                        EduQuestLightBlue
                                                                )
                                                )
                                )
                                .padding(24.dp),
                contentAlignment = Alignment.Center
        ) {
            Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Ranking",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                )
                Text(
                        text = "Ranking Global",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                )
                Text(
                        text = "#${posicion ?: "?"} de $totalEstudiantes",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                )
            }
        }
    }
}

@Composable
fun StatisticsGrid(cursosCompletados: Int, misionesCompletadas: Int, horasEstudio: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        StatRow(
                icon = Icons.Default.MenuBook,
                label = "Cursos completados",
                value = "$cursosCompletados",
                color = EduQuestBlue
        )
        StatRow(
                icon = Icons.Default.CheckCircle,
                label = "Misiones completadas",
                value = "$misionesCompletadas",
                color = AccentGreen
        )
        StatRow(
                icon = Icons.Default.AccessTime,
                label = "Horas de estudio",
                value = "$horasEstudio",
                color = EduQuestPurple
        )
    }
}

@Composable
fun StatRow(
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        label: String,
        value: String,
        color: Color
) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                )
                Text(text = label, fontSize = 16.sp, color = TextPrimary)
            }
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun PersonalInfoSection(
        nombreCompleto: String,
        email: String,
        fechaNacimiento: String,
        biografia: String,
        onSaveClick: () -> Unit
) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                    text = "Información Personal",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
            )
            Text(text = "Actualiza tus datos personales", fontSize = 14.sp, color = TextSecondary)

            Spacer(modifier = Modifier.height(8.dp))

            // Campo Nombre completo
            OutlinedTextField(
                    value = nombreCompleto,
                    onValueChange = { /* TODO: Actualizar estado */},
                    label = { Text("Nombre completo", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    colors =
                            OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = BackgroundGray,
                                    unfocusedContainerColor = BackgroundGray,
                                    focusedBorderColor = EduQuestBlue,
                                    unfocusedBorderColor = TextSecondary,
                                    focusedLabelColor = EduQuestBlue,
                                    unfocusedLabelColor = TextSecondary
                            ),
                    shape = RoundedCornerShape(12.dp)
            )

            // Campo Email
            OutlinedTextField(
                    value = email,
                    onValueChange = { /* TODO: Actualizar estado */},
                    label = { Text("Correo electrónico", color = TextSecondary) },
                    leadingIcon = {
                        Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email",
                                tint = TextSecondary
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    colors =
                            OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = BackgroundGray,
                                    unfocusedContainerColor = BackgroundGray,
                                    focusedBorderColor = EduQuestBlue,
                                    unfocusedBorderColor = TextSecondary,
                                    focusedLabelColor = EduQuestBlue,
                                    unfocusedLabelColor = TextSecondary
                            ),
                    shape = RoundedCornerShape(12.dp)
            )

            // Campo Fecha de nacimiento
            OutlinedTextField(
                    value = fechaNacimiento,
                    onValueChange = { /* TODO: Actualizar estado */},
                    label = { Text("Fecha de nacimiento", color = TextSecondary) },
                    leadingIcon = {
                        Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Fecha",
                                tint = TextSecondary
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    colors =
                            OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = BackgroundGray,
                                    unfocusedContainerColor = BackgroundGray,
                                    focusedBorderColor = EduQuestBlue,
                                    unfocusedBorderColor = TextSecondary,
                                    focusedLabelColor = EduQuestBlue,
                                    unfocusedLabelColor = TextSecondary
                            ),
                    shape = RoundedCornerShape(12.dp)
            )

            // Campo Biografía
            OutlinedTextField(
                    value = biografia,
                    onValueChange = { /* TODO: Actualizar estado */},
                    label = { Text("Biografía", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 5,
                    readOnly = true,
                    colors =
                            OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = BackgroundGray,
                                    unfocusedContainerColor = BackgroundGray,
                                    focusedBorderColor = EduQuestBlue,
                                    unfocusedBorderColor = TextSecondary,
                                    focusedLabelColor = EduQuestBlue,
                                    unfocusedLabelColor = TextSecondary
                            ),
                    shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botón Guardar cambios
            Button(
                    onClick = onSaveClick,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EduQuestBlue),
                    shape = RoundedCornerShape(12.dp)
            ) { Text(text = "Guardar cambios", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        }
    }
}
