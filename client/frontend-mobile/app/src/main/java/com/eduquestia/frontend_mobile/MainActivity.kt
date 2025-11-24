package com.eduquestia.frontend_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.eduquestia.frontend_mobile.data.local.TokenManager
import com.eduquestia.frontend_mobile.data.repository.AuthRepository
import com.eduquestia.frontend_mobile.ui.navigation.NavGraph
import com.eduquestia.frontend_mobile.ui.navigation.Screen
import com.eduquestia.frontend_mobile.ui.theme.FrontendmobileTheme
import com.eduquestia.frontend_mobile.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.first

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            FrontendmobileTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val tokenManager = remember { TokenManager(this@MainActivity) }
                    val authRepository = remember { AuthRepository(tokenManager = tokenManager) }
                    val authViewModel: AuthViewModel = viewModel { 
                        AuthViewModel(authRepository = authRepository)
                    }
                    
                    val navController = rememberNavController()
                    
                    // Determinar destino inicial basado en autenticación
                    var startDestination by remember { mutableStateOf<String?>(null) }
                    
                    LaunchedEffect(Unit) {
                        val isLoggedIn = tokenManager.isLoggedIn()
                        startDestination = if (isLoggedIn) {
                            Screen.Home.route
                        } else {
                            Screen.Login.route
                        }
                    }
                    
                    // Mostrar NavGraph solo cuando tengamos el destino inicial
                    startDestination?.let { destination ->
                        NavGraph(
                            navController = navController,
                            startDestination = destination
                        )
                    }
                }
            }
        }
    }
}
