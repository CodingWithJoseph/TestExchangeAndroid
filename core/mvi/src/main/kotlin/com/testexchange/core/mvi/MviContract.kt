package com.testexchange.core.mvi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MviIntent

interface MviState

interface MviEffect

/** A feature boundary, not a store implementation. */
interface MviStore<Intent : MviIntent, State : MviState, Effect : MviEffect> {
    val state: StateFlow<State>
    val effects: Flow<Effect>

    fun accept(intent: Intent)
}

