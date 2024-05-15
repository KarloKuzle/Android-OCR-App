package com.example.thesis_app

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer


class MyImageAnalyzer(onTextFound: (String) -> Unit) : ImageAnalysis.Analyzer{
    private val textRecognizer = MyTextRecognizer(onTextFound)

    private fun convertImageToBitmap(image: Image): Bitmap {
        val yBuffer = image.planes[0].buffer // y
        val vuBuffer = image.planes[2].buffer // vu
        val ySize = yBuffer.remaining()
        val vuSize = vuBuffer.remaining()
        val nv21 = ByteArray(ySize + vuSize)
        yBuffer.get(nv21, 0, ySize)
        vuBuffer.get(nv21, ySize, vuSize)
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val outStream = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, outStream)
        val imageBytes = outStream.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, null)
    }

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val image = imageProxy.image ?: return

        val bmp = convertImageToBitmap(image)

        val height = bmp.height
        val width = bmp.width

//        var left: Int
//        var right: Int
//        var top: Int
//        var bottom: Int
//        var diameter: Int

        var diameter = width
        if (height < width){
            diameter = height
        }

        val offset = (0.05 * diameter).toInt()
        diameter -= offset

        val left = width / 2 - diameter / 3
        val top = height / 2 - diameter / 3
        val right = width / 2 + diameter / 3
        val bottom = height / 2 + diameter / 3

        val boxHeight = bottom - top
        val boxWidth = right - left

        // Cropped bitmap
        var bitmap = Bitmap.createBitmap(bmp, left, top * 2 / 3, boxWidth, boxHeight)

        val croppedImage = InputImage.fromBitmap(bitmap, imageProxy.imageInfo.rotationDegrees)

        textRecognizer.recognizeImageText(croppedImage, imageProxy.imageInfo.rotationDegrees){
            imageProxy.close()
        }
    }
}