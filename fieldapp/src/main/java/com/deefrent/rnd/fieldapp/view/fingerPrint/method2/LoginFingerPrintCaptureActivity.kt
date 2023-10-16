package com.deefrent.rnd.fieldapp.view.fingerPrint.method2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.digitalpersona.uareu.Engine
import com.digitalpersona.uareu.Fid
import com.digitalpersona.uareu.Fmd
import com.digitalpersona.uareu.Reader
import com.digitalpersona.uareu.UareUGlobal
import com.deefrent.rnd.common.abstractions.CommonBaseActivity
import com.deefrent.rnd.common.network.CommonSharedPreferences
import com.deefrent.rnd.common.network.ResourceNetworkFlow
import com.deefrent.rnd.common.utils.IMAGE_PROFILE_TEST
import com.deefrent.rnd.common.utils.AUTH_IMAGE_FILE_PATH
import com.deefrent.rnd.common.utils.deleteImageFile
import com.deefrent.rnd.common.utils.saveBitmapToFile
import com.deefrent.rnd.common.utils.showOneButtonDialog
import com.deefrent.rnd.common.utils.visibilityView
import com.deefrent.rnd.fieldapp.databinding.ActivityLoginFingerPrintCaptureBinding
import com.deefrent.rnd.fieldapp.databinding.AppSubToolBarLayoutBinding
import com.deefrent.rnd.fieldapp.utils.makeGone
import com.deefrent.rnd.fieldapp.utils.makeVisible
import com.deefrent.rnd.fieldapp.utils.setToolbarTitle
import com.deefrent.rnd.fieldapp.view.fingerPrint.FingerPrintViewModel
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import javax.inject.Inject


class LoginFingerPrintCaptureActivity : CommonBaseActivity() {
    @Inject
    lateinit var viewModel: FingerPrintViewModel

    @Inject
    lateinit var commonSharedPreferences: CommonSharedPreferences

    private lateinit var binding: ActivityLoginFingerPrintCaptureBinding
    private var m_back: Button? = null
    private var m_imgView: ImageView? = null
    private var m_selectedDevice: TextView? = null
    private var m_title: TextView? = null
    private var m_text: TextView? = null
    private var m_text_conclusion: TextView? = null
    private var lottieView: LottieAnimationView? = null
    private lateinit var mmf_toolBar: AppSubToolBarLayoutBinding//? = null
    //

    private var m_deviceName = ""

    private var m_enginError: String? = null

    private var m_reader: Reader? = null
    private var m_DPI = 0
    private var m_bitmap: Bitmap? = null

    private var m_reset = false

    private var m_textString: String? = null
    private var m_text_conclusionString: String? = null
    private lateinit var m_engine: Engine //? = null
    private var m_fmd: Fmd? = null
    private var m_score = -1
    private var m_first = true
    private var m_resultAvailableToDisplay = false
    private var cap_result: Reader.CaptureResult? = null

    private fun initializeActivity() {
        m_title = binding.title//findViewById<View>(R.id.title) as TextView?
        m_selectedDevice =
            binding.selectedDevice//findViewById<View>(R.id.selected_device) as TextView?
        m_imgView = binding.bitmapImage//findViewById<View>(R.id.bitmap_image) as ImageView?
        m_back = binding.back//findViewById<View>(R.id.back) as Button?
        m_text = binding.text//findViewById<View>(R.id.text) as TextView?
        lottieView = binding.lottieCaptureFingerprint
        //findViewById<View>(R.id.lottie_capture_fingerprint) as LottieAnimationView?
        m_text_conclusion =
            binding.textConclusion//findViewById<View>(R.id.text_conclusion) as TextView?
        mmf_toolBar = binding.toolBar//findViewById<View>(R.id.toolBar) as ConstraintLayout?
        /**
         *
         */
        m_title!!.text = "Verification"
        m_title!!.visibility = View.GONE
        m_enginError = ""

        m_deviceName = getIntent().getExtras()?.getString("device_name").toString()
        m_selectedDevice!!.text = "Device: $m_deviceName"
        m_selectedDevice!!.visibility = View.GONE

        //
        m_bitmap = Globals.GetLastBitmap()
        /* m_bitmap =
             BitmapFactory.decodeResource(getResources(), R.drawable.black_tets)*/
        m_back!!.setOnClickListener {
            // onBackPressed()
            performApiLoginUsersWithFingerPrintRequest()
        }
        setToolbarTitle(
            toolBarBinding = mmf_toolBar,
            activity = this,
            title = "Authorize Transaction",
            action = {
                onBackPressed()
            }
        )

        Globals.DefaultImageProcessing = Reader.ImageProcessing.IMG_PROC_DEFAULT
        UpdateGUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_finger_print_capture)
        binding = ActivityLoginFingerPrintCaptureBinding.inflate(layoutInflater)
        setContentView(binding.root)
        m_textString = "Place customer finger on the reader"
        initializeActivity()

