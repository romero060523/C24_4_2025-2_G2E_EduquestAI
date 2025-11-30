package com.eduquestia.frontend_mobile.ui.screens.shop

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.eduquestia.frontend_mobile.R
import com.eduquestia.frontend_mobile.data.local.TokenManager
import com.eduquestia.frontend_mobile.data.model.ItemTienda
import com.eduquestia.frontend_mobile.data.model.ItemsTiendaDefault
import com.eduquestia.frontend_mobile.data.repository.AuthRepository
import com.eduquestia.frontend_mobile.ui.components.AppDrawer
import com.eduquestia.frontend_mobile.ui.components.BottomNavBar
import com.eduquestia.frontend_mobile.ui.navigation.Screen
import com.eduquestia.frontend_mobile.ui.theme.*
import com.eduquestia.frontend_mobile.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(navController: NavController? = null) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val authRepository = remember { AuthRepository(tokenManager = tokenManager) }
    val authViewModel: AuthViewModel = viewModel { AuthViewModel(authRepository = authRepository) }

    var userName by remember { mutableStateOf("Usuario") }
    var userEmail by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        userName = tokenManager.getUserNombre() ?: "Usuario"
        userEmail = tokenManager.getUserEmail() ?: ""
    }

    // Estado de monedas del usuario (TODO: obtener del backend/local)
    var eduCoins by remember { mutableIntStateOf(250) }
    var xpTotal by remember { mutableIntStateOf(1250) }
    var nivel by remember { mutableIntStateOf(5) }

    // Items de la tienda
    val items = remember { ItemsTiendaDefault.obtenerItems() }
    var selectedItem by remember { mutableStateOf<ItemTienda?>(null) }
    var showPurchaseDialog by remember { mutableStateOf(false) }

    // Items comprados (TODO: persistir)
    var itemsComprados by remember { mutableStateOf(setOf<String>()) }

    val currentRoute = Screen.Shop.route

    ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                AppDrawer(
                        currentRoute = currentRoute,
                        userName = userName,
                        userEmail = userEmail,
                        onMenuItemClick = { item ->
                            scope.launch { drawerState.close() }
                            when {
                                item.isLogout -> {
                                    authViewModel.logout()
                                    navController?.navigate(Screen.Login.route) {
                                        popUpTo(Screen.Home.route) { inclusive = true }
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
                                            imageVector = Icons.Default.Store,
                                            contentDescription = "Tienda",
                                            tint = EduQuestPurple
                                    )
                                    Text(
                                            text = "Tienda",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
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
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                // Header con stats del usuario
                UserStatsHeader(eduCoins = eduCoins, xp = xpTotal, nivel = nivel)

                // Grid de items
                LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                ) {
                    items(items) { item ->
                        ShopItemCard(
                                item = item,
                                isOwned = itemsComprados.contains(item.id),
                                canAfford = eduCoins >= item.precio,
                                onClick = {
                                    selectedItem = item
                                    showPurchaseDialog = true
                                }
                        )
                    }
                }
            }
        }
    }

    // Diálogo de compra
    if (showPurchaseDialog && selectedItem != null) {
        PurchaseDialog(
                item = selectedItem!!,
                currentCoins = eduCoins,
                isOwned = itemsComprados.contains(selectedItem!!.id),
                onDismiss = { showPurchaseDialog = false },
                onPurchase = {
                    if (eduCoins >= selectedItem!!.precio &&
                                    !itemsComprados.contains(selectedItem!!.id)
                    ) {
                        eduCoins -= selectedItem!!.precio
                        itemsComprados = itemsComprados + selectedItem!!.id
                    }
                    showPurchaseDialog = false
                }
        )
    }
}

