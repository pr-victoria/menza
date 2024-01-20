/*
 *    Copyright 2024, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.menza.features.today.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.lastaapps.api.core.domain.model.Dish
import cz.lastaapps.api.core.domain.model.DishCategory
import cz.lastaapps.menza.features.settings.domain.model.DishLanguage
import cz.lastaapps.menza.features.settings.domain.model.PriceType
import cz.lastaapps.menza.ui.components.MaterialPullIndicatorAligned
import cz.lastaapps.menza.ui.components.NoItems
import cz.lastaapps.menza.ui.locals.LocalWindowWidth
import cz.lastaapps.menza.ui.theme.Padding
import cz.lastaapps.menza.ui.util.appCardColors
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TodayDishGrid(
    isLoading: Boolean,
    onRefresh: () -> Unit,
    data: ImmutableList<DishCategory>,
    onNoItems: () -> Unit,
    onDishSelected: (Dish) -> Unit,
    priceType: PriceType,
    downloadOnMetered: Boolean,
    language: DishLanguage,
    isOnMetered: Boolean,
    header: @Composable () -> Unit,
    footer: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    scrollGrid: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    pullState: PullRefreshState = rememberPullRefreshState(
        refreshing = isLoading, onRefresh = onRefresh,
    ),
    widthSize: WindowWidthSizeClass = LocalWindowWidth.current,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .pullRefresh(pullState),
    ) {
        Surface(shape = MaterialTheme.shapes.large) {
            DishContent(
                data = data,
                onDishSelected = onDishSelected,
                onNoItems = onNoItems,
                priceType = priceType,
                downloadOnMetered = downloadOnMetered,
                language = language,
                isOnMetered = isOnMetered,
                scroll = scrollGrid,
                header = header,
                footer = footer,
                widthSize = widthSize,
                modifier = Modifier
                    .padding(top = Padding.Smaller) // so text is not cut off
                    .fillMaxSize(),
            )
        }

        MaterialPullIndicatorAligned(isLoading, pullState)
    }
}

@Composable
private fun DishContent(
    data: ImmutableList<DishCategory>,
    onDishSelected: (Dish) -> Unit,
    onNoItems: () -> Unit,
    priceType: PriceType,
    downloadOnMetered: Boolean,
    language: DishLanguage,
    isOnMetered: Boolean,
    scroll: LazyStaggeredGridState,
    header: @Composable () -> Unit,
    footer: @Composable () -> Unit,
    widthSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier,
) {
    if (data.isEmpty()) {
        NoItems(modifier, onNoItems)
        return
    }

    Column(modifier, verticalArrangement = Arrangement.spacedBy(Padding.Medium)) {
        val columns =
            if (widthSize == WindowWidthSizeClass.Compact) {
                StaggeredGridCells.Fixed(1)
            } else {
                StaggeredGridCells.Adaptive(172.dp)
            }

        LazyVerticalStaggeredGrid(
            columns = columns,
            modifier = Modifier.weight(1f),
            verticalItemSpacing = Padding.MidSmall,
            horizontalArrangement = Arrangement.spacedBy(Padding.MidSmall),
            state = scroll,
        ) {
            item {
                header()
            }

            data.forEach { category ->
                itemsIndexed(category.dishList) { index, dish ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Padding.Medium),
                    ) {
                        if (index == 0) {
                            DishHeader(
                                courseType = category,
                                language = language,
                            )
                        }
                        DishItem(
                            dish = dish,
                            onDishSelected = onDishSelected,
                            priceType = priceType,
                            downloadOnMetered = downloadOnMetered,
                            language = language,
                            isOnMetered = isOnMetered,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }

            item {
                footer()
            }
        }
    }
}

@Composable
private fun DishItem(
    dish: Dish,
    onDishSelected: (Dish) -> Unit,
    priceType: PriceType,
    downloadOnMetered: Boolean,
    language: DishLanguage,
    isOnMetered: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        colors = appCardColors(MaterialTheme.colorScheme.primaryContainer),
        shape = MaterialTheme.shapes.large,
        modifier = modifier.clickable { onDishSelected(dish) },
    ) {
        Column(
            Modifier.padding(Padding.MidSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Padding.Small),
        ) {

            if (dish.photoLink != null) {
                DishImageWithBadge(
                    dish = dish,
                    priceType = priceType,
                    downloadOnMetered = downloadOnMetered,
                    isOnMetered = isOnMetered,
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(Padding.Small),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Padding.Smaller),
                ) {
                    DishNameRow(
                        dish = dish,
                        language = language,
                        modifier = modifier.weight(1f),
                    )
                    if (dish.photoLink == null) {
                        DishBadge(dish = dish, priceType = priceType)
                    }
                }

                DishInfoRow(dish, language)
            }
        }
    }
}

@Composable
private fun DishImageWithBadge(
    dish: Dish,
    priceType: PriceType,
    downloadOnMetered: Boolean,
    isOnMetered: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        DishImageRatio(
            photoLink = dish.photoLink ?: "Impossible",
            loadImmediately = downloadOnMetered || !isOnMetered,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(Padding.Small),
        )
        DishBadge(
            dish = dish,
            priceType = priceType,
            modifier = Modifier.align(Alignment.BottomEnd),
        )
    }
}
