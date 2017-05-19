package com.example.android.androidcamerademo;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.io.IOException;
import java.util.List;

public class CameraAndFlashlight extends Activity {

    private  static final  String TAG = "MAINACTIVITY";
    boolean isPreview = false;
    boolean ledIsOn = false;
    Camera camera;
    SurfaceView surfaceView;
    Window window;
    Button torch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        window = getWindow();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.camera_flashlight);

        surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
       // surfaceView.setVisibility(View.INVISIBLE);
        torch = (Button)findViewById(R.id.torch);
        torch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ledIsOn){
                    try{
                        Camera.Parameters mParameters;
                        mParameters = camera.getParameters();
                        mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        camera.setParameters(mParameters);
                        ledIsOn =true;
                    } catch(Exception ex){ex.printStackTrace();}
                }else{
                    try{
                        Camera.Parameters mParameters;
                        mParameters = camera.getParameters();
                        mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        camera.setParameters(mParameters);
                        ledIsOn = false;
                    } catch(Exception ex){ex.printStackTrace();}
                }
            }
        });

        surfaceView.getHolder().setFixedSize(800, 480);
        //下面设置surfaceView不维护自己的缓冲区,而是等待屏幕的渲染引擎将内容推送到用户面前
        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceView.getHolder().addCallback(new SurfaceCallback());
    }


    public static void turnLightOn(Camera mCamera) {
        if (mCamera == null) {
            return;
        }

        Camera.Parameters parameters = mCamera.getParameters();

        if (parameters == null) {
            return;
        }
        List<String> flashModes = parameters.getSupportedFlashModes();
        // Check if camera flash exists
        if (flashModes == null) {
            // Use the screen as a flashlight (next best thing)
            return;
        }
        String flashMode = parameters.getFlashMode();
        if (!Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
            // Turn on the flash
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(parameters);
            }
        }
    }

    private class SurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera = Camera.open();//打开硬件摄像头，这里导包得时候一定要注意是android.hardware.Camera
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);//得到窗口管理器
                Display display = wm.getDefaultDisplay();//得到当前屏幕
                Camera.Parameters parameters = camera.getParameters();//得到摄像头的参数
                camera.setParameters(parameters);
                camera.setPreviewDisplay(surfaceView.getHolder());//通过SurfaceView显示取景画面
                camera.startPreview();//开始预览
                isPreview = true;//设置是否预览参数为真
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (camera != null) {
                if (isPreview) {//如果正在预览
                    camera.stopPreview();
                    camera.release();
                }
            }
        }
    }
}