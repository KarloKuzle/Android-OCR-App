package com.example.thesis_app

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.thesis_app.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.math.RoundingMode


class MainActivity : AppCompatActivity() {
    private val isAllPermissionsGranted get() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
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

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (isAllPermissionsGranted) startCamera() else requestPermissions()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraAdapter.shutdown()
    }

}