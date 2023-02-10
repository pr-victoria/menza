/*
 *    Copyright 2023, Petr Laštovička as Lasta apps, All rights reserved
 *
 *     This file is part of Menza.
 *
 *     Menza is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Menza is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Menza.  If not, see <https://www.gnu.org/licenses/>.
 */

package cz.lastaapps.menza.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import cz.lastaapps.menza.features.settings.domain.model.AppThemeType
import cz.lastaapps.menza.features.settings.domain.model.AppThemeType.Agata
import cz.lastaapps.menza.features.settings.domain.model.AppThemeType.CTU
import cz.lastaapps.menza.features.settings.domain.model.AppThemeType.System
import cz.lastaapps.menza.features.settings.domain.model.AppThemeType.Uwu
import cz.lastaapps.menza.features.settings.domain.model.DarkMode
import cz.lastaapps.menza.ui.theme.generated.AppTypography
import cz.lastaapps.menza.ui.theme.generated.DarkThemeColors
import cz.lastaapps.menza.ui.theme.generated.LightThemeColors

@Composable
fun AppTheme(
    darkMode: DarkMode = DarkMode.System,
    theme: AppThemeType = Agata,
    colorSystemBars: Boolean = false,
    content: @Composable () -> Unit,
) {
    val isLightMode = !darkMode.shouldUseDark()

    val colorScheme = when (theme) {
        System ->
            if (isLightMode)
                dynamicLightColorScheme(LocalContext.current)
            else
                dynamicDarkColorScheme(LocalContext.current)
        Agata ->
            if (isLightMode)
                LightThemeColors
            else
                DarkThemeColors
        CTU ->
            if (isLightMode)
                DarkThemeColors
            else
                LightThemeColors
        Uwu ->
            if (isLightMode)
                LightThemeColors
            else
                DarkThemeColors
    }.animated()

    if (colorSystemBars) {
        val systemUiController = rememberSystemUiController()

        SideEffect {
            systemUiController.setStatusBarColor(
                color = colorScheme.background,
                darkIcons = isLightMode,
            )
            systemUiController.setNavigationBarColor(
                color = colorScheme.surfaceVariant,
                darkIcons = isLightMode,
            )
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = Shapes,
        content = content,
    )
}

private val Shapes = Shapes(
    /*extraSmall = ShapeTokens.CornerExtraSmall
    small = ShapeTokens.CornerSmall,
    medium = ShapeTokens.CornerMedium,
    large = ShapeTokens.CornerLarge,
    extraLarge = ShapeTokens.CornerExtraLarge,*/
)

@Composable
private fun DarkMode.shouldUseDark(): Boolean =
    when (this) {
        DarkMode.Dark -> true
        DarkMode.Light -> false
        DarkMode.System -> isSystemInDarkTheme()
    }

@Composable
private fun ColorScheme.animated(): ColorScheme {
    return ColorScheme(
        background = animateColorAsState(background).value,
        error = animateColorAsState(error).value,
        errorContainer = animateColorAsState(errorContainer).value,
        inverseOnSurface = animateColorAsState(inverseOnSurface).value,
        inversePrimary = animateColorAsState(inversePrimary).value,
        inverseSurface = animateColorAsState(inverseSurface).value,
        onBackground = animateColorAsState(onBackground).value,
        onError = animateColorAsState(onError).value,
        onErrorContainer = animateColorAsState(onErrorContainer).value,
        onPrimary = animateColorAsState(onPrimary).value,
        onPrimaryContainer = animateColorAsState(onPrimaryContainer).value,
        onSecondary = animateColorAsState(onSecondary).value,
        onSecondaryContainer = animateColorAsState(onSecondaryContainer).value,
        onSurface = animateColorAsState(onSurface).value,
        onSurfaceVariant = animateColorAsState(onSurfaceVariant).value,
        onTertiary = animateColorAsState(onTertiary).value,
        onTertiaryContainer = animateColorAsState(onTertiaryContainer).value,
        outline = animateColorAsState(outline).value,
        primary = animateColorAsState(primary).value,
        primaryContainer = animateColorAsState(primaryContainer).value,
        secondary = animateColorAsState(secondary).value,
        secondaryContainer = animateColorAsState(secondaryContainer).value,
        surface = animateColorAsState(surface).value,
        surfaceVariant = animateColorAsState(surfaceVariant).value,
        surfaceTint = animateColorAsState(surfaceTint).value,
        tertiary = animateColorAsState(tertiary).value,
        tertiaryContainer = animateColorAsState(tertiaryContainer).value,
        outlineVariant = animateColorAsState(outlineVariant).value,
        scrim = animateColorAsState(scrim).value,
    )
}
