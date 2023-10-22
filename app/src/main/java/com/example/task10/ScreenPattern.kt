package com.example.task10

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.task10.navigation.Navigation
import com.example.task10.navigation.NavigationScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenPattern(screen: @Composable (PaddingValues) -> Unit, context: Context) {
    val drawerState =
        androidx.compose.material3.rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed)

    val navController = rememberNavController()
    var actionBarTitle = rememberSaveable { mutableStateOf("Домашняя страница") }
    navController.currentDestination?.route = "Домашняя страница"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = actionBarTitle.value.toString()) }
            )
        },
        bottomBar = {
            MyBottomNavigation(
                navController = navController,
                screens = listOf(
                    Item(
                        "Домашняя страница",
                        NavigationScreens.MAIN_SCREEN,
                    ),
                    Item(
                        "Настройки",
                        NavigationScreens.SETTINGS_SCREEN,
                    ),
                    Item(
                        "Разработчик",
                        NavigationScreens.INFO_SCREEN
                    )
                )
            )
        }) { innerPadding ->
        Navigation(
            navController = navController,
            actionBarTitle = actionBarTitle,
            modifier = Modifier.padding(innerPadding)
        )
    }
}



