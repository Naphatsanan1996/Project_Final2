package com.example.officesyndrome;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;


import android.graphics.Camera;
import android.media.FaceDetector;
import android.os.Build;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OpenCamera extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "OpenCV";
    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);  //rgb color. green.
    private static Scalar HAND_RECT_COLOR = new Scalar(255, 0, 0, 255);  //rgb color. green.
    private static Scalar ORANGE = new Scalar(255, 165, 0);
    private Mat mRgba;   //Mat is OpenCv class
    private Mat mGray;
    private Mat mFGMask, hierarchy;
    private JavaCameraView javaCameraView;
    private ImageView imageView;
    public int counter;

    private BackgroundSubtractorMOG2 mBGSub;

    private double lRate = 0.5;

    private Boolean Findface = false;
    private File mCascadeFile;
    private File mCascadeFile2;
    private File mCascadeFile3;

    private CascadeClassifier mJavaDetector;
    private CascadeClassifier mJavaDetector2;
    private CascadeClassifier mJavaDetector3;

    private String[] mDetectorName;
    private float mRelativeFaceSize = 0.2f;
    private int mAbsoluteFaceSize = 0;

    private String[] mDetectorNameHand;
    private float mRelativeHandSize = 0.2f;
    private int mAbsoluteHandSize = 0;

    private String[] mDetectorNameBody;
    private float mRelativeBodySize = 0.2f;
    private int mAbsoluteBodySize = 0;

    private CameraBridgeViewBase mOpenCvCameraView;
    double xCenter = -1;
    double yCenter = -1;

    double xCenter2 = -1;
    double yCenter2 = -1;

    double xCenter3 = -1;
    double yCenter3 = -1;

    private Button BtnStart;

    private CountDownTimer timer;
    private TextView textView;


    private Boolean findface = false;
    ArrayList<Rect> arr = new ArrayList<Rect>();
    List<MatOfPoint> contours = new ArrayList<MatOfPoint>(); //A list to store all the contours
    Rect rect = null;
    double maxArea = 0;
    Point p1, p2, p3, p4;
    Mat oldFrame;

    VideoCapture cap;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {        //Callback method, called after OpenCV library initialization.
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");

                    try {
//                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface); // load cascade file from application resources
//                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
//                        mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
//                        FileOutputStream os = new FileOutputStream(mCascadeFile);
//                        byte[] buffer = new byte[4096];
//
//                        int bytesRead;  //to hold number of bytes read for each read(byte[]) call
//
//                        while ((bytesRead = is.read(buffer)) != -1) {
//                            os.write(buffer, 0, bytesRead);  //passing along the buffer byte array as well as how many bytes were read into the array as parameters.
//                        }
//                        is.close();
//                        os.close();
//
                        InputStream ish = getResources().openRawResource(R.raw.handcascade);
                        File cascadeDir2 = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile2 = new File(cascadeDir2, "handcascade.xml");
                        FileOutputStream os2 = new FileOutputStream(mCascadeFile2);
                        byte[] buffer2 = new byte[4096];
                        int bytesRead2;

                        while ((bytesRead2 = ish.read(buffer2)) != -1) {
                            os2.write(buffer2, 0, bytesRead2);
                        }
                        ish.close();
                        os2.close();


                        InputStream isb = getResources().openRawResource(R.raw.headandshoulder);
                        File cascadeFileDir3 = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile3 = new File(cascadeFileDir3, "headandshoulder.xml");
                        FileOutputStream os3 = new FileOutputStream(mCascadeFile3);
                        byte[] buffer3 = new byte[4096];
                        int bytesRead3;

                        while ((bytesRead3 = isb.read(buffer3)) != -1) {
                            os3.write(buffer3, 0, bytesRead3);
                        }
                        isb.close();
                        os3.close();

//                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath()); //class for object detection
//                        if (mJavaDetector.empty()) {
//                            Log.e(TAG, "Failed to load cascade classifier");
//                            mJavaDetector = null;
//                        } else
//                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());
//                        cascadeDir.delete();
//
                        mJavaDetector2 = new CascadeClassifier(mCascadeFile2.getAbsolutePath());
                        if (mJavaDetector2.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            mJavaDetector2 = null;
                        } else {
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile2.getAbsolutePath());
                        }
                        cascadeDir2.delete();

                        mJavaDetector3 = new CascadeClassifier(mCascadeFile3.getAbsolutePath());
                        if (mJavaDetector3.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            mJavaDetector3 = null;
                        } else {
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile3.getAbsolutePath());
                        }
                        cascadeFileDir3.delete();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }
                    mOpenCvCameraView.enableFpsMeter();
                    mOpenCvCameraView.setCameraIndex(1);
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_open_camera);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.CameraView);
        mOpenCvCameraView.setCvCameraViewListener(this);
        textView = (TextView) findViewById(R.id.text_GoOutFrame);
        imageView = (ImageView) findViewById(R.id.gindline);


