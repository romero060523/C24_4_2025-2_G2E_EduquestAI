package com.eduquestia.frontend_mobile.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eduquestia.frontend_mobile.ui.theme.*
import com.eduquestia.frontend_mobile.ui.viewmodel.AuthViewModel
import androidx.compose.foundation.background

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo/Icono
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "EduQuest Logo",
            modifier = Modifier.size(64.dp),
            tint = EduQuestBlue
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Título
        Text(
            text = "EduQuest AI",
            fontSize = 32.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = EduQuestDarkBlue
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Sistema Educativo Gamificado",
            fontSize = 16.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Campo Email
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = null
                viewModel.clearError()
            },
            label = { Text("Email") },
            placeholder = { Text("usuario@ejemplo.com") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = emailError != null,
            supportingText = emailError?.let { { Text(it, color = AccentRed) } },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EduQuestBlue,
                unfocusedBorderColor = TextSecondary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null
                viewModel.clearError()
            },
            label = { Text("Contraseña") },
            placeholder = { Text("Ingresa tu contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = passwordError != null,
            supportingText = passwordError?.let { { Text(it, color = AccentRed) } },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EduQuestBlue,
                unfocusedBorderColor = TextSecondary
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botón Login
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
            )
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = "Iniciar Sesión",
                    fontSize = 16.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
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
                    fontSize = 14.sp
                )
            }
        }
    }
}