        // initiliaze dp sdk
        initDpSDK()


    }


    private fun initDpSDK() {
        try {
            val applContext: Context = getApplicationContext()
            m_reader = Globals.getInstance().getReader(m_deviceName, applContext)
            m_reader!!.Open(Reader.Priority.EXCLUSIVE)
            m_DPI = Globals.GetFirstDPI(m_reader)
            m_engine = UareUGlobal.GetEngine()
        } catch (e: Exception) {
            Log.w("UareUSampleJava", "error during init of reader")
            m_deviceName = ""
            onBackPressed()
            return
        }

        // loop capture on a separate thread to avoid freezing the UI
        Thread {
            m_reset = false
            while (!m_reset) {
                try {
                    cap_result = m_reader!!.Capture(
                        Fid.Format.ANSI_381_2004,
                        Globals.DefaultImageProcessing,
                        m_DPI,
                        -1
                    )
                } catch (e: Exception) {
                    if (!m_reset) {
                        Log.w("UareUSampleJava", "error during capture: $e")
                        m_deviceName = ""
                        onBackPressed()
                        commonSharedPreferences.setIsFingerPrintDone(false)
                    }
                }
                m_resultAvailableToDisplay = false

                // an error occurred
                if (cap_result == null || cap_result!!.image == null) continue
                try {
                    m_enginError = ""

                    // save bitmap image locally
                    m_bitmap = Globals.GetBitmapFromRaw(
                        cap_result!!.image.views[0].imageData,
                        cap_result!!.image.views[0].width,
                        cap_result!!.image.views[0].height
                    )
                    if (m_fmd == null) {
                        m_fmd = m_engine.CreateFmd(
                            cap_result!!.image,
                            Fmd.Format.ANSI_378_2004
                        )
                    } else {
                        m_score = m_engine.Compare(
                            m_fmd,
                            0,
                            m_engine.CreateFmd(
                                cap_result!!.image,
                                Fmd.Format.ANSI_378_2004
                            ),
                            0
                        )
                        m_fmd = null
                        m_resultAvailableToDisplay = true
                    }
                } catch (e: Exception) {
                    m_enginError = e.toString()
                    Log.w("UareUSampleJava", "Engine error: $e")
                }
                m_text_conclusionString = Globals.QualityToString(cap_result)
                if (!m_enginError!!.isEmpty()) {
                    m_text_conclusionString = "Engine: $m_enginError"
                } else if (m_fmd == null) {
                    if (!m_first && m_resultAvailableToDisplay) {
                        val formatting = DecimalFormat("##.######")
                        m_text_conclusionString =
                            "Dissimilarity Score: $m_score, False match rate: " + java.lang.Double.valueOf(
                                formatting.format(m_score.toDouble() / 0x7FFFFFFF)
                            ) + " (" + (if (m_score < 0x7FFFFFFF / 100000) "match" else "no match") + ")"
                    }
                    m_textString = "Place customer finger on the reader"
                } else {
                    m_first = false
                    m_textString = "Place customer finger on the reader"
                }
                runOnUiThread(Runnable { UpdateGUI() })
            }
        }.start()

    }

    private fun UpdateGUI() {
        m_imgView!!.setImageBitmap(m_bitmap)
        m_imgView!!.invalidate()
        m_text_conclusion!!.text = m_text_conclusionString
        m_text!!.text = m_textString

        if (m_bitmap == null) {
            lottieView!!.visibilityView(true)
            // toast("EMPTY")
        } else {
            lottieView!!.visibilityView(false)
            m_imgView!!.setImageBitmap(m_bitmap)
            saveToDevice(m_bitmap)
            // toast("NOT EMPTY")
            IMAGE_PROFILE_TEST = saveBitmapToFile(
                bitmap = m_bitmap!!,
                context = this@LoginFingerPrintCaptureActivity.applicationContext,
                fileName = "${IMAGE_PROFILE_TEST}_${(100..10000).random()}"
            ).toString()

        }
    }


    override fun onBackPressed() {

        try {
            m_reset = true
            try {
                m_reader!!.CancelCapture()
            } catch (e: Exception) {
            }
            m_reader!!.Close()
        } catch (e: Exception) {
            Log.w("UareUSampleJava", "error during reader shutdown")
        }
        val i = Intent()
        i.putExtra("device_name", m_deviceName)
        setResult(Activity.RESULT_OK, i)
        finish()
    }

    private fun performApiLoginUsersWithFingerPrintRequest() {
        lifecycleScope.launch {
            viewModel.loginUsersWithMultipleImages()
                .collect {
                    when (it) {
                        is ResourceNetworkFlow.Error -> {
                            binding.progressbar.mainPBar.makeGone()
                            showOneButtonDialog(
                                this@LoginFingerPrintCaptureActivity,
                                title = "ERROR",
                                description = it.error.toString(),
                                image = com.deefrent.rnd.common.R.drawable.ic_baseline_error_outline_24
                            )
                            commonSharedPreferences.setIsFingerPrintDone(false)
                            //onBackPressed()
                        }

                        is ResourceNetworkFlow.Loading -> {
                            binding.progressbar.mainPBar.makeVisible()
                        }

                        is ResourceNetworkFlow.Success -> {
                            binding.progressbar.mainPBar.makeGone()
                            Log.e("RESPONSE", "${it.data}")
                            if (it.data?.status == 200) {
                                deleteImageFile(AUTH_IMAGE_FILE_PATH)
                                commonSharedPreferences.setIsFingerPrintDone(true)
                                onBackPressed()
                            } else {
                                showOneButtonDialog(
                                    this@LoginFingerPrintCaptureActivity,
                                    title = "Failed",
                                    description = it.data?.message.toString() ?:  "verification failed",
                                    image = com.deefrent.rnd.common.R.drawable.ic_baseline_error_outline_24
                                )
                                commonSharedPreferences.setIsFingerPrintDone(false)
                                //onBackPressed()
                            }
                        }

                        else -> {
                            Log.e("", "else RESPONSE:")
                        }
                    }
                }
        }
    }

    private fun saveToDevice(fingerPrintBitmap: Bitmap?) {
        AUTH_IMAGE_FILE_PATH = saveBitmapToFile(
            bitmap = fingerPrintBitmap!!,
            context = this@LoginFingerPrintCaptureActivity.applicationContext,
            fileName = "AUTH_FINGER_${(100..10000).random()}"
        ).toString()
    }

    companion object {
        const val IS_AUTH_SUCCESSFUL = "CUST_AUTH"
    }
}