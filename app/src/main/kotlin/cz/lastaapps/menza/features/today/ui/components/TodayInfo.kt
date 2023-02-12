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

package cz.lastaapps.menza.features.today.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import cz.lastaapps.api.core.domain.model.common.Dish
import cz.lastaapps.api.core.domain.model.common.ServingPlace
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.settings.domain.model.ShowCzech
import cz.lastaapps.menza.features.today.ui.util.allergenForId
import cz.lastaapps.menza.features.today.ui.util.getAmount
import cz.lastaapps.menza.features.today.ui.util.getName
import cz.lastaapps.menza.ui.theme.MenzaPadding
import kotlin.math.max
import kotlinx.collections.immutable.ImmutableList

@Composable
fun TodayInfo(
    dish: Dish,
    showCzech: ShowCzech,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DishImage(
            dish = dish,
            modifier = Modifier.fillMaxWidth(),
        )

        Header(
            dish = dish,
            showCzech = showCzech,
        )
        PriceView(
            dish = dish,
            showCzech = showCzech,
        )
        IssueLocationList(
            list = dish.servingPlaces,
        )
        AllergenList(
            allergens = dish.allergens,
        )
    }
}

@Composable
private fun Header(
    dish: Dish,
    showCzech: ShowCzech,
    modifier: Modifier = Modifier,
) {
    Text(
        text = dish.getName(showCzech),
        style = MaterialTheme.typography.headlineMedium,
        modifier = modifier,
    )
}

@Composable
private fun PriceView(
    dish: Dish,
    showCzech: ShowCzech,
    modifier: Modifier = Modifier,
) {
    Row(modifier) {
        Text(text = dish.getAmount(showCzech) ?: "")
        Text(
            text = "${dish.priceDiscount ?: "∅"} / ${dish.priceNormal ?: "∅"} Kč",
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun IssueLocationList(
    list: ImmutableList<ServingPlace>,
    modifier: Modifier = Modifier,
) {
    if (list.isEmpty()) return

    Column(modifier) {
        Row {
            Text(
                text = stringResource(R.string.today_info_location),
                style = MaterialTheme.typography.titleMedium,
            )
            /*Text(
                text = stringResource(R.string.today_info_window),
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
            )*/
        }
        list.forEach {
            Text(text = it.name)
        }
    }
}

@Composable
private fun AllergenList(
    allergens: ImmutableList<Int>?,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {

        Text(
            stringResource(R.string.today_info_allergens_title),
            style = MaterialTheme.typography.titleLarge
        )

        if (allergens == null) {
            Text(stringResource(R.string.today_info_allergens_unknown))
            return@Column
        }
        if (allergens.isEmpty()) {
            Text(stringResource(R.string.today_info_allergens_none))
            return@Column
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            allergens.forEach {
                AllergenRow(id = it)
            }
        }
    }
}

@Composable
private fun AllergenRow(id: Int, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MenzaPadding.Smaller),
        modifier = modifier,
    ) {
        val info = allergenForId(id = id)

        Row(
            horizontalArrangement = Arrangement.spacedBy(MenzaPadding.Small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AllergenIdBadge(id = id)
            Text(
                text = info?.first ?: stringResource(R.string.today_info_unknown_allergen_title),
                style = MaterialTheme.typography.titleMedium
            )
        }

        Text(
            text = info?.second ?: stringResource(R.string.today_info_unknown_allergen_description),
        )
    }
}

@Composable
private fun AllergenIdBadge(id: Int, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.tertiary,
        shape = CircleShape,
        modifier = modifier,
    ) {
        /*val density = LocalDensity.current
        val minSize = remember(density) {
            with(density) { 24.dp.roundToPx() }
        }*/
        Layout(
            content = {
                Text(
                    id.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(
                        start = 6.dp, end = 6.dp,
                        top = 2.dp, bottom = 2.dp
                    ),
                )
            }
        ) { measurable, constrains ->
            val placeable = measurable[0].measure(constrains)
            //val h = max(placeable.height, minSize)
            val h = placeable.height
            val w = max(placeable.width, h)
            layout(w, h) {
                placeable.place(
                    (w - placeable.width) / 2,
                    (h - placeable.height) / 2,
                )
            }
        }
    }
}

@Composable
private fun DishImage(dish: Dish, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.animateContentSize(),
        shape = MaterialTheme.shapes.medium,
    ) {

        if (dish.photoLink != null) {

            //temporary solution for refreshing
            var retryHash by remember { mutableStateOf(0) }

            val imageRequest = with(ImageRequest.Builder(LocalContext.current)) {
                data(dish.photoLink)
                diskCacheKey(dish.photoLink)
                memoryCacheKey(dish.photoLink)
                crossfade(true)
                setParameter("retry_hash", retryHash)
            }.build()

            SubcomposeAsyncImage(
                imageRequest,
                contentDescription = null,
                loading = {
                    Box(
                        Modifier
                            .aspectRatio(4f / 3f)
                            .placeholder(
                                true, color = MaterialTheme.colorScheme.secondary,
                                shape = MaterialTheme.shapes.extraSmall,
                                highlight = PlaceholderHighlight.fade(
                                    highlightColor = MaterialTheme.colorScheme.primary,
                                )
                            )
                            .clickable { retryHash++ }
                    )
                },
                error = {
                    Box(
                        Modifier
                            .aspectRatio(4f / 3f)
                            .clickable { retryHash++ },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            stringResource(R.string.today_info_image_load_failed)
                        )
                    }
                },
            )
        } else {
            Box(Modifier.aspectRatio(4f / 1f))
        }
    }
}