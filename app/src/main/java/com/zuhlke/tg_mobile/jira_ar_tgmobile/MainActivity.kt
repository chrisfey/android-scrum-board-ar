package com.zuhlke.tg_mobile.jira_ar_tgmobile

import android.content.Context
import android.content.Intent
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.hardware.camera2.CameraCaptureSession.CaptureCallback
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Surface
import android.widget.ImageView
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.imgcodecs.Imgcodecs
import java.util.*

class MainActivity : AppCompatActivity()  {
    var lock = false;
    var imageReader : ImageReader? = null
    var cameraOpened : CameraDevice? = null
    private var knn: KNN? = null;
    val backgroundHandler = Handler()

    val mCaptureCallback = object : CaptureCallback() {
        override fun onCaptureCompleted(session: CameraCaptureSession?, request: CaptureRequest?, result: TotalCaptureResult?) {
            super.onCaptureCompleted(session, request, result)

        }
    }

    val onImageAvailableListener = object : ImageReader.OnImageAvailableListener{
        override fun onImageAvailable(reader: ImageReader?) {
            val image = reader!!.acquireLatestImage()

            if (OpenCVLoader.initDebug()) {
                if(!lock) {
                    lock = true;
                    val iv = findViewById(R.id.cameraImageView) as ImageView;
                    Log.d("Image Avaialble", "asdfasdf")
                    val bm = ImageProcessing.process(image, knn)
                    iv.setImageBitmap(bm)
                    lock = false;
                }
            } else {
                image.close()
                throw RuntimeException("couldnt load opencv")
            }
            image.close()

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        startActivity(Intent(applicationContext, LoginActivity::class.java))


        setContentView(R.layout.activity_main)
//        val toolbar = findViewById(R.id.toolbar) as Toolbar
//        setSupportActionBar(toolbar)
        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {
            view ->
            val i = Intent(applicationContext, JiraListActivity::class.java)
            startActivity(i);
        }
        val fab2 = findViewById(R.id.fab2) as FloatingActionButton
        //I cant compile with any of the vuforia stuff, need to include the .jar?
//        fab2.setOnClickListener {
//            view ->
//            val i = Intent(applicationContext, VuforiaTargets::class.java)
//            startActivity(i);
//        }
        val fab3 = findViewById(R.id.fab3) as FloatingActionButton
        fab3.setOnClickListener {
            view -> startCamera()
        }
    }

    private fun startCamera() {
        imageReader = ImageReader.newInstance(1024, 800, ImageFormat.JPEG, 2)
        //mTextureView =  findViewById(R.id.textureView) as TextureView
        //mTextureView!!.surfaceTextureListener = this

        setContentView(R.layout.activity_camera)
        imageReader = ImageReader.newInstance(1024,800,  ImageFormat.JPEG, 2)
        val manager =  getSystemService(Context.CAMERA_SERVICE) as CameraManager;
        val cameraIds = manager.cameraIdList
        //        val camCharacteristics = manager.getCameraCharacteristics(cameraIds.get(0))
        if (cameraIds.size > 0) {
            manager.openCamera(cameraIds.get(0), stateCallbackHandler(), backgroundHandler)
        }
        if (OpenCVLoader.initDebug()) {
            val trainingData = Utils.loadResource(applicationContext, R.drawable.training, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE)
            knn = KNN(trainingData)
        }
    }

    private fun captureSession(): CameraCaptureSession.StateCallback? {
        return object : CameraCaptureSession.StateCallback() {
            override fun onConfigureFailed(session: CameraCaptureSession?) {
                throw RuntimeException("Need To implement this later")
            }

            override fun onConfigured(session: CameraCaptureSession?) {

                imageReader!!.setOnImageAvailableListener(onImageAvailableListener, backgroundHandler);

                val mPreviewRequestBuilder = cameraOpened!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                //mPreviewRequestBuilder.addTarget(mSurface);
                mPreviewRequestBuilder.addTarget(imageReader!!.surface);

                val mPreviewRequest = mPreviewRequestBuilder.build();
                session!!.setRepeatingRequest(mPreviewRequest,
                        mCaptureCallback, backgroundHandler);

            }

        }
    }

    private fun stateCallbackHandler(): CameraDevice.StateCallback {

        return  object : CameraDevice.StateCallback(){
            override fun onError(camera: CameraDevice?, error: Int) {
                throw RuntimeException("Need To implement this later")
            }

            override fun onOpened(camera: CameraDevice?){
                cameraOpened = camera
                val surfaces = ArrayList<Surface>()

                //surfaces.add(mSurface!!);
                surfaces.add(imageReader!!.surface!!);
                camera?.createCaptureSession(surfaces, captureSession(), backgroundHandler)

            }



            override fun onDisconnected(camera: CameraDevice?) {
                Log.d("LATER","Need To implement this later")
            }

        }
    }


}
