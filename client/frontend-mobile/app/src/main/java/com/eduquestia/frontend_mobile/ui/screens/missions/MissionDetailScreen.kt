package com.eduquestia.frontend_mobile.ui.screens.missions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.eduquestia.frontend_mobile.data.model.MisionEstudiante
import com.eduquestia.frontend_mobile.data.repository.MisionRepository
import com.eduquestia.frontend_mobile.ui.theme.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionDetailScreen(
    navController: NavController,
    misionId: String
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val misionRepository = remember { MisionRepository() }
    val scope = rememberCoroutineScope()

    var mision by remember { mutableStateOf<MisionEstudiante?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var contenidoEntrega by remember { mutableStateOf("") }
    var comentarios by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var submitSuccess by remember { mutableStateOf(false) }
    var submitError by remember { mutableStateOf<String?>(null) }
    var puntosGanados by remember { mutableStateOf(0) }

    // Verificar si la fecha límite ha expirado
    fun haExpirado(fechaLimite: String?): Boolean {
        if (fechaLimite == null) return false
        return try {
            val fecha = LocalDateTime.parse(fechaLimite.replace("Z", ""))
            fecha.isBefore(LocalDateTime.now())
        } catch (e: Exception) {
            false
        }
    }

    // Cargar misión
    LaunchedEffect(misionId) {
        isLoading = true
        error = null
        try {
            val userId = tokenManager.getUserId()
            if (userId != null) {
                misionRepository.obtenerMisionPorId(misionId, userId)
                    .onSuccess { mision = it }
                    .onFailure { error = it.message ?: "Error desconocido" }
            } else {
                error = "No se pudo obtener el ID del usuario"
            }
        } catch (e: Exception) {
            error = e.message ?: "Error al cargar la misión"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Misión", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundWhite
                )
            )
        },
        containerColor = BackgroundGray
    ) { paddingValues ->

        when {
            submitSuccess -> {
                // Pantalla de éxito
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completado",
                        tint = AccentGreen,
                        modifier = Modifier.size(96.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "¡Misión Completada!",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = EduQuestLightBlue)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "+$puntosGanados XP",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = EduQuestBlue
                            )
                            Text(
                                text = "¡Has ganado puntos de experiencia!",
                                fontSize = 16.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(containerColor = EduQuestBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text("Volver a Misiones", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = EduQuestBlue)
                }
            }

            error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        tint = AccentRed,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = error ?: "Error desconocido",
                        color = AccentRed,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(containerColor = EduQuestBlue)
                    ) {
                        Text("Volver")
                    }
                }
            }

            mision != null -> {
                val m = mision!!
                val expirado = haExpirado(m.fechaLimite)
                val esQuiz = m.categoria.uppercase() == "QUIZ"

                val difficultyColor = when (m.dificultad.uppercase()) {
                    "FACIL" -> AccentGreen
                    "MEDIO" -> AccentOrange
                    "DIFICIL" -> AccentRed
                    "EXPERTO" -> EduQuestPurple
                    else -> TextSecondary
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header con icono y título
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BackgroundWhite)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(EduQuestLightBlue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (m.categoria.uppercase()) {
                                            "QUIZ" -> Icons.Default.Quiz
                                            "LECTURA" -> Icons.Default.MenuBook
                                            "EJERCICIO" -> Icons.Default.FitnessCenter
                                            "PROYECTO" -> Icons.Default.Work
                                            else -> Icons.Default.Assignment
                                        },
                                        contentDescription = "Misión",
                                        tint = EduQuestBlue,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = m.titulo,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = m.cursoNombre,
                                        fontSize = 14.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            // Badges
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(difficultyColor.copy(alpha = 0.1f))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = m.dificultad.lowercase()
                                            .replaceFirstChar { it.uppercase() },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = difficultyColor
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AccentOrange.copy(alpha = 0.1f))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "XP",
                                            tint = AccentOrange,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "+${m.puntosRecompensa} XP",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = AccentOrange
                                        )
                                    }
                                }

                                if (esQuiz) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(EduQuestPurple.copy(alpha = 0.1f))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "Quiz",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = EduQuestPurple
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Descripción
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BackgroundWhite)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Descripción",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = m.descripcion?.ifBlank { "Sin descripción" }
                                    ?: "Sin descripción disponible",
                                fontSize = 14.sp,
                                color = TextSecondary,
                                lineHeight = 22.sp
                            )

                            HorizontalDivider(color = BackgroundGray)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Fecha límite", fontSize = 12.sp, color = TextLight)
                                    Text(
                                        text = m.fechaLimite?.take(10) ?: "Sin fecha",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (expirado) AccentRed else TextPrimary
                                    )
                                    if (expirado) {
                                        Text(
                                            text = "⚠️ Expirada",
                                            fontSize = 12.sp,
                                            color = AccentRed
                                        )
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Categoría", fontSize = 12.sp, color = TextLight)
                                    Text(
                                        text = m.categoria.lowercase()
                                            .replaceFirstChar { it.uppercase() },
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }
                    }

                    // Contenido según el tipo
                    when {
                        m.completada -> {
                            // Misión ya completada
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = AccentGreen.copy(alpha = 0.1f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Completada",
                                        tint = AccentGreen,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Text(
                                        text = "¡Misión Completada!",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AccentGreen
                                    )
                                    Text(
                                        text = "Has ganado ${m.puntosObtenidos ?: m.puntosRecompensa} XP",
                                        fontSize = 14.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }

                        esQuiz -> {
                            // Es un Quiz - mostrar mensaje
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = EduQuestPurple.copy(alpha = 0.1f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Quiz,
                                        contentDescription = "Quiz",
                                        tint = EduQuestPurple,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Text(
                                        text = "Esta es una evaluación tipo Quiz",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EduQuestPurple
                                    )
                                    Text(
                                        text = "Los quizzes tienen preguntas con opciones de respuesta. " +
                                                "Responde correctamente para ganar puntos.",
                                        fontSize = 14.sp,
                                        color = TextSecondary,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )

                                    // Por ahora mostrar como no disponible
                                    Button(
                                        onClick = { /* TODO: Implementar Quiz */ },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = EduQuestPurple
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                        enabled = !expirado
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Iniciar",
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            if (expirado) "Quiz Expirado" else "Iniciar Quiz",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        expirado -> {
                            // Misión expirada
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = AccentRed.copy(alpha = 0.1f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = "Expirada",
                                        tint = AccentRed,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Text(
                                        text = "Misión Expirada",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AccentRed
                                    )
                                    Text(
                                        text = "La fecha límite para esta misión ha pasado.",
                                        fontSize = 14.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }

                        else -> {
                            // Formulario de entrega
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = BackgroundWhite)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Text(
                                        text = "Entregar Misión",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )

                                    OutlinedTextField(
                                        value = contenidoEntrega,
                                        onValueChange = { contenidoEntrega = it },
                                        label = { Text("Tu respuesta *") },
                                        placeholder = { Text("Escribe tu respuesta o entrega aquí...") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(150.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = EduQuestBlue,
                                            cursorColor = EduQuestBlue
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    )

                                    OutlinedTextField(
                                        value = comentarios,
                                        onValueChange = { comentarios = it },
                                        label = { Text("Comentarios (opcional)") },
                                        placeholder = { Text("Añade comentarios adicionales...") },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = EduQuestBlue,
                                            cursorColor = EduQuestBlue
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    )

                                    // Mostrar error de envío
                                    submitError?.let { errorMsg ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(
                                                containerColor = AccentRed.copy(alpha = 0.1f)
                                            )
                                        ) {
                                            Text(
                                                text = errorMsg,
                                                color = AccentRed,
                                                modifier = Modifier.padding(12.dp),
                                                fontSize = 14.sp
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            scope.launch {
                                                isSubmitting = true
                                                submitError = null
                                                try {
                                                    val userId = tokenManager.getUserId()
                                                    if (userId != null) {
                                                        misionRepository.completarMision(
                                                            misionId = misionId,
                                                            estudianteId = userId,
                                                            contenidoEntrega = contenidoEntrega,
                                                            comentarios = comentarios.ifBlank { null }
                                                        ).onSuccess { result ->
                                                            puntosGanados =
                                                                result.puntosObtenidos
                                                                    ?: m.puntosRecompensa
                                                            submitSuccess = true
                                                        }.onFailure { e ->
                                                            submitError = e.message
                                                                ?: "Error al completar misión"
                                                        }
                                                    } else {
                                                        submitError =
                                                            "No se pudo obtener el ID del usuario"
                                                    }
                                                } catch (e: Exception) {
                                                    submitError =
                                                        e.message ?: "Error al enviar"
                                                } finally {
                                                    isSubmitting = false
                                                }
                                            }
                                        },
                                        enabled = contenidoEntrega.isNotBlank() && !isSubmitting,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = EduQuestBlue,
                                            disabledContainerColor = EduQuestBlue.copy(alpha = 0.5f)
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp)
                                    ) {
                                        if (isSubmitting) {
                                            CircularProgressIndicator(
                                                color = Color.White,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Send,
                                                contentDescription = "Enviar",
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                "Completar Misión",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
