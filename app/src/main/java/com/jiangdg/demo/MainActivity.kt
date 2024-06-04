/*
 * Copyright 2017-2022 Jiangdg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jiangdg.demo

import android.Manifest.permission.CAMERA
import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import com.gyf.immersionbar.ImmersionBar
import com.jiangdg.ausbc.utils.ToastUtils
import com.jiangdg.ausbc.utils.Utils
import com.jiangdg.demo.databinding.ActivityMainBinding
import com.jiangdg.yolov8.Yolov8Fragment
import com.jiangdg.yolov8.Yolov8Ncnn

/**
 * Demos of camera usage
 *
 * @author Created by jiangdg on 2021/12/27
 */
class MainActivity : AppCompatActivity() {
    private var mWakeLock: PowerManager.WakeLock? = null
    private var immersionBar: ImmersionBar? = null
    private lateinit var viewBinding: ActivityMainBinding
    
    // 2024.05.28
    private var spinnerModel: Spinner? = null
    private var spinnerCPUGPU: Spinner? = null
    private var current_model = 0
    private var current_cpugpu = 0
    val yolov8ncnn = Yolov8Ncnn()
    // 2024.05.28
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBar()
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
//        replaceDemoFragment(DemoMultiCameraFragment())
//        replaceDemoFragment(DemoFragment())
//        replaceDemoFragment(GlSurfaceFragment())
        
        replaceDemoFragment(Yolov8Fragment())
        
        yolov8ncnnLoadModel()
    }
    
    private fun yolov8ncnnLoadModel()
    {
        
        val ret_init: Boolean = yolov8ncnn.loadModel(assets, current_model, current_cpugpu)
        if(!ret_init)
        {
            Log.e("MainActivity", "yolov8ncnn loadModel failed")
        }
    }
    
    override fun onStart() {
        super.onStart()
        mWakeLock = Utils.wakeLock(this)
    }

    override fun onStop() {
        super.onStop()
        mWakeLock?.apply {
            Utils.wakeUnLock(this)
        }
    }
    private fun replaceDemoFragment(fragment: Fragment) {
        val hasCameraPermission = PermissionChecker.checkSelfPermission(this, CAMERA)
        val hasStoragePermission =
            PermissionChecker.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
        if (hasCameraPermission != PermissionChecker.PERMISSION_GRANTED || hasStoragePermission != PermissionChecker.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, CAMERA)) {
                ToastUtils.show(R.string.permission_tip)
            }
            ActivityCompat.requestPermissions(
                this,
                arrayOf(CAMERA, WRITE_EXTERNAL_STORAGE, RECORD_AUDIO),
                REQUEST_CAMERA
            )
            return
        }
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commitAllowingStateLoss()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA -> {
                val hasCameraPermission = PermissionChecker.checkSelfPermission(this, CAMERA)
                if (hasCameraPermission == PermissionChecker.PERMISSION_DENIED) {
                    ToastUtils.show(R.string.permission_tip)
                    return
                }
//                replaceDemoFragment(DemoMultiCameraFragment())
                replaceDemoFragment(DemoFragment())
//                replaceDemoFragment(GlSurfaceFragment())
            }
            REQUEST_STORAGE -> {
                val hasCameraPermission =
                    PermissionChecker.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
                if (hasCameraPermission == PermissionChecker.PERMISSION_DENIED) {
                    ToastUtils.show(R.string.permission_tip)
                    return
                }
                // todo
            }
            else -> {
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar= null
    }

    private fun setStatusBar() {
        immersionBar = ImmersionBar.with(this)
            .statusBarDarkFont(false)
            .statusBarColor(R.color.black)
            .navigationBarColor(R.color.black)
            .fitsSystemWindows(true)
            .keyboardEnable(true)
        immersionBar?.init()
    }

    companion object {
        private const val REQUEST_CAMERA = 0
        private const val REQUEST_STORAGE = 1
    }
}