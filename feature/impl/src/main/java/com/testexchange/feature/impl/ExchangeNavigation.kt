package com.testexchange.feature.impl

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.testexchange.feature.api.ExchangeRoute

fun NavGraphBuilder.exchangeScreen() {
    composable<ExchangeRoute> {
        // TODO(owner): Obtain the feature ViewModel, collect state with lifecycle awareness,
        // collect effects once, and pass state plus the intent sink to a stateless screen.
        ExchangeScreen()
    }
}

