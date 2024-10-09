package com.example.textrecognition.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.textrecognition.PhotoManager
import com.example.textrecognition.util.TextRecognitionScreens
import com.google.firebase.ml.vision.common.FirebaseVisionImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier, navController: NavController) {
    val viewModel = viewModel<MainViewModel>()
    var photoUri: Uri? by remember { mutableStateOf(null) }
    val resultTxt: String? by viewModel.resultTxt.collectAsState()
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            photoUri = uri
        }

    val painter = rememberAsyncImagePainter(
        ImageRequest
            .Builder(context)
            .data(data = photoUri)
            .build()
    )


    val firebaseVisionImage = photoUri?.let { PhotoManager.uriToBitmap(context, it) }
        ?.let { FirebaseVisionImage.fromBitmap(it) }

    viewModel.getTextRecognitionResult(firebaseVisionImage)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        resultTxt?.let {
            Column(modifier = Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F),
                    painter = painter,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(modifier = Modifier.weight(1F), text = it)
            }

        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding().background(Color.Blue),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Absolute.SpaceEvenly,
        ) {

            IconButton(
                modifier = Modifier
                    .height(100.dp)
                    .width(100.dp),
                onClick = {
                    launcher.launch(
                        PickVisualMediaRequest(
                            mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Gallery"
                )
            }

            IconButton(modifier = Modifier
                .height(100.dp)
                .width(100.dp),
                onClick = {
                    navController.navigate(TextRecognitionScreens.CameraPreview.name)
                }) {

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Take photo"
                )

            }
        }
    }
}