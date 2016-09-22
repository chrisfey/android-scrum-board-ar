package com.zuhlke.tg_mobile.jira_ar_tgmobile.postit;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;

import static org.opencv.core.CvType.CV_32FC1;


public class TrainingData {

    private static  int DIGITS_TO_LEARN;
    private int SAMPLES_IN_ROW ;
    private Mat image = new Mat();
    private int CELL_SIZE;

    private MatOfFloat trainingData = new MatOfFloat();
    private MatOfInt trainLabels = new MatOfInt();

    TrainingData(int numberOfExamplesInEachRow, int cellSize, Mat image, int digitsToLearn){
        image.convertTo(this.image, CvType.CV_32FC1);
        this.DIGITS_TO_LEARN = digitsToLearn;
        this.SAMPLES_IN_ROW = numberOfExamplesInEachRow;
        this.CELL_SIZE = cellSize;
        buildTrainingData();
        buildTrainingLabels();
    }


    private void buildTrainingLabels() {
        int[] aaa = new int[SAMPLES_IN_ROW * DIGITS_TO_LEARN];
        for (int number = 0; number <= DIGITS_TO_LEARN - 1; number++) {
            for (int i = 0; i < SAMPLES_IN_ROW; i++) {
                aaa[number * SAMPLES_IN_ROW + i] = number;
            }
        }
        trainLabels =  new MatOfInt(aaa);
    }

    private MatOfFloat buildTrainingData() {
        for (int row = 0; row < DIGITS_TO_LEARN; row++) {
            addRowOfTrainingData(trainingData, SAMPLES_IN_ROW, row );// *5 because there are 5 rows of training data for each digit
        }
        return trainingData;
    }


    private void addRowOfTrainingData(MatOfFloat trainFeatures,  int examplesInRow, int row) {
        for (int x = 0; x < examplesInRow; x++) {
            Mat resized = getDigitAtCoord(x, row);
            trainFeatures.push_back(resized);
        }
    }

    private Mat getDigitAtCoord(int x, int y) {
        Mat digit = image.submat(y * CELL_SIZE, y * CELL_SIZE + CELL_SIZE, x * CELL_SIZE, x * CELL_SIZE + CELL_SIZE);
        Mat rightFormat = new Mat();
        digit.convertTo(rightFormat, CV_32FC1);
        return rightFormat.reshape(1, 1);
    }

    public Mat getFeatures() {
        return trainingData;
    }

    public Mat getLabels() {
        return trainLabels;
    }
}
