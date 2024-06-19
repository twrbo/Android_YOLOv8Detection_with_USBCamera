package com.jiangdg.yolov8;

import android.content.res.AssetManager;
import android.view.Surface;

public class Yolov8Ncnn
{
    public native boolean loadModel(AssetManager mgr, int modelid, int cpugpu);
    public native boolean detectObjects(byte[] source, byte[] frameResult, int width, int height);

    static {
        System.loadLibrary("yolov8ncnn");
    }
}
