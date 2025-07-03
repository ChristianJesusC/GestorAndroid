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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import coil.compose.AsyncImage
import com.chiu.renovadoproyecto1.core.di.CameraModule
import android.util.Base64
import android.graphics.BitmapFactory

@Composable
fun CameraPicker(
    selectedImage: String?,
    onImageCaptured: (String) -> Unit,
    placeholder: String = "Tomar foto"
) {
    val context = LocalContext.current
    val activity = context as FragmentActivity

    val capturePhotoUseCase = remember {
        CameraModule.provideCapturePhotoUseCase(context, activity)
    }

    var showPermissionDialog by remember { mutableStateOf(false) }

    fun capturePhoto() {
        if (!capturePhotoUseCase.hasPermission()) {
            capturePhotoUseCase.requestPermission { granted ->
                if (granted) {
                    capturePhoto()
                } else {
                    showPermissionDialog = true
                }
            }
            return
        }

        capturePhotoUseCase.capturePhoto(
            onSuccess = { base64String ->
                onImageCaptured(base64String)
            },
            onError = { error ->
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { capturePhoto() },
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
                selectedImage != null -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (selectedImage.length > 100) {
                            val imageBytes = Base64.decode(selectedImage, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Imagen capturada",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            AsyncImage(
                                model = selectedImage,
                                contentDescription = "Imagen del juego",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        IconButton(
                            onClick = { capturePhoto() },
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
                                contentDescription = "Cambiar foto",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
                else -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Tomar foto",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "📷 Toca para capturar",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Permiso de Cámara") },
            text = { Text("Esta aplicación necesita acceso a la cámara para tomar fotos.") },
            confirmButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Entendido")
                }
            }
        )
    }
}