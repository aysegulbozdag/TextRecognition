package com.abcoding.textrecognition.ui

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {

    private val _bitmaps = MutableStateFlow<List<Bitmap>>(emptyList())
    val bitmaps = _bitmaps.asStateFlow()

    private val _resultTxt = MutableStateFlow<String?>(null)
    val resultTxt = _resultTxt.asStateFlow()

    private val _uri = MutableStateFlow<Uri?>(null)
    val uri = _uri.asStateFlow()


    fun onTakePhoto(bitmap: Bitmap) {
        _bitmaps.value += bitmap
    }

    fun setUriValue(uri: Uri){
        _uri.value = uri
    }


    private fun processTextRecognitionResult(firebaseVisionText: FirebaseVisionText): String {
        var detectedText: String? = ""
        val textBlockList = firebaseVisionText.textBlocks

        textBlockList.let {
            for (textBlock in textBlockList) {
                detectedText += textBlock.text
            }
        }
        return detectedText ?: "No Text Found in image"
    }

    fun getTextRecognitionResult(firebaseVisionImage: FirebaseVisionImage?) {
        FirebaseVision.getInstance().onDeviceTextRecognizer.apply {
            firebaseVisionImage?.let {
                this.processImage(firebaseVisionImage)
                    .addOnSuccessListener { firebaseVisionText ->
                        _resultTxt.value = processTextRecognitionResult(firebaseVisionText)
                    }.addOnFailureListener { e ->
                        _resultTxt.value = e.message.toString()
                    }
            }
        }

    }
}