package com.jack.lightmeter

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class InstrumentedTest {
    private var CameraObject:CameraSettings?=null;
    lateinit var instrumentationContext: Context
    @Before
    fun setup(){
        instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext
        CameraObject = CameraSettings(0f, 1.0f, 50, instrumentationContext);
    }
    @Test
    fun checkCmaeraObjectNotNull() {
        assertNotEquals(null, this.CameraObject)
    }
}