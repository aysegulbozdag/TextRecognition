package com.example.textrecognition.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.textrecognition.util.TextRecognitionScreens

@Composable
fun TextRecognitionScreen(modifier: Modifier) {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = TextRecognitionScreens.Main.name,
        modifier = modifier
    ) {

        composable(TextRecognitionScreens.Main.name) {
            MainScreen(modifier, navController)
        }

        composable(TextRecognitionScreens.CameraPreview.name) {
         CameraPreview(navController)
        }
    }
}