package com.zuhlke.tg_mobile.jira_ar_tgmobile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.hardware.camera2.CameraCaptureSession.CaptureCallback
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Surface
import android.view.SurfaceView
import android.view.TextureView
import android.widget.ImageView
import org.opencv.android.Utils
import org.opencv.core.Mat
import java.nio.ByteBuffer
import java.util.*

class MainActivity : AppCompatActivity()  {



    val mCaptureCallback = object : CaptureCallback() {

        override fun onCaptureCompleted(session: CameraCaptureSession?, request: CaptureRequest?, result: TotalCaptureResult?) {
            super.onCaptureCompleted(session, request, result)

        }

    }

    var lock = false;
    val onImageAvailableListener = object : ImageReader.OnImageAvailableListener{
        override fun onImageAvailable(reader: ImageReader?) {
            val image = reader!!.acquireLatestImage()
            if(!lock) {
                lock = true;
                val iv = findViewById(R.id.imageView) as ImageView;
                Log.d("Image Avaialble", "asdfasdf")
                val bm = ImageProcessing.process(image)
                iv.setImageBitmap(bm)
                lock = false;
            }
            image.close()

        }

    }
    var imageReader : ImageReader? = null
    var cameraOpened : CameraDevice? = null
    //var mSurface : Surface? = null
    //var mTextureView : TextureView? = null
    val backgroundHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show() }

        imageReader = ImageReader.newInstance(1024,800,  ImageFormat.JPEG, 2)
        //mTextureView =  findViewById(R.id.textureView) as TextureView
        //mTextureView!!.surfaceTextureListener = this

        val manager =  getSystemService(Context.CAMERA_SERVICE) as CameraManager;
        val cameraIds = manager.cameraIdList
        val camCharacteristics = manager.getCameraCharacteristics(cameraIds.get(0))
        manager.openCamera(cameraIds.get(0), stateCallbackHandler(), backgroundHandler)


    }

    private fun captureSession(): CameraCaptureSession.StateCallback? {
        return object : CameraCaptureSession.StateCallback() {
            override fun onConfigureFailed(session: CameraCaptureSession?) {
                throw UnsupportedOperationException()
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

    private fun stateCallbackHandler(): CameraDevice.StateCallback? {

        return  object : CameraDevice.StateCallback(){
            override fun onError(camera: CameraDevice?, error: Int) {
                throw UnsupportedOperationException()
            }

            override fun onOpened(camera: CameraDevice?){
                cameraOpened = camera
                val surfaces = ArrayList<Surface>()

                //surfaces.add(mSurface!!);
                surfaces.add(imageReader!!.surface!!);
                camera?.createCaptureSession(surfaces, captureSession(), backgroundHandler)

            }



            override fun onDisconnected(camera: CameraDevice?) {
                throw UnsupportedOperationException()
            }

        }
    }

//    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
//
//        //throw UnsupportedOperationException()
//    }
//
//    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
//        //throw UnsupportedOperationException()
//    }
//
//    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
//        throw UnsupportedOperationException()
//    }

//    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
//
//
//
//        val manager =  getSystemService(Context.CAMERA_SERVICE) as CameraManager;
//        val cameraIds = manager.cameraIdList
//        val camCharacteristics = manager.getCameraCharacteristics(cameraIds.get(0))
//
//        //val texture = mTextureView!!.surfaceTexture
//
//        mSurface = Surface(texture);
//
//
//
//
//
//        manager.openCamera(cameraIds.get(0), stateCallbackHandler(), backgroundHandler)
//
//    }


}
