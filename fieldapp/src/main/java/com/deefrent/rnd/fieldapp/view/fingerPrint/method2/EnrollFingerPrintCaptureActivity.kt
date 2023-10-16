package com.deefrent.rnd.fieldapp.view.fingerPrint.method2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
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
import com.deefrent.rnd.common.data.fingerprint.FingerPrintData
import com.deefrent.rnd.common.utils.CUSTOMER_PHONE_NUMBER_AT_FP_TAKING
import com.deefrent.rnd.common.utils.IMAGE_PROFILE_TEST
import com.deefrent.rnd.common.utils.NUMBER_OF_FINGER_BEING_TAKEN
import com.deefrent.rnd.common.utils.saveBitmapToFile
import com.deefrent.rnd.common.utils.visibilityView
import com.deefrent.rnd.fieldapp.databinding.ActivityFingerPrintCaptureBinding
import com.deefrent.rnd.fieldapp.databinding.AppSubToolBarLayoutBinding
import com.deefrent.rnd.fieldapp.utils.makeGone
import com.deefrent.rnd.fieldapp.utils.makeVisible
import com.deefrent.rnd.fieldapp.utils.setToolbarTitle
import com.deefrent.rnd.fieldapp.view.fingerPrint.FingerPrintViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


class EnrollFingerPrintCaptureActivity : CommonBaseActivity() {

    @Inject
    lateinit var viewModel: FingerPrintViewModel
    private lateinit var binding: ActivityFingerPrintCaptureBinding
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
        //m_title!!.visibility = View.GONE
        m_title!!.visibilityView(false)
        m_enginError = ""
        m_deviceName = getIntent().getExtras()?.getString("device_name").toString()
        m_selectedDevice!!.text = "Device: $m_deviceName"
        m_selectedDevice!!.visibilityView(false)
        //
        m_bitmap = Globals.GetLastBitmap()
        /* m_bitmap =
             BitmapFactory.decodeResource(getResources(), R.drawable.black_tets)*/
        m_back!!.setOnClickListener {
            //
            onBackPressed()
            if (m_bitmap != null) {
                lifecycleScope.launch {
                    binding.progressbar.mainPBar.makeVisible()
                    delay(100L)
                    viewModel.saveFingerPrintDataRoom(
                        FingerPrintData(
                            phoneNumber = CUSTOMER_PHONE_NUMBER_AT_FP_TAKING,
                            fingerImage = saveBitmapToFile(
                                bitmap = m_bitmap,
                                context = this@EnrollFingerPrintCaptureActivity.applicationContext,
                                fileName = "${CUSTOMER_PHONE_NUMBER_AT_FP_TAKING}_${(100..10000).random()}"
                            ),
                            handType = "1",
                            fingerPosition = NUMBER_OF_FINGER_BEING_TAKEN,
                            description = "Customer"
                        )
                    )
                    binding.progressbar.mainPBar.makeGone()

                }
                /**
                 * WHEN IMAGE IS BASE 64
                 */
                /*    lifecycleScope.launch {
                        binding.progressbar.mainPBar.makeVisible()
                        delay(100L)
                        viewModel.saveFingerPrintDataRoom(
                            FingerPrintData(
                                phoneNumber = CUSTOMER_PHONE_NUMBER_AT_FP_TAKING,
                                fingerImage = ImageUtil.convertBitMapToString(
                                    m_bitmap!!
                                ),
                                handType = "1",
                                fingerPosition = TYPE_OF_FINGER,
                                description = "Customer"
                            )
                        )
                        binding.progressbar.mainPBar.makeGone()

                    }*/
            }
        }
        setToolbarTitle(
            toolBarBinding = mmf_toolBar,
            activity = this,
            title = "Capture fingerprint", //getString(com.ekenya.rnd.common.R.string.funeral_insurance),
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
        binding = ActivityFingerPrintCaptureBinding.inflate(layoutInflater)
        setContentView(binding.root)
        m_textString = "Place customer finger on the reader"
        initializeActivity()

        // initiliaze dp sdk
        initDpSDK()

        //simulateFingerPrintWithCamera()

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
                    /* if (!m_first && m_resultAvailableToDisplay) {
                         val formatting = DecimalFormat("##.######")
                         m_text_conclusionString =
                             "Dissimilarity Score: $m_score, False match rate: " + java.lang.Double.valueOf(
                                 formatting.format(m_score.toDouble() / 0x7FFFFFFF)
                             ) + " (" + (if (m_score < 0x7FFFFFFF / 100000) "match" else "no match") + ")"
                     }*/
                    m_textString = "Place customer finger on the reader"
                } else {
                    m_first = false
                    // m_textString = "Place the same or a different finger on the reader"
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
            //toast("NOT EMPTY")
            IMAGE_PROFILE_TEST = saveBitmapToFile(
                bitmap = m_bitmap!!,
                context = this@EnrollFingerPrintCaptureActivity.applicationContext,
                fileName = "TEST_${(100..10000).random()}"
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


    /**
     * START OF SIMULATION WITH CAMERA
     */
    /*    private fun simulateFingerPrintWithCamera() {
            m_title?.setOnClickListener {
                onClickRequestOpenCameraPermission {
                    openCameraForImageCapturing(CAPTURE_CAMERA_CODE)
                }
            }
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            try {
                if (requestCode == CAPTURE_CAMERA_CODE && resultCode == Activity.RESULT_OK && data != null) {
                    val thumbnailIDBack = data!!.extras!!.get("data") as Bitmap
                    IMAGE_PROFILE_BITMAP = thumbnailIDBack
                    IMAGE_PROFILE_TEST = ImageUtil.convertBitMapToString(
                        thumbnailIDBack
                    )
                    m_imgView!!.setImageBitmap(thumbnailIDBack)

                    lifecycleScope.launch {
                        viewModel.saveFingerPrintDataRoom(
                            FingerPrintData(
                                phoneNumber = "0798997948",
                                fingerImage = ImageUtil.convertBitMapToString(
                                    thumbnailIDBack
                                ),
                                handType = "1",
                                fingerPosition = TYPE_OF_FINGER,
                                description = "Customer"
                            )
                        )
                    }

                    if (thumbnailIDBack != null) {
                        val rnds = (100..10000).random()
                        val uri = convertBitmapToFile("PROFILE_IMAGE${rnds}", thumbnailIDBack)
                        //val uri = getImageUri(thumbnailIDBack, "BackId")
                        val uriPathHelper = URIPathHelper()
                        IMAGE_PROFILE_FILE_NAME =
                            uriPathHelper.getPath(this, uri)!!

                    }
                }
            } catch (e: Exception) {
                Timber.d("Exception ${e.message}")
            }
            super.onActivityResult(requestCode, resultCode, data)
        }*/


    /**
     * END OF SIMULATION WITH CAMERA
     */
}