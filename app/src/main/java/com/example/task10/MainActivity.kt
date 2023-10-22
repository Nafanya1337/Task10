package com.example.task10

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.example.task10.navigation.NavigationScreens
import com.example.task10.ui.theme.Task10Theme
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Task10Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainActivityScreen()
                }
            }
        }
    }

    suspend fun downloadPhoto(URL: String): Bitmap? {
        val deferred = CompletableDeferred<Bitmap?>()
        val handler = Handler(Looper.getMainLooper())

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val image = withContext(Dispatchers.IO) {
                    Glide.with(this@MainActivity)
                        .asBitmap()
                        .load(URL)
                        .submit()
                        .get()
                }

                handler.post {
                    Toast.makeText(
                        this@MainActivity,
                        "Картинка успешно загружена",
                        Toast.LENGTH_LONG
                    ).show()
                }

                saveImage(image, URL)
                deferred.complete(image) // Завершаем deferred с успешным результатом
            } catch (e: Exception) {
                e.printStackTrace()
                handler.post {
                    Toast.makeText(
                        this@MainActivity,
                        "Картинку не удалось загрузить",
                        Toast.LENGTH_LONG
                    ).show()
                }
                deferred.complete(null) // Завершаем deferred с null в случае ошибки
            }
        }

        return deferred.await() // Ожидаем результат загрузки и возвращаем его
    }

    private fun saveImage(imageBitmap: Bitmap?, URL: String) {
        CoroutineScope(Dispatchers.IO).launch {
            // Создаем имя файла на основе URL
            val settings = getSharedPreferences("PreferencesName", MODE_PRIVATE)
            val directory = settings.getString("directory", "/YOUR_FOLDER_NAME")
            val name = URL.substringAfterLast("/") // Извлекаем имя файла из URL
            val imageFileName = "JPEG_$name"
            val storageDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .toString() + directory
            )

            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }

            val imageFile = File(storageDir, imageFileName)
            try {
                val fOut: OutputStream = FileOutputStream(imageFile)
                imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.close()

                galleryAddPic(imageFile.path)
                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    Toast.makeText(
                        this@MainActivity,
                        "Картинка сохранена в память устройства",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    Toast.makeText(
                        this@MainActivity,
                        "Картинку не удалось сохранить",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun galleryAddPic(imagePath: String?) {
        imagePath?.let { path ->
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val f = File(path)
            val contentUri: Uri = Uri.fromFile(f)
            mediaScanIntent.data = contentUri
            sendBroadcast(mediaScanIntent)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainActivityScreen() {
    val context = LocalContext.current

    ScreenPattern(
        { ScreenContent() }, context)

}


@Composable
fun ScreenContent() {
    val downloadedImageList = remember {
        mutableStateListOf<Bitmap>()
    }

    val urlText = remember {
        mutableStateOf("")
    }

    val context = LocalContext.current

    Surface() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = PaddingValues(35.dp, 85.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                value = urlText.value,
                maxLines = 1,
                onValueChange = { urlText.value = it },
                label = { Text("Введите URL фото") }
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(9f)
            ) {
                items(count = downloadedImageList.toList().size, itemContent = { index: Int ->
                    val image = downloadedImageList.toList().get(index)
                    Image(
                        bitmap = image.asImageBitmap(),
                        contentDescription = "",
                        modifier = Modifier
                            .clip(RoundedCornerShape(15.dp))
                            .padding(10.dp)
                    )
                })
            }

            FloatingActionButton(
                onClick = {

                    val delaySeconds = 3
                    val inputData = Data.Builder().putInt("delaySeconds", delaySeconds).build()
                    val workRequest = OneTimeWorkRequest.Builder(MyWorkManager::class.java)
                        .setInputData(inputData)
                        .build()

                    WorkManager.getInstance(context).enqueue(workRequest)

                    val URL = urlText.value.toString()

                    CoroutineScope(Dispatchers.Default).launch {
                        delay(3000)
                        val image = (context as MainActivity).downloadPhoto(URL)
                        if (image != null) {
                            downloadedImageList.add(image)
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Скачать изображение")
            }
        }
    }
}

@Composable
fun MyBottomNavigation(
    navController: NavController,
    screens: List<Item>
) {
    BottomNavigation {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        screens.forEach { screen ->
            BottomNavigationItem(
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { ScreenIcon(screen.name) },
            )
        }
    }
}


data class Item(val name: String, val route: String)

@Composable
fun ScreenIcon(name: String) {
    Box(modifier = Modifier, contentAlignment = Alignment.Center) {
        Text(text = name)
    }
}

@Composable
fun getScreenTitle(route: String): String {
    return when (route) {
        NavigationScreens.MAIN_SCREEN -> "Домашняя страница"
        NavigationScreens.SETTINGS_SCREEN -> "Настройки"
        NavigationScreens.INFO_SCREEN -> "Разработчик"
        else -> "Unknown"
    }
}



@Preview
@Composable
fun MainActivityScreenPreview() {
    MainActivityScreen()
}