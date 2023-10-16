package com.deefrent.rnd.jiboostfieldapp.ui.printer;

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
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.deefrent.rnd.jiboostfieldapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.nexgo.oaf.apiv3.APIProxy;
import com.nexgo.oaf.apiv3.DeviceEngine;
import com.nexgo.oaf.apiv3.SdkResult;
import com.nexgo.oaf.apiv3.device.printer.AlignEnum;
import com.nexgo.oaf.apiv3.device.printer.GrayLevelEnum;
import com.nexgo.oaf.apiv3.device.printer.OnPrintListener;
import com.nexgo.oaf.apiv3.device.printer.Printer;
import com.pos.sdk.DeviceManager;
import com.pos.sdk.DevicesFactory;
import com.pos.sdk.callback.ResultCallback;
import com.pos.sdk.printer.PrinterDevice;
import com.pos.sdk.printer.PrinterState;
import com.pos.sdk.printer.param.PrintItemAlign;
import com.pos.sdk.printer.param.QrPrintItemParam;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

import vpos.apipackage.PosApiHelper;
import vpos.apipackage.Print;

/**
 * The type Print service activity.
 */
public class AppPrintServiceActivity extends AppCompatActivity implements OnPrintListener {

    private BroadcastReceiver receiver;
    private IntentFilter filter;
    private int voltage_level;
    private int BatteryV;


    /**
     * The Preferences.
     */
    SharedPreferences preferences;
    /**
     * The Sp.
     */
    SharedPreferences sp;
    /**
     * The Editor.
     */
    SharedPreferences.Editor editor;

    /**
     * The constant SEPARATOR_LINE.
     */
    public static final String SEPARATOR_LINE = "---------------------------";
    private static final String CENTER_PREFIX = "CENTRE:";
    /**
     * The constant TOPUP_CASH.
     */
    public static String TOPUP_CASH = "TOPUP (CASH)";
    /**
     * The constant CARDLESS_FULFILMENT.
     */
    public static String CARDLESS_FULFILMENT = "CARDLESS WITHDRAWAL";
    /**
     * The constant CARDLESS_FULFILMENT_DEPOSIT.
     */
    public static String CARDLESS_FULFILMENT_DEPOSIT = "CARDLESS DEPOSIT";
    /**
     * The constant BILL_PAYMENT.
     */
    public static String BILL_PAYMENT = "BILL PAYMENT";
    /**
     * The constant CASH_DEPOSIT.
     */
    public static String CASH_DEPOSIT = "CASH DEPOSIT";
    /**
     * The constant FLOAT_TRANSFER.
     */
    public static String FLOAT_TRANSFER = "FLOAT TRANSFER";

    /**
     * The Ret.
     */
    int ret = -1;
    private boolean m_bThreadFinished = true;


    //private Pos pos;

    /**
     * The Is working.
     */
    int IsWorking = 0;

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
    /**
     * The constant NONE.
     */
    public static final String NONE = "NONE";

    private PosApiHelper posApiHelper;
    private String receiptTitle = "";

    private DeviceType deviceType = null;
    private DeviceEngine deviceEngine;

