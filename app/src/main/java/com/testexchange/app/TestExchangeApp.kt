package com.testexchange.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.testexchange.core.designsystem.TestExchangeTheme
import com.testexchange.feature.exchange.api.ExchangeRoute
import com.testexchange.feature.exchange.impl.exchangeScreen

@Composable
internal fun TestExchangeApp() {
    TestExchangeTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = ExchangeRoute,
        ) {
            exchangeScreen()
        }
    }
}

