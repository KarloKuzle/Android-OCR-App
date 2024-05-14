package com.example.thesis_app

import android.content.Context
import android.graphics.ImageFormat
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraAdapter(onTextFound: (String) -> Unit){
    private val imageAnalzyerExecutor: ExecutorService by lazy { Executors.newSingleThreadExecutor() }
    private val imageAnalzyer by lazy {
        ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
            .build()
            .also {
                it.setAnalyzer(
                    imageAnalzyerExecutor,
                    MyImageAnalyzer(onTextFound)
                )
            }
    }

    fun startCamera(context: Context, lifecycleOwner: LifecycleOwner, surfaceProvider: Preview.SurfaceProvider){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        var runnable = Runnable {
            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(surfaceProvider) }
            with(cameraProviderFuture.get()) {
                unbindAll()
                bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalzyer
                )
            }
        }
        cameraProviderFuture.addListener(runnable, ContextCompat.getMainExecutor(context))
    }
    fun shutdown(){
        imageAnalzyerExecutor.shutdown()
    }
}
