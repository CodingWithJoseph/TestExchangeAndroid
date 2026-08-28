package com.testexchange.domain.exchange

import kotlinx.coroutines.flow.Flow

/** Domain-facing port; the concrete data adapter belongs in an implementation module. */
interface ExchangeRepository<ReadModel> {
    fun observe(): Flow<ReadModel>
}

/** Use-case boundary; add product-specific operations without exposing framework types. */
fun interface ObserveExchangeUseCase<ReadModel> {
    operator fun invoke(): Flow<ReadModel>
}

// TODO(owner): Replace the generic seams with product-specific models and business rules.
// This module must remain independent of Android, Hilt, Retrofit, Room, and Compose.
