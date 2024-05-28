package com.jiangdg.yolov8;

import android.content.res.AssetManager;
import android.view.Surface;

public class Yolov8Ncnn
{
    public native boolean loadModel(AssetManager mgr, int modelid, int cpugpu);
    public native boolean openCamera(int facing);
    public native boolean closeCamera();
    public native boolean setOutputWindow(Surface surface);

    public native Object[] detectObjects(byte[] imageData, int width, int height);

    public static class Yolov8Object {
        public int x;
        public int y;
        public int width;
        public int height;
        public int label;
        public float prob;

        public Yolov8Object(int x, int y, int width, int height, int label, float prob) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.label = label;
            this.prob = prob;
        }
    }

    static {
        System.loadLibrary("yolov8ncnn");
    }
}
