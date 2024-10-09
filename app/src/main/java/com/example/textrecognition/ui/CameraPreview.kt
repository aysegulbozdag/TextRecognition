package com.example.textrecognition.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.textrecognition.util.TextRecognitionScreens
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CameraPreview(navController: NavHostController) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val controller = remember {
        LifecycleCameraController(context.applicationContext).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or
                        CameraController.VIDEO_CAPTURE
            )
        }
    }

    val viewModel = viewModel<MainViewModel>()

    Box(Modifier.fillMaxSize()) {

        AndroidView(
            factory = {
                PreviewView(it).apply {
                    this.controller = controller
                    controller.bindToLifecycle(lifecycleOwner)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.align(Alignment.BottomCenter)) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(5.dp),
                onClick = {
                    takePhoto(
                        applicationContext = context,
                        controller = controller,
                        navController,
                        onPhotoTaken = viewModel::onTakePhoto
                    )
                },
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center // İçeriği ortalar
                ) {
                    Text(text = "Fotoğraf çek")
                }
            }
        }


    }

}

private fun takePhoto(
    applicationContext: Context,
    controller: LifecycleCameraController,
    navController: NavHostController,
    onPhotoTaken: (Bitmap) -> Unit
) {
    val photoFile = createFile()

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    controller.takePicture(outputOptions,
        ContextCompat.getMainExecutor(applicationContext),
        object : ImageCapture.OnImageCapturedCallback(), ImageCapture.OnImageSavedCallback {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

                val matrix = Matrix().apply {
                    postRotate(image.imageInfo.rotationDegrees.toFloat())
                }
                val rotatedBitmap = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.width,
                    image.height,
                    matrix,
                    true
                )

                onPhotoTaken(rotatedBitmap)


            }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)
                // Galeriye kaydetme
                MediaScannerConnection.scanFile(applicationContext, arrayOf(savedUri.path), null, null)
                navController.navigate(TextRecognitionScreens.Main.name)
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("Camera", "Couldn't take photo: ", exception)
                navController.navigate(TextRecognitionScreens.Main.name)
            }
        }
    )
}

private fun createFile(): File {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK).format(Date())
    val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    return File(storageDir, "JPEG_${timestamp}_.jpg")
}