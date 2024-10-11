package com.example.textrecognition.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.textrecognition.util.PhotoManager
import com.example.textrecognition.R
import com.example.textrecognition.util.TextRecognitionScreens
import com.google.firebase.ml.vision.common.FirebaseVisionImage


@Composable
fun MainScreen(modifier: Modifier, navController: NavController, viewModel: MainViewModel) {
    val resultTxt: String? by viewModel.resultTxt.collectAsState()
    val context = LocalContext.current
    val photoUri = viewModel.uri.collectAsState().value
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                viewModel.setUriValue(uri)
            }
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

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
    ) {

        resultTxt?.let {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .weight(1F)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F),
                    painter = painter,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(modifier = Modifier.wrapContentSize().weight(0.3f), text = it)
            }

        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Absolute.SpaceEvenly,
        ) {

            IconButton(
                modifier = Modifier
                    .height(150.dp)
                    .width(150.dp),
                onClick = {
                    launcher.launch(
                        PickVisualMediaRequest(
                            mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Gallery",
                    tint = colorResource(id = R.color.blue)
                )
            }

            IconButton(modifier = Modifier
                .height(150.dp)
                .width(150.dp),
                onClick = {
                    navController.navigate(TextRecognitionScreens.CameraPreview.name)
                }) {

                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Take photo",
                    tint = colorResource(id = R.color.blue)
                )

            }
        }
    }
}