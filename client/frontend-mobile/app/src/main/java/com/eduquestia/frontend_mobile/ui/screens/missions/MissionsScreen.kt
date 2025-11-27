package com.eduquestia.frontend_mobile.ui.screens.missions

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
import com.eduquestia.frontend_mobile.data.model.MisionEstudiante
import com.eduquestia.frontend_mobile.data.repository.MisionRepository
import com.eduquestia.frontend_mobile.ui.components.BottomNavBar
import com.eduquestia.frontend_mobile.ui.navigation.Screen
import com.eduquestia.frontend_mobile.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionsScreen(
    navController: NavController? = null
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val misionRepository = remember { MisionRepository() }
    val scope = rememberCoroutineScope()

    var misiones by remember { mutableStateOf<List<MisionEstudiante>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) }

    // Cargar misiones al iniciar
    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            error = null
            try {
                val userId = tokenManager.getUserId()
                if (userId != null) {
                    misionRepository.obtenerMisionesPorEstudiante(userId)
                        .onSuccess { misiones = it }
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

    val misionesActivas = misiones.filter { !it.completada }
    val misionesCompletadas = misiones.filter { it.completada }
    val misionesVencenHoy = misionesActivas.filter {
        it.fechaLimite?.contains("T")?.let { true } ?: false // TODO: Comparar fecha real
    }
    val xpSemana = misiones.sumOf { it.puntosObtenidos ?: 0 }

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
                    IconButton(onClick = { /* TODO: Menú */ }) {
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
                        text = "Misiones",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Completa misiones para ganar puntos y subir de nivel",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            }

            // Stats Grid (2x2)
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MissionStatCard(
                            value = "${misionesActivas.size}",
                            label = "Activas",
                            color = EduQuestBlue,
                            modifier = Modifier.weight(1f)
                        )
                        MissionStatCard(
                            value = "${misionesCompletadas.size}",
                            label = "Completadas",
                            color = AccentGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MissionStatCard(
                            value = "${misionesVencenHoy.size}",
                            label = "Vencen hoy",
                            color = AccentOrange,
                            modifier = Modifier.weight(1f)
                        )
                        MissionStatCard(
                            value = "+$xpSemana",
                            label = "XP esta semana",
                            color = EduQuestPurple,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Tabs
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = BackgroundWhite,
                    contentColor = EduQuestBlue
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "Activas (${misionesActivas.size})",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "Completadas (${misionesCompletadas.size})",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        }
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

            // Lista de misiones según tab seleccionado
            val misionesToShow = if (selectedTab == 0) misionesActivas else misionesCompletadas

            if (!isLoading && misionesToShow.isEmpty() && error == null) {
                item {
                    EmptyMissionsMessage(isCompletedTab = selectedTab == 1)
                }
            }

            items(misionesToShow) { mision ->
                MissionDetailCard(
                    mision = mision,
                    onClick = {
                        navController?.navigate(Screen.MissionDetail.createRoute(mision.id))
                    }
                )
            }
        }
    }
}

@Composable
fun MissionStatCard(
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
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                fontSize = 14.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun MissionDetailCard(
    mision: MisionEstudiante,
    onClick: () -> Unit
) {
    val difficultyColor = when (mision.dificultad.lowercase()) {
        "facil" -> AccentGreen
        "medio" -> AccentOrange
        "dificil" -> AccentRed
        "experto" -> EduQuestPurple
        else -> TextSecondary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
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
                            imageVector = when (mision.categoria.lowercase()) {
                                "quiz" -> Icons.Default.Quiz
                                "lectura" -> Icons.Default.MenuBook
                                "ejercicio" -> Icons.Default.FitnessCenter
                                "proyecto" -> Icons.Default.Work
                                else -> Icons.Default.Assignment
                            },
                            contentDescription = "Misión",
                            tint = EduQuestBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = mision.titulo,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = mision.cursoNombre,
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Badge de dificultad
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(difficultyColor.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = mision.dificultad.replaceFirstChar { it.uppercase() },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = difficultyColor
                    )
                }
            }

            // Info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Duración",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "30 min", // TODO: Obtener duración real
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "XP",
                        tint = AccentOrange,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "+${mision.puntosRecompensa} XP",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = AccentOrange
                    )
                }
            }

            // Fecha límite y botón
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = "Fecha límite",
                        tint = AccentRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Vence: ${mision.fechaLimite?.take(10) ?: "Sin fecha"}",
                        fontSize = 12.sp,
                        color = AccentRed
                    )
                }

                if (!mision.completada) {
                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(containerColor = EduQuestBlue),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Comenzar",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completada",
                            tint = AccentGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Completada",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = AccentGreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyMissionsMessage(isCompletedTab: Boolean) {
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
                imageVector = if (isCompletedTab) Icons.Default.CheckCircle else Icons.Default.Assignment,
                contentDescription = "Sin misiones",
                tint = TextLight,
                modifier = Modifier.size(64.dp)
            )

            Text(
                text = if (isCompletedTab) "Aún no has completado misiones" else "No tienes misiones asignadas",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )

            Text(
                text = if (isCompletedTab)
                    "Completa tus misiones activas para verlas aquí"
                else
                    "Tu profesor asignará misiones a tu curso",
                fontSize = 14.sp,
                color = TextLight,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
