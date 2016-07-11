package com.zuhlke.tg_mobile.jira_ar_tgmobile;

import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.ml.KNearest;

import static org.opencv.core.CvType.CV_32FC1;
import static org.opencv.ml.Ml.ROW_SAMPLE;

public class KNN {

    static int NUMBER_OF_DIGITS = 10;
    static int SAMPLES_IN_ROW = 10;
    static int K = 3; // Argument to K nearest neighbours
    private final KNearest knn;


    public KNN(Mat img) {
        TrainingData trainingData = new TrainingData(SAMPLES_IN_ROW, CELL_SIZE, img);
        knn = KNearest.create();
        knn.train(trainingData.getFeatures(), ROW_SAMPLE, trainingData.getLabels());

    }

    public static MatOfFloat createTestFeature(Mat digit) {
        MatOfFloat feature = new MatOfFloat();
        Mat rightFormat = new Mat();
        digit.convertTo(rightFormat, CV_32FC1);
        Mat resized = rightFormat.reshape(1, 1);
        feature.push_back(resized);
        return feature;
    }

    public double matchDigits(Mat testFeature) {
        // Mat response,dist;
        Mat response = new Mat();
        Mat dist = new Mat();
        Mat results = new Mat();

        System.out.println("Classifer?" + knn.isClassifier());
        // knn->findNearest(testFeature, K, noArray(), response, dist);
        knn.findNearest(testFeature, K, results, response, dist);
        double result = response.get(0, 0)[0];
        System.out.println(result);

        return result;

    }









    private static final int CELL_SIZE = 20;
}


