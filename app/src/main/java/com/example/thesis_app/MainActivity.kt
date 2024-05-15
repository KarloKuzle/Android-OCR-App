package com.example.thesis_app

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.thesis_app.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.math.RoundingMode



class MainActivity : AppCompatActivity() {
    public var winMan = windowManager
    private lateinit var binding: ActivityMainBinding
    // SurfaceView surfaceView = binding.overlay

    private var cameraHeight: Int = 0
    private var cameraWidth: Int = 0
    public var xOffset: Int = 0
    public var yOffset: Int = 0

    private lateinit var canvas: Canvas
    private lateinit var paint: Paint
    private lateinit var holder: SurfaceHolder

//    public fun getBoxWidth() : Int{
//        return boxWidth
//    }
//    public fun getBoxHeight() : Int{
//        return boxHeight
//    }
    private val isAllPermissionsGranted get() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        public var boxWidth: Int = 0
        public var boxHeight: Int = 0
        private val TAG = MainActivity::class.java.name
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    private fun requestPermissions() = ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (isAllPermissionsGranted) {
                startCamera()
            } else {
                Snackbar.make(binding.previewView, "Odbijen pristup kameri.", Snackbar.LENGTH_LONG).setAction("Ponovno") {
                    requestPermissions()
                }.show()
            }
        }
    }

    private val cameraAdapter = CameraAdapter {
        val regex1 = """^\d+.*$""".toRegex()
        if (regex1.matches(it)){
            val regex2 = """^\d+""".toRegex()
            val eur = regex2.find(it)?.value
            val kune = eur?.toFloatOrNull()?.times(7.5345)
            val kuneRounded = kune?.toBigDecimal()?.setScale(2, RoundingMode.UP)
            binding.textView.text = "$eur Eur -> $kuneRounded Kn"

        }
        // binding.textView.text = it
    }

    private fun startCamera() = cameraAdapter.startCamera(this, this, binding.previewView.surfaceProvider)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (isAllPermissionsGranted) startCamera() else requestPermissions()

        binding.overlay.setZOrderOnTop(true)
        holder = binding.overlay.holder
        holder.setFormat(PixelFormat.TRANSPARENT)
        holder.addCallback(object: SurfaceHolder.Callback{
            override fun surfaceCreated(holder: SurfaceHolder) {}

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                drawFocusRect(Color.parseColor("#b3dabb"))
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {}
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        cameraAdapter.shutdown()
    }

    private fun drawFocusRect(color: Int){
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = binding.previewView.height
        val width = binding.previewView.width

        cameraHeight = height
        cameraWidth = width

        var left: Int
        var right: Int
        var top : Int
        var bottom: Int
        var diameter: Int

        diameter = width
        if (height < width){
            diameter = height
        }

        val offset = (0.05 * diameter).toInt()
        diameter -= offset

        canvas = holder.lockCanvas()
        canvas.drawColor(0, PorterDuff.Mode.CLEAR)

        paint = Paint()
        paint.style = Paint.Style.STROKE
        paint.color = color
        paint.strokeWidth = 5f

        left = width / 2 - diameter / 3
        top = height / 2 - diameter / 3
        right = width / 2 + diameter / 3
        bottom = height / 2 + diameter / 2

        xOffset = left
        yOffset = top
        boxHeight = bottom - top
        boxWidth = right - left

        canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
        holder.unlockCanvasAndPost(canvas)
    }


}