package com.deefrent.rnd.fieldapp.view.fingerPrint.method1

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.digitalpersona.uareu.Fid
import com.digitalpersona.uareu.Quality
import com.digitalpersona.uareu.Reader
import com.digitalpersona.uareu.jni.DpfjQuality
import com.deefrent.rnd.fieldapp.view.fingerPrint.method2.Globals
import io.reactivex.subjects.BehaviorSubject
import okhttp3.internal.notify
import okhttp3.internal.wait

interface FingerPrintCallback {
    fun fingerPrintAvailable(): BehaviorSubject<Bitmap>
}

class ConfigurationForCaptureFingerprint(private val context: Context, m_deviceName: String) :
    FingerPrintCallback {
    private val TAG = "***CaptureFingerPrint***"
    private var m_deviceName = ""
    private var m_reader: Reader? = null
    private var m_DPI = 0
    private val m_first = true
    private val m_resultAvailableToDisplay = false
    private var cap_result: Reader.CaptureResult? = null
    private var m_reset = false
    private var m_bitmap: Bitmap? = null
    var bSubject = BehaviorSubject.create<Bitmap>()

    init {
        this.m_deviceName = m_deviceName
    }

    fun initDpSdk() {
        // initialize dp sdk
        try {
            val applContext = context.applicationContext
            m_reader = Globals.getInstance().getReader(m_deviceName, applContext)
            m_reader?.Open(Reader.Priority.EXCLUSIVE)
            m_DPI = Globals.GetFirstDPI(m_reader)
            val result = m_reader?.GetParameter(Reader.ParamId.DPFPDD_PARMID_PAD_ENABLE)
        } catch (e: Exception) {
            Log.w("UareUSampleJava", "error during init of reader")
            m_deviceName = ""
            return
        }
        captureF()
    }

    private fun captureF() {

        // loop capture on a separate thread to avoid freezing the UI
        Thread {
            try {
                m_reset = false
                while (!m_reset) {
                    // capture the image (synchronous)
                    if (true) {
                        cap_result = m_reader!!.Capture(
                            Fid.Format.ANSI_381_2004,
                            Globals.DefaultImageProcessing,
                            m_DPI,
                            -1
                        )
                    }

                    // capture the image (asynchronous)
                    if (false) {
                        val captureComplete = Any()
                        m_reader!!.CaptureAsync(
                            Fid.Format.ANSI_381_2004,
                            Globals.DefaultImageProcessing,
                            m_DPI,
                            -1
                        ) { result ->
                            synchronized(captureComplete) {
                                cap_result = result
                                captureComplete.notify()
                            }
                        }

                        // note: may need to place a time limit on the wait
                        synchronized(captureComplete) { captureComplete.wait() }
                    }


                    // an error occurred
                    if (cap_result == null) continue
                    if (cap_result!!.image != null) {
                        // save bitmap image locally
                        m_bitmap = Globals.GetBitmapFromRaw(
                            cap_result!!.image.views[0].imageData,
                            cap_result!!.image.views[0].width,
                            cap_result!!.image.views[0].height
                        )
                        if (m_bitmap != null) {
                            bSubject.onNext(m_bitmap!!)
                        }

                        // calculate nfiq score
                        val quality = DpfjQuality()
                        val nfiqScore = quality.nfiq_raw(
                            cap_result!!.image.views[0].imageData,  // raw image data
                            cap_result!!.image.views[0].width,  // image width
                            cap_result!!.image.views[0].height,  // image height
                            m_DPI,  // device DPI
                            cap_result!!.image.bpp,  // image bpp
                            Quality.QualityAlgorithm.QUALITY_NFIQ_NIST // qual. algo.
                        )

                        // log NFIQ score
                        Log.i("UareUSampleJava", "capture result nfiq score: $nfiqScore")

                        // update ui string
                    } else {
                        // update ui string
                    }
                    myResults()
                }
            } catch (e: Exception) {
                if (!m_reset) {
                    Log.w("UareUSampleJava", "error during capture: $e")
                    m_deviceName = ""
                }
            }
        }.start()
    }

    private fun myResults(): Bitmap? {
        return m_bitmap
    }

    override fun fingerPrintAvailable(): BehaviorSubject<Bitmap> {
        return bSubject
    }
}

