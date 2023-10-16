package com.deefrent.rnd.fieldapp.view.printreceipt;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.deefrent.rnd.common.network.CommonSharedPreferences;
import com.deefrent.rnd.fieldapp.R;
import com.deefrent.rnd.jiboostfieldapp.ui.printer.PrinterConfigs;
import com.deefrent.rnd.jiboostfieldapp.ui.printer.DeviceType;
import com.deefrent.rnd.jiboostfieldapp.ui.printer.QRUtil;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.nexgo.oaf.apiv3.SdkResult;
import com.nexgo.oaf.apiv3.device.printer.OnPrintListener;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

import vpos.apipackage.PosApiHelper;
import vpos.apipackage.Print;


/**
 * The type Print service activity.
 */
public class MoneyMartPrintServiceActivity extends AppCompatActivity implements OnPrintListener {
    private BroadcastReceiver receiver;
    private IntentFilter filter;
    private int voltage_level;
    private int BatteryV;


    /**
     * The Sp.
     */
    SharedPreferences sp;

    CommonSharedPreferences commonSharedPreferences;
    /**
     * The constant SEPARATOR_LINE.
     */
    public static final String SEPARATOR_LINE = "---------------------------";
    private static final String CENTER_PREFIX = "CENTRE:";

    /**
     * The Print service activity tv.
     */
// will enable user to enter any text to be printed
    TextView PrintServiceActivityTV;

    /**
     * The Finish activity on print.
     */
    boolean finishActivityOnPrint = true;
    /**
     * The Encoded image.
     */
    String encodedImage;
    /**
     * The Byte array image.
     */
    byte[] byteArrayImage;
    /**
     * The Bitmap.
     */
    Bitmap bitmap,
    /**
     * The Qr bitmap.
     */
    QRBitmap;
    /**
     * The Print button.
     */
    MaterialButton printButton;

    /**
     * The Params map.
     */
    HashMap<String, String> paramsMap;
    /**
     * The Print text.
     */
    String[] printText;


    private String TAG = "PrintServiceActivity";
    /**
     * The Handler.
     */
    Handler handler = new Handler();
    /**
     * The constant CUSTOMER.
     */
    public static final String CUSTOMER = "CUSTOMER";
    /**
     * The constant AGENT.
     */
    public static final String AGENT = "AGENT";
    /**
     * The constant BOTH.
     */
    public static final String BOTH = "BOTH";
    private PosApiHelper posApiHelper;
    private String receiptTitle = "";

    private DeviceType deviceType = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_service);

        Toolbar toolbar = (Toolbar) findViewById(R.id.dashboard_toolbar);
        toolbar.setTitle("Print Preview");
        ((TextView) findViewById(R.id.toolbarTitleTextView)).setText("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(view -> {
            onBackPressed();
            finish();
        });

        printButton = (MaterialButton) findViewById(R.id.print);

        paramsMap = new HashMap<>();

        loadImage();
        commonSharedPreferences = new CommonSharedPreferences(this.getApplicationContext());
        PrintServiceActivityTV = (TextView) findViewById(R.id.print_preview_tv);

        if (PrinterConfigs.RECEIPT_TEXT_ARRAY != null) {
            printText = PrinterConfigs.RECEIPT_TEXT_ARRAY;
            //
            // receiptTitle = printText[1].replace("CENTRE:", "");
            receiptTitle = PrinterConfigs.TYPE_OF_RECEIPT;
        } else {
            printText = new String[]{};
            showToast("Nothing to print!");
        }

        printButton.setOnClickListener(view -> onButtonPushed());

        if (this.getIntent().hasExtra("params")) {
            finishActivityOnPrint = PrinterConfigs.FINISH_ACTIVITY_ON_PRINT;//this.getIntent().getExtras().getBoolean("finishActivityOnPrint");
        } else {
            finishActivityOnPrint = true;
        }

        //Initialize the SDK components
        try {
            posApiHelper = PosApiHelper.getInstance();
            deviceType = DeviceType.CS10;
        } catch (Throwable e) {
            resume();
        }

    }

    private void resume() {
        init_Gray();
        try {
            if (deviceType == DeviceType.CS10) {
                posApiHelper.PrintInit();
                posApiHelper.PrintCtnStart();
            }
        } catch (Exception ex) {
            showToast("Exception thrown resume:  " + ex.getMessage());
            Log.e(TAG, "Exception Response :-  " + ex.getMessage());
        }
    }

    public void onButtonPushed() { // send text to printer
        disablePrintButton();
        new Thread() {
            @Override
            public void run() {
                try {
                    if (deviceType == DeviceType.CS10) {
                        int ret = posApiHelper.PrintCheckStatus();
                        Log.e(TAG, "PrintCheckStatus :-  " + ret);
                        posApiHelper.PrintInit();
                        posApiHelper.PrintCtnStart();
                        Print.Lib_PrnSetAlign(0);
                        printCS10(paramsMap, printText);
                    }
                } catch (Exception ex) {
                    showToast("Exception thrown onButtonPushed:  " + ex.getMessage());
                    Log.e(TAG, "Exception Response :-  " + ex.getMessage());
                } finally {
                    if (deviceType == DeviceType.CS10) {
                        posApiHelper.PrintClose();
                    }
                }
            }
        }.start();
    }


    /**
     * Send msg.
     *
     * @param strInfo the str info
     */
    public void SendMsg(String strInfo) {
        Message msg = new Message();
        Bundle b = new Bundle();
        b.putString("MSG", strInfo);
        msg.setData(b);
        handler.sendMessage(msg);
    }


    private String formatPreviewLine(String line) {

        String separatorLine = SEPARATOR_LINE + SEPARATOR_LINE + SEPARATOR_LINE + SEPARATOR_LINE;
        if (line.startsWith(CENTER_PREFIX)) {
            return centerText(line.substring(CENTER_PREFIX.length()), 36);
        } else if (SEPARATOR_LINE.equals(line)) {
            return separatorLine.substring(0, 50);
        }
        return line;
    }

    private String formatPreviewText(String[] header, String[] printText, String[] footer) {
        String previewText = "\n";
        for (String line : header) {
            previewText += formatPreviewLine(line) + "\n";
        }
        for (String line : printText) {
            previewText += formatPreviewLine(line) + "\n";
        }
        for (String line : footer) {
            previewText += formatPreviewLine(line) + "\n";
        }
        return previewText;
    }


    private void loadImage() {
        InputStream imageStream = this.getResources().openRawResource(com.deefrent.rnd.common.R.raw.mmf_logo_for_printer);
        ImageView logoPrintServiceActivityIV = (ImageView) findViewById(R.id.logoPrintServiceActivityImageView);
        bitmap = BitmapFactory.decodeStream(imageStream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 1, baos); //bm is the bitmap object
        byteArrayImage = baos.toByteArray();
        logoPrintServiceActivityIV.setImageBitmap(bitmap);
        encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

        if (PrinterConfigs.QR_AUTH_CODE_TO_PRINT != "") {
            QRBitmap = getQRBitmap(PrinterConfigs.QR_AUTH_CODE_TO_PRINT);
            ((ImageView) findViewById(R.id.qrPrintServiceActivityImageView)).setImageBitmap(QRBitmap);
            // logoPrintServiceActivityIV.setImageBitmap(bitmap);
        }
    }

    /**
     * Disable print button.
     */
    public void disablePrintButton() {
        runOnUiThread(() -> {
            printButton.setEnabled(false);
            printButton.setBackgroundColor(getResources().getColor(com.deefrent.rnd.common.R.color.purple_200));
        });
    }

    /**
     * Enable print button.
     */
    public void enablePrintButton() {
        runOnUiThread(() -> {
            printButton.setEnabled(true);
            //printButton.setBackground(getResources().getDrawable(R.drawable.default_button_bg));
            printButton.setBackgroundColor(getResources().getColor(com.deefrent.rnd.common.R.color.kcb_darker_blue));
        });
    }

    /**
     * Show toast.
     *
     * @param message the message
     */
    public void showToast(final String message) {
        runOnUiThread(() -> Toast.makeText(MoneyMartPrintServiceActivity.this, message, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onBackPressed() {
        //showToast("Print receipt to go back");
        Intent i = new Intent();
        i.putExtra("print", "printerCS10");
        setResult(Activity.RESULT_OK, i);
        finish();

        commonSharedPreferences.setIsFingerPrintDone(false);
        commonSharedPreferences.setIsPrintReceipt(false);
    }

    /**
     * Centered text string.
     *
     * @param text the text
     * @param val  the val
     * @return the string
     */
    public static String centeredText(String text, int val) {
        return centeredText(text);
    }

    /**
     * Centered text string.
     *
     * @param text the text
     * @return the string
     */
    public static String centeredText(String text) {
        return CENTER_PREFIX + text;
    }

    /**
     * Center text string.
     *
     * @param text      the text
     * @param paperSize the paper size
     * @return the string
     */
    public static String centerText(String text, int paperSize) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }
        int textLength = text.length();

        if (textLength > paperSize) {
            String compactText = text.substring(0, paperSize);
            int lastSpaceIndex = compactText.lastIndexOf(" ");
            if (lastSpaceIndex <= 0) {
                return compactText + "\n" +
                        centerText(text.substring(paperSize, textLength), paperSize);
            } else {
                return centerText(compactText.substring(0, lastSpaceIndex), paperSize) + "\n" +
                        centerText(text.substring(lastSpaceIndex + 1, textLength), paperSize);
            }
        } else if (textLength == paperSize) {
            return text;
        } else {
            int whiteSpaceLength = paperSize - textLength;
            int padding = whiteSpaceLength / 2;
            for (int i = 0; i < padding; i++) {
                text = " " + text;
            }
            return text;
        }
    }

    private String[] getFooter(HashMap<String, String> paramsMap) {
        return new String[]{
                SEPARATOR_LINE,
                "Served by: " + PrinterConfigs.SERVER_BY,//+ "\n",
                "Powered by: " + "Eclectics International",//+ "\n",
                SEPARATOR_LINE,
                "\n",
                "\n"
        };
    }

    private String[] getHeader(HashMap<String, String> paramsMap, String copyOwner) {
        Log.d("taggerd", receiptTitle);
        return new String[]{
                "** " + receiptTitle + " **",
                SEPARATOR_LINE,
                copyOwner,
                /*      "\n",*/
                PrinterConfigs.AGENT_BRANCH_STREET,
                /*          "\n",*/
                "LOAN OFFICER NAME: " + PrinterConfigs.AGENT_NAME,
                //PrinterConfigs.AGENT_NAME,
                /*         "\n",*/
                "DATE               TIME",
                PrinterConfigs.TIME_OF_TRANSACTION_REQUEST + " ",//+ "\n",
                /*      "\n",*/
                "TRANS REF: " + PrinterConfigs.TRANSACTION_REFERENCE,
                SEPARATOR_LINE
        };

    }

    /**
     * Start printing.
     *
     * @param context       the context
     * @param map           the map
     * @param printText     the print text
     * @param shouldArchive the should archive
     */
    public static void startPrinting(Context context, HashMap<String, String> map, String[] printText, boolean shouldArchive) {
        if (shouldArchive) {
            archiveReceiptData(context, map, printText);
        }
        /*Intent intent = new Intent(context, SignatureCapture.class);
        intent.putExtra("finishActivityOnPrint", true);
        intent.putExtra("printText", printText);
        intent.putExtra("params", map);
        context.startActivity(intent);*/
       /* Intent intent = new Intent(context, PrintServiceActivity.class);
        intent.putExtra("finishActivityOnPrint", true);
        intent.putExtra("printText", printText);
        intent.putExtra("params", map);
        context.startActivity(intent);*/
    }

    /**
     * Start printing.
     *
     * @param context   the context
     * @param map       the map
     * @param printText the print text
     */
    public static void startPrinting(Context context, HashMap<String, String> map, String[] printText) {
        startPrinting(context, map, printText, true);
    }

    private static void archiveReceiptData(Context context, HashMap<String, String> map, String[] printText) {
        // these fields should not be saved
        map.remove("cardSecurityInfo");
        map.remove("cardPinBlock");
        map.remove("cardPAN");
        map.remove("cardField60");
        map.remove("cardTrack2Data");
        map.remove("cardICCData");
        map.remove("cardTrack1Data");
        map.remove("cardExpiryDate");
        map.remove("cardPANSequence");
        map.remove("jwtToken");
        map.remove("charges");
        map.remove("cashPin");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String params = gson.toJson(map);
        String text = gson.toJson(printText);

        // move second receipt to index of third receipt
        if (prefs.getString("params_1", null) != null) {
            editor.putString("params_2", prefs.getString("params_1", null));
            editor.putString("text_2", prefs.getString("text_1", null));
        }

        // move first receipt to index of second receipt
        if (prefs.getString("params_0", null) != null) {
            editor.putString("params_1", prefs.getString("params_0", null));
            editor.putString("text_1", prefs.getString("text_0", null));
        }

        // save new receipt with index of first receipt i.e. 0
        editor.putString("params_0", params);
        editor.putString("text_0", text);

        editor.apply();
    }


    private void finishedPrinting(boolean closeConnection) {
        if (closeConnection) {
            if (finishActivityOnPrint) {
                handler.postDelayed(() -> finish(), 1000);
            }
        }
    }


    private Bitmap getQRBitmap(String qrUrl) {
        Bitmap qrBtMap = QRUtil.generateQR(qrUrl);
        return qrBtMap;
    }

    private void printImage(Bitmap btMap) {
        posApiHelper.PrintBmp(btMap);
    }

    private void setValue(int val) {
        sp = getSharedPreferences("Gray", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("value", val);
        editor.commit();
    }

    private int getValue() {
        sp = getSharedPreferences("Gray", MODE_PRIVATE);
        int value = sp.getInt("value", 3);
        return value;
    }

    private void init_Gray() {
        if (deviceType == DeviceType.CS10) {
            int flag = getValue();
            posApiHelper.PrintSetGray(flag);
        }
    }

    @Override
    protected void onResume() {
        disableFunctionLaunch(true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        printButton.callOnClick();
        if (PrinterConfigs.RECEIPT_TEXT_ARRAY != null) {
            printText = PrinterConfigs.RECEIPT_TEXT_ARRAY;
            //
            receiptTitle = printText[1].replace("CENTRE:", "");
        } else {
            printText = new String[]{};
            showToast("Nothing to print!");
        }

        String previewText = formatPreviewText(getHeader(paramsMap, "LOAN OFFICER COPY"), printText, getFooter(paramsMap));
        PrintServiceActivityTV.setText(previewText);
        super.onResume();
        filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        receiver = new BatteryReceiver();
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        disableFunctionLaunch(false);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        disablePrintButton();
        if (isFinishing()) {
        }
        super.onPause();
        // QuitHandler();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        stopService(mPrintServiceIntent);
//        unbindService(serviceConnection);
    }

    /**
     * The type Battery receiver.
     */
    public class BatteryReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            voltage_level = intent.getExtras().getInt("level");
            Log.e("wbw", "current  = " + voltage_level);
            BatteryV = intent.getIntExtra("voltage", 0);
            Log.e("wbw", "BatteryV  = " + BatteryV);
            Log.e("wbw", "V  = " + BatteryV * 2 / 100);
            //	m_voltage = (int) (65+19*voltage_level/100); //放大十倍
            //   Log.e("wbw","m_voltage  = " + m_voltage );
        }
    }

    // disable the power key when the device is boot from alarm but not ipo boot
    private static final String DISABLE_FUNCTION_LAUNCH_ACTION = "android.intent.action.DISABLE_FUNCTION_LAUNCH";

    private void disableFunctionLaunch(boolean state) {
        Intent disablePowerKeyIntent = new Intent(DISABLE_FUNCTION_LAUNCH_ACTION);
        if (state) {
            disablePowerKeyIntent.putExtra("state", true);
        } else {
            disablePowerKeyIntent.putExtra("state", false);
        }
        sendBroadcast(disablePowerKeyIntent);
    }

    private void printCS10(final HashMap<String, String> paramsMap, final String[] printText) {
        disablePrintButton();
        final String copies;
        if (paramsMap.containsKey("receiptCopies")) {
            copies = paramsMap.get("receiptCopies");
        } else {
            copies = "BOTH";
        }
        InputStream imageStream = this.getResources().openRawResource(com.deefrent.rnd.common.R.raw.mmf_logo_for_printer);
        Bitmap bmp1 = BitmapFactory.decodeStream(imageStream);
        //Bitmap bmp1 = BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.abc3);
        boolean mNeedsCuttingPause = false;

        if (TextUtils.equals(copies, AGENT) ||
                TextUtils.equals(copies, BOTH)) {
            mNeedsCuttingPause = true;
            Print.Lib_PrnSetAlign(1);
            posApiHelper.PrintSetAlign(0);
            int ret = posApiHelper.PrintBmp(bmp1);
            sendCS10Data(getHeader(paramsMap, "LOAN OFFICER COPY"), false); // print header text
            Print.Lib_PrnSetAlign(0);
            posApiHelper.PrintSetAlign(0);
            sendCS10Data(printText, false); // print receipt text
            Print.Lib_PrnSetAlign(0);
            sendCS10Data(getFooter(paramsMap), false); // print footer text
            if (PrinterConfigs.HAS_SIGNATURE_BITMAP) {
                printImage(PrinterConfigs.SignatureBitmap); // print Signature image
                PrinterConfigs.SignatureBitmap = null;
            }
            if (PrinterConfigs.QR_AUTH_CODE_TO_PRINT != "") {
                posApiHelper.PrintSetAlign(1);
//                printer.appendImage(QRBitmap, AlignEnum.CENTER);
                posApiHelper.PrintBmp(QRBitmap); // print QR image
                posApiHelper.PrintStr("\n\n\n");
                posApiHelper.PrintSetAlign(0);
                posApiHelper.PrintStr(PrinterConfigs.RECEIPT_CAPTION);
                posApiHelper.PrintStr("\n\n\n");
            }
            posApiHelper.PrintStr("                                         \n");
            posApiHelper.PrintStr("                                         \n");
            posApiHelper.PrintStart();

        }

        if (TextUtils.equals(copies, CUSTOMER) || // if BI/MI, print customer copy only
                TextUtils.equals(copies, BOTH)) { // print customer copy
            if (mNeedsCuttingPause) {
                this.paramsMap.put("receiptCopies", CUSTOMER); // makes sure only customer copy is printed by clicking button
                enablePrintButton();
                return;
            }
            disablePrintButton();
            posApiHelper.PrintSetAlign(0);
            posApiHelper.PrintBmp(bmp1);
            Print.Lib_PrnSetAlign(0);
            posApiHelper.PrintSetAlign(0);
            sendCS10Data(getHeader(paramsMap, "CUSTOMER COPY"), false);
            Print.Lib_PrnSetAlign(0);
            sendCS10Data(printText, false);
            if (PrinterConfigs.QR_AUTH_CODE_TO_PRINT != "") {
                sendCS10Data(getFooter(paramsMap), false);
                posApiHelper.PrintSetAlign(1);
                posApiHelper.PrintBmp(QRBitmap);
                posApiHelper.PrintStr("\n\n\n");
                posApiHelper.PrintSetAlign(0);
                posApiHelper.PrintStr(PrinterConfigs.RECEIPT_CAPTION);
                posApiHelper.PrintStr("                                         \n");
                posApiHelper.PrintStr("                                         \n");
                posApiHelper.PrintStr("                                         \n");
            } else {
                sendCS10Data(getFooter(paramsMap), true);
                posApiHelper.PrintStr("\n\n\n");
//                printer.appendPrnStr("\n\n\n", 20, AlignEnum.CENTER, false);
                posApiHelper.PrintStr(PrinterConfigs.RECEIPT_CAPTION);
                posApiHelper.PrintStr("                                         \n");
                posApiHelper.PrintStr("                                         \n");
                posApiHelper.PrintStr("                                         \n");
            }
            posApiHelper.PrintStart();
        }

        commonSharedPreferences.setIsFingerPrintDone(false);
        commonSharedPreferences.setIsPrintReceipt(false);
        finishedPrinting(true);
    }

    void sendCS10Data(String[] printText, boolean closeConnection) {
        try {
            for (String line : printText) {
                if (line.startsWith(CENTER_PREFIX)) {
                    posApiHelper.PrintSetAlign(0);
                    line = line.substring(CENTER_PREFIX.length());
                }
                posApiHelper.PrintStr(line);
                posApiHelper.PrintCtnStart();
            }
            finishedPrinting(closeConnection);
        } catch (NullPointerException e) {
            Log.e(TAG, "Exception Response :-  " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception Response :-  " + e.getMessage());
        }
    }


    @Override
    public void onPrintResult(int resultCode) {
        switch (resultCode) {
            case SdkResult.Success:
                Log.d(TAG, "Printer job finished successfully!");
                break;
            case SdkResult.Printer_Print_Fail:
                Log.e(TAG, "Printer Failed: " + resultCode);
                break;
            case SdkResult.Printer_Busy:
                Log.e(TAG, "Printer is Busy: " + resultCode);
                break;
            case SdkResult.Printer_PaperLack:
                Log.e(TAG, "Printer is out of paper: " + resultCode);
                break;
            case SdkResult.Printer_Fault:
                Log.e(TAG, "Printer fault: " + resultCode);
                break;
            case SdkResult.Printer_TooHot:
                Log.e(TAG, "Printer temperature is too hot: " + resultCode);
                break;
            case SdkResult.Printer_UnFinished:
                Log.w(TAG, "Printer job is unfinished: " + resultCode);
                break;
            case SdkResult.Printer_Other_Error:
                Log.e(TAG, "Printer Other_Error: " + resultCode);
                break;
            default:
                Log.e(TAG, "Generic Fail Error: " + resultCode);
                break;
        }
    }


}