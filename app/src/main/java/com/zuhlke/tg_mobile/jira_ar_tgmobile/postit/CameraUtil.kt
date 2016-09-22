package com.zuhlke.tg_mobile.jira_ar_tgmobile.postit;

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Surface
import java.util.*


class CameraUtil constructor(val context: Activity) {
    val CAMERA_PERMISSION_REQUEST_CODE = 1
    val imageReader = ImageReader.newInstance(1024, 800, ImageFormat.JPEG, 2)
    val backgroundHandler = Handler()
    var cameraOpened: CameraDevice? = null

    fun setupCamera() {
        Log.d("POSTIT-CLASS", "setupCamera()")
        if (hasCameraPermission()) openCamera()
        else getCameraPermission()
    }

    fun getCameraPermission() {
        Log.d("POSTIT-CLASS", "getCameraPermission()")
        if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.CAMERA)) {
            // TODO Show an expanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
        }
        ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE);
    }

    fun hasCameraPermission(): Boolean {
        Log.d("POSTIT-CLASS", "hasCameraPermission()")
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    fun openCamera() {
        Log.d("POSTIT-CLASS", "openCamera()")
        val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager;
        val cameraIds = manager.cameraIdList
        // val camCharacteristics = manager.getCameraCharacteristics(cameraIds.get(0))
        if (cameraIds.size > 0) {
            manager.openCamera(cameraIds.get(0), stateCallbackHandler, backgroundHandler)
        }
    }

    val stateCallbackHandler = object : CameraDevice.StateCallback() {
        override fun onError(camera: CameraDevice?, error: Int) {
            throw RuntimeException("Need To implement this later")
        }

        override fun onOpened(camera: CameraDevice?) {
            cameraOpened = camera!!
            val surfaces = ArrayList<Surface>()
            surfaces.add(imageReader.surface)
            camera.createCaptureSession(surfaces, captureSession, backgroundHandler)
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
            imageReader.setOnImageAvailableListener(context as ImageReader.OnImageAvailableListener, backgroundHandler);
            val mPreviewRequestBuilder = cameraOpened!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(imageReader.surface);
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



}
