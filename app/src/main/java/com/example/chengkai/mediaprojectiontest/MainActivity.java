package com.example.chengkai.mediaprojectiontest;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int LOCAL_REQUEST_CODE = 1;
    private SurfaceView surfaceView = null;
    private MediaProjectionManager mProjectionManager = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView ()
    {
        findViewById(R.id.StartRecordBT).setOnClickListener(onClickListener);
        findViewById(R.id.StopRecordBT).setOnClickListener(onClickListener);
        surfaceView = findViewById(R.id.surface_view);
    }
    private void initData()
    {
        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }
    private void startRecord()
    {
        if(mProjectionManager == null)
            return;
        startActivityForResult(mProjectionManager.createScreenCaptureIntent(),LOCAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case LOCAL_REQUEST_CODE:{
                Log.d(TAG, "onActivityResult:  resultCode = " + resultCode);
                if(resultCode == Activity.RESULT_OK){
                    Log.d(TAG, "onActivityResult:  resultCode RESULT_OK");
                    MediaProjection mediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
                    if (mediaProjection == null) {
                        Log.e(TAG, "media projection is null");
                        return;
                    }
                    File file = new File("/sdcard/xx.mp4");  //录屏生成文件
                    Log.d(TAG, "onActivityResult: getAbsolutePath == " + file.getAbsolutePath());
                    MediaRecordService mediaRecord = new MediaRecordService(surfaceView.getHolder().getSurface(), 1280, 720, 6000000, 1,
                            mediaProjection,file.getAbsolutePath());
                    mediaRecord.start();

//                    作者：charles0427
//                    链接：https://www.jianshu.com/p/8b313692ac85
//                    來源：简书
//                    著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
                }else{
                    Toast.makeText(getApplicationContext(),"用户拒绝了录屏请求",Toast.LENGTH_SHORT).show();
                }
                break;
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.StartRecordBT:{
                    startRecord();
                    break;
                }
                case R.id.StopRecordBT:{
                    break;
                }
                default:{
                    break;
                }
            }
        }
    };
}
