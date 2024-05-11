package com.example.thesis_app

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class MyImageAnalyzer(onTextFound: (String) -> Unit) : ImageAnalysis.Analyzer{
    private val textRecognizer = MyTextRecognizer(onTextFound)

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val image = imageProxy.image ?: return
        textRecognizer.recognizeImageText(image, imageProxy.imageInfo.rotationDegrees){
            imageProxy.close()
        }
    }
}