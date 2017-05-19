package com.example.android.androidcamerademo;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Tommy on 2017/5/18.
 */

public class FlahlightDemo extends Activity implements View.OnClickListener {
    Camera camera;
    Button on,off;
    Context context;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flashlight_demo);
        on = (Button)findViewById(R.id.on);
        off = (Button)findViewById(R.id.off);
        context = getBaseContext();
        on.setOnClickListener(this);
        off.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Toast.makeText(getBaseContext(),"FLASＨLIGHT DISABLED",Toast.LENGTH_SHORT);
        }
        if (v.getId() == R.id.on){
            camera = Camera.open();
            Camera.Parameters p = camera.getParameters();
            p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(p);
            camera.startPreview();
            camera.autoFocus(new Camera.AutoFocusCallback() {
                public void onAutoFocus(boolean success, Camera camera) {
                }
            });
        }else if(v.getId() == R.id.off){
            camera.stopPreview();
            camera.release();
        }
    }
}
