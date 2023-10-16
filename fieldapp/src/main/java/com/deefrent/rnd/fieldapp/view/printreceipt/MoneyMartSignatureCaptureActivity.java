package com.deefrent.rnd.fieldapp.view.printreceipt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.deefrent.rnd.fieldapp.R;
import com.deefrent.rnd.jiboostfieldapp.ui.printer.PrinterConfigs;
import com.github.gcacace.signaturepad.views.SignaturePad;

import java.util.HashMap;


public class MoneyMartSignatureCaptureActivity extends AppCompatActivity implements View.OnClickListener {

    private SignaturePad mSignaturePad;
    private boolean isSigned = false;
    private int TRANSACTION_SIGNATURE = 234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature_capture);

        // TODO: REMOVE THIS REPETITIVE BLOCK, MAYBE TAKE IT TO SUPERCLASS OR FIND A BETTER IMPLEMENTATION
        Toolbar toolbar = (Toolbar) findViewById(R.id.signature_pad_toolbar);
        setTitle("");
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbarTitleTextView);
//        mTitle.setText(R.string.title_signature_capture);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(view -> finish());


        mSignaturePad = (SignaturePad) findViewById(R.id.signature_pad);
        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {

            @Override
            public void onStartSigning() {
                //Event triggered when the pad is touched
//                isSigned = false;
            }

            @Override
            public void onSigned() {
                //Event triggered when the pad is signed
                isSigned = true;
            }

            @Override
            public void onClear() {
                //Event triggered when the pad is cleared
                isSigned = false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        if (!isSigned) {
            Toast.makeText(this, "No signature detected.", Toast.LENGTH_SHORT).show();
//            return;
        }
        if (this.getIntent().hasExtra("params") &&
                this.getIntent().hasExtra("printText")) {
            PrinterConfigs.SignatureBitmap = mSignaturePad.getSignatureBitmap();
            PrinterConfigs.SignatureBitmap = scaleDownBitmap(PrinterConfigs.SignatureBitmap, 100, this);
            String[] printText = this.getIntent().getExtras().getStringArray("printText");
            HashMap<String, String> map = (HashMap<String, String>) this.getIntent().getExtras().getSerializable("params");
            Intent intent = new Intent(this, MoneyMartPrintServiceActivity.class);
            intent.putExtra("finishActivityOnPrint", true);
            intent.putExtra("printText", printText);
            intent.putExtra("hasSignatureBitmap", true);
            intent.putExtra("params", map);
            startActivityForResult(intent, TRANSACTION_SIGNATURE);
        }
    }

    public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {
        photo = Bitmap.createScaledBitmap(photo, 384, 209, true);
        Bitmap bmpGrayscale = Bitmap.createBitmap(384, 209, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(photo, 0, 0, paint);
        return photo;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TRANSACTION_SIGNATURE) {
            finish();
        }
    }
}
