package com.testexchange.data.exchange.api

import kotlinx.coroutines.flow.Flow

/** Replaceable remote boundary; Raw is intentionally owned by the data implementation. */
interface ExchangeRemoteDataSource<Raw> {
    suspend fun fetch(): Raw
}

/** Replaceable local boundary; Raw must not cross into the domain layer. */
interface ExchangeLocalDataSource<Raw> {
    fun observe(): Flow<Raw>

    suspend fun replace(value: Raw)
}

// TODO(owner): Add narrower ports only when they express a real substitution or test seam.
// Do not expose Retrofit responses, Room entities, or other implementation types from this module.
