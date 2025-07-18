package com.chiu.renovadoproyecto1.features.juegos.presentation.View.Dialog

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.chiu.renovadoproyecto1.features.juegos.domain.model.Juego
import com.chiu.renovadoproyecto1.features.juegos.presentation.View.components.ImageCameraPicker
import com.chiu.renovadoproyecto1.features.juegos.presentation.ViewModel.CameraState

@Composable
fun CreateJuegoDialog(
    onDismiss: () -> Unit,
    onConfirm: (Juego) -> Unit,
    cameraState: CameraState,
    onCapturePhoto: () -> Unit,
    onRequestPermission: () -> Unit,
    onResetCameraState: () -> Unit,
    hasCameraPermission: Boolean,
    isCameraAvailable: Boolean
) {
    var nombre by remember { mutableStateOf("") }
    var compania by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var logo by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Text("🎮", style = MaterialTheme.typography.displayMedium)
        },
        title = {
            Text(
                "Nuevo Videojuego",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // En CreateJuegoDialog.kt, agrega estos logs
                ImageCameraPicker(
                    selectedImage = logo,
                    onImageSelected = { selectedLogo ->
                        Log.d("CreateDialog", "🖼️ Imagen seleccionada: ${selectedLogo?.length}")
                        logo = selectedLogo
                    },
                    placeholder = "Logo del juego",
                    cameraState = cameraState,
                    onCapturePhoto = {
                        Log.d("CreateDialog", "📸 onCapturePhoto llamado")
                        onCapturePhoto()
                    },
                    onRequestPermission = {
                        Log.d("CreateDialog", "📋 onRequestPermission llamado")
                        onRequestPermission()
                    },
                    onResetCameraState = onResetCameraState,
                    hasCameraPermission = hasCameraPermission,
                    isCameraAvailable = isCameraAvailable
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("🎯 Nombre del juego") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )

                OutlinedTextField(
                    value = compania,
                    onValueChange = { compania = it },
                    label = { Text("🏢 Compañía desarrolladora") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )

                OutlinedTextField(
                    value = cantidad,
                    onValueChange = { cantidad = it },
                    label = { Text("📦 Cantidad en stock") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("📝 Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    shape = RoundedCornerShape(16.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    try {
                        val cantidadInt = cantidad.toIntOrNull() ?: 0
                        if (nombre.isBlank()) {
                            return@Button
                        }
                        if (logo.isNullOrBlank()) {
                            return@Button
                        }
                        val nuevoJuego = Juego(
                            nombre = nombre.trim(),
                            compania = compania.trim(),
                            descripcion = descripcion.trim(),
                            cantidad = cantidadInt,
                            logo = logo
                        )
                        onConfirm(nuevoJuego)
                    } catch (e: Exception) {
                        Log.e("CreateJuegoDialog", "Error creando juego: ${e.message}")
                    }
                },
                enabled = nombre.isNotBlank() &&
                        compania.isNotBlank() &&
                        descripcion.isNotBlank() &&
                        cantidad.toIntOrNull() != null &&
                        cantidad.toIntOrNull()!! > 0 &&
                        !logo.isNullOrBlank(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("✨ Crear", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("❌ Cancelar")
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun EditJuegoDialog(
    juego: Juego,
    onDismiss: () -> Unit,
    onConfirm: (Juego) -> Unit,
    cameraState: CameraState,
    onCapturePhoto: () -> Unit,
    onRequestPermission: () -> Unit,
    onResetCameraState: () -> Unit,
    hasCameraPermission: Boolean,
    isCameraAvailable: Boolean
) {
    var nombre by remember { mutableStateOf(juego.nombre ?: "") }
    var compania by remember { mutableStateOf(juego.compania ?: "") }
    var descripcion by remember { mutableStateOf(juego.descripcion ?: "") }
    var cantidad by remember { mutableStateOf((juego.cantidad ?: 0).toString()) }
    var logo by remember { mutableStateOf(juego.logo) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Text("✏️", style = MaterialTheme.typography.displayMedium)
        },
        title = {
            Text(
                "Editar Videojuego",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ImageCameraPicker(
                    selectedImage = logo,
                    onImageSelected = { selectedLogo -> logo = selectedLogo },
                    placeholder = "Logo del juego",
                    cameraState = cameraState,
                    onCapturePhoto = onCapturePhoto,
                    onRequestPermission = onRequestPermission,
                    onResetCameraState = onResetCameraState,
                    hasCameraPermission = hasCameraPermission,
                    isCameraAvailable = isCameraAvailable
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("🎯 Nombre del juego") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )

                OutlinedTextField(
                    value = compania,
                    onValueChange = { compania = it },
                    label = { Text("🏢 Compañía desarrolladora") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )

                OutlinedTextField(
                    value = cantidad,
                    onValueChange = { cantidad = it },
                    label = { Text("📦 Cantidad en stock") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("📝 Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    shape = RoundedCornerShape(16.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cantidadInt = cantidad.toIntOrNull() ?: 0
                    val juegoEditado = juego.copy(
                        nombre = nombre.trim(),
                        compania = compania.trim(),
                        descripcion = descripcion.trim(),
                        cantidad = cantidadInt,
                        logo = logo
                    )
                    onConfirm(juegoEditado)
                },
                enabled = nombre.isNotBlank() &&
                        compania.isNotBlank() &&
                        descripcion.isNotBlank() &&
                        cantidad.toIntOrNull() != null &&
                        cantidad.toIntOrNull()!! > 0,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("💾 Guardar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("❌ Cancelar")
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun DeleteJuegoDialog(
    juego: Juego,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Text("⚠️", style = MaterialTheme.typography.displayMedium)
        },
        title = {
            Text(
                "Eliminar Videojuego",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "¿Estás seguro de que quieres eliminar este juego?",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "🎮 ${juego.nombre ?: "Sin nombre"}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Esta acción no se puede deshacer",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("🗑️ Eliminar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("❌ Cancelar")
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}