package com.example.chengkai.mediaprojectiontest;

import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.util.Log;
import android.view.Surface;

/**
 * Created by chengkai on 18-1-15.
 */

public class MediaRecordService extends Thread {
    private static final String TAG = "MediaRecordService";

    private int mWidth;
    private int mHeight;
    private int mBitRate;
    private int mDpi;
    private String mDstPath;
    private MediaRecorder mMediaRecorder;
    private MediaProjection mMediaProjection;
    private static final int FRAME_RATE = 60; // 60 fps

    private VirtualDisplay mVirtualDisplay;
    private Surface surface;
    public MediaRecordService(Surface surface,int width, int height, int bitrate, int dpi, MediaProjection mp, String dstPath) {
        mWidth = width;
        mHeight = height;
        mBitRate = bitrate;
        mDpi = dpi;
        mMediaProjection = mp;
        mDstPath = dstPath;
        this.surface = surface;
    }

    @Override
    public void run() {
        try {
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            mMediaRecorder.setOutputFile(mDstPath);
            mMediaRecorder.setVideoSize(mWidth, mHeight);
            mMediaRecorder.setVideoFrameRate(FRAME_RATE);
            mMediaRecorder.setVideoEncodingBitRate(mBitRate);
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            try {
                mMediaRecorder.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i(TAG, "media recorder" + mBitRate + "kps");
            //在mediarecorder.prepare()方法后调用
            mVirtualDisplay = mMediaProjection.createVirtualDisplay(
                    "MainScreen",
                    mWidth,
                    mHeight,
                    mDpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
//                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                    mMediaRecorder.getSurface(),
                    null, null);
            Log.i(TAG, "created virtual display: " + mVirtualDisplay);
            mMediaRecorder.start();
            Log.i(TAG, "mediarecorder start");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        release();
    }
    boolean flag = true;
    public void release() {
        flag = false;
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }
        if (mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);
            mMediaProjection.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
        }
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        Log.i(TAG, "release");
    }

//    作者：charles0427
//    链接：https://www.jianshu.com/p/8b313692ac85
//    來源：简书
//    著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
}
