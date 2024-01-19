package com.deefrent.rnd.scannerlib;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.darwin.viola.still.FaceDetectionListener;
import com.darwin.viola.still.Viola;
import com.darwin.viola.still.model.FaceDetectionError;
import com.darwin.viola.still.model.FaceOptions;
import com.darwin.viola.still.model.Result;

import com.deefrent.rnd.scannerlib.models.CardDetails;
import com.deefrent.rnd.scannerlib.models.IdScanResult;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCapture;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureConfig;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureFactory;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureResult;
import com.huawei.hms.mlplugin.card.gcr.MLGcrCaptureUIConfig;
import com.huawei.hms.mlsdk.common.MLException;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.faceverify.MLFaceTemplateResult;
import com.huawei.hms.mlsdk.faceverify.MLFaceVerificationAnalyzer;
import com.huawei.hms.mlsdk.faceverify.MLFaceVerificationAnalyzerFactory;
import com.huawei.hms.mlsdk.faceverify.MLFaceVerificationAnalyzerSetting;
import com.huawei.hms.mlsdk.faceverify.MLFaceVerificationResult;
import com.huawei.hms.mlsdk.livenessdetection.MLLivenessCapture;
import com.huawei.hms.mlsdk.livenessdetection.MLLivenessCaptureResult;
import com.huawei.hms.mlsdk.text.MLText;

