package com.example.officesyndrome;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;


import android.os.Build;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import org.opencv.core.Core;
import org.opencv.core.Mat;
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

public class OpenCamera extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "OpenCV";
    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);  //rgb color. green.
    public static final int JAVA_DETECTOR = 0;

    private Mat mRgba;   //Mat is OpenCv class
    private Mat mGray;
    private Mat mFGMask;

    private BackgroundSubtractorMOG2 mBGSub ;

    private Boolean Findface = false;
    private File mCascadeFile;
    private CascadeClassifier mJavaDetector;

    private String[] mDetectorName;
    private float mRelativeFaceSize = 0.2f;
    private int mAbsoluteFaceSize = 0;

    private CameraBridgeViewBase mOpenCvCameraView;
    double xCenter = -1;
    double yCenter = -1;

    private Button BtnStart;




    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {        //Callback method, called after OpenCV library initialization.
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");

                    try {
                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface); // load cascade file from application resources
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;  //to hold number of bytes read for each read(byte[]) call
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);  //passing along the buffer byte array as well as how many bytes were read into the array as parameters.
                        }
                        is.close();
                        os.close();

                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath()); //class for object detection
                        if (mJavaDetector.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            mJavaDetector = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

                        cascadeDir.delete();

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

        BtnStart = findViewById(R.id.btn_start) ;

        BtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
    }

    @Override
    public void onCameraViewStopped() {

        mGray.release();
        mRgba.release();

    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        Core.flip(mRgba,mRgba,1);



        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
        }

        MatOfRect faces = new MatOfRect();

        if (mJavaDetector != null) {
            mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                    new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());

        } else {
            Log.e(TAG, "Detector is null!");
        }

        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++) {
//            กรอบ4เหลี่ยม
//            Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
            xCenter = (facesArray[i].x + facesArray[i].width + facesArray[i].x) / 2;
            yCenter = (facesArray[i].y + facesArray[i].y + facesArray[i].height) / 2;
            Point center = new Point(xCenter, yCenter);
            Imgproc.circle(mRgba, center, 20, new Scalar(255, 0, 0, 255), 5);

        }



// ตำแหน่งคร่าวๆ
//        if (Findface == true) {
//
////          hand  right
//            Imgproc.rectangle(mRgba, new Point(200, 100), new Point(300, 200), new Scalar(76, 255, 0), 3);
////          elbow right
//            Imgproc.rectangle(mRgba, new Point(200, 600), new Point(300, 500), new Scalar(76, 255, 0), 3);
////          shoulder right
//            Imgproc.rectangle(mRgba, new Point(400, 500), new Point(500, 400), new Scalar(76, 255, 0), 3);
//
////          hand   left
//            Imgproc.rectangle(mRgba, new Point(1200, 100), new Point(1300, 200), new Scalar(76, 255, 0), 3);
////          elbow left
//            Imgproc.rectangle(mRgba, new Point(1200, 600), new Point(1300, 500), new Scalar(76, 255, 0), 3);
////          shoulder left
//            Imgproc.rectangle(mRgba, new Point(1000, 500), new Point(1100, 400), new Scalar(76, 255, 0), 3);
//        }


        return mRgba;
    }

}
