package com.hfad.sensorsdetect

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.hardware.*
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.util.*

@Suppress("DEPRECATION")
class SensorActivity : AppCompatActivity(), SensorListener {


    lateinit var xViewA: TextView
    lateinit var yViewA: TextView
    lateinit var zViewA: TextView
    lateinit var xViewO: TextView
    lateinit var yViewO: TextView
    lateinit var zViewO: TextView
    lateinit var sm: SensorManager
    var s =""
    var lis: SensorListener = this
    var tag = ""
    lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor)

        saveButton = findViewById(R.id.detect_button)
        saveButton.isEnabled = false

        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1)
        } else {
            saveButton.isEnabled = true
        }

        xViewA = findViewById(R.id.xViewA)
        yViewA = findViewById(R.id.yViewA)
        zViewA = findViewById(R.id.zViewA)
        xViewO = findViewById(R.id.xViewO)
        yViewO = findViewById(R.id.yViewO)
        zViewO = findViewById(R.id.zViewO)

        sm = getSystemService(SENSOR_SERVICE) as SensorManager
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    saveButton.isEnabled = true
                } else {
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }


    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(sensor: Int, values: FloatArray) {
        synchronized(this) {
            if (sensor == SensorManager.SENSOR_ORIENTATION) {
                xViewO.text = "\n Orientation X: " + values[0]
                yViewO.text = "\n Orientation Y: " + values[1]
                zViewO.text = "\n Orientation Z: " + values[2]
            }
            if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
                xViewA.text = "\n Accel X: " + values[0]
                yViewA.text = "\n Accel Y: " + values[1]
                zViewA.text = "\n Accel Z: " + values[2]
            }
        }
    }

    override fun onAccuracyChanged(sensor: Int, accuracy: Int) {
        Log.d(tag, "onAccuracyChanged: $sensor, accuracy: $accuracy")
    }

    fun onSaveButtonClick(v: View) {
        var now = Date()
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now)

        try {
            var mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg"
            var v1: View = window.decorView.rootView
            v1.isDrawingCacheEnabled = true
            var bitmap: Bitmap = Bitmap.createBitmap(v1.drawingCache)
            v1.isDrawingCacheEnabled = false

            var imageFile = File(mPath)

            var outputStream = FileOutputStream(imageFile)
            var quality = 100
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.flush()
            outputStream.close()

            var toast = Toast.makeText(applicationContext, "Скриншот сохранен", Toast.LENGTH_SHORT)
            toast.show()
            //openScreenshot(imageFile)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun openScreenshot(imageFile: File) {
        var intent = Intent()
        intent.action = Intent.ACTION_VIEW
        var uri = Uri.fromFile(imageFile)
        intent.setDataAndType(uri, "image/*")
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        sm.registerListener(lis, SensorManager.SENSOR_ORIENTATION or SensorManager.SENSOR_ACCELEROMETER, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onStop() {
        super.onStop()
        sm.unregisterListener(lis)
    }
}
