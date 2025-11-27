package com.eduquestia.frontend_mobile.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eduquestia.frontend_mobile.ui.theme.*
import com.eduquestia.frontend_mobile.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    // Observar cambios en el estado de autenticación
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onLoginSuccess()
        }
    }

    // Mostrar errores
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            when {
                error.contains("email", ignoreCase = true) -> emailError = error
                error.contains("contraseña", ignoreCase = true) || error.contains("password", ignoreCase = true) -> passwordError = error
                else -> {
                    emailError = null
                    passwordError = null
                }
            }
        }
    }

    // Fondo gris oscuro (simulando el frame del teléfono)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C2C2E)) // Fondo oscuro del Figma
    ) {
        // Card blanco central con bordes redondeados
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 40.dp)
                .align(Alignment.Center),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo con gorra de graduación (usando icono de Person temporalmente)
                Icon(
                    imageVector = Icons.Default.Person, // TODO: Reemplazar con icono personalizado de gorra
                    contentDescription = "EduQuest Logo",
                    modifier = Modifier.size(48.dp),
                    tint = EduQuestBlue
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Título "EduQuest"
                Text(
                    text = "EduQuest",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = EduQuestBlue
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subtítulo "Inicia sesión en tu cuenta"
                Text(
                    text = "Inicia sesión en tu cuenta",
                    fontSize = 14.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Mensaje de bienvenida
                Text(
                    text = "Bienvenido de nuevo",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Ingresa tus credenciales para continuar",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Campo Email con icono
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null
                        viewModel.clearError()
                    },
                    label = { Text("Correo electrónico") },
                    placeholder = { Text("tu@email.com") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Email",
                            tint = TextSecondary
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = emailError != null,
                    supportingText = emailError?.let { { Text(it, color = AccentRed) } },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EduQuestBlue,
                        unfocusedBorderColor = TextSecondary,
                        focusedContainerColor = BackgroundGray,
                        unfocusedContainerColor = BackgroundGray
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo Contraseña con icono
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = null
                        viewModel.clearError()
                    },
                    label = { Text("Contraseña") },
                    placeholder = { Text("Ingresa tu contraseña") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Contraseña",
                            tint = TextSecondary
                        )
                    },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    isError = passwordError != null,
                    supportingText = passwordError?.let { { Text(it, color = AccentRed) } },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EduQuestBlue,
                        unfocusedBorderColor = TextSecondary,
                        focusedContainerColor = BackgroundGray,
                        unfocusedContainerColor = BackgroundGray
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Checkbox "Recordarme" y link "¿Olvidaste tu contraseña?"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = EduQuestBlue
                            )
                        )
                        Text(
                            text = "Recordarme",
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                    }

                    TextButton(onClick = { /* TODO: Implementar recuperación de contraseña */ }) {
                        Text(
                            text = "¿Olvidaste tu contraseña?",
                            fontSize = 14.sp,
                            color = EduQuestBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón "Iniciar sesión"
                Button(
                    onClick = {
                        // Validación básica
                        var hasError = false
                        if (email.isBlank()) {
                            emailError = "El email es obligatorio"
                            hasError = true
                        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            emailError = "Email inválido"
                            hasError = true
                        }

                        if (password.isBlank()) {
                            passwordError = "La contraseña es obligatoria"
                            hasError = true
                        }

                        if (!hasError) {
                            viewModel.login(email.trim(), password)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EduQuestBlue
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = "Iniciar sesión",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Mensaje de error general
                uiState.error?.let { error ->
                    if (emailError == null && passwordError == null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = error,
                            color = AccentRed,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Separador "O prueba los diferentes roles"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = TextLight
                    )
                    Text(
                        text = "O prueba los diferentes roles",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = TextLight
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botones de roles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Botón Estudiante
                    OutlinedButton(
                        onClick = { /* TODO: Auto-fill con credenciales de demo estudiante */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = EduQuestBlue
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(EduQuestBlue, EduQuestBlue))
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Estudiante",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Botón Profesor
                    OutlinedButton(
                        onClick = { /* TODO: Auto-fill con credenciales de demo profesor */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = AccentGreen
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(AccentGreen, AccentGreen))
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Profesor",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Botón Admin
                    OutlinedButton(
                        onClick = { /* TODO: Auto-fill con credenciales de demo admin */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = EduQuestPurple
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(EduQuestPurple, EduQuestPurple))
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Admin",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Link de registro
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "¿No tienes una cuenta? ",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "Registrate aqui",
                        fontSize = 14.sp,
                        color = EduQuestBlue,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { /* TODO: Navegar a registro */ }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Link "Volver al inicio"
                Text(
                    text = "- Volver al inicio",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.clickable { /* TODO: Navegar a inicio público */ }
                )
            }
        }
    }
}
