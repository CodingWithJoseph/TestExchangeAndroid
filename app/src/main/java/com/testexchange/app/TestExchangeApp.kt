package com.testexchange.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.testexchange.core.designsystem.TestExchangeTheme
import com.testexchange.feature.api.ExchangeRoute
import com.testexchange.feature.impl.exchangeScreen

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

