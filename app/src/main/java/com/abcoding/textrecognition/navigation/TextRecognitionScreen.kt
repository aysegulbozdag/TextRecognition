package com.abcoding.textrecognition.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abcoding.textrecognition.ui.CameraPreview
import com.abcoding.textrecognition.ui.MainScreen
import com.abcoding.textrecognition.ui.MainViewModel
import com.abcoding.textrecognition.util.TextRecognitionScreens

@Composable
fun TextRecognitionScreen(modifier: Modifier) {
    val navController: NavHostController = rememberNavController()
    val viewModel = viewModel<MainViewModel>()
    NavHost(
        navController = navController,
        startDestination = TextRecognitionScreens.Main.name,
        modifier = modifier
    ) {

        composable(TextRecognitionScreens.Main.name) {
            MainScreen(modifier, navController, viewModel)
        }

        composable(TextRecognitionScreens.CameraPreview.name) {
         CameraPreview(navController, viewModel)
        }
    }
}