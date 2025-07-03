package com.chiu.renovadoproyecto1.features.juegos.presentation.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoLibrary
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
import android.Manifest
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import android.graphics.Bitmap

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

@Composable
fun ImageCameraPicker(
    selectedImage: String?,
    onImageSelected: (String) -> Unit,
    placeholder: String = "Seleccionar imagen",
    activity: FragmentActivity? = null
) {
    val context = LocalContext.current

    val finalActivity = activity ?: remember(context) {
        context.findActivity() as? FragmentActivity
    }

    var showOptionsDialog by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var imageLoadError by remember { mutableStateOf(false) }
    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para permisos de cámara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
        } else {
            showPermissionDialog = true
        }
    }

    // Launcher para capturar foto
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && currentPhotoUri != null) {
            // Convertir la imagen a base64
            try {
                val inputStream = context.contentResolver.openInputStream(currentPhotoUri!!)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                val imageBytes = outputStream.toByteArray()
                val base64String = Base64.encodeToString(imageBytes, Base64.DEFAULT)

                onImageSelected("data:image/jpeg;base64,$base64String")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    // Función para crear URI temporal para la foto
    fun createImageUri(): Uri? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "JPEG_${timeStamp}_"
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                imageFile
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Función para verificar permisos
    fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Función para capturar desde cámara
    fun captureFromCamera() {
        if (!hasCameraPermission()) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            return
        }

        val uri = createImageUri()
        if (uri != null) {
            currentPhotoUri = uri
            cameraLauncher.launch(uri)
        }
    }

    if (finalActivity == null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "⚠️ Error de contexto",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "No se puede acceder a la cámara",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        return
    }

    // Función para decodificar base64
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

    // UI Principal
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
                            // Es base64
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
                            // Es URI
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

                        // Botón de editar
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

    // Dialog de opciones
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
                    // Opción Cámara
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

    // Dialog de permisos
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