package com.zuhlke.tg_mobile.jira_ar_tgmobile;

import android.media.Image;
import android.os.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.net.URL;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */

public class UtilTest {

    @Test
    public void json_deserialize() {
        String someJson= "{\"some\":{\"aaa\":5},\"more\":234}";

        try {
            Map<String, Object> json = new ObjectMapper().readValue(someJson, Map.class);
            assertEquals(5, ((Map)json.get("some")).get("aaa"));
            assertEquals(234, json.get("more"));
        }catch(Exception e){
            throw new RuntimeException(e);
        }



    }
}