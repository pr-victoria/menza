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

package cz.lastaapps.api.core.domain.model

import cz.lastaapps.api.core.domain.model.common.Menza

// All the types must have unique names, or the DI will break
sealed interface MenzaType {
    sealed interface Agata : MenzaType {
        data class Subsystem(val subsystemId: Int) : Agata
        data object Strahov : Agata {
            val instance = Menza(
                Strahov,
                "Restaurace Strahov",
                isOpened = true,
                isImportant = true
            )
        }
    }

    sealed interface Buffet : MenzaType {
        data object FS : Buffet
        data object FEL : Buffet
    }
}