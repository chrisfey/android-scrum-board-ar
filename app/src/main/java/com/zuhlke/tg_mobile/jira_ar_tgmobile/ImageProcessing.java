package com.zuhlke.tg_mobile.jira_ar_tgmobile;

import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.opencv.imgproc.Imgproc.approxPolyDP;
import static org.opencv.imgproc.Imgproc.arcLength;
import static org.opencv.imgproc.Imgproc.boundingRect;
import static org.opencv.imgproc.Imgproc.contourArea;
import static org.opencv.imgproc.Imgproc.isContourConvex;

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

    public static Bitmap process(Image image, KNN knn){



        Mat imageRgb = imageToMat(image);
        Mat imageHsv = rgbImageToHsv(imageRgb);
        Mat saturation = hsvToSaturation(imageHsv);
        Mat thresh = threshold(saturation);
        ContourResult contours = contours(thresh);
        //Mat prettyContours = drawContours(contours);
        ContourResult filtered = filterContours(contours, 1000);
        Mat prettyfilteredContours = drawContoursOnTo(filtered, imageRgb);

        ArrayList<SortedMap<Integer, Mat>> postItsAndTheirDigits = extractDigits(filtered, imageHsv);


        //Mat finalMat = Mat.zeros(100, 20, postItsAndTheirDigits.get(0).get(0).type());
        //resize digits to 2020
        Log.d("POSTITS", "size" + postItsAndTheirDigits.size());
        for(SortedMap<Integer, Mat> digits : postItsAndTheirDigits){
            Log.d("POSTITS", "digits size"+digits.size());


            String found = "found digits:";
            int i = 0;
            for(Mat digit: digits.values()){
                Mat resized = new Mat();
                Imgproc.resize(digit, resized, new Size(20, 20));

                double a = knn.matchDigits(KNN.createTestFeature(resized));


                found += Double.valueOf(a).intValue();


                //Copy the digits to the main image
                Mat rgbResized = new Mat();
                Imgproc.cvtColor(resized, rgbResized, Imgproc.COLOR_GRAY2RGB);

               // Imgproc.resize(rgbResized, rgbResized, new Size(40, 40));

                // Log.d   ("POSTITS", "resized" + resized.size());
                for (int x = 0; x < rgbResized.size().width; x++) {
                    for (int y = 0; y < rgbResized.size().height; y++) {
                        prettyfilteredContours.put(y , x + (20 * i), rgbResized.get(y, x));
                        //resized.copyTo(prettyfilteredContours);
                    }
                }
                //digits.add(i, resized);
                i++;
            }

            Log.d("FOUND DIGITS", found);
        }


       // Imgproc.cvtColor(someDig,rgb, Imgproc.COLOR_HSV2RGB );
        //Mat ar = drawContours(filtered, imageRgb);
        return matToBitmap(prettyfilteredContours);
    }

    private static Mat hsvToSaturation(Mat imageHsv) {
        ArrayList<Mat> hsv = new ArrayList<>(3);
        Core.split(imageHsv, hsv);
        return hsv.get(1);
    }

    private static Mat rgbImageToHsv(Mat imageRgb) {
        //Convert to hue, saturation brightness channels
        Mat imageHsv = new Mat();
        Imgproc.cvtColor(imageRgb, imageHsv, Imgproc.COLOR_RGB2HSV);
        return imageHsv;
    }

    private static ArrayList<SortedMap<Integer, Mat>> extractDigits(ContourResult postItNotes, Mat originalImage) {


        ArrayList<SortedMap<Integer, Mat>> postIts = new ArrayList<>();
        System.out.println("Number of filtered results: " + postItNotes.points.size());
        for(int i = 0; i < postItNotes.points.size(); i++) {
            Rect r = boundingRect(postItNotes.points.get(i));
            double minContourSize = r.area() * 0.0001;


            Rect digitRect = new Rect(new Point(r.x + r.width / 2, r.y), new Point(r.x + r.width, r.y + r.height / 3));
            Mat bound = originalImage.submat(digitRect);

            ArrayList<Mat> hsvDigit = new ArrayList<>(3);
            Core.split(bound, hsvDigit);
            Mat brightDigit = hsvDigit.get(2);
            Mat thresh = threshold(brightDigit);

            ContourResult contourResult = contours(thresh);
            //Mat contour = drawContours(contourResult);
            //writeToFile(contour, i ,"contourDigit.png");
            int largest = contourResult.removeLargestContour();
            ContourResult filtered = removeSmallContours(contourResult, minContourSize,largest);

            //filteredContour.copyTo(output);

            int MAX_NUMBER_OF_DIGITS_TO_ALLOW = 8;
            Log.d("EXTRACT-DIGITS", "filteredpointsSize" +filtered.points);

            if(filtered.points.size() < MAX_NUMBER_OF_DIGITS_TO_ALLOW) {
                SortedMap<Integer,Mat> digitsForThisPostit = new TreeMap<>();
                for (int j = 0; j < filtered.points.size(); j++) {
                    Rect digitBox = boundingRect(filtered.points.get(j));
                    Mat digit = thresh.submat(digitBox);
                    digitsForThisPostit.put(digitBox.x, digit);
                    System.out.println("Found digit");
                }

                postIts.add(digitsForThisPostit);
            }

        }
        return postIts;
    }


    private static Mat drawContours(ContourResult result) {
        return drawContoursOnTo(result, Mat.zeros(result.size, CvType.CV_8UC3));
    }
    private static Mat drawContoursOnTo(ContourResult result, Mat background) {
        Random rnd = new Random();
        System.out.println("found "+result.points.size()+"contours");
        System.out.println("fqekfbo;qbqljABNQLNSF'qsfnoqeALVNeip");
        for (int i = 0; i < result.points.size(); i++) {
            Scalar color = new Scalar(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255));
            Imgproc.drawContours(background, result.points, i, color, 2, 8, result.hierarchy, 0, new Point());
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


    private static ContourResult filterContours(ContourResult result, double minSizeContour){
        // remove small and non convex contours
        ArrayList<MatOfPoint> filteredPoints = new ArrayList<>();
        for (int i = 0; i < result.points.size(); i++) {
            MatOfPoint2f approx = new MatOfPoint2f();
            // Approximate contour with accuracy proportional
            // to the contour perimeter
            double epsilon = arcLength(new MatOfPoint2f(result.points.get(i).toArray()), true) * 0.02;
            MatOfPoint2f curve = new MatOfPoint2f(result.points.get(i).toArray());
            approxPolyDP(curve, approx, epsilon, true);
            // Skip small or non-convex objects
            if (Math.abs(contourArea(result.points.get(i))) < minSizeContour || !isContourConvex(new MatOfPoint(approx.toArray())))
                continue;
            filteredPoints.add(result.points.get(i));
            System.out.println("Passed Filtering "+i);

        }
        return new ContourResult(result, filteredPoints);
    }

    private static ContourResult removeSmallContours(ContourResult result, double minSizeContour, int parent){
        ArrayList<MatOfPoint> filteredPoints = new ArrayList<>();
        for (int i = 0; i < result.points.size(); i++)
        {
            if (Math.abs(contourArea(result.points.get(i))) < minSizeContour ) continue;
            //if(result.hierarchy.get(0,i)[3] != parent) continue;
            filteredPoints.add(new MatOfPoint(result.points.get(i).toArray()));
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

        final ArrayList<MatOfPoint> points = new ArrayList<>();
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

        public int removeLargestContour() {
            double biggestSize = -1;
            int biggestIndex = -1;
            for(int i =0; i < points.size();i++){
                double currentSize = Math.abs(contourArea(points.get(i)));
                if(currentSize > biggestSize ){
                    biggestIndex = i;
                    biggestSize = currentSize;
                }
            }
            points.remove(biggestIndex);
            return biggestIndex;
        }
    }

}
