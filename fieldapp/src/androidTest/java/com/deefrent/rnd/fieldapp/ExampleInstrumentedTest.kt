package com.deefrent.rnd.fieldapp

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.deefrent.rnd.etourism", appContext.packageName)
    }

    @Test
    fun P(){
        val regexPattern = """^(?:254|\+254|0)?7(?:(?:[129][0-9])|(?:0[0-8])|(?:4[0-1]))[0-9]{6}$""".toRegex()

            val phoneNumber = "0748188534"

            if (phoneNumber.matches(regexPattern)) {
                println("Valid phone number.")
            } else {
                println("Invalid phone number.")
            }
    }
}