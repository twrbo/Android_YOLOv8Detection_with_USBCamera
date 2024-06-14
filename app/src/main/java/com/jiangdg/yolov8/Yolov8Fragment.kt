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
package com.jiangdg.yolov8

import android.content.res.AssetManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.jiangdg.ausbc.MultiCameraClient
import com.jiangdg.ausbc.base.CameraFragment
import com.jiangdg.ausbc.callback.ICameraStateCallBack
import com.jiangdg.ausbc.callback.IPreviewDataCallBack
import com.jiangdg.ausbc.utils.ToastUtils
import com.jiangdg.ausbc.widget.AspectRatioTextureView
import com.jiangdg.ausbc.widget.IAspectRatio
import com.jiangdg.demo.databinding.FragmentYolov8Binding


/** CameraFragment Usage Demo
 *
 * @author Created by jiangdg on 2022/1/28
 */
class Yolov8Fragment : CameraFragment(), IPreviewDataCallBack
{
    private lateinit var mViewBinding: FragmentYolov8Binding
    private val TEST_TAG = "TEST_TAG"
    val yolov8Ncnn = Yolov8Ncnn()
    private lateinit var assets: AssetManager
    private var currentModel = 0   // 0: n     1:s
    private var currentProcessor = 0  // 0: CPU   1:GPU
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        
        assets = requireContext().assets
        
        mViewBinding.spinnerModel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(arg0: AdapterView<*>?, arg1: View, position: Int, id: Long)
            {
                if(position != currentModel)
                {
                    currentModel = position
                    yolov8NcnnLoadModel()
                }
            }
            
            override fun onNothingSelected(arg0: AdapterView<*>?)
            {
            }
        }
        
        mViewBinding.spinnerProcessor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(arg0: AdapterView<*>?, arg1: View, position: Int, id: Long)
            {
                if(position != currentProcessor)
                {
                    Log.e("TEST", "onItemSelectedListener")
                    currentProcessor = position
                    yolov8NcnnLoadModel()
                }
            }
            
            override fun onNothingSelected(arg0: AdapterView<*>?)
            {
            }
        }
        
        yolov8NcnnLoadModel()
    }
    
    override fun onCameraState(self: MultiCameraClient.ICamera, code: ICameraStateCallBack.State, msg: String?)
    {
        addPreviewDataCallBack(this)
        when(code)
        {
            ICameraStateCallBack.State.OPENED -> handleCameraOpened()
            ICameraStateCallBack.State.CLOSED -> handleCameraClosed()
            ICameraStateCallBack.State.ERROR -> handleCameraError(msg)
        }
    }
    
    
    private fun handleCameraError(msg: String?)
    {
        ToastUtils.show("camera opened error: $msg")
    }
    
    private fun handleCameraClosed()
    {
        
        ToastUtils.show("camera closed success")
    }
    
    private fun handleCameraOpened()
    {
    
    }
    
    override fun getCameraView(): IAspectRatio
    {
        return AspectRatioTextureView(requireContext())
    }
    
    override fun getCameraViewContainer(): ViewGroup
    {
        return mViewBinding.cameraViewContainer
    }
    
    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View
    {
        mViewBinding = FragmentYolov8Binding.inflate(inflater, container, false)
        return mViewBinding.root
    }
    
    
    override fun getGravity(): Int = Gravity.CENTER
    
    
    override fun onPreviewData(data: ByteArray?, width: Int, height: Int, format: IPreviewDataCallBack.DataFormat)
    {
        data?.let {
            yolov8Ncnn.detectObjects(data, getCameraRequest().previewWidth, getCameraRequest().previewHeight)
//            yolov8Ncnn.setOutputWindow(mViewBinding.cameraSurfaceView.holder.surface)
//            yolov8Ncnn.setOutputWindow()
        }
    }
    
    private fun yolov8NcnnLoadModel()
    {
        val ret_init: Boolean = yolov8Ncnn.loadModel(assets, currentModel, currentProcessor)
        if(!ret_init)
        {
            Log.e("MainActivity", "yolov8Ncnn loadModel failed")
        }
    }
}