//        imageView.setImageResource(R.drawable.t2);
        BtnStart = findViewById(R.id.btn_start);
//
//
//        cap = new VideoCapture();
//
//
        BtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnStart.setVisibility(View.GONE);
                new CountDownTimer(5000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        textView.setText(" " + millisUntilFinished / 1000);
                    }

                    public void onFinish() {
                        textView.setText("done!");
                        imageView.setImageResource(R.drawable.t1);
                        textView.setText("");
                    }
                }.start();

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();

    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
        mFGMask = new Mat();
        hierarchy = new Mat();

//        mBGSub = new BackgroundSubtractorMOG2(3);
//        contours = new ArrayList<MatOfPoint>();

    }

    @Override
    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
        hierarchy.release();

    }


    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//        contours.clear();
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();
//        mFGMask = Mat.zeros(1, 1, Imgproc.THRESH_BINARY);

//        Core.flip(mRgba, mRgba, 1);
//
//        doBackgroundRemovalAbsDiff(mRgba);
//
//
////
//        Imgproc.findContours(mGray,contours,mFGMask,Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);
//
//        Imgproc.drawContours(mRgba,contours,-1,new Scalar(255,255,0));

//
////Face
//        if (mAbsoluteFaceSize == 0) {
//            int height = mGray.rows();
//            if (Math.round(height * mRelativeFaceSize) > 0) {
//                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
//            }
//        }
//        MatOfRect faces = new MatOfRect();
//        if (mJavaDetector != null) {
//            mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
//                    new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
//            findface = true;
//        } else {
//            Log.e(TAG, "Detector is null!");
//            findface = false;
//        }
//        Rect[] facesArray = faces.toArray();
//        for (int i = 0; i < facesArray.length; i++) {
////            กรอบ4เหลี่ยม
//            Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
//            xCenter = (facesArray[i].x + facesArray[i].width + facesArray[i].x) / 2;
//            yCenter = (facesArray[i].y + facesArray[i].y + facesArray[i].height) / 2;
//            Point center = new Point(xCenter, yCenter);
//            Imgproc.circle(mRgba, center, 20, new Scalar(255, 0, 0, 255), 5);
//            Imgproc.putText(mRgba, "Face", new Point(facesArray[i].tl().x, facesArray[i].br().y), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255, 255), 3);
//
//            Log.e(TAG,"FacecenterPoint :"+ center);
//
//
//        }


//

//
//            Imgproc.rectangle(mRgba, new Point(400, 500), new Point(500, 400), FACE_RECT_COLOR, 3);
//            Imgproc.putText(mRgba, "shoulderR", new Point(400, 500), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255, 255), 3);
//
//

//            Imgproc.putText(mRgba, "elbowL", new Point(1200, 600), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255, 255), 3);
//
//            Imgproc.rectangle(mRgba, new Point(1000, 500), new Point(1100, 400), FACE_RECT_COLOR, 3);
//            Imgproc.putText(mRgba, "shoulderL", new Point(1000, 500), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255, 255), 3);

//
        if (mAbsoluteHandSize == 0) {
            int height2 = mGray.rows();
            if (Math.round(height2 * mRelativeHandSize) > 0) {
                mAbsoluteHandSize = Math.round(height2 * mRelativeHandSize);
            }
        }
        MatOfRect hands = new MatOfRect();
        if (mJavaDetector2 != null) {
            mJavaDetector2.detectMultiScale(mGray, hands, 1.1, 2, 2, new Size(mAbsoluteHandSize, mAbsoluteHandSize), new Size());
        } else {
            Log.e(TAG, "Detector is null!");
        }
        Rect[] handsArray = hands.toArray();
        for (int i = 0; i < handsArray.length; i++) {
            xCenter2 = (handsArray[i].x + handsArray[i].width + handsArray[i].x) / 2;
            yCenter2 = (handsArray[i].y + handsArray[i].y + handsArray[i].height) / 2;
            Point center2 = new Point(xCenter2, yCenter2);
//            Imgproc.rectangle(mRgba, handsArray[i].tl(), handsArray[i].br(), HAND_RECT_COLOR, 3);
            Imgproc.circle(mRgba, center2, 20, FACE_RECT_COLOR, 10);
            Log.i(TAG, "Center" + center2);


//            if (xCenter2 < xCenter3) {
//                Imgproc.putText(mRgba, "Right", new Point(handsArray[i].tl().x, handsArray[i].br().y), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255, 255), 3);
//            } else {
//                Imgproc.putText(mRgba, "Left", new Point(handsArray[i].tl().x, handsArray[i].br().y), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255, 255), 3);
//            }

        }

