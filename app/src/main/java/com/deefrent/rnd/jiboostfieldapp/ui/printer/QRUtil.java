package com.deefrent.rnd.jiboostfieldapp.ui.printer;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * Created by Kelvin Chot Kinyunye on 4/9/2018.
 * Email: kinyunye.kelvin@ekenya.co.ke
 * Phone: (+254) 71-204-4508
 * Company: Eclectics International Ltd
 */
public class QRUtil {

    /**
     * Generate qr bitmap.
     *
     * @param content the content
     * @return the bitmap
     */
    public static Bitmap generateQR(String content) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 300, 300);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bmp;
        } catch (WriterException e) {
            //Trace();
        }
        return null;
    }
}
