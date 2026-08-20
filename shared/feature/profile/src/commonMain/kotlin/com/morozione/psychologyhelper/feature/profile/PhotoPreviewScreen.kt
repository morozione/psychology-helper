package com.morozione.psychologyhelper.feature.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.morozione.psychologyhelper.ui.component.PsychologyButton
import com.morozione.psychologyhelper.ui.component.PsychologyOutlinedButton
import com.morozione.psychologyhelper.ui.theme.Dimens
import com.morozione.psychologyhelper.ui.util.toJpegByteArray
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap

private const val MIN_SCALE = 1f
private const val MAX_SCALE = 5f

/**
 * [onUsePhoto] is passed in rather than resolved here via koinScreenModel<ProfileScreenModel>():
 * Voyager scopes koinScreenModel to the calling Screen, so a lookup from this screen would create
 * a separate ProfileScreenModel instance instead of reusing the one ProfileScreen observes.
 */
class PhotoPreviewScreen(
    private val imageBytes: ByteArray,
    private val onUsePhoto: (ByteArray) -> Unit
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        val imageBitmap = remember(imageBytes) { imageBytes.decodeToImageBitmap() }
        val graphicsLayer = rememberGraphicsLayer()

        var scale by remember { mutableStateOf(1f) }
        var offset by remember { mutableStateOf(Offset.Zero) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Preview Photo") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(Dimens.spaceLg),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(Dimens.avatarLg * 2)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                val newScale = (scale * zoom).coerceIn(MIN_SCALE, MAX_SCALE)
                                scale = newScale
                                offset = clampOffset(offset + pan, newScale, size.width.toFloat())
                            }
                        }
                        .drawWithContent {
                            graphicsLayer.record { this@drawWithContent.drawContent() }
                            drawLayer(graphicsLayer)
                        }
                ) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = "Selected photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                translationX = offset.x
                                translationY = offset.y
                            }
                    )
                }
                Spacer(Modifier.height(Dimens.spaceLg))
                Text(
                    text = "Pinch to zoom, drag to reposition",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.weight(1f))
                PsychologyButton(
                    text = "Use Photo",
                    onClick = {
                        scope.launch {
                            val cropped = graphicsLayer.toImageBitmap()
                            onUsePhoto(cropped.toJpegByteArray())
                            navigator.pop()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(Dimens.spaceSm))
                PsychologyOutlinedButton(
                    text = "Choose a different photo",
                    onClick = { navigator.pop() },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/** Keeps the image from panning far enough to reveal empty space inside the crop frame. */
private fun clampOffset(offset: Offset, scale: Float, frameSizePx: Float): Offset {
    val maxOffset = frameSizePx * (scale - 1f) / 2f
    return Offset(
        x = offset.x.coerceIn(-maxOffset, maxOffset),
        y = offset.y.coerceIn(-maxOffset, maxOffset)
    )
}