import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.internal.observers.BlockingBaseObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ScannerInterfaceImp implements ScannerInterface {

    private final String TAG = ScannerInterfaceImp.class.getSimpleName();
    private IdScanResult scanResult;
    private CardDetails cardDetails = null;
    private CardResults mCardDetailsCallback;
    private LivenessResults mLivenessCallback;
    private VerificationScore mFaceMatchCallback;

    private Bitmap croppedImage;

    private static final int FACEMAX = 3;
    MLFaceVerificationAnalyzer analyzer;

    /**
     * Scans the details and photo from the specified <code>cardType</code>
     *
     * @param context  The android context, used for accessing the device camera
     * @param cardType Type type of card being scanned
     * @return The scanned results form the captured image
     */
    @Nullable
    @Override
    public IdScanResult scanIdCard(@NonNull Context context, @NonNull CardType cardType, @NonNull ScannerInterfaceImp.CardResults cardDetailsCallback) {
        //the app's context
        mCardDetailsCallback = cardDetailsCallback;

        //Check for camera permission otherwise initialise application
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            mCardDetailsCallback.onCardFailure("Camera permission required to access functionality");
            return null;
        }

        switch (cardType) {
            case IDCARDUG:
                startTakePhotoActivity(context, null, new MLGcrCapture.Callback() {
                    @Override
                    public int onResult(MLGcrCaptureResult result, Object o) {
                        if (result != null) {// Check whether a result is returned.

                            scanResult = scanUgandanID(result);

                            if (scanResult == null) {// Check whether the processing result meets the requirements. Implement the isMatch method based on your use case.
                                return MLGcrCaptureResult.CAPTURE_CONTINUE;// The processing result does not meet the requirements, and the recognition continues.
                            }// Process the results that meet the requirements.
                        }
                        return MLGcrCaptureResult.CAPTURE_STOP;// The processing ends, and the recognition exits.
                    }

                    @Override
                    public void onCanceled() {
                        mCardDetailsCallback.onCardFailure("onCanceled called");
                    }

                    @Override
                    public void onFailure(int i, Bitmap bitmap) {
                        mCardDetailsCallback.onCardFailure("onFailure called");
                    }

                    @Override
                    public void onDenied() {
                        mCardDetailsCallback.onCardFailure("onDenied called");
                    }
                });
                break;
            case HUDUMACARD:
                break;
            case IDCARD:
                startTakePhotoActivity(context, null, new MLGcrCapture.Callback() {
                    @Override
                    public int onResult(MLGcrCaptureResult result, Object o) {
                        if (result != null) {// Check whether a result is returned.
                            scanResult = scanKenyanID(result);
                            if (scanResult == null) {// Check whether the processing result meets the requirements. Implement the isMatch method based on your use case.
                                return MLGcrCaptureResult.CAPTURE_CONTINUE;// The processing result does not meet the requirements, and the recognition continues.
                            }// Process the results that meet the requirements.
                        }
                        return MLGcrCaptureResult.CAPTURE_STOP;// The processing ends, and the recognition exits.
                    }

                    @Override
                    public void onCanceled() {
                        mCardDetailsCallback.onCardFailure("onCanceled called");
                    }

                    @Override
                    public void onFailure(int i, Bitmap bitmap) {
                        mCardDetailsCallback.onCardFailure("onFailure called");
                    }

                    @Override
                    public void onDenied() {
                        mCardDetailsCallback.onCardFailure("onDenied called");
                    }
                });
                break;
            default:
                mCardDetailsCallback.onCardFailure("Card type not supported");
                break;
        }
        return scanResult;
    }

    private IdScanResult scanUgandanID(MLGcrCaptureResult result) {
        MLText resultText = result.text; //overall result after reading card

        if (cardDetails == null)
            cardDetails = new CardDetails();

        cardDetails.setFirstName(resultText.getStringValue());

        scanResult = new IdScanResult();
        scanResult.setDetails(cardDetails);


        Bitmap b = result.cardBitmap; //extract card
        Bitmap croppedBitmap = operateOnFrontBitmap(b); //extract face

        scanResult.setFace(croppedBitmap);
        scanResult.setCard(b);

        return scanResult;
    }


    /**
     * Specifically scans a Kenyan ID for values
     *
     * @param result the data extracted from the card
     * @return true if the Kenyan ID has been scanned successfully and closes the engine
     */
    private IdScanResult scanKenyanID(MLGcrCaptureResult result) {
        MLText resultText = result.text;
        Bitmap b = result.cardBitmap;

        List<MLText.Block> textBlocks = resultText.getBlocks();

        int blocksSize = textBlocks.size();
        if (blocksSize < 3 || blocksSize > 5) {
            mCardDetailsCallback.onCardFailure("Kindly rescan card!!!");
            return null;
        }

        List<MLText.TextLine> firstTextBlock = textBlocks.get(0).getContents(); //first textBlock to extract text lines
        if (firstTextBlock.size() == 8) { // back side of ID card
            if (cardDetails == null)
                cardDetails = new CardDetails();

            cardDetails.setCounty(firstTextBlock.get(1).getStringValue());
            cardDetails.setDistrict(firstTextBlock.get(3).getStringValue());
            cardDetails.setDivision(firstTextBlock.get(5).getStringValue());
            cardDetails.setLocation(firstTextBlock.get(7).getStringValue());
        } else if (blocksSize == 4) {
            if (cardDetails == null)
                cardDetails = new CardDetails();

            List<MLText.Word> firstTextLine = firstTextBlock.get(0).getContents();

            if (firstTextLine.size() == 3) {
                cardDetails.setNationality(firstTextLine.get(2).getStringValue());
            }

            if (firstTextBlock.size() == 4) {
                String[] name = firstTextBlock.get(3).getStringValue().split(" ");

                switch (name.length) {
                    case 3:
                        cardDetails.setFirstName(name[0].replace(" ",""));
                        cardDetails.setMiddleName(name[1].replace(" ",""));
                        cardDetails.setSurname(name[2].replace(" ",""));
                        break;
                    case 2:
                        cardDetails.setFirstName(name[0].replace(" ",""));
                        cardDetails.setSurname(name[1].replace(" ",""));
                        break;
                    case 4:
                        cardDetails.setFirstName(name[0].replace(" ",""));
                        cardDetails.setMiddleName(name[1].replace(" ","") + " " + name[2].replace(" ",""));
                        cardDetails.setSurname(name[3].replace(" ",""));
                        break;
                    default:
                        cardDetails.setFirstName(firstTextBlock.get(3).getStringValue());
                        break;
                }
            }

            if (cardDetails.getFirstName().equalsIgnoreCase("Unknown")){
                mCardDetailsCallback.onCardFailure("Failed to read name! Kindly re-scan ID");
                return null;
            }

            List<MLText.TextLine> secondTextBlock = textBlocks.get(1).getContents(); //second textBlock to extract text lines
            if (secondTextBlock.size() >= 2) {
                List<MLText.Word> secondTextLine = secondTextBlock.get(1).getContents();
                switch (secondTextLine.size()) {
                    case 1:
                        cardDetails.setCardNumber(secondTextLine.get(0).getStringValue());
                        break;
                    case 2:
                        cardDetails.setCardNumber(secondTextLine.get(1).getStringValue());
                        break;
                    case 3:
                        cardDetails.setCardNumber(secondTextLine.get(2).getStringValue());
                        break;
                }
                String cardNumber = cardDetails.getCardNumber().replaceAll("\\D+", ""); //cleans string from any letters
                cardDetails.setCardNumber(cardNumber);
            }

            List<MLText.TextLine> thirdTextBlock = textBlocks.get(2).getContents(); //third textBlock to extract text lines
            if (thirdTextBlock.size() < 9) {
                mCardDetailsCallback.onCardFailure("Kindly re-scan ID");
                return null;
            }

            cardDetails.setDob(thirdTextBlock.get(1).getStringValue());
            cardDetails.setGender(thirdTextBlock.get(3).getStringValue());
            cardDetails.setPlaceOfBirth(thirdTextBlock.get(5).getStringValue());
            cardDetails.setPlaceOfIssue(thirdTextBlock.get(7).getStringValue());
            cardDetails.setDateOfIssue(thirdTextBlock.get(9).getStringValue());

            Bitmap croppedBitmap = operateOnFrontBitmap(b);

            scanResult = new IdScanResult();
            scanResult.setDetails(cardDetails);
            scanResult.setFace(croppedBitmap);
            scanResult.setCard(b);

            mCardDetailsCallback.onCardResult(scanResult);
        }
        return scanResult;
    }

    /**
     * Method takes picture from card and extracts the face
     *
     * @param b the image found in the card
     * @return the bitmap with cropped face
     */
    private Bitmap operateOnFrontBitmap(Bitmap b) {
        Viola viola = new Viola(new FaceDetectionListener() {
            @Override
            public void onFaceDetected(Result result) {
                if (result.getFacePortraits().size() > 0) {
                    croppedImage = result.getFacePortraits().get(0).getFace();
                    scanResult.setFace(croppedImage);
                    mCardDetailsCallback.onCardResult(scanResult);
                }
            }

            @Override
            public void onFaceDetectionFailed(FaceDetectionError faceDetectionError, String s) {
                Log.d(TAG, "FaceDetectionError error " + s);
            }
        });

        FaceOptions faceOptions = new FaceOptions.Builder()
                .enableProminentFaceDetection()
                .enableDebug()
                .build();

        viola.detectFace(b, faceOptions);

        return croppedImage;
    }

    /**
     * recognizing general cards in picture taking.
     *
     * @param mContext to allow camera resource
     * @param object   used in the library
     * @param callback for returning results
     */
    private void startTakePhotoActivity(Context mContext, Object object, MLGcrCapture.Callback callback) {
        // Create a general card recognition configurator that can be used to configure languages recognized.
        MLGcrCaptureConfig cardConfig = new MLGcrCaptureConfig.Factory().
                setLanguage("en").
                create();
        // Create a general card recognition UI configurator.
        MLGcrCaptureUIConfig uiConfig = new MLGcrCaptureUIConfig.Factory()
                .setScanBoxCornerColor(Color.WHITE) // Set the color of the scanning box.
                .setScanBoxAspectRatio(5f)

                // Set the recognition screen display orientation.
                // MLGcrCaptureUIConfig.ORIENTATION_AUTO: adaptive mode. The display orientation is determined by the physical sensor.
                // MLGcrCaptureUIConfig.ORIENTATION_LANDSCAPE: landscape mode.
                // MLGcrCaptureUIConfig.ORIENTATION_PORTRAIT: portrait mode.
                .setOrientation(MLGcrCaptureUIConfig.ORIENTATION_AUTO)
                .create();
        // Method 1: Create a general card recognition processor based on the customized card recognition UI configurator.
        MLGcrCapture ocrManager = MLGcrCaptureFactory.getInstance().getGcrCapture(cardConfig, uiConfig);
        // Method 2: Use the default UI to create a general card recognition processor.
//        MLGcrCapture ocrManager = MLGcrCaptureFactory.getInstance().getGcrCapture(cardConfig);
        // Bind the general card recognition processor to the processing result callback function.
        ocrManager.capturePhoto(mContext, object, callback);
    }

    /**
     * Probes and check if the image/face is live face
     *
     * @param activity The android Activity, used for accessing the device camera
     */
    @Nullable
    @Override
    public void checkLiveliness(@NonNull Activity activity, @NonNull ScannerInterfaceImp.LivenessResults livenessResultsCallback) {
        mLivenessCallback = livenessResultsCallback;
        //Check for camera permission otherwise initialise application
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            mLivenessCallback.checkFailed("Camera permission required to access functionality");
        }

        MLLivenessCapture capture = MLLivenessCapture.getInstance();
        capture.startDetect(activity, new MLLivenessCapture.Callback() {
            @Override
            public void onSuccess(MLLivenessCaptureResult result) {
                mLivenessCallback.isLive(result.isLive());
                // Processing logic when the detection is successful. The detection result indicates whether the face is of a real person.
                if (result.isLive()) {
                    Bitmap b = result.getBitmap();
                    Viola viola = new Viola(new FaceDetectionListener() {
                        @Override
                        public void onFaceDetected(Result result) {
                            if (result.getFacePortraits().size() > 0) {
                                croppedImage = result.getFacePortraits().get(0).getFace();
                                mLivenessCallback.faceImage(croppedImage);
                            }
                        }

                        @Override
                        public void onFaceDetectionFailed(FaceDetectionError faceDetectionError, String s) {

                        }
                    });
                    FaceOptions faceOptions = new FaceOptions.Builder()
                            .enableProminentFaceDetection()
                            .enableDebug()
                            .build();
                    viola.detectFace(b, faceOptions);
                }
            }

            @Override
            public void onFailure(int i) {
                mLivenessCallback.checkFailed("Liveness check failed");
            }
        });
    }

    @Override
    public void matchFaces(@NonNull Bitmap original, @NonNull Bitmap probe, @NonNull ScannerInterfaceImp.VerificationScore verificationScoreCallback) {
        initAnalyzer();
        loadProbeBitmap(probe); //loading the image to be verified
        mFaceMatchCallback = verificationScoreCallback;

        final long startTime = System.currentTimeMillis();
        try {
            Task<List<MLFaceVerificationResult>> task = analyzer.asyncAnalyseFrame(MLFrame.fromBitmap(original));
            final StringBuilder sb = new StringBuilder();
            sb.append("##getFaceSimilarity|");
            task.addOnSuccessListener(new OnSuccessListener<List<MLFaceVerificationResult>>() {
                @Override
                public void onSuccess(List<MLFaceVerificationResult> mlCompareList) {
                    long endTime = System.currentTimeMillis();
                    sb.append("COST[");
                    sb.append(endTime - startTime);
                    sb.append("]|Success!");
                    for (MLFaceVerificationResult template : mlCompareList) {
                        Rect location = template.getFaceInfo().getFaceRect();

                        int id = template.getTemplateId();
                        float similarity = template.getSimilarity();
                        sb.append("|Face[");
                        sb.append(location);
                        sb.append("]Id[");
                        sb.append(id);
                        sb.append("]Similarity[");
                        sb.append(similarity);
                        sb.append("]");
                        mFaceMatchCallback.matchScore(similarity);
                    }
                    sb.append("\n");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    long endTime = System.currentTimeMillis();
                    sb.append("COST[");
                    sb.append(endTime - startTime);
                    sb.append("]|Failure!");
                    if (e instanceof MLException) {
                        MLException mlException = (MLException) e;
                        // Obtain the error code. Developers can process the error code and provide differentiated page prompts based on the error code.
                        int errorCode = mlException.getErrCode();
                        // Obtain the error information. Developers can quickly locate the fault based on the error code.
                        String errorMessage = mlException.getMessage();
                        sb.append("|ErrorCode[");
                        sb.append(errorCode);
                        sb.append("]Msg[");
                        sb.append(errorMessage);
                        mFaceMatchCallback.matchFailed(errorMessage);
                    } else {
                        sb.append("|Error[");
                        sb.append(e.getMessage());
                        mFaceMatchCallback.matchFailed(e.getMessage());
                    }
                    sb.append("]");
                    sb.append("\n");
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "Error verifying bitmaps " + e.getMessage());
        }
    }

    /**
     * Loads the image to be verified against
     *
     * @param probe
     */
    private void loadProbeBitmap(Bitmap probe) {
        long startTime = System.currentTimeMillis();
        try {
            List<MLFaceTemplateResult> results = analyzer.setTemplateFace(MLFrame.fromBitmap(probe));
            long endTime = System.currentTimeMillis();
            StringBuilder sb = new StringBuilder();
            sb.append("##setTemplateFace|COST[");
            sb.append(endTime - startTime);
            sb.append("]");
            if (results.isEmpty()) {
                sb.append("Failure!");
            } else {
                sb.append("Success!");
            }
            for (MLFaceTemplateResult template : results) {
                int id = template.getTemplateId();
                Rect location = template.getFaceInfo().getFaceRect();
                sb.append("|Face[");
                sb.append(location);
                sb.append("]ID[");
                sb.append(id);
                sb.append("]");
            }
            sb.append("\n");

        } catch (Exception e) {
            Log.d(TAG, "Error loading probe bitmap " + e.getMessage());
        }
    }

    /**
     * Initialises the image analyzer
     */
    private void initAnalyzer() {
        MLFaceVerificationAnalyzerSetting.Factory factory = new MLFaceVerificationAnalyzerSetting.Factory().setMaxFaceDetected(FACEMAX);
        MLFaceVerificationAnalyzerSetting setting = factory.create();
        analyzer = MLFaceVerificationAnalyzerFactory
                .getInstance()
                .getFaceVerificationAnalyzer(setting);
    }

    @Nullable
    @Override
    public String scanDocument(@NonNull Context context, @NonNull Bitmap photo) {
        return null;
    }

    public interface CardResults {

        void onCardResult(IdScanResult scanResult);

        void onCardFailure(String error);

    }

    public interface LivenessResults {

        void isLive(boolean result);

        void checkFailed(String error);

        void faceImage(Bitmap face);

    }

    public interface VerificationScore {

        void matchScore(float result);

        void matchFailed(String error);

    }
}
