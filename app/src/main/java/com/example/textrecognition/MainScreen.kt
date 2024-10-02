package com.example.textrecognition

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@Composable
fun MainScreen() {
    var photoUri: Uri? by remember { mutableStateOf(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
       photoUri = uri
    }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Absolute.SpaceEvenly,
    ) {

        ElevatedCard(
            modifier = Modifier
                .height(100.dp)
                .width(100.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            onClick = {  launcher.launch(
                PickVisualMediaRequest(
                //Here we request only photos. Change this to .ImageAndVideo if
                //you want videos too.
                //Or use .VideoOnly if you only want videos.
                mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
            )
            ) },
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


        if (photoUri != null) {
            //Use Coil to display the selected image
            val painter = rememberAsyncImagePainter(
                ImageRequest
                    .Builder(LocalContext.current)
                    .data(data = photoUri)
                    .build()
            )

            Image(
                modifier = Modifier.width(200.dp).height(200.dp).background(Color.Red),
                painter = painter,
                contentDescription = null
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    MainScreen()
}