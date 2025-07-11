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
import com.chiu.renovadoproyecto1.features.juegos.di.JuegosModule
import com.chiu.renovadoproyecto1.features.juegos.domain.model.Juego
import com.chiu.renovadoproyecto1.features.juegos.presentation.View.Dialog.*
import com.chiu.renovadoproyecto1.features.juegos.presentation.ViewModel.*
import com.chiu.renovadoproyecto1.features.juegos.presentation.View.Content.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JuegosScreen(
    onNavigateToLogin: () -> Unit = {}
) {
    val context = LocalContext.current

    val viewModel: JuegosViewModel = viewModel(
        factory = JuegosModule.getJuegosViewModelFactory(context)
    )

    val state by viewModel.state.collectAsState()
    val authState by viewModel.authState.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var juegoToEdit by remember { mutableStateOf<Juego?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var juegoToDelete by remember { mutableStateOf<Juego?>(null) }

    LaunchedEffect(authState) {
        if (!authState) {
            Toast.makeText(context, "Sesión expirada", Toast.LENGTH_SHORT).show()
            onNavigateToLogin()
        }
    }

    LaunchedEffect(state) {
        val currentState = state
        if (currentState is JuegosState.ActionSuccess) {
            Toast.makeText(context, currentState.message, Toast.LENGTH_SHORT).show()
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
                )
            },
            floatingActionButton = {
                val currentState = state
                if (currentState is JuegosState.Success) {
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
                // Header mejorado
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
                            // Layout vertical para pantallas pequeñas
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
                                        Text(
                                            text = "📊 ${currentState.juegos.size} ${if (currentState.juegos.size == 1) "título disponible" else "títulos disponibles"}",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Medium
                                        )
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
                            // Layout horizontal para pantallas más grandes
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

                // Contenido principal
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
                }
            }
        }

        if (showCreateDialog) {
            CreateJuegoDialog(
                onDismiss = { showCreateDialog = false },
                onConfirm = { juego ->
                    viewModel.createJuego(juego)
                    showCreateDialog = false
                }
            )
        }

        if (showEditDialog && juegoToEdit != null) {
            EditJuegoDialog(
                juego = juegoToEdit!!,
                onDismiss = { showEditDialog = false },
                onConfirm = { juegoEditado ->
                    viewModel.updateJuego(juegoEditado)
                    showEditDialog = false
                }
            )
        }

        if (showDeleteDialog && juegoToDelete != null) {
            DeleteJuegoDialog(
                juego = juegoToDelete!!,
                onDismiss = { showDeleteDialog = false },
                onConfirm = {
                    viewModel.deleteJuego(juegoToDelete!!.id!!)
                    showDeleteDialog = false
                }
            )
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
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                GameImage(
                    imageData  = juego.logo,
                    gameName = juego.nombre,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = juego.nombre ?: "Sin nombre",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
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
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("📦", style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Stock: ${juego.cantidad ?: 0}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
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
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
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