////
////
//Body

        if (mAbsoluteBodySize == 0) {
            int height3 = mGray.rows();
            if (Math.round(height3 * mRelativeBodySize) > 0) {
                mAbsoluteBodySize = Math.round(height3 * mRelativeBodySize);
            }
        }
        MatOfRect body = new MatOfRect();

        if (mJavaDetector3 != null) {
            mJavaDetector3.detectMultiScale(mGray, body, 1.1, 2, 2, new Size(mAbsoluteBodySize, mAbsoluteBodySize), new Size());
            findface = true;
        } else {
            findface = false;
            Log.e(TAG, "Detect is null");
        }
        Rect[] bodyArray = body.toArray();
        for (int i = 0; i < bodyArray.length; i++) {

            xCenter3 = (bodyArray[i].x + bodyArray[i].width + bodyArray[i].x) / 2;
            yCenter3 = (bodyArray[i].y + bodyArray[i].height + bodyArray[i].y) / 2;
            Point center3 = new Point(xCenter3, yCenter3);
//                Imgproc.rectangle(mRgba, bodyArray[i].tl(), bodyArray[i].br(), ORANGE, 10);
            Imgproc.circle(mRgba, center3, 50, ORANGE, 10);
            //            Imgproc.circle(mRgba, bodyArray[i].br(), 50, HAND_RECT_COLOR, 10);
//            Log.i(TAG, "CenterFace " + center3);

            p1 = new Point(bodyArray[i].tl().x, bodyArray[i].tl().y);  //top left
            p2 = new Point(bodyArray[i].tl().x + bodyArray[i].width, bodyArray[i].tl().y);  //Top Right
            p3 = new Point(bodyArray[i].tl().x, bodyArray[i].tl().y + bodyArray[i].height); // Bottom left
            p4 = new Point(bodyArray[i].tl().x + bodyArray[i].width, bodyArray[i].tl().y + bodyArray[i].height); //Bottom right


            checkpointheadandshoulder(center3);


//                Imgproc.findContours(mGray, contours, mFGMask, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
//                Imgproc.drawContours(mRgba, contours, -1, new Scalar(255, 130, 179), 2);


            //            Imgproc.line(mRgba, center3, p3, FACE_RECT_COLOR, 10);
//                Imgproc.line(mRgba, p3, p4, FACE_RECT_COLOR, 10);
            //


            //            Log.e(TAG, "FacecenterPoint :" + center3);
            //
            ////            if (findface == true) {
            //
            //                Imgproc.rectangle(mRgba, new Point(200, 100), new Point(300, 200), FACE_RECT_COLOR, 3);
            //                Imgproc.putText(mRgba, "handR", new Point(200, 100), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255, 255), 3);
            //
            //                Imgproc.rectangle(mRgba, new Point(1200, 100), new Point(1300, 200), FACE_RECT_COLOR, 3);
            //                Imgproc.putText(mRgba, "handL", new Point(1200, 100), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255, 255), 3);
            //
            //
            //            } else {
            //
            //                Imgproc.rectangle(mRgba, new Point(200, 600), new Point(300, 500), FACE_RECT_COLOR, 3);
            //                Imgproc.putText(mRgba, "elbowR", new Point(200, 600), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255, 255), 3);
            //
            //                Imgproc.rectangle(mRgba, new Point(1200, 600), new Point(1300, 500), FACE_RECT_COLOR, 3);
            //                Imgproc.putText(mRgba, "elbowL", new Point(1200, 600), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255, 255), 3);
            //
            //            }

        }


        return mRgba;
    }


    public Point checkpointheadandshoulder(Point centerfsce) {

        if (xCenter3 >= 850 && xCenter3 <= 890) {
//       Imgproc.rectangle(mRgba, new Point(350, 450), new Point(750, 850), FACE_RECT_COLOR, 3);
            Point centerh = new Point(870, 310);
            Point centera = new Point(870, 500);
            Imgproc.circle(mRgba, centerh, 50, FACE_RECT_COLOR, 10);
            Imgproc.circle(mRgba, centera, 50, FACE_RECT_COLOR, 10);

//            textView.setText("10");

            new CountDownTimer(10000, 1000) {

                public void onTick(long millisUntilFinished) {
                    textView.setText(" " + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    textView.setText(" ");
                    imageView.setImageResource(R.drawable.t2);
                }
            }.start();
        }
        if (xCenter2 >= 300 && xCenter2 <= 550) {
            Point centerha = new Point(400, 400);
            Imgproc.circle(mRgba, centerha, 50, FACE_RECT_COLOR, 10);

        }

        return centerfsce;
    }

    public Mat doBackgroundRemovalAbsDiff(Mat currFrame) {

        if (oldFrame == null) {
            oldFrame = mRgba;
        }
        Core.absdiff(mRgba, oldFrame, mFGMask);
        Imgproc.cvtColor(mFGMask, mGray, Imgproc.COLOR_RGB2GRAY);

        int thresh_type = Imgproc.THRESH_BINARY_INV;
        thresh_type = Imgproc.THRESH_BINARY;

        Imgproc.threshold(mGray, mGray, 10, 255, thresh_type);
        mGray.copyTo(mFGMask, mGray);

        oldFrame = mRgba;
//
        return mFGMask;
    }


}
