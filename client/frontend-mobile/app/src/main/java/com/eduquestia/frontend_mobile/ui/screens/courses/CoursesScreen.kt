package com.eduquestia.frontend_mobile.ui.screens.courses

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.ModalNavigationDrawer
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.eduquestia.frontend_mobile.data.local.TokenManager
import com.eduquestia.frontend_mobile.data.model.CursoEstudiante
import com.eduquestia.frontend_mobile.data.repository.CursoRepository
import com.eduquestia.frontend_mobile.ui.components.BottomNavBar
import com.eduquestia.frontend_mobile.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesScreen(
    navController: NavController? = null
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val cursoRepository = remember { CursoRepository() }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val authRepository = remember { com.eduquestia.frontend_mobile.data.repository.AuthRepository(tokenManager = tokenManager) }
    val authViewModel: com.eduquestia.frontend_mobile.ui.viewmodel.AuthViewModel = viewModel {
        com.eduquestia.frontend_mobile.ui.viewmodel.AuthViewModel(authRepository = authRepository)
    }

    var userName by remember { mutableStateOf("Usuario") }
    var userEmail by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        userName = tokenManager.getUserNombre() ?: "Usuario"
        userEmail = tokenManager.getUserEmail() ?: ""
    }

    var cursos by remember { mutableStateOf<List<CursoEstudiante>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Calcular estadísticas
    val progresoPromedio = if (cursos.isNotEmpty()) cursos.map { it.progreso }.average().toInt() else 0
    val totalMisionesCompletadas = cursos.sumOf { it.misionesCompletadas }
    val totalMisiones = cursos.sumOf { it.totalMisiones }

    // Cargar cursos al iniciar
    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            error = null
            try {
                val userId = tokenManager.getUserId()
                if (userId != null) {
                    cursoRepository.obtenerCursosPorEstudiante(userId)
                        .onSuccess { cursos = it }
                        .onFailure { error = it.message }
                } else {
                    error = "No se pudo obtener el ID del usuario"
                }
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }

    val currentRoute = com.eduquestia.frontend_mobile.ui.navigation.Screen.Courses.route

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
                            navController?.navigate(com.eduquestia.frontend_mobile.ui.navigation.Screen.Login.route) {
                                popUpTo(com.eduquestia.frontend_mobile.ui.navigation.Screen.Home.route) { inclusive = true }
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
            // Header
            item {
                Column {
                    Text(
                        text = "Mis Cursos",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Estás inscrito en ${cursos.size} asignaturas",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            }

            // Stats Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Progreso Promedio
                    CourseStatCard(
                        icon = Icons.Default.TrendingUp,
                        title = "Progreso Promedio",
                        value = "$progresoPromedio%",
                        iconColor = EduQuestBlue,
                        modifier = Modifier.weight(1f)
                    )

                    // Misiones Completadas
                    CourseStatCard(
                        icon = Icons.Default.Assignment,
                        title = "Misiones Completadas",
                        value = "$totalMisionesCompletadas/$totalMisiones",
                        iconColor = AccentGreen,
                        modifier = Modifier.weight(1f)
                    )

                    // Próxima Clase
                    CourseStatCard(
                        icon = Icons.Default.Schedule,
                        title = "Próxima Clase",
                        value = "Mañana",
                        iconColor = EduQuestPurple,
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

            // Lista de cursos
            if (!isLoading && cursos.isEmpty() && error == null) {
                item {
                    EmptyCoursesMessage()
                }
            }

            items(cursos) { curso ->
                CourseCard(
                    curso = curso,
                    onClick = { /* TODO: Navegar a detalle del curso */ }
                )
            }
        }
    }
    }
}

@Composable
fun CourseStatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    iconColor: Color,
    modifier: Modifier = Modifier
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
    }
}

@Composable
fun CourseCard(
    curso: CursoEstudiante,
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
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Icono del curso
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(EduQuestLightBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "Curso",
                            tint = EduQuestBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = curso.nombre,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Prof. ${curso.profesorNombre ?: "Sin asignar"}",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Badge de progreso
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AccentGreen.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${curso.progreso}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Barra de progreso
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progreso del curso",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "${curso.progreso}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }

                LinearProgressIndicator(
                    progress = { curso.progreso / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = AccentGreen,
                    trackColor = BackgroundGray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Info adicional
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = "Estudiantes",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Estudiantes",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "${curso.totalEstudiantes}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Assignment,
                        contentDescription = "Misiones",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Misiones",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "${curso.misionesCompletadas}/${curso.totalMisiones}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyCoursesMessage() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = "Sin cursos",
                tint = TextLight,
                modifier = Modifier.size(64.dp)
            )

            Text(
                text = "No estás inscrito en ningún curso",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )

            Text(
                text = "Contacta a tu administrador para inscribirte a tus cursos",
                fontSize = 14.sp,
                color = TextLight,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
