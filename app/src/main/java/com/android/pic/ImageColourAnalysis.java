package com.android.pic;

import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.Rect;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.Highgui;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.InstallCallbackInterface;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import android.app.Activity;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.SurfaceView;

//import java.lang.Math;

public class ImageColourAnalysis extends Activity implements LoaderCallbackInterface/*, CvCameraViewListener2*/ {
    protected BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    onOpenCVReady();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        Log.i("DEMO", "Trying to load OpenCV library");
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_4, this, mOpenCVCallBack))
        {
            Log.e("DEMO", "Cannot connect to OpenCV Manager");
        }
    }

    protected void onOpenCVReady(){
        //this should crash if opencv is not loaded
        Mat img = new Mat();
        Toast.makeText(getApplicationContext(), "opencv ready", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onManagerConnected(int status) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPackageInstall(int operation,
                                 InstallCallbackInterface callback) {
        // TODO Auto-generated method stub

    }

    public Scalar getRGB(Uri imagePath, int sideLength) {
        System.out.println("In ImageColourAnalysis");
        Mat mRgba = new Highgui().imread(imagePath.toString(), Highgui.CV_LOAD_IMAGE_COLOR);

       if (sideLength < (mRgba.rows() > mRgba.cols() ? mRgba.cols() : mRgba.rows()))
       {
           int minDim = (mRgba.rows() > mRgba.cols() ? mRgba.cols() : mRgba.rows());
           int startX = minDim / 2 - sideLength / 2;
           int startY = minDim / 2 - sideLength / 2;
           int cols = (int)(Math.ceil(sideLength / 2.));
           int rows = (int)(Math.ceil(sideLength / 2.));

           Mat temp = new Mat(sideLength,sideLength, mRgba.type());
           mRgba.colRange(startY, startY + rows).rowRange(startX, startX + cols).copyTo(temp);

           mRgba = temp;
       }

        Mat touchedRegionHsv = new Mat();
        Mat touchedRegionRgba = new Mat();
        Scalar mBlobColorRgba = new Scalar(255);
        Scalar mBlobColorHsv = new Scalar(255);
        Scalar CONTOUR_COLOR = new Scalar(255,0,0,255);

        Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

        // Calculate average color of touched region
        mBlobColorHsv = Core.sumElems(touchedRegionHsv);
        int pointCount = sideLength*sideLength;
        for (int i = 0; i < mBlobColorHsv.val.length; i++)
            mBlobColorHsv.val[i] /= pointCount;

        mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);

        mBlobColorHsv = Core.sumElems(touchedRegionHsv);
        mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);
        ColorBlobDetector mDetector = new ColorBlobDetector();
        mDetector.process(mRgba);
        List<MatOfPoint> contours = mDetector.getContours();
        Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR);

        Mat colorLabel = mRgba.submat(4, 68, 4, 68);
        return(mBlobColorRgba);
    }

    private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }
}
