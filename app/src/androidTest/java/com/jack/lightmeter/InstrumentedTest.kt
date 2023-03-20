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
    fun checkCameraObjectNotNull() {
        assertNotEquals(null, this.CameraObject)
    }

    @Test
    fun UserDefinedShouldBeEmpty(){
        assertEquals(0,this.CameraObject?.userDefinedISOs?.size)
        assertEquals(0, this.CameraObject?.userDefinedApertures?.size)
        assertEquals(0, this.CameraObject?.userDefinedShutterSpeeds?.size)
    }

    @Test
    fun RemovingNonExistentValues(){
        assertEquals(2, this.CameraObject?.RemoveCustomAperture(0.99f))
        assertEquals(2, this.CameraObject?.RemoveCustomISO(1))
        assertEquals(2, this.CameraObject?.RemoveCustomshutterSpeed("1/16000"))
    }

    @Test
    fun CheckAddAndRemove(){
        assertEquals(0, this.CameraObject?.AddCustomAperture(0.95f))
        assertEquals(0, this.CameraObject?.AddCustomISO(5))
        assertEquals(0, this.CameraObject?.AddCustomShutterSpeed("1/16000"))

        assertEquals(0, this.CameraObject?.RemoveCustomAperture(0.95f))
        assertEquals(0, this.CameraObject?.RemoveCustomISO(5))
        assertEquals(0,this.CameraObject?.RemoveCustomshutterSpeed("1/16000"))
    }
}