package com.chiu.renovadoproyecto1.features.juegos.presentation.View

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import android.util.Log
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chiu.renovadoproyecto1.core.network.NetworkState
import com.chiu.renovadoproyecto1.features.juegos.di.JuegosModule
import com.chiu.renovadoproyecto1.features.juegos.domain.model.Juego
import com.chiu.renovadoproyecto1.features.juegos.presentation.View.Dialog.*
import com.chiu.renovadoproyecto1.features.juegos.presentation.ViewModel.*
import com.chiu.renovadoproyecto1.features.juegos.presentation.View.Content.*
import com.chiu.renovadoproyecto1.features.juegos.presentation.View.components.GameImage
import com.chiu.renovadoproyecto1.core.security.SecureScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JuegosScreen(
    onNavigateToLogin: () -> Unit = {},
    onNavigateToUsuarios: () -> Unit = {}
) {
    SecureScreen {
    val context = LocalContext.current

    val viewModel: JuegosViewModel = viewModel(
        factory = JuegosModule.getJuegosViewModelFactory(context)
    )

    val state by viewModel.state.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val cameraState by viewModel.cameraState.collectAsState()

    val networkState by viewModel.networkState.collectAsState()
    val connectionStatus by viewModel.connectionStatus.collectAsState()
    val connectionType by viewModel.connectionType.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var juegoToEdit by remember { mutableStateOf<Juego?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var juegoToDelete by remember { mutableStateOf<Juego?>(null) }

    LaunchedEffect(state) {
        val currentState = state
        when (currentState) {
            is JuegosState.ActionSuccess -> {
                Toast.makeText(context, currentState.message, Toast.LENGTH_SHORT).show()
            }

            is JuegosState.OfflineSaved -> {
                Toast.makeText(context, currentState.message, Toast.LENGTH_LONG).show()
            }

            else -> {}
        }
    }

    LaunchedEffect(authState) {
        if (!authState) {
            Toast.makeText(context, "Sesión expirada", Toast.LENGTH_SHORT).show()
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "🎮",
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "GameStore Admin",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    actions = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = onNavigateToUsuarios,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(
                                        MaterialTheme.colorScheme.secondaryContainer.copy(
                                            alpha = 0.8f
                                        )
                                    )
                            ) {
                                Icon(
                                    Icons.Default.People,
                                    contentDescription = "Ver Usuarios",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                            // Botón de estado de red
                            IconButton(
                                onClick = {
                                    if (!connectionStatus) {
                                        viewModel.retryLastOperation()
                                    } else {
                                        viewModel.checkNetworkStatus()
                                    }
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = when (networkState) {
                                        NetworkState.NoConnection -> Icons.Default.CloudOff
                                        NetworkState.Wifi -> Icons.Default.Wifi
                                        NetworkState.Mobile -> Icons.Default.SignalCellular4Bar
                                        NetworkState.Unknown -> Icons.Default.CloudSync
                                    },
                                    contentDescription = if (connectionStatus) {
                                        "Conectado por $connectionType"
                                    } else {
                                        "Sin conexión - Toca para reintentar"
                                    },
                                    tint = if (connectionStatus) {
                                        when (networkState) {
                                            NetworkState.Wifi -> Color(0xFF4CAF50)
                                            NetworkState.Mobile -> Color(0xFF2196F3)
                                            else -> MaterialTheme.colorScheme.primary
                                        }
                                    } else {
                                        MaterialTheme.colorScheme.error
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Botón de logout
                            IconButton(
                                onClick = { viewModel.logout() },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f))
                            ) {
                                Icon(
                                    Icons.Default.ExitToApp,
                                    contentDescription = "Cerrar Sesión",
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                )
            },
            floatingActionButton = {
                val currentState = state
                if (currentState !is JuegosState.Loading) {
                    AnimatedVisibility(
                        visible = true,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut()
                    ) {
                        ExtendedFloatingActionButton(
                            onClick = { showCreateDialog = true },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Agregar Juego")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Nuevo Juego", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        if (maxWidth < 400.dp) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "📚 Biblioteca de Juegos",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    val currentState = state
                                    if (currentState is JuegosState.Success) {
                                        val onlineCount =
                                            currentState.juegos.count { !it.isOffline }
                                        val offlineCount =
                                            currentState.juegos.count { it.isOffline }

                                        Column {
                                            Text(
                                                text = "📊 ${currentState.juegos.size} ${if (currentState.juegos.size == 1) "título disponible" else "títulos disponibles"}",
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Medium
                                            )

                                            if (offlineCount > 0) {
                                                Text(
                                                    text = "🌐 Online: $onlineCount • 💾 Offline: $offlineCount",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }

                                FilledTonalButton(
                                    onClick = { viewModel.loadJuegos() },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                ) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = "Actualizar lista"
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Actualizar", fontWeight = FontWeight.SemiBold)
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "📚 Biblioteca de Juegos",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    val currentState = state
                                    if (currentState is JuegosState.Success) {
                                        Text(
                                            text = "📊 ${currentState.juegos.size} ${if (currentState.juegos.size == 1) "título disponible" else "títulos disponibles"}",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                FilledTonalButton(
                                    onClick = { viewModel.loadJuegos() },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                ) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = "Actualizar lista"
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Actualizar", fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }

                val currentState = state
                when (currentState) {
                    is JuegosState.Loading -> {
                        LoadingContent()
                    }

                    is JuegosState.Success -> {
                        SuccessContent(
                            juegos = currentState.juegos,
                            onAddClick = { showCreateDialog = true },
                            onEdit = { juego ->
                                juegoToEdit = juego
                                showEditDialog = true
                            },
                            onDelete = { juego ->
                                juegoToDelete = juego
                                showDeleteDialog = true
                            }
                        )
                    }

                    is JuegosState.Error -> {
                        ErrorContent(
                            error = currentState.error,
                            onRetry = { viewModel.loadJuegos() }
                        )
                    }

                    is JuegosState.ActionSuccess -> {
                        ActionSuccessContent(message = currentState.message)
                    }

                    is JuegosState.OfflineSaved -> {
                        // ✅ Mostrar contenido especial para guardado offline
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(24.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "💾",
                                    style = MaterialTheme.typography.headlineMedium
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = currentState.message,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

            }
        }

        if (showCreateDialog) {
            CreateJuegoDialog(
                onDismiss = {
                    showCreateDialog = false
                    viewModel.resetCameraState()
                },
                onConfirm = { juego ->
                    viewModel.createJuego(juego)
                    showCreateDialog = false
                    viewModel.resetCameraState()
                },
                cameraState = cameraState,
                onCapturePhoto = { viewModel.capturePhoto() },
                onRequestPermission = { viewModel.requestCameraPermission() },
                onResetCameraState = { viewModel.resetCameraState() },
                hasCameraPermission = viewModel.hasCameraPermission(),
                isCameraAvailable = viewModel.isCameraAvailable()
            )
        }

        if (showEditDialog && juegoToEdit != null) {
            EditJuegoDialog(
                juego = juegoToEdit!!,
                onDismiss = {
                    showEditDialog = false
                    viewModel.resetCameraState()
                },
                onConfirm = { juegoEditado ->
                    viewModel.updateJuego(juegoEditado)
                    showEditDialog = false
                    viewModel.resetCameraState()
                },
                cameraState = cameraState,
                onCapturePhoto = { viewModel.capturePhoto() },
                onRequestPermission = { viewModel.requestCameraPermission() },
                onResetCameraState = { viewModel.resetCameraState() },
                hasCameraPermission = viewModel.hasCameraPermission(),
                isCameraAvailable = viewModel.isCameraAvailable()
            )
        }

        if (showDeleteDialog && juegoToDelete != null) {
            DeleteJuegoDialog(
                juego = juegoToDelete!!,
                onDismiss = { showDeleteDialog = false },
                onConfirm = {
                    val juego = juegoToDelete!!
                    if (juego.isOffline) {
                        viewModel.deleteOfflineJuego(juego)
                    } else {
                        juego.id?.let { id ->
                            viewModel.deleteJuego(id)
                        } ?: run {
                            Log.e("JuegosScreen", "❌ ID nulo para juego online")
                        }
                    }
                    showDeleteDialog = false
                }
            )
        }
    }
}
}

@Composable
fun EmptyJuegosCard(onAddClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "🎮",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = "¡Tu biblioteca está vacía!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Ingresa el primer juego",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onAddClick,
                modifier = Modifier.fillMaxWidth(),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "🚀",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agregar primer juego", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun JuegoCard(
    juego: Juego,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        colors = CardDefaults.cardColors(
            // ✅ Color diferente para juegos offline
            containerColor = if (juego.isOffline) {
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            if (juego.isOffline) {
                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                            } else {
                                MaterialTheme.colorScheme.surface
                            },
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            // ✅ Indicador offline en la parte superior
            if (juego.isOffline) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "📱 OFFLINE",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                GameImage(
                    imageData = juego.logo,
                    gameName = juego.nombre,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = juego.nombre ?: "Sin nombre",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (juego.isOffline) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "💾",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (juego.isOffline) {
                                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                            } else {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            }
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🏢", style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = juego.compania ?: "Sin compañía",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (juego.isOffline) {
                                        MaterialTheme.colorScheme.onTertiaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    }
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("📦", style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Stock: ${juego.cantidad ?: 0}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (juego.isOffline) {
                                        MaterialTheme.colorScheme.onTertiaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = juego.descripcion ?: "Sin descripción disponible",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalIconButton(
                        onClick = onEdit,
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = if (juego.isOffline) {
                                MaterialTheme.colorScheme.tertiaryContainer
                            } else {
                                MaterialTheme.colorScheme.primaryContainer
                            }
                        ),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = if (juego.isOffline) {
                                MaterialTheme.colorScheme.onTertiaryContainer
                            } else {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    FilledTonalIconButton(
                        onClick = onDelete,
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}