    private Printer printer;
    private PrinterDevice k9printer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_service_app);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.dashboard_toolbar);
        toolbar.setTitle("Print Preview");
        ((TextView) findViewById(R.id.toolbarTitleTextView)).setText("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(view -> {
//                onBackPressed();
            finish();
        });*/

        printButton = (MaterialButton) findViewById(R.id.print);

        if (this.getIntent().hasExtra("params"))
            paramsMap = (HashMap<String, String>) this.getIntent().getExtras().getSerializable("params");
        else {
            paramsMap = new HashMap<>();
            paramsMap.put("requestTime", "2017-15-12");
            paramsMap.put("reference", "5256565456545");
        }
        loadImage();

        PrintServiceActivityTV = (TextView) findViewById(R.id.print_preview_tv);

        if (this.getIntent().hasExtra("params")) {
            printText = this.getIntent().getExtras().getStringArray("printText");
            receiptTitle = printText[1].replace("CENTRE:","");
        } else {
            printText = new String[]{};
            showToast("Nothing to print!");
        }

        printText = preparePrintText(printText);
        printButton.setOnClickListener(view -> onButtonPushed());

        if (this.getIntent().hasExtra("params")) {
            finishActivityOnPrint = this.getIntent().getExtras().getBoolean("finishActivityOnPrint");
        } else {
            finishActivityOnPrint = true;
        }

        //Initialize the SDK components
        try {
            Log.e(TAG, "device: " + PosApiHelper.getInstance());
            deviceEngine = APIProxy.getDeviceEngine(this);
            printer = deviceEngine.getPrinter();
            deviceType = DeviceType.N5;
        } catch (Throwable e) {

        }

        try {
            posApiHelper = PosApiHelper.getInstance();
            deviceType = DeviceType.CS10;
        } catch (Throwable e) {

        }

        try {
            DevicesFactory.create(this, new ResultCallback<DeviceManager>() {
                @Override
                public void onFinish(DeviceManager deviceManager) {
                    Log.d(TAG, "onFinish: ");
                    k9printer = deviceManager.getPrintDevice();
                    deviceType = DeviceType.CTA_K;
                    resume();
                }

                @Override
                public void onError(int i, String s) {
                    Log.d(TAG, "onError: " + i + "," + s);
                    resume();
                }
            });
        }catch (Exception exception){
            resume();
        }

    }

    private void resume(){
        init_Gray();
        try {
            if (deviceType == DeviceType.CS10) {
                posApiHelper.PrintInit();
                posApiHelper.PrintCtnStart();
            }else if (deviceType == DeviceType.CTA_K){
            } else if(deviceType == DeviceType.N5){
                printer.initPrinter();

                int initResult = printer.getStatus();
                switch (initResult) {
                    case SdkResult.Success:
                        Log.d(TAG, "Printer init success");
                        break;
                    case SdkResult.Printer_PaperLack:
                        Log.w(TAG, "Printer is out of paper");
                        Toast.makeText(this, "Out of Paper!", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        Log.w(TAG, "Printer Init Misc Error: " + initResult);
                        break;
                }
            }
        } catch (Exception ex) {
            handlePrintingException(ex);
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
                    } else if (deviceType == DeviceType.N5){
                        print(paramsMap, printText);
                    }else if(deviceType == DeviceType.CTA_K){
                        printK9();
                    }
                } catch (Exception ex) {
                    handlePrintingException(ex);
                    Log.e(TAG, "Exception Response :-  " + ex.getMessage());
                } finally {
                    if (deviceType == DeviceType.CS10) {
                        posApiHelper.PrintClose();
                    }
                }
            }
        }.start();
    }

    private void printK9() {
        try {
            addTestData();
            Bundle bundle = new Bundle();
            //you can also set grayscale here
            //bundle.putInt(PrinterParamTag.TAG_PRINT_GRAY, 0x18);
            PrinterState printerState = k9printer.printSync(bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addTestData() {
        long begin = System.currentTimeMillis();
        SlipModelUtils slip = new SlipModelUtils();
        try {
            k9printer.addTextPrintItem(slip.addTextOnePrint("\n\n", false, -1, PrintItemAlign.CENTER));
            k9printer.addBitmapPrintItem(slip.addBitmapAsset(this, "mmf_logo_for_printer.bmp"));
            k9printer.addTextPrintItem(slip.addTextOnePrint("\n\n", false, -1, PrintItemAlign.CENTER));
            k9printer.addTextPrintItem(slip.addTextOnePrint("CASH DEPOSIT SUCCESSFUL", false, 32, PrintItemAlign.LEFT));
            addLineHa(slip);
            k9printer.addTextPrintItem(slip.addTextOnePrint("AGENT COPY", false, -1, PrintItemAlign.LEFT));
            k9printer.addTextPrintItem(slip.addTextOnePrint("Pata Kila Kitu stall", false, -1, PrintItemAlign.LEFT));
            k9printer.addTextPrintItem(slip.addTextOnePrint("AGENT ID : 103000", false, -1, PrintItemAlign.LEFT));
            k9printer.addTextPrintItem(slip.addTextOnePrint("Awesome Jim Enterprises", false, -1, PrintItemAlign.LEFT));
            k9printer.addTextPrintItem(slip.addTextOnePrint("DATE        TIME     TERMINAL  ID", false, -1, PrintItemAlign.LEFT));
            k9printer.addTextPrintItem(slip.addTextOnePrint("2022-05-09  20:32:30 100000    78", false, -1, PrintItemAlign.LEFT));
            k9printer.addTextPrintItem(slip.addTextOnePrint("TRANS REF: 0509115937219", false, -1, PrintItemAlign.LEFT));
            addLineHa(slip);
            k9printer.addTextPrintItem(slip.addTextOnePrint("CASH DEPOSIT", false, -1, PrintItemAlign.CENTER));
            k9printer.addTextPrintItem(slip.addTextOnePrint("CASH DEPOSIT SUCCESSFUL", false, -1, PrintItemAlign.CENTER));
            addLineHa(slip);
            k9printer.addMultipleTextPrintItem(slip.addLeftAndRightItem("Txn Amount:", "KES 2,000", null));
            k9printer.addMultipleTextPrintItem(slip.addLeftAndRightItem("Txn Fee", "KES 0.00", null));
            k9printer.addMultipleTextPrintItem(slip.addLeftAndRightItem("Excise Duty", "KES 0.00", null));
            k9printer.addMultipleTextPrintItem(slip.addLeftAndRightItem("Total Amount:", "KES 2,000", null));
            addLineHa(slip);
            k9printer.addMultipleTextPrintItem(slip.addLeftAndRightItem("Account No: ", "KES 2,000", null));
            k9printer.addMultipleTextPrintItem(slip.addLeftAndRightItem("Total Amount:", "KES 2,000", null));
            k9printer.addMultipleTextPrintItem(slip.addLeftAndRightItem("Total Amount:", "KES 2,000", null));
            k9printer.addMultipleTextPrintItem(slip.addLeftAndRightItem("Account No:", "********367", null));
            k9printer.addMultipleTextPrintItem(slip.addLeftAndRightItem("Account Name:", "Awesome Jim", -1, 32, new float[]{1, 3}));
            k9printer.addMultipleTextPrintItem(slip.addLeftAndRightItem("Narration:", "Custom to NCBA", null));
            addLineHa(slip);
            k9printer.addMultipleTextPrintItem(slip.addLeftAndRightItem("Served by:", "James Maina Mbugua", null));
            k9printer.addMultipleTextPrintItem(slip.addLeftAndRightItem("Powered by:", "Eclectics International", -1, 32, new float[]{1, 1}));
            k9printer.addTextPrintItem(slip.addTextOnePrint("\n", false, -1, PrintItemAlign.CENTER));

            QrPrintItemParam qrPrintItemParam = new QrPrintItemParam();
            qrPrintItemParam.setQRCode("dkodkod");
            qrPrintItemParam.setQrWidth(250);
            qrPrintItemParam.setItemAlign(PrintItemAlign.CENTER);
            k9printer.addQrPrintItemParam(qrPrintItemParam);
            k9printer.addTextPrintItem(slip.addTextOnePrint("\n", false, -1, PrintItemAlign.CENTER));

            k9printer.addTextPrintItem(slip.addTextOnePrint("I ACKNOWLEDGE SATISFACTORY RECEIPT OF RELATIVE GOODS/SERVICES", false, 16, PrintItemAlign.CENTER));
            k9printer.addTextPrintItem(slip.addTextOnePrint("\n\n", false, -1, PrintItemAlign.CENTER));
            enablePrintButton();
            long end = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addLineHa(SlipModelUtils slip) throws RemoteException {
        k9printer.addTextPrintItem(slip.addTextOnePrint("--------------------------------------", true, -1, PrintItemAlign.CENTER));
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

    /*private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case DISABLE_RG:
                    IsWorking = 1;
                    rb_high.setEnabled(false);
                    rb_middle.setEnabled(false);
                    rb_low.setEnabled(false);
                    radioButton_4.setEnabled(false);
                    radioButton_5.setEnabled(false);
                    break;

                case ENABLE_RG:
                    IsWorking = 0;
                    break;
                default:
                    Bundle b = msg.getData();
                    String strInfo = b.getString("MSG");
                   // textViewMsg.setText(strInfo);

                    break;
            }
        }
    };*/
    private void handlePrintingException(Exception ex) {
        ex.printStackTrace();
        showToast("Exception thrown: ");
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

    private String[] preparePrintText(String[] printText) {

        return printText;
    }
     private void print(final HashMap<String, String> paramsMap, final String[] printText) {
         disablePrintButton();
         final String copies;
         if (paramsMap.containsKey("receiptCopies")) {
             copies = paramsMap.get("receiptCopies");
         } else {
             copies = "BOTH";
         }
         InputStream imageStream = this.getResources().openRawResource(R.raw.mmf_logo_for_printer);
         Bitmap bmp1 = BitmapFactory.decodeStream(imageStream);
         //Bitmap bmp1 = BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.abc3);
         boolean mNeedsCuttingPause = false;

         if (TextUtils.equals(copies, AGENT) ||
                 TextUtils.equals(copies, BOTH)) {
             mNeedsCuttingPause = true;
//            Print.Lib_PrnSetAlign(1);
//            posApiHelper.PrintSetAlign(0);
//            int ret = posApiHelper.PrintBmp(bmp1);
             Log.e("PrintBmp", "Lib_PrnStart fail, ret = " + ret);
             Log.e("PrintBmp height", "Lib_PrnStart fail, ret = " + bitmap.getHeight());
             Log.e("PrintBmp width", "Lib_PrnStart fail, ret = " + bitmap.getWidth());
             sendData(getHeader(paramsMap, "AGENT COPY"), false, AlignEnum.LEFT); // print header text
             //Print.Lib_PrnSetAlign(0);
//            posApiHelper.PrintSetAlign(0);
             sendData(preparePrintText(printText), false, AlignEnum.LEFT); // print receipt text
             //Print.Lib_PrnSetAlign(0);
             sendData(getFooter(paramsMap), false, AlignEnum.LEFT); // print footer text
             if (getIntent().hasExtra("hasSignatureBitmap") && getIntent().getBooleanExtra("hasSignatureBitmap", false)) {
                 //  printImage(Config.SignatureBitmap); // print Signature image
//                int ret2 = posApiHelper.PrintBmp(Config.SignatureBitmap);
//                Log.e("PrintBmp", "Lib_PrnStart fail, ret = " + ret2);
//                Log.e("PrintBmp height", "Lib_PrnStart fail, ret = " + Config.SignatureBitmap.getHeight());
//                Log.e("PrintBmp width", "Lib_PrnStart fail, ret = " + Config.SignatureBitmap.getWidth());
                 printer.appendImage(PrinterConfigs.SignatureBitmap, AlignEnum.CENTER);
                 PrinterConfigs.SignatureBitmap = null;
             }
             if (paramsMap.containsKey("qr_auth_code")) {
//                posApiHelper.PrintSetAlign(1);
                 printer.appendImage(QRBitmap, AlignEnum.CENTER);
//                posApiHelper.PrintBmp(QRBitmap); // print QR image
//                posApiHelper.PrintStr("\n\n\n");
                 printer.appendPrnStr("\n\n\n", 20, AlignEnum.CENTER, false);
//                posApiHelper.PrintSetAlign(0);
//                posApiHelper.PrintStr( ");
                 printer.appendPrnStr("", 24, AlignEnum.CENTER, false);
                 printer.appendPrnStr("\n\n\n", 20, AlignEnum.CENTER, false);
//                posApiHelper.PrintStr("\n\n\n");
             }
             printer.appendPrnStr("                                         \n", 20, AlignEnum.CENTER, false);
             printer.appendPrnStr("                                         \n", 20, AlignEnum.CENTER, false);
//            posApiHelper.PrintStr("                                         \n");
//            posApiHelper.PrintStr("                                         \n");
//            posApiHelper.PrintStart();
             printer.startPrint(true, this);
         }

         if (TextUtils.equals(copies, CUSTOMER) || // if BI/MI, print customer copy only
                 TextUtils.equals(copies, BOTH)) { // print customer copy
             if (mNeedsCuttingPause) {
                 this.paramsMap.put("receiptCopies", CUSTOMER); // makes sure only customer copy is printed by clicking button
                 enablePrintButton();
                 return;
             }
             disablePrintButton();

//            Print.Lib_PrnSetAlign(1);

//            posApiHelper.PrintSetAlign(0);
             //printImage(bitmap);// print image image
//            posApiHelper.PrintBmp(bmp1);
             printer.appendImage(bmp1, AlignEnum.CENTER);
             // Print.Lib_PrnSetAlign(0);
//            posApiHelper.PrintSetAlign(0);
             sendData(getHeader(paramsMap, "CUSTOMER COPY"), false, AlignEnum.LEFT);
             //Print.Lib_PrnSetAlign(0);
             sendData(preparePrintText(printText), false, AlignEnum.LEFT);
             if (paramsMap.containsKey("qr_auth_code")) {
                 sendData(getFooter(paramsMap), false, AlignEnum.LEFT);
//                posApiHelper.PrintSetAlign(1);
//                posApiHelper.PrintBmp(QRBitmap);
                 printer.appendImage(QRBitmap, AlignEnum.CENTER);
                 printer.appendPrnStr("\n\n\n", 20, AlignEnum.CENTER, false);
//                posApiHelper.PrintStr("\n\n\n");
//                posApiHelper.PrintSetAlign(0);
                 printer.appendPrnStr("", 20, AlignEnum.CENTER, false);
                 printer.appendPrnStr("                                         \n", 20, AlignEnum.CENTER, false);
                 printer.appendPrnStr("                                         \n", 20, AlignEnum.CENTER, false);
                 printer.appendPrnStr("                                         \n", 20, AlignEnum.CENTER, false);
//                posApiHelper.PrintStr( ");
//                posApiHelper.PrintStr("                                         \n");
//                posApiHelper.PrintStr("                                         \n");
//                posApiHelper.PrintStr("                                         \n");
             } else {
                 sendData(getFooter(paramsMap), true, AlignEnum.LEFT);
//                posApiHelper.PrintStr("\n\n\n");
                 printer.appendPrnStr("\n\n\n", 20, AlignEnum.CENTER, false);
//                posApiHelper.PrintStr( ");
//                posApiHelper.PrintStr("                                         \n");
//                posApiHelper.PrintStr("                                         \n");
//                posApiHelper.PrintStr("                                         \n");
                 printer.appendPrnStr("", 24, AlignEnum.CENTER, false);
                 printer.appendPrnStr("                                         \n", 20, AlignEnum.CENTER, false);
                 printer.appendPrnStr("                                         \n", 20, AlignEnum.CENTER, false);
                 printer.appendPrnStr("                                         \n", 20, AlignEnum.CENTER, false);

             }
//            posApiHelper.PrintStart();
             printer.startPrint(true, this);
         }
         finishedPrinting(true);
    }

    private void loadImage() {
        InputStream imageStream = this.getResources().openRawResource(R.raw.mmf_logo_for_printer);
        ImageView logoPrintServiceActivityIV = (ImageView) findViewById(R.id.logoPrintServiceActivityImageView);
        bitmap = BitmapFactory.decodeStream(imageStream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 1, baos); //bm is the bitmap object
        byteArrayImage = baos.toByteArray();
        logoPrintServiceActivityIV.setImageBitmap(bitmap);
        encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

        if (paramsMap.containsKey("qr_auth_code")) {
            QRBitmap = getQRBitmap(paramsMap.get("qr_auth_code"));
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
            printButton.setBackgroundColor(getResources().getColor(com.deefrent.rnd.common.R.color.kcb_blue));
        });
    }

    /**
     * Enable print button.
     */
    public void enablePrintButton() {
        runOnUiThread(() -> {
            printButton.setEnabled(true);
           // printButton.setBackground(getResources().getDrawable(R.drawable.default_button_bg));
        });
    }

    /**
     * Show toast.
     *
     * @param message the message
     */
    public void showToast(final String message) {
        runOnUiThread(() -> Toast.makeText(AppPrintServiceActivity.this, message, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onBackPressed() {
        showToast("Print receipt to go back");
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
        /*if (paramsMap.get("txnResultMessage") != null) {

            header = new String[]{
                    "***** " + paramsMap.get("txnResultMessage") + " *****",
                    SEPARATOR_LINE,
                    copyOwner,
                    *//*      "\n",*//*
                    Config.AGENT_BANK_STREET,
                    *//*          "\n",*//*
                    "AGENT ID: " + Config.AGENT_CODE,
                    Config.AGENT_NAME,
                    *//*         "\n",*//*
                    "DATE       TIME     TERMINAL ID",
                    paramsMap.get("requestTime") + " " + Config.TERMINAL_NO,//+ "\n",
                    *//*      "\n",*//*
                    "TRANS REF: " + paramsMap.get("reference"),
                    SEPARATOR_LINE
            };

        } else if(paramsMap.get("txnResultMessage") != null) {
            header = new String[]{
                    "***** " + paramsMap.get("txnResultMessage") + " *****",
                    SEPARATOR_LINE,
                    copyOwner,
                    *//*      "\n",*//*
                    Config.AGENT_BANK_STREET,
                    *//*          "\n",*//*
                    "AGENT ID: " + Config.AGENT_CODE,
                    Config.AGENT_NAME,
                    *//*         "\n",*//*
                    "DATE       TIME     TERMINAL ID",
                    paramsMap.get("requestTime") + " " + Config.TERMINAL_NO,//+ "\n",
                    *//*      "\n",*//*
                    "TRANS REF: " + paramsMap.get("reference"),
                    SEPARATOR_LINE
            };
        }
        else */
        Log.d("taggerd",receiptTitle);

        return new String[]{
                    "** " + receiptTitle + " **",
                    SEPARATOR_LINE,
                    copyOwner                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               ,
                    /*      "\n",*/
                    PrinterConfigs.AGENT_BRANCH_STREET,
                    /*          "\n",*/
                    "AGENT ID: " + PrinterConfigs.AGENT_CODE,
                    PrinterConfigs.AGENT_NAME,
                    /*         "\n",*/
                    "DATE       TIME     TERMINAL ID",
                    paramsMap.get("requestTime") + " " + PrinterConfigs.TERMINAL_NO,//+ "\n",
                    /*      "\n",*/
                    "TRANS REF: " + paramsMap.get("reference"),
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
/*        Intent intent = new Intent(context, SignatureCapture.class);
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

    /**
     * This will send data to be printed by the bluetooth printer
     *
     * @param printText       the print text
     * @param closeConnection the close connection
     */
    void sendData(String[] printText, boolean closeConnection, AlignEnum alignEnum) {
        try {
            for (String line : printText) {
                if (line.startsWith(CENTER_PREFIX)) {
//                    posApiHelper.PrintSetAlign(0);
                    line = line.substring(CENTER_PREFIX.length());
                }
                printer.appendPrnStr(line, 24, alignEnum, false);
//                posApiHelper.PrintStr(line);
                // posApiHelper.PrintCtnStart();
            }
            finishedPrinting(closeConnection);
        } catch (NullPointerException e) {
            Log.e(TAG, "Exception Response :-  " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception Response :-  " + e.getMessage());
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
        } else if (deviceType == DeviceType.N5 ){
            printer.setGray(GrayLevelEnum.LEVEL_0);
        }
    }

    @Override
    protected void onResume() {
        disableFunctionLaunch(true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        printButton.callOnClick();
        receiptTitle = printText[1].replace("CENTRE:","");

        String previewText = formatPreviewText(getHeader(paramsMap, "AGENT COPY"), printText, getFooter(paramsMap));
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
        InputStream imageStream = this.getResources().openRawResource(R.raw.mmf_logo_for_printer);
        Bitmap bmp1 = BitmapFactory.decodeStream(imageStream);
        //Bitmap bmp1 = BitmapFactory.decodeResource(getApplication().getResources(), R.drawable.abc3);
        boolean mNeedsCuttingPause = false;

        if (TextUtils.equals(copies, AGENT) ||
                TextUtils.equals(copies, BOTH)) {
            mNeedsCuttingPause = true;
            Print.Lib_PrnSetAlign(1);
            posApiHelper.PrintSetAlign(0);
            int ret = posApiHelper.PrintBmp(bmp1);
            sendCS10Data(getHeader(paramsMap, "AGENT COPY"), false); // print header text
            Print.Lib_PrnSetAlign(0);
            posApiHelper.PrintSetAlign(0);
            sendCS10Data(preparePrintText(printText), false); // print receipt text
            Print.Lib_PrnSetAlign(0);
            sendCS10Data(getFooter(paramsMap), false); // print footer text
            if (getIntent().hasExtra("hasSignatureBitmap") && getIntent().getBooleanExtra("hasSignatureBitmap", false)) {
                printImage(PrinterConfigs.SignatureBitmap); // print Signature image
//                int ret2 = posApiHelper.PrintBmp(Config.SignatureBitmap);
//                Log.e("PrintBmp", "Lib_PrnStart fail, ret = " + ret2);
//                Log.e("PrintBmp height", "Lib_PrnStart fail, ret = " + Config.SignatureBitmap.getHeight());
//                Log.e("PrintBmp width", "Lib_PrnStart fail, ret = " + Config.SignatureBitmap.getWidth());
                PrinterConfigs.SignatureBitmap = null;
            }
            if (paramsMap.containsKey("qr_auth_code")) {
                posApiHelper.PrintSetAlign(1);
//                printer.appendImage(QRBitmap, AlignEnum.CENTER);
                posApiHelper.PrintBmp(QRBitmap); // print QR image
                posApiHelper.PrintStr("\n\n\n");
//                printer.appendPrnStr("\n\n\n", 20, AlignEnum.CENTER, false);
                posApiHelper.PrintSetAlign(0);
                posApiHelper.PrintStr("");
//                printer.appendPrnStr(", 24, AlignEnum.CENTER, false);
//                printer.appendPrnStr("\n\n\n", 20, AlignEnum.CENTER, false);
                posApiHelper.PrintStr("\n\n\n");
            }
//            printer.appendPrnStr("                                         \n", 20, AlignEnum.CENTER, false);
//            printer.appendPrnStr("                                         \n", 20, AlignEnum.CENTER, false);
            posApiHelper.PrintStr("                                         \n");
            posApiHelper.PrintStr("                                         \n");
            posApiHelper.PrintStart();
//            printer.startPrint(true, this);
        }

        if (TextUtils.equals(copies, CUSTOMER) || // if BI/MI, print customer copy only
                TextUtils.equals(copies, BOTH)) { // print customer copy
            if (mNeedsCuttingPause) {
                this.paramsMap.put("receiptCopies", CUSTOMER); // makes sure only customer copy is printed by clicking button
                enablePrintButton();
                return;
            }
            disablePrintButton();

//            Print.Lib_PrnSetAlign(1);

            posApiHelper.PrintSetAlign(0);
            //printImage(bitmap);// print image image
            posApiHelper.PrintBmp(bmp1);
//            printer.appendImage(bmp1, AlignEnum.CENTER);
            Print.Lib_PrnSetAlign(0);
            posApiHelper.PrintSetAlign(0);
            sendCS10Data(getHeader(paramsMap, "CUSTOMER COPY"), false);
            Print.Lib_PrnSetAlign(0);
            sendCS10Data(preparePrintText(printText), false);
            if (paramsMap.containsKey("qr_auth_code")) {
                sendCS10Data(getFooter(paramsMap), false);
                posApiHelper.PrintSetAlign(1);
                posApiHelper.PrintBmp(QRBitmap);
//                printer.appendImage(QRBitmap, AlignEnum.CENTER);
//                printer.appendPrnStr("\n\n\n", 20, AlignEnum.CENTER, false);
                posApiHelper.PrintStr("\n\n\n");
                posApiHelper.PrintSetAlign(0);
//                printer.appendPrnStr(", 20, AlignEnum.CENTER, false);
//                printer.appendPrnStr("                                         \n", 20, AlignEnum.CENTER, false);
//                printer.appendPrnStr("                                         \n", 20, AlignEnum.CENTER, false);
//                printer.appendPrnStr("                                         \n", 20, AlignEnum.CENTER, false);
                posApiHelper.PrintStr("");
                posApiHelper.PrintStr("                                         \n");
                posApiHelper.PrintStr("                                         \n");
                posApiHelper.PrintStr("                                         \n");
            } else {
                sendCS10Data(getFooter(paramsMap), true);
                posApiHelper.PrintStr("\n\n\n");
//                printer.appendPrnStr("\n\n\n", 20, AlignEnum.CENTER, false);
                posApiHelper.PrintStr("");
                posApiHelper.PrintStr("                                         \n");
                posApiHelper.PrintStr("                                         \n");
                posApiHelper.PrintStr("                                         \n");
//                printer.appendPrnStr(", 24, AlignEnum.CENTER, false);
//                printer.appendPrnStr("                                         \n", 20, AlignEnum.CENTER, false);
//                printer.appendPrnStr("                                         \n", 20, AlignEnum.CENTER, false);
//                printer.appendPrnStr("                                         \n", 20, AlignEnum.CENTER, false);

            }
            posApiHelper.PrintStart();
//            printer.startPrint(true, this);
        }
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