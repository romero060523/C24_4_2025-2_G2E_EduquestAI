package com.eduquestia.frontend_mobile.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.eduquestia.frontend_mobile.ui.theme.BackgroundGray

@Composable
fun HomeScreen(navController: NavController? = null) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text("Home Screen - En desarrollo")
    }
}

