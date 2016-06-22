package com.zuhlke.tg_mobile.jira_ar_tgmobile;

import android.graphics.Bitmap;
import android.media.Image;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

import static org.opencv.imgproc.Imgproc.approxPolyDP;
import static org.opencv.imgproc.Imgproc.arcLength;
import static org.opencv.imgproc.Imgproc.contourArea;
import static org.opencv.imgproc.Imgproc.isContourConvex;

/**
 * Created by chfe on 21/06/16.
 */
public class ImageProcessing {

    public static Mat imageToMat(Image image) {
        if (OpenCVLoader.initDebug()) {
            Mat buf = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC1);
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            buf.put(0, 0, bytes);
            return Imgcodecs.imdecode(buf, Imgcodecs.IMREAD_COLOR);
        }
        throw new RuntimeException("Couldn't load opencv");
    }

    private static Bitmap matToBitmap(Mat gray) {
        Bitmap bm = Bitmap.createBitmap(gray.cols(), gray.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(gray, bm);
        return bm;
    }

    public static Bitmap process(Image image){
        Mat imageRgb = imageToMat(image);

        //Convert to hue, saturation brightness channels
        Mat imageHsv = new Mat();
        Imgproc.cvtColor(imageRgb, imageHsv, Imgproc.COLOR_RGB2HSV);
        //Split channels and extract saturation
        ArrayList<Mat> hsv = new ArrayList<Mat>(3);
        Core.split(imageHsv, hsv);
        Mat sat = hsv.get(1);

        ////Do edge detection
        //Mat edges = edgeDetect(sat);
        //Do thresholding
        Mat thresh = threshold(sat);
        //return matToBitmap(thresh);
        //get contours
        ContourResult contours = contours(thresh);
        //draw them nicely
        //Mat prettyContours = drawContours(contours);
        //return matToBitmap(prettyContours);

        // remove small and non convex contours
        ContourResult filtered = analyseContours(contours);
        //draw them nicely
        //Mat filteredContours = drawContours(filtered);
        //superimpose them back on the original image
        Mat ar = drawContours(filtered, imageRgb);
        return matToBitmap(ar);
    }


    private static Mat drawContours(ContourResult result) {
        return drawContours(result, Mat.zeros(result.size, CvType.CV_8UC3));
    }
    private static Mat drawContours(ContourResult result, Mat background) {
        Random rnd = new Random();
        System.out.println("found "+result.points.size()+"contours");
        for (int i = 0; i < result.points.size(); i++) {
            Scalar color = new Scalar(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255));
            Imgproc.drawContours(background, result.points, i, color, 20, 8, result.hierarchy, 0, new Point());
        }
        return background;
    }


    private static Mat threshold(Mat src) {
        final Mat dst = new Mat(src.rows(), src.cols(), src.type());
        src.copyTo(dst);
        Mat output = new Mat();
        Imgproc.threshold(dst, output, 255, 255, Imgproc.THRESH_OTSU);
        return output;
    }


    private static ContourResult analyseContours(ContourResult result){


        ArrayList<MatOfPoint> filteredPoints = new ArrayList<MatOfPoint>();
        for (int i = 0; i < result.points.size(); i++)
        {

            MatOfPoint2f approx = new MatOfPoint2f();
            // Approximate contour with accuracy proportional

            // to the contour perimeter
            double epsilon = arcLength(new MatOfPoint2f(result.points.get(i).toArray()), true) * 0.02;
            MatOfPoint2f curve = new MatOfPoint2f(result.points.get(i).toArray());
            approxPolyDP(curve, approx, epsilon, true);

            // Skip small or non-convex objects
            if (Math.abs(contourArea(result.points.get(i))) < 1000 || !isContourConvex(new MatOfPoint(approx.toArray())))
                continue;


            filteredPoints.add(result.points.get(i));
            System.out.println("Passed Filtering "+i);

        }
        return new ContourResult(result, filteredPoints);
    }



    private static Mat edgeDetect(Mat src) {
        final Mat dst = new Mat(src.rows(), src.cols(), src.type());
        src.copyTo(dst);
        Mat edges = new Mat();
        Imgproc.Canny(dst, edges, 70,90);
        return edges;
    }


    public static ContourResult contours(final Mat src) {
        final Mat dst = new Mat(src.rows(), src.cols(), src.type());
        src.copyTo(dst);

        final ArrayList<MatOfPoint> points = new ArrayList<MatOfPoint>();
        final Mat hierarchy = new Mat();
        Imgproc.findContours(dst, points, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        return new ContourResult(hierarchy, points, dst.size());

    }
    static class ContourResult{
        public ArrayList<MatOfPoint> points;
        public Mat hierarchy;
        public Size size;

        public ContourResult(Mat hierarchy, ArrayList<MatOfPoint> points, Size size) {
            this.hierarchy = hierarchy;
            this.points = points;
            this.size = size;
        }

        public ContourResult(ContourResult result, ArrayList<MatOfPoint> filteredPoints) {
            this.hierarchy = result.hierarchy;
            this.points = filteredPoints;
            this.size = result.size;
        }
    }
}
