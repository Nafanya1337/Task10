package com.example.task10.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.task10.InfoScreenContent
import com.example.task10.MainActivity
import com.example.task10.MainActivityScreen
import com.example.task10.ScreenContent
import com.example.task10.SettingsScreen
import com.example.task10.SettingsScreenContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation(
    navController: NavHostController,
    actionBarTitle: MutableState<String>,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = NavigationScreens.MAIN_SCREEN) {
        composable(NavigationScreens.MAIN_SCREEN) {
            actionBarTitle.value = "Домашняя страница"
            ScreenContent()
        }

        composable(NavigationScreens.SETTINGS_SCREEN) {
            actionBarTitle.value = "Настройки"
            SettingsScreenContent()
        }

        composable(NavigationScreens.INFO_SCREEN) {
            actionBarTitle.value = "Разработчик"
            InfoScreenContent()
        }
    }
}