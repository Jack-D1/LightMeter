package com.jack.lightmeter

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.jack.lightmeter.R.*
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {
    var sensorManager:SensorManager?= null
    var sensor:Sensor?=null
    var tv1:TextView?=null
    var tv2:TextView?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)
        tv1 = findViewById(id.text)
        tv2 = findViewById(id.text_lux)


    }

    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(this,sensor,SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun onPause(){
        super.onPause();
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(event!!.values[0] > 0){
            var lx:Float
            lx = event!!.values[0]
            var ts:Float
            ts = (((1.4f * 1.4f) * 250f)) / (lx * 1600f)
            tv1?.text = ts.toString() + " seconds"
            tv2?.text = lx.toString() + "lx"
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }


}


