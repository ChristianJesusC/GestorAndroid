package com.chiu.renovadoproyecto1.features.juegos.presentation.View

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun GameImage(
    imageData: String?,
    gameName: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        when {
            imageData.isNullOrEmpty() -> {
                GamePlaceholder(gameName = gameName)
            }
            imageData.startsWith("data:image") -> {
                val bitmap = remember(imageData) {
                    try {
                        val base64Data = imageData.substringAfter("base64,")
                        val imageBytes = Base64.decode(base64Data, Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    } catch (e: Exception) {
                        null
                    }
                }

                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Logo de ${gameName ?: "juego"}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = contentScale
                    )
                } else {
                    GamePlaceholder(gameName = gameName)
                }
            }
            else -> {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageData)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Logo de ${gameName ?: "juego"}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale,
                    onError = {
                    }
                )
            }
        }
    }
}

@Composable
private fun GamePlaceholder(
    gameName: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🎮",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            if (!gameName.isNullOrEmpty()) {
                Text(
                    text = gameName.take(2).uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
