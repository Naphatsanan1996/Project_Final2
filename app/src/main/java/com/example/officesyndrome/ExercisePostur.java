package com.example.officesyndrome;

public class ExercisePostur {
}
//
//Project: fingerblox   File: ImageProcessing.java   View Source Code	Vote up	8 votes
//public Mat skinDetection(Mat src) {
//        // define the upper and lower boundaries of the HSV pixel
//        // intensities to be considered 'skin'
//        Scalar lower = new Scalar(0, 48, 80);
//        Scalar upper = new Scalar(20, 255, 255);
//
//        // Convert to HSV
//        Mat hsvFrame = new Mat(src.rows(), src.cols(), CvType.CV_8U, new Scalar(3));
//        Imgproc.cvtColor(src, hsvFrame, Imgproc.COLOR_RGB2HSV, 3);
//
//        // Mask the image for skin colors
//        Mat skinMask = new Mat(hsvFrame.rows(), hsvFrame.cols(), CvType.CV_8U, new Scalar(3));
//        Core.inRange(hsvFrame, lower, upper, skinMask);
////        currentSkinMask = new Mat(hsvFrame.rows(), hsvFrame.cols(), CvType.CV_8U, new Scalar(3));
////        skinMask.copyTo(currentSkinMask);
//
//// apply a series of erosions and dilations to the mask
//// using an elliptical kernel
//final Size kernelSize = new Size(11, 11);
//final Point anchor = new Point(-1, -1);
//final int iterations = 2;
//
//        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, kernelSize);
//        Imgproc.erode(skinMask, skinMask, kernel, anchor, iterations);
//        Imgproc.dilate(skinMask, skinMask, kernel, anchor, iterations);
//
//// blur the mask to help remove noise, then apply the
//// mask to the frame
//final Size ksize = new Size(3, 3);
//
//        Mat skin = new Mat(skinMask.rows(), skinMask.cols(), CvType.CV_8U, new Scalar(3));
//        Imgproc.GaussianBlur(skinMask, skinMask, ksize, 0);
//        Core.bitwise_and(src, src, skin, skinMask);
//
//        return skin;
//        }
//
