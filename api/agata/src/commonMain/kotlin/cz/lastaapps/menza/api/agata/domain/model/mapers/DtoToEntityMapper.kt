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

package cz.lastaapps.menza.api.agata.domain.model.mapers

import agata.AddressEntity
import agata.ContactEntity
import agata.DishEntity
import agata.DishTypeEntity
import agata.InfoEntity
import agata.LinkEntity
import agata.OpenTimeEntity
import agata.PictogramEntity
import agata.ServingPlaceEntity
import agata.SubsystemEntity
import cz.lastaapps.core.util.takeIfNotBlack
import cz.lastaapps.menza.api.agata.domain.model.common.LatLong
import cz.lastaapps.menza.api.agata.domain.model.common.NewsHeader
import cz.lastaapps.menza.api.agata.domain.model.dto.AddressDto
import cz.lastaapps.menza.api.agata.domain.model.dto.ContactDto
import cz.lastaapps.menza.api.agata.domain.model.dto.DishDto
import cz.lastaapps.menza.api.agata.domain.model.dto.DishTypeDto
import cz.lastaapps.menza.api.agata.domain.model.dto.InfoDto
import cz.lastaapps.menza.api.agata.domain.model.dto.LinkDto
import cz.lastaapps.menza.api.agata.domain.model.dto.OpenTimeDto
import cz.lastaapps.menza.api.agata.domain.model.dto.PictogramDto
import cz.lastaapps.menza.api.agata.domain.model.dto.ServingPlaceDto
import cz.lastaapps.menza.api.agata.domain.model.dto.SubsystemDto
import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

internal fun SubsystemDto.toEntity(isImportant: Boolean) =
    SubsystemEntity(
        id = id.toLong(),
        name = name,
        opened = opened == 1,
        isImportant = isImportant,
    )

internal fun DishDto.toEntity() =
    DishEntity(
        id = id.toLong(),
        subsystemId = subsystemId.toLong(),
        typeId = typeId.toLong(),
        servingPlaces = servingPlaceList.split(';').map { it.toLong() },
        amount = amount,
        name = name,
        sideDishA = sideDishA,
        sideDishB = sideDishB,
        priceNormal = priceNormal.toDouble(),
        priceDiscount = priceDiscount.toDouble(),
        allergens = allergens.parseAllergens().map { it.toLong() },
        photoLink = photoLink.takeIfNotBlack(),
        pictogram = pictogram.toLong(),
        isActive = isActive,
    )


internal fun String.parseAllergens() =
    split(',', ' ', '.' /*just for sure*/, ';', '-', '_', '|')
        .filter { it.isNotBlank() }
        .mapNotNull { it.toIntOrNull() }

internal fun DishTypeDto.toEntity() =
    DishTypeEntity(
        id = id.toLong(),
        subsystemId = subsystemId.toLong(),
        nameShort = nameShort,
        nameLong = nameLong,
        itemOrder = order.toLong(),
    )

internal fun PictogramDto.toEntity() =
    PictogramEntity(
        id = id.toLong(),
        name = name,
    )

internal fun ServingPlaceDto.toEntity() =
    ServingPlaceEntity(
        id = id.toLong(),
        subsystemId = subsystemId.toLong(),
        name = name,
        description = description,
        abbrev = abbrev,
    )

internal fun InfoDto.toEntity() =
    InfoEntity(
        id = id.toLong(),
        subsystemId = subsystemId.toLong(),
        header = header.removeHtml(),
        footer = footer.removeHtml(),
    )

internal fun String.toNews() =
    NewsHeader(removeHtml())

private fun String.removeHtml() = this
    .replace("<br>", "\n")
    .replace("<BR>", "\n")
    .replace("""<[^>]*>""".toRegex(), "")
    .trim()

internal fun ContactDto.toEntity() =
    ContactEntity(
        id = id.toLong(),
        subsystemId = subsystemId.toLong(),
        itemOrder = order.toLong(),
        role = role,
        name = name,
        phone = phone,
        email = email,
    )

internal fun OpenTimeDto.toEntity() =
    OpenTimeEntity(
        id = id.toLong(),
        subsystemId = subsystemId.toLong(),
        servingPlaceId = servingPlaceId.toLong(),
        servingPlaceName = servingPlaceName,
        servingPlaceAbbrev = servingPlaceAbbrev,
        description = description,
        itemOrder = order.toLong(),
        dayFrom = dayFrom.toDayOfWeek(),
        dayTo = dayTo.toDayOfWeek(),
        timeFrom = Json.decodeFromString(timeFrom),
        timeTo = Json.decodeFromString(timeTo),
    )

private val czechDaysOfWeek = arrayOf("Po", "Út", "St", "Čt", "Pá", "So", "Ne")
private fun String.toDayOfWeek() =
    DayOfWeek.of(czechDaysOfWeek.indexOf(this) - 1)

internal fun LinkDto.toEntity() =
    LinkEntity(
        id = id.toLong(),
        subsystemId = id.toLong(),
        link = link,
        description = description,
    )

internal fun AddressDto.toEntity() =
    AddressEntity(
        id = id.toLong(),
        subsystemId = subsystemId.toLong(),
        address = address,
        gps = gps.toLatLong(),
    )

private fun String.toLatLong() =
    this
        .split(",")
        .map { it.toFloat() }
        .let { (lat, long) ->
            LatLong(lat = lat, long = long)
        }