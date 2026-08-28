package com.testexchange.core.common

import kotlinx.coroutines.CoroutineDispatcher

/** Coroutine execution policy owned by the composition root and replaceable in tests. */
interface AppDispatchers {
    val default: CoroutineDispatcher
    val io: CoroutineDispatcher
}

