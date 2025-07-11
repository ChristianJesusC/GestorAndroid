package com.chiu.renovadoproyecto1.features.juegos.presentation.View.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import android.graphics.BitmapFactory
import android.util.Base64
import coil.compose.AsyncImage
import com.chiu.renovadoproyecto1.features.juegos.presentation.ViewModel.CameraState

@Composable
fun ImageCameraPicker(
    selectedImage: String?,
    onImageSelected: (String) -> Unit,
    placeholder: String = "Seleccionar imagen",
    cameraState: CameraState,
    onCapturePhoto: () -> Unit,
    onRequestPermission: () -> Unit,
    onResetCameraState: () -> Unit,
    hasCameraPermission: Boolean,
    isCameraAvailable: Boolean
) {
    var showOptionsDialog by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var imageLoadError by remember { mutableStateOf(false) }

    LaunchedEffect(cameraState) {
        when (cameraState) {
            is CameraState.PhotoCaptured -> {
                onImageSelected(cameraState.base64Image)
                onResetCameraState()
            }
            is CameraState.PermissionDenied -> {
                showPermissionDialog = true
                onResetCameraState()
            }
            is CameraState.Error -> {
                onResetCameraState()
            }
            else -> { }
        }
    }

    fun decodeBase64ToBitmap(base64String: String): android.graphics.Bitmap? {
        return try {
            val base64Data = if (base64String.startsWith("data:image")) {
                base64String.substringAfter("base64,")
            } else {
                base64String
            }
            val imageBytes = Base64.decode(base64Data, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        } catch (e: Exception) {
            null
        }
    }

    fun captureFromCamera() {
        if (!isCameraAvailable) {
            return
        }

        if (!hasCameraPermission) {
            onRequestPermission()
            return
        }

        onCapturePhoto()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { showOptionsDialog = true },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                selectedImage != null && !imageLoadError -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (selectedImage.startsWith("data:image") || selectedImage.length > 100) {
                            val bitmap = remember(selectedImage) {
                                decodeBase64ToBitmap(selectedImage)
                            }

                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Imagen seleccionada",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                LaunchedEffect(Unit) {
                                    imageLoadError = true
                                }
                                ImagePlaceholder(placeholder)
                            }
                        } else {
                            AsyncImage(
                                model = selectedImage,
                                contentDescription = "Imagen del juego",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                onError = {
                                    imageLoadError = true
                                }
                            )
                        }

                        IconButton(
                            onClick = { showOptionsDialog = true },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                    RoundedCornerShape(20.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Cambiar imagen",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
                else -> {
                    ImagePlaceholder(placeholder)
                    LaunchedEffect(selectedImage) {
                        if (selectedImage == null) {
                            imageLoadError = false
                        }
                    }
                }
            }
        }
    }

    if (showOptionsDialog) {
        AlertDialog(
            onDismissRequest = { showOptionsDialog = false },
            icon = {
                Text("📷", style = MaterialTheme.typography.displayMedium)
            },
            title = {
                Text(
                    "Seleccionar Imagen",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showOptionsDialog = false
                                captureFromCamera()
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Cámara",
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Column {
                                Text(
                                    "📷 Tomar Foto",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showOptionsDialog = false }) {
                    Text("Cancelar")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            icon = {
                Text("🔒", style = MaterialTheme.typography.displayMedium)
            },
            title = {
                Text(
                    "Permiso de Cámara",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Esta aplicación necesita acceso a la cámara para tomar fotos de los videojuegos.")
            },
            confirmButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Entendido")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (cameraState is CameraState.Capturing) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun ImagePlaceholder(placeholder: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Cámara",
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = placeholder,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Text(
            text = "📷 Tomar foto",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}