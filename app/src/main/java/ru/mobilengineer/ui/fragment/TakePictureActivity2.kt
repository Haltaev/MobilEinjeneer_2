package ru.mobilengineer.ui.fragment;
//
//import android.Manifest
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.hardware.camera2.CameraAccessException
//import android.hardware.camera2.CameraCaptureSession
//import android.hardware.camera2.CameraDevice
//import android.hardware.camera2.CameraManager
//import android.os.Build
//import android.os.Bundle
//import android.util.Log
//import android.view.Surface
//import androidx.annotation.RequiresApi
//import androidx.core.content.ContextCompat
//import kotlinx.android.synthetic.main.camera.*
//import ru.mobilengineer.R
//import ru.mobilengineer.ui.activity.BaseActivity
//
//class TakePictureActivity2 : BaseActivity() {
//    private var myCameras: Array<CameraService>? = null
//    private var mCameraManager: CameraManager? = null
//    private val CAMERA1 = 0
//    private val CAMERA2 = 1
//
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
////        setContentView(R.layout.camera)
//        Log.d(LOG_TAG, "Запрашиваем разрешение")
//        if ((checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || (ContextCompat.checkSelfPermission(
//                this@TakePictureActivity2,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED))
//        ) {
//            requestPermissions(
//                arrayOf<String>(
//                    Manifest.permission.CAMERA,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//                ), 1
//            )
//        }
//        back_button.setOnClickListener {
//            if (myCameras!!.get(CAMERA2).isOpen) {
//                myCameras!![CAMERA2].closeCamera()
//            }
//            if (!myCameras!![CAMERA1].isOpen) myCameras!![CAMERA1].openCamera()
//        }
//        button_choose_manually.setOnClickListener {
//            if (myCameras!![CAMERA1].isOpen) {
//                myCameras!![CAMERA1].closeCamera()
//            }
//            if (!myCameras!![CAMERA2].isOpen) myCameras!![CAMERA2].openCamera()
//        }
//        button_take_picture.setOnClickListener {
//            // if (myCameras[CAMERA1].isOpen()) myCameras[CAMERA1].makePhoto();
//            // if (myCameras[CAMERA2].isOpen()) myCameras[CAMERA2].makePhoto();
//        }
//        mCameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
//        try {
//            // Получение списка камер с устройства
//            myCameras =
//                arrayOf(CameraService(mCameraManager!!, "0"), CameraService(mCameraManager!!, "1"))
//            for (cameraID in mCameraManager!!.getCameraIdList()) {
//                Log.i(LOG_TAG, "cameraID: " + cameraID)
//                val id = Integer.parseInt(cameraID)
//                // создаем обработчик для камеры
//                myCameras!![id] = CameraService(mCameraManager!!, cameraID)
//            }
//        } catch (e: CameraAccessException) {
////            Log.e(LOG_TAG, e.getMessage())
//            e.printStackTrace()
//        }
//    }
//
//    inner class CameraService(cameraManager: CameraManager, cameraID: String) {
//        private val mCameraID: String
//        private var mCameraDevice: CameraDevice? = null
//        private lateinit var mCaptureSession: CameraCaptureSession
//        private val mCameraCallback = object : CameraDevice.StateCallback() {
//            override fun onOpened(camera: CameraDevice) {
//                mCameraDevice = camera
//                Log.i(LOG_TAG, "Open camera with id:" + mCameraDevice!!.getId())
//                createCameraPreviewSession()
//            }
//
//            override fun onDisconnected(camera: CameraDevice) {
//                mCameraDevice!!.close()
//                Log.i(LOG_TAG, "disconnect camera with id:" + mCameraDevice!!.getId())
//                mCameraDevice = null
//            }
//
//            override fun onError(camera: CameraDevice, error: Int) {
//                Log.i(LOG_TAG, "error! camera id:" + camera.getId() + " error:" + error)
//            }
//        }
//        val isOpen: Boolean
//            get() {
//                return mCameraDevice != null
//            }
//
//        init {
//            mCameraManager = cameraManager
//            mCameraID = cameraID
//        }
//
//        private fun createCameraPreviewSession() {
//            val texture = scanner_view.getSurfaceTexture()
//            texture.setDefaultBufferSize(1920, 1080)
//            val surface = Surface(texture)
//            try {
//                val builder = mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
//                builder?.addTarget(surface)
//                mCameraDevice?.createCaptureSession(
//                    listOf(surface),
//                    object : CameraCaptureSession.StateCallback() {
//                        override fun onConfigured(session: CameraCaptureSession) {
//                            mCaptureSession = session
//                            try {
//                                mCaptureSession.setRepeatingRequest(builder!!.build(), null, null)
//                            } catch (e: CameraAccessException) {
//                                e.printStackTrace()
//                            }
//                        }
//
//                        override fun onConfigureFailed(session: CameraCaptureSession) {}
//                    }, null
//                )
//            } catch (e: CameraAccessException) {
//                e.printStackTrace()
//            }
//        }
//
//        fun openCamera() {
//            try {
//                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//                    mCameraManager?.openCamera(mCameraID, mCameraCallback, null)
//                }
//            } catch (e: CameraAccessException) {
////                Log.i(LOG_TAG, e.getMessage())
//            }
//        }
//
//        fun closeCamera() {
//            if (mCameraDevice != null) {
//                mCameraDevice!!.close()
//                mCameraDevice = null
//            }
//        }
//    }
//
//    override fun getLayoutId(): Int {
//        return R.layout.camera
//    }
//
//    override fun onPause() {
//        if (myCameras!![CAMERA1].isOpen) {
//            myCameras!![CAMERA1].closeCamera()
//        }
//        if (myCameras!![CAMERA2].isOpen) {
//            myCameras!![CAMERA2].closeCamera()
//        }
//        super.onPause()
//    }
//
//    companion object {
//        fun open(context: Context) {
//            val intent = Intent(context, TakePictureActivity2::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            context.startActivity(intent)
//        }
//
//        val LOG_TAG = "myLogs"
//    }
//}