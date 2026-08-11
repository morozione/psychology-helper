package com.morozione.psychologyhelper.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimensions(
    val spaceXxs: Dp = 2.dp,
    val spaceXs: Dp = 4.dp,
    val spaceSm: Dp = 8.dp,
    val spaceMd: Dp = 12.dp,
    val spaceLg: Dp = 16.dp,
    val spaceXl: Dp = 20.dp,
    val spaceXxl: Dp = 24.dp,
    val space3xl: Dp = 32.dp,
    val space4xl: Dp = 48.dp,
    val space5xl: Dp = 64.dp,
    val radiusSm: Dp = 8.dp,
    val radiusMd: Dp = 12.dp,
    val radiusLg: Dp = 16.dp,
    val radiusXl: Dp = 24.dp,
    val radiusFull: Dp = 100.dp,
    val iconSm: Dp = 16.dp,
    val iconMd: Dp = 24.dp,
    val iconLg: Dp = 32.dp,
    val iconXl: Dp = 48.dp,
    val avatarSm: Dp = 36.dp,
    val avatarMd: Dp = 56.dp,
    val avatarLg: Dp = 80.dp,
    val buttonHeight: Dp = 52.dp,
    val inputHeight: Dp = 56.dp,
    val cardElevation: Dp = 0.dp,
    val bottomNavHeight: Dp = 80.dp,
)

val LocalDimensions = staticCompositionLocalOf { Dimensions() }

val Dimens: Dimensions
    @androidx.compose.runtime.Composable
    get() = LocalDimensions.current
