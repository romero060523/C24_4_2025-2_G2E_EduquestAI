package com.eduquestia.frontend_mobile.ui.screens.ranking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.eduquestia.frontend_mobile.data.local.TokenManager
import com.eduquestia.frontend_mobile.data.model.Ranking
import com.eduquestia.frontend_mobile.data.model.RankingEstudiante
import com.eduquestia.frontend_mobile.data.repository.GamificacionRepository
import com.eduquestia.frontend_mobile.ui.components.BottomNavBar
import com.eduquestia.frontend_mobile.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(
    navController: NavController? = null
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val gamificacionRepository = remember { GamificacionRepository() }
    val scope = rememberCoroutineScope()

    var ranking by remember { mutableStateOf<Ranking?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var currentUserId by remember { mutableStateOf<String?>(null) }

    // Cargar ranking al iniciar
    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            error = null
            currentUserId = tokenManager.getUserId()

            gamificacionRepository.obtenerRankingGlobal()
                .onSuccess { ranking = it }
                .onFailure { error = it.message }

            isLoading = false
        }
    }

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
                        text = "Ranking",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Compite con tus compañeros y sube de posición",
                        fontSize = 14.sp,
                        color = TextSecondary
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

            // Lista de ranking
            ranking?.let { rankingData ->
                item {
                    Text(
                        text = "${rankingData.cursoNombre} - ${rankingData.totalEstudiantes} estudiantes",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                itemsIndexed(rankingData.estudiantes) { index, estudiante ->
                    RankingCard(
                        estudiante = estudiante,
                        posicion = index + 1,
                        isCurrentUser = estudiante.estudianteId == currentUserId
                    )
                }
            }

            if (!isLoading && ranking?.estudiantes?.isEmpty() == true) {
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
                                imageVector = Icons.Default.Leaderboard,
                                contentDescription = "Sin ranking",
                                tint = TextLight,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "El ranking estará disponible pronto",
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RankingCard(
    estudiante: RankingEstudiante,
    posicion: Int,
    isCurrentUser: Boolean
) {
    val medalColor = when (posicion) {
        1 -> Color(0xFFFFD700) // Oro
        2 -> Color(0xFFC0C0C0) // Plata
        3 -> Color(0xFFCD7F32) // Bronce
        else -> TextLight
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentUser) EduQuestBlue.copy(alpha = 0.1f) else BackgroundWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (posicion <= 3) 4.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Posición
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (posicion <= 3) medalColor else BackgroundGray),
                contentAlignment = Alignment.Center
            ) {
                if (posicion <= 3) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Medalla",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "#$posicion",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            // Info del estudiante
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = estudiante.nombreEstudiante,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "${estudiante.nombreNivel} • ${estudiante.misionesCompletadas} misiones",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            // Puntos
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${estudiante.puntosTotales}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentOrange
                )
                Text(
                    text = "XP",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }
    }
}