@Composable
fun UserStatsHeader(eduCoins: Int, xp: Int, nivel: Int) {
    Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                                                ),
                                        shape = RoundedCornerShape(20.dp)
                                )
                                .padding(20.dp)
        ) {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                // Nivel
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Nivel", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    Text(
                            text = "$nivel",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                    )
                }

                // XP con imagen
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Image(
                            painter = painterResource(id = R.drawable.exp),
                            contentDescription = "XP",
                            modifier = Modifier.size(28.dp),
                            contentScale = ContentScale.Fit
                    )
                    Column {
                        Text(text = "XP", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                        Text(
                                text = "$xp",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                        )
                    }
                }

                // EduCoins con imagen
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier =
                                Modifier.background(
                                                color = Color.White.copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Image(
                            painter = painterResource(id = R.drawable.coin1),
                            contentDescription = "EduCoins",
                            modifier = Modifier.size(28.dp),
                            contentScale = ContentScale.Fit
                    )
                    Text(
                            text = "$eduCoins",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ShopItemCard(item: ItemTienda, isOwned: Boolean, canAfford: Boolean, onClick: () -> Unit) {
    Card(
            modifier =
                    Modifier.fillMaxWidth()
                            .aspectRatio(0.8f)
                            .clickable(enabled = !isOwned, onClick = onClick),
            shape = RoundedCornerShape(16.dp),
            colors =
                    CardDefaults.cardColors(
                            containerColor = if (isOwned) BackgroundGray else BackgroundWhite
                    ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isOwned) 0.dp else 4.dp)
    ) {
        Column(
                modifier = Modifier.fillMaxSize().padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Imagen del item
            Box(
                    modifier =
                            Modifier.fillMaxWidth()
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(EduQuestPurple.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
            ) {
                // Placeholder si no hay imagen
                item.imagenRes?.let { resId ->
                    Image(
                            painter = painterResource(id = resId),
                            contentDescription = item.nombre,
                            modifier = Modifier.fillMaxSize().padding(8.dp),
                            contentScale = ContentScale.Fit
                    )
                }
                        ?: Icon(
                                imageVector = Icons.Default.CardGiftcard,
                                contentDescription = item.nombre,
                                tint = EduQuestPurple,
                                modifier = Modifier.size(48.dp)
                        )

                // Badge de comprado
                if (isOwned) {
                    Box(
                            modifier =
                                    Modifier.fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                    ) {
                        Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Comprado",
                                tint = AccentGreen,
                                modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Nombre
            Text(
                    text = item.nombre,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isOwned) TextSecondary else TextPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Precio
            if (!isOwned) {
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier =
                                Modifier.background(
                                                color =
                                                        if (canAfford)
                                                                AccentOrange.copy(alpha = 0.1f)
                                                        else AccentRed.copy(alpha = 0.1f),
                                                shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Box(
                            modifier = Modifier.size(16.dp).background(AccentOrange, CircleShape),
                            contentAlignment = Alignment.Center
                    ) {
                        Text(
                                text = "E",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                        )
                    }
                    Text(
                            text = "${item.precio}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (canAfford) AccentOrange else AccentRed
                    )
                }
            } else {
                Text(
                        text = "Obtenido",
                        fontSize = 12.sp,
                        color = AccentGreen,
                        fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun PurchaseDialog(
        item: ItemTienda,
        currentCoins: Int,
        isOwned: Boolean,
        onDismiss: () -> Unit,
        onPurchase: () -> Unit
) {
    val canAfford = currentCoins >= item.precio && !isOwned

    AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = BackgroundWhite,
            shape = RoundedCornerShape(24.dp),
            title = { Text(text = item.nombre, fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Imagen
                    Box(
                            modifier =
                                    Modifier.fillMaxWidth()
                                            .height(120.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(EduQuestPurple.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                    ) {
                        item.imagenRes?.let { resId ->
                            Image(
                                    painter = painterResource(id = resId),
                                    contentDescription = item.nombre,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                            )
                        }
                                ?: Icon(
                                        imageVector = Icons.Default.CardGiftcard,
                                        contentDescription = item.nombre,
                                        tint = EduQuestPurple,
                                        modifier = Modifier.size(64.dp)
                                )
                    }

                    Text(text = item.descripcion, fontSize = 14.sp, color = TextSecondary)

                    item.beneficio?.let { beneficio ->
                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .background(
                                                        AccentGreen.copy(alpha = 0.1f),
                                                        RoundedCornerShape(8.dp)
                                                )
                                                .padding(12.dp)
                        ) {
                            Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Beneficio",
                                    tint = AccentGreen,
                                    modifier = Modifier.size(20.dp)
                            )
                            Text(
                                    text = beneficio,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = AccentGreen
                            )
                        }
                    }

                    // Precio y balance
                    Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Precio:", fontSize = 14.sp, color = TextSecondary)
                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                    modifier =
                                            Modifier.size(20.dp)
                                                    .background(AccentOrange, CircleShape),
                                    contentAlignment = Alignment.Center
                            ) {
                                Text(
                                        text = "E",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                )
                            }
                            Text(
                                    text = "${item.precio}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                            )
                        }
                    }

                    Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Tu balance:", fontSize = 14.sp, color = TextSecondary)
                        Text(
                                text = "$currentCoins EduCoins",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (canAfford) AccentGreen else AccentRed
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                        onClick = onPurchase,
                        enabled = canAfford,
                        colors =
                                ButtonDefaults.buttonColors(
                                        containerColor =
                                                if (canAfford) EduQuestBlue else BackgroundGray,
                                        disabledContainerColor = BackgroundGray
                                ),
                        shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                            text =
                                    if (isOwned) "Ya lo tienes"
                                    else if (canAfford) "Comprar" else "Sin fondos",
                            fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text(text = "Cancelar", color = TextSecondary) }
            }
    )
}
