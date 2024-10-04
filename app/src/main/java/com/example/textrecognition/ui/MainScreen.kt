package com.example.textrecognition.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.CameraController
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.textrecognition.R
import com.example.textrecognition.util.TextRecognitionScreens
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier, navController: NavController) {
    var photoUri: Uri? by remember { mutableStateOf(null) }
    var txt: String by remember { mutableStateOf("") }
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            photoUri = uri
        }

    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val controller = remember {
        LifecycleCameraController(context.applicationContext).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or
                        CameraController.VIDEO_CAPTURE
            )
        }
    }

    val viewModel = viewModel<MainViewModel>()
    val bitmaps by viewModel.bitmaps.collectAsState()


    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {

        if (photoUri != null) {
            val painter = rememberAsyncImagePainter(
                ImageRequest
                    .Builder(LocalContext.current)
                    .data(data = photoUri)
                    .build()
            )
           


            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = painter,
                contentDescription = null
            )

            val firebaseVisionImage = photoUri?.let { uriToBitmap(context, it) }
                ?.let { FirebaseVisionImage.fromBitmap(it) }

            FirebaseVision.getInstance().onDeviceTextRecognizer.apply {
                if (firebaseVisionImage != null) {
                    this.processImage(firebaseVisionImage)
                        .addOnSuccessListener { firebaseVisionText ->
                            txt = processTextRecognitionResult(firebaseVisionText)
                        }.addOnFailureListener { e ->
                            Toast.makeText(context, "Error : " + e.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))


        Text(text = txt)



        Row(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding(),
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
                    contentDescription = "Take photo"
                )
            }

            IconButton(modifier = Modifier
                .height(100.dp)
                .width(100.dp),
                onClick = {
                    navController.navigate(TextRecognitionScreens.ImagePicker.name)
                }) {

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Take photo"
                )

            }
        }
    }


}

fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun processTextRecognitionResult(firebaseVisionText: FirebaseVisionText): String {
    var detectedText = ""
    val textBlockList = firebaseVisionText.textBlocks
    if (textBlockList.size == 0) {
        detectedText = "No Text Found in image"
    } else {
        for (textBlock in textBlockList) {
            detectedText += textBlock.text
        }
    }

    return detectedText
}