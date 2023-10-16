package com.deefrent.rnd.scannerlib;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.deefrent.rnd.scannerlib.models.IdScanResult;

/**
 *
 */
public interface ScannerInterface {
    /**
     * The supported card types when scanning ID Cards
     */
    enum CardType{
        IDCARD,HUDUMACARD,IDCARDUG
    }

    /**
     * Scans the details and photo from the specified <code>cardType</code>
     * @param context The android context, used for accessing the device camera
     * @param cardType Type type of card being scanned
     * @return The scanned results form the captured image
     */
    @Nullable
    IdScanResult scanIdCard(@NonNull Context context,@NonNull CardType cardType, @NonNull ScannerInterfaceImp.CardResults cardDetailsCallback);

    /**
     * Probes and check if the image/face is live face
     * @param activity The android activity, used for accessing the device camera
     * @param livenessResultsCallback Callback to return results
     * @return The score in the rage of 0 to 1
     */
    @Nullable
    void checkLiveliness(@NonNull Activity activity, @NonNull ScannerInterfaceImp.LivenessResults livenessResultsCallback);

    /**
     * Compares the <code>original</code> photo with the <code>probe</code>
     * sample and returns a score in the range of 0 to 1
     * @param original The original photo/image
     * @param probe The sample to me matched
     */
    void matchFaces(@NonNull Bitmap original,@NonNull Bitmap probe, @NonNull ScannerInterfaceImp.VerificationScore verificationScoreCallback);

    /**
     * Scan all test in the given photo
     * @param context The android context
     * @param photo The captured photo
     * @return The scanned text
     */
    @Nullable
    String scanDocument(@NonNull Context context,@NonNull Bitmap photo);
}
