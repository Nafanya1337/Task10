package com.example.task10

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingsScreen(){
    val context = LocalContext.current


    ScreenPattern(
        { SettingsScreenContent() }, context)
}

@Composable
fun SettingsScreenContent(){

    val context = LocalContext.current
    val temp = getDirectory()
    val directory = remember {
        mutableStateOf(value = temp)
    }
    Surface() {
        Column(
            modifier = androidx.compose.ui.Modifier
                .padding(PaddingValues(25.dp, 85.dp))
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                value = directory.value.toString(),
                maxLines = 1,
                onValueChange = { directory.value = it },
                label = {
                    Text("Папка для сохранения")

                }
            )

            Button(
                onClick = {
                    saveFolder(
                        context = context,
                        folder = directory.value.toString()
                    )
                }
            ) {
                Text(text = "Сохранить")
            }
        }
    }
}

@Composable
fun getDirectory(): String? {
    val context = LocalContext.current
    val settings = context.getSharedPreferences("PreferencesName", ComponentActivity.MODE_PRIVATE)
    val folder = settings.getString("directory", "/YOUR_FOLDER_NAME")
    return folder
}

fun saveFolder(context: Context, folder: String) {
    val settings = context.getSharedPreferences("PreferencesName", ComponentActivity.MODE_PRIVATE)
    val result: Boolean = settings.edit().putString("directory", folder).commit()
    if (result) {
        Toast.makeText(context, "Папка изменена", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(context, "Не удалось изменить папку", Toast.LENGTH_SHORT).show()
    }
}