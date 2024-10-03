package com.example.textrecognition.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText

@Composable
fun MainScreen(modifier: Modifier) {
    var photoUri: Uri? by remember { mutableStateOf(null) }
    var txt: String by remember { mutableStateOf("") }
    val launcher =
        rememberLauncherForActivityResult(


            ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            photoUri = uri
        }
    val ctx = LocalContext.current


    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {

        if (photoUri != null) {
            val painter = rememberAsyncImagePainter(
                ImageRequest
                    .Builder(LocalContext.current)
                    .data(data = photoUri)
                    .build()
            )

            Image(
                modifier = Modifier
                    .width(200.dp)
                    .height(200.dp)
                    .background(Color.Red),
                painter = painter,
                contentDescription = null
            )

            val firebaseVisionImage = photoUri?.let { uriToBitmap(ctx, it) }
                ?.let { FirebaseVisionImage.fromBitmap(it) }

            FirebaseVision.getInstance().onDeviceTextRecognizer.apply {
                if (firebaseVisionImage != null) {
                    this.processImage(firebaseVisionImage)
                        .addOnSuccessListener { firebaseVisionText ->
                           txt= processTextRecognitionResult(firebaseVisionText)
                        }.addOnFailureListener { e ->
                            Toast.makeText(ctx, "Error : " + e.message, Toast.LENGTH_SHORT).show()
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

            ElevatedCard(
                modifier = Modifier
                    .height(100.dp)
                    .width(100.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                onClick = {
                    launcher.launch(
                        PickVisualMediaRequest(
                            mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
            ) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center // İçeriği ortalar
                ) {
                    Text(text = "Galeri")
                }

            }

            ElevatedCard(modifier = Modifier
                .height(100.dp)
                .width(100.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                onClick = { /*TODO*/ }) {

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

fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            // Android 28 ve altı için BitmapFactory kullanarak yükleme
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