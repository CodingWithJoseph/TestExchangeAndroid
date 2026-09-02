package com.testexchange.feature.impl

import com.testexchange.core.mvi.MviEffect
import com.testexchange.core.mvi.MviIntent
import com.testexchange.core.mvi.MviState

// TODO(owner): Add explicit intent variants based on user and lifecycle inputs.
sealed interface ExchangeIntent : MviIntent

// TODO(owner): Replace this empty state with the complete immutable rendering model.
data object ExchangeState : MviState

// TODO(owner): Add non-replayable navigation or message effects only when needed.
sealed interface ExchangeEffect : MviEffect

