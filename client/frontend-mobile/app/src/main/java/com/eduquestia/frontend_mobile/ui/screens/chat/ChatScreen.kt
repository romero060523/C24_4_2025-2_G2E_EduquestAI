package com.eduquestia.frontend_mobile.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.eduquestia.frontend_mobile.data.model.ChatResponse
import com.eduquestia.frontend_mobile.data.repository.ChatRepository
import com.eduquestia.frontend_mobile.ui.components.BottomNavBar
import com.eduquestia.frontend_mobile.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val contenido: String,
    val esUsuario: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController? = null
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val chatRepository = remember { ChatRepository() }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var mensajes by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
    var mensajeActual by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var conversacionId by remember { mutableStateOf<String?>(null) }
    var userId by remember { mutableStateOf<String?>(null) }
    var userRol by remember { mutableStateOf<String?>(null) }
    var userName by remember { mutableStateOf("Usuario") }

    // Obtener datos del usuario al iniciar
    LaunchedEffect(Unit) {
        userId = tokenManager.getUserId()
        userRol = tokenManager.getUserRol()
        userName = tokenManager.getUserNombre() ?: "Usuario"

        // Agregar mensaje de bienvenida de la IA
        val nombreCorto = userName.split(" ").firstOrNull() ?: "Usuario"
        mensajes = listOf(
            ChatMessage(
                contenido = "¡Hola $nombreCorto! 👋 Soy tu asistente educativo IA. ¿En qué puedo ayudarte hoy?",
                esUsuario = false
            )
        )
    }

    // Scroll al último mensaje cuando se agrega uno nuevo
    LaunchedEffect(mensajes.size) {
        if (mensajes.isNotEmpty()) {
            listState.animateScrollToItem(mensajes.size - 1)
        }
    }

    fun enviarMensaje() {
        if (mensajeActual.isBlank() || isLoading) return

        val mensaje = mensajeActual.trim()
        mensajeActual = ""

        // Agregar mensaje del usuario
        mensajes = mensajes + ChatMessage(
            contenido = mensaje,
            esUsuario = true
        )

        // Enviar al backend
        scope.launch {
            isLoading = true
            try {
                val result = chatRepository.enviarMensaje(
                    mensaje = mensaje,
                    usuarioId = userId ?: "",
                    rolUsuario = userRol ?: "estudiante",
                    conversacionId = conversacionId
                )

                result.onSuccess { response ->
                    conversacionId = response.conversacionId
                    mensajes = mensajes + ChatMessage(
                        contenido = response.respuesta,
                        esUsuario = false
                    )
                }.onFailure { error ->
                    mensajes = mensajes + ChatMessage(
                        contenido = "Lo siento, ocurrió un error al procesar tu mensaje. Por favor intenta de nuevo.",
                        esUsuario = false
                    )
                }
            } catch (e: Exception) {
                mensajes = mensajes + ChatMessage(
                    contenido = "Error de conexión. Verifica tu conexión a internet e intenta de nuevo.",
                    esUsuario = false
                )
            } finally {
                isLoading = false
            }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header del Chat
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Chat IA",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Tu asistente educativo personalizado",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Avatar del asistente
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(EduQuestBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = "IA",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Asistente IA",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(AccentGreen)
                                )
                                Text(
                                    text = "En línea",
                                    fontSize = 12.sp,
                                    color = AccentGreen
                                )
                            }
                        }
                    }
                }
            }

            // Lista de mensajes
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(mensajes) { mensaje ->
                    ChatBubble(mensaje = mensaje)
                }

                // Indicador de carga
                if (isLoading) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(BackgroundWhite)
                                    .padding(16.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = EduQuestBlue,
                                        strokeWidth = 2.dp
                                    )
                                    Text(
                                        text = "Escribiendo...",
                                        fontSize = 14.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Input de mensaje
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = mensajeActual,
                        onValueChange = { mensajeActual = it },
                        placeholder = { Text("Escribe tu pregunta...") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        maxLines = 3
                    )

                    FilledIconButton(
                        onClick = { enviarMensaje() },
                        enabled = mensajeActual.isNotBlank() && !isLoading,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = EduQuestBlue,
                            contentColor = Color.White,
                            disabledContainerColor = TextLight,
                            disabledContentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Enviar"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(mensaje: ChatMessage) {
    val alignment = if (mensaje.esUsuario) Alignment.End else Alignment.Start
    val bubbleColor = if (mensaje.esUsuario) EduQuestBlue else BackgroundWhite
    val textColor = if (mensaje.esUsuario) Color.White else TextPrimary
    val bubbleShape = if (mensaje.esUsuario) {
        RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
    }

    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val timeString = timeFormat.format(Date(mensaje.timestamp))

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(bubbleShape)
                .background(bubbleColor)
                .padding(12.dp)
        ) {
            Text(
                text = mensaje.contenido,
                fontSize = 14.sp,
                color = textColor,
                lineHeight = 20.sp
            )
        }

        Text(
            text = timeString,
            fontSize = 10.sp,
            color = TextLight,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}
