/*
 *    Copyright 2021, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.storage.repo

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import cz.lastaapps.entity.allergens.Allergen
import cz.lastaapps.entity.menza.MenzaLocation
import cz.lastaapps.menza.db.MenzaDatabase
import cz.lastaapps.scraping.AllergenScraper
import cz.lastaapps.scraping.LocationScraper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class LocationRepoImpl<R : Any>(
    private val database: MenzaDatabase,
    private val scraper: LocationScraper<R>,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : LocationRepo {

    override val errors: Channel<Errors>
        get() = mErrors
    override val requestInProgress: StateFlow<Boolean>
        get() = mRequestInProgress

    private val mErrors = Channel<Errors>(Channel.CONFLATED)
    private val mRequestInProgress = MutableStateFlow(false)

    private val mutex = Mutex()

    override suspend fun getData(): Flow<List<MenzaLocation>> = mutex.withLock {
        withContext(dispatcher) {

            val hasData = hasDataStored().first()
            if (!hasData) {
                launch(coroutineContext) {
                    refreshInternal()
                }
            }

            val queries = database.locationQueries
            return@withContext queries.getAllLocations { menzaId, address, coordinates ->
                MenzaLocation(menzaId, address, coordinates)
            }
                .asFlow().mapToList(coroutineContext)
        }
    }

    override suspend fun refreshData() {
        refreshInternal()
    }

    private suspend fun refreshInternal(): Boolean {
        val request = try {
            scraper.createRequest()
        } catch (e: Exception) {
            mErrors.send(Errors.ConnectionError)
            return false
        }
        val data = try {
            scraper.scrape(request)
        } catch (e: Exception) {
            mErrors.send(Errors.ParsingError)
            return false
        }

        val queries = database.locationQueries
        queries.transaction {
            queries.deleteLocations()
            data.forEach {
                queries.insertLocations(it.id, it.address, it.location)
            }
        }
        return false
    }

    override suspend fun hasDataStored(): Flow<Boolean> {
        return withContext(dispatcher) {
            val queries = database.locationQueries
            return@withContext queries.hasData().asFlow().mapToOneOrNull(coroutineContext)
                .map { it != null }
        }
    }
}