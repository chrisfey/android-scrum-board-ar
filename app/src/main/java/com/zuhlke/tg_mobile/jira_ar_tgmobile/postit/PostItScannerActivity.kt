package com.zuhlke.tg_mobile.jira_ar_tgmobile.postit;

import android.content.pm.PackageManager
import android.graphics.*
import android.media.ImageReader
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ImageView
import com.zuhlke.tg_mobile.jira_ar_tgmobile.App
import com.zuhlke.tg_mobile.jira_ar_tgmobile.R
import com.zuhlke.tg_mobile.jira_ar_tgmobile.jira.JiraIssues
import com.zuhlke.tg_mobile.jira_ar_tgmobile.jira.JiraItem
import com.zuhlke.tg_mobile.jira_ar_tgmobile.jira.JiraRestApi
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.imgcodecs.Imgcodecs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class PostItScannerActivity : ImageReader.OnImageAvailableListener, Callback<JiraIssues>, AppCompatActivity() {
    val LOGTAG = App.LOGTAG + ".PostItScannerActivity"
    override fun onFailure(call: Call<JiraIssues>?, t: Throwable?) {
        throw UnsupportedOperationException()
    }

    var knn: KNN? = null
    var imageView: ImageView? = null
    var backgroundImageView: ImageView? = null
    val cameraUtil: CameraUtil = CameraUtil(this)

    var jiraIssues: JiraIssues? = null

    override fun onResponse(call: Call<JiraIssues>?, response: Response<JiraIssues>?) {
        Log.d(LOGTAG, "jira response")
        jiraIssues = response?.body()
    }

    var avatar: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera)

        //getBitmapFromURL("https://jira.atlassian.com/secure/useravatar?avatarId=10612")
        JiraRestApi().getIssues().enqueue(this)
        OpenCVLoader.initDebug()

        cameraUtil.setupCamera()
        knn = setupKnn()
        imageView = findViewById(R.id.cameraImageView) as ImageView;
        backgroundImageView = findViewById(R.id.backgroundImageView) as ImageView;
    }


    private fun setupKnn(): KNN {
        val trainingData = Utils.loadResource(applicationContext, R.drawable.training_modified, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE)
        return KNN(trainingData)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode.equals(cameraUtil.CAMERA_PERMISSION_REQUEST_CODE)) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraUtil.setupCamera()
            }
        }
    }

    inline fun <T> Lock.tryLock(action: () -> T, alternative: () -> T): T {
        if (tryLock()) {
            try {
                return action()
            } finally {
                unlock();
            }
        } else {
            return alternative();
        }
    }

    val lock = ReentrantLock();


    fun overlay(bmp1: Bitmap, bmp2: Bitmap): Bitmap {
        val bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        val canvas = Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, Matrix(), null);
        canvas.drawBitmap(bmp2, Matrix(), null);
        return bmOverlay;
    }

//    fun getBitmapFromURL(src: String) {
//        GetAvatar().execute(src)
//    }

//    inner class GetAvatar : AsyncTask<String, Void, Void>() {
//
//        override fun doInBackground(vararg params: String?): Void? {
//            try {
//                val url = URL(params[0])
//                val connection = url.openConnection()
//                connection.setDoInput(true);
//
//                connection.connect();
//                val input = connection.getInputStream();
//                val svg = SVGParser.getSVGFromInputStream(input);
//                val drawable = svg.createPictureDrawable();
//
//                avatar = BitmapFactory.decodeStream(input);
//
//                Log.d(App.LOGTAG, "Got bitmap")
//            } catch (e: IOException) {
//                throw RuntimeException("could not get avatar image")
//            }
//            return null
//        }
//        override fun onPreExecute() {
//            super.onPreExecute()
//        }
//        override fun onPostExecute(result: Void?) {
//            super.onPostExecute(result)
//        }
//    }

    override fun onImageAvailable(reader: ImageReader?) {
        val image = reader!!.acquireLatestImage()
        if (image != null) {
            lock.tryLock({
                val result = ImageProcessing.process(image, knn)
                val jiraItem = getMatchingJira(result.digits)
                val finalBitmap = result.finalBitmap;
                drawJiraItemOnBitmap(result.finalBitmap, jiraItem)
                imageView!!.setImageBitmap(finalBitmap)

            }, {})
        }
        image.close()
    }

    private fun drawJiraItemOnBitmap(finalBitmap: Bitmap?, jiraItem: JiraItem?): Bitmap? {
        val can = Canvas(finalBitmap);
        val paint = Paint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.rgb(61, 61, 61));
        // text size in pixels
        paint.textSize = 14f;
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        // draw text to the Canvas center
        val bounds = Rect();
        val text = "Hello" + jiraItem?.priority
        paint.getTextBounds(text, 0, text.length, bounds);
        can.drawText(text, 0f, 20f, paint);
        return finalBitmap
    }

    private fun getMatchingJira(postIts: ArrayList<String>): JiraItem? {
        val jiraIssues = jiraIssues
        if (jiraIssues != null) {
            return postIts.asSequence()
                    .map { digits -> getMatchingJiraItem(digits, jiraIssues) }
                    .firstOrNull()
        }
        return null
    }

    private fun getMatchingJiraItem(digits: String, jiraIssues: JiraIssues): JiraItem? {
        return jiraIssues.items()
                .asSequence()
                .filter { item -> item.key.contains(digits) }
                .firstOrNull()
    }
}
