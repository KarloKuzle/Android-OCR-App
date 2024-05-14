package com.example.thesis_app

import android.media.Image
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class MyTextRecognizer(private val onTextFound: (String) -> Unit) {
    fun recognizeImageText(inputImage: InputImage, rotationDegrees: Int, onResult: (Boolean) -> Unit) {
        // val inputImage = InputImage.fromMediaImage(image, rotationDegrees)
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            .process(inputImage)
            .addOnSuccessListener { recognizedText ->
                processTextFromImage(recognizedText)
                onResult(true)
            }
    }

    private fun processTextFromImage(text: Text) {
        text.textBlocks.joinToString {
            it.text.lines().joinToString(" ")
        }.let {
            if (!it.isBlank()) {
                onTextFound(it)
            }
        }
    }

    companion object {
        private val TAG = MyTextRecognizer::class.java.name
    }
}