package com.zuhlke.tg_mobile.jira_ar_tgmobile;

import android.Manifest.permission.CAMERA
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Surface
import android.widget.ImageView
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.imgcodecs.Imgcodecs
import java.util.*

class PostItScannerActivity : AppCompatActivity() {

    var imageReader: ImageReader? = null
    var cameraOpened: CameraDevice? = null
    private var knn: KNN? = null;
    val backgroundHandler = Handler()
    var lock = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        imageReader = ImageReader.newInstance(1024, 800, ImageFormat.JPEG, 2)
        setContentView(R.layout.activity_camera)
        setupCamera()
        setupKnn()
    }

    private fun setupKnn() {
        if (OpenCVLoader.initDebug()) {
            val trainingData = Utils.loadResource(applicationContext, R.drawable.training, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE)
            knn = KNN(trainingData)
        }
    }

    private val CAMERA_PERMISSION_REQUEST_CODE = 1

    private fun setupCamera() {
        Log.d("POSTIT-CLASS","setupCamera()" )
        if (hasCameraPermission()) openCamera()
        else getCameraPermission()
    }

    private fun hasCameraPermission(): Boolean {
        Log.d("POSTIT-CLASS","hasCameraPermission()" )

        return ContextCompat.checkSelfPermission(this, CAMERA) == PERMISSION_GRANTED
    }

    private fun openCamera() {
        Log.d("POSTIT-CLASS","openCamera()" )
        val manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager;
        val cameraIds = manager.cameraIdList
        // val camCharacteristics = manager.getCameraCharacteristics(cameraIds.get(0))
        if (cameraIds.size > 0) {
            manager.openCamera(cameraIds.get(0), stateCallbackHandler, backgroundHandler)
        }
    }

    private fun getCameraPermission() {
        Log.d("POSTIT-CLASS","getCameraPermission()" )

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                CAMERA)) {
            // TODO Show an expanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
        }
        ActivityCompat.requestPermissions(this, arrayOf(CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE);
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Log.d("POSTIT-CLASS","onRequestPermissionsResult()" )

        if (requestCode.equals(CAMERA_PERMISSION_REQUEST_CODE)) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.size > 0 && grantResults[0] == PERMISSION_GRANTED) {
                setupCamera()
            }
        }
    }


    val stateCallbackHandler = object : CameraDevice.StateCallback() {
        override fun onError(camera: CameraDevice?, error: Int) {
            throw RuntimeException("Need To implement this later")
        }

        override fun onOpened(camera: CameraDevice?) {
            cameraOpened = camera
            val surfaces = ArrayList<Surface>()
            surfaces.add(imageReader!!.surface!!);
            camera?.createCaptureSession(surfaces, captureSession, backgroundHandler)

        }

        override fun onDisconnected(camera: CameraDevice?) {
            Log.d("LATER", "Need To implement this later")
        }

    }


    val captureSession = object : CameraCaptureSession.StateCallback() {
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


    val mCaptureCallback = object : CameraCaptureSession.CaptureCallback() {
        override fun onCaptureCompleted(session: CameraCaptureSession?, request: CaptureRequest?, result: TotalCaptureResult?) {
            super.onCaptureCompleted(session, request, result)

        }
    }

    val onImageAvailableListener = object : ImageReader.OnImageAvailableListener {
        override fun onImageAvailable(reader: ImageReader?) {
            val image = reader!!.acquireLatestImage()

            if (OpenCVLoader.initDebug()) {
                if (!lock) {
                    lock = true;
                    val iv = findViewById(R.id.cameraImageView) as ImageView;
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


}
