package com.example.textrecognition.ui

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.example.textrecognition.PhotoManager
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

    fun onTakePhoto(bitmap: Bitmap) {
        _bitmaps.value += bitmap
    }


    private fun processTextRecognitionResult(firebaseVisionText: FirebaseVisionText): String {
        var detectedText: String? = null
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