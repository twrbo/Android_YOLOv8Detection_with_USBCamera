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

import android.R
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.ImageView
import com.jiangdg.ausbc.MultiCameraClient
import com.jiangdg.ausbc.base.CameraFragment
import com.jiangdg.ausbc.callback.ICameraStateCallBack
import com.jiangdg.ausbc.callback.IPreviewDataCallBack
import com.jiangdg.ausbc.utils.ToastUtils
import com.jiangdg.ausbc.widget.AspectRatioTextureView
import com.jiangdg.ausbc.widget.IAspectRatio
import com.jiangdg.demo.databinding.FragmentYolov8Binding
import java.nio.ByteBuffer


/** CameraFragment Usage Demo
 *
 * @author Created by jiangdg on 2022/1/28
 */
class Yolov8Fragment : CameraFragment(), IPreviewDataCallBack
{
    private lateinit var viewBinding: FragmentYolov8Binding
    private val TEST_TAG = "TEST_TAG"
    private val LOG_TAG = "Yolov8Fragment"
    val yolov8Ncnn = Yolov8Ncnn()
    private lateinit var assets: AssetManager
    private var currentModel = 0   // 0: n     1:s
    private var currentProcessor = 0  // 0: GPU   1:CPU
    private lateinit var frameResult: ByteArray
    private lateinit var bitmap: Bitmap
    private var imageViewFrameResult: ImageView? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        
        assets = requireContext().assets
        
        viewBinding.spinnerModel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
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
        
        viewBinding.spinnerProcessor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(arg0: AdapterView<*>?, arg1: View, position: Int, id: Long)
            {
                if(position != currentProcessor)
                {
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
        return viewBinding.cameraViewContainer
    }
    
    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View
    {
        viewBinding = FragmentYolov8Binding.inflate(inflater, container, false)
        return viewBinding.root
    }
    
    
    override fun getGravity(): Int = Gravity.CENTER
    
    
    override fun onPreviewData(source: ByteArray?, width: Int, height: Int, format: IPreviewDataCallBack.DataFormat)
    {
        if(!::frameResult.isInitialized)
        {
            frameResult = ByteArray(width * height * 4)
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        }
        source?.let {
            if(yolov8Ncnn.detectObjects(source,frameResult, width, height))
            {
                bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(frameResult))
                // Update UI
                requireActivity().runOnUiThread {
                    if(imageViewFrameResult == null)
                    {
                        imageViewFrameResult = ImageView(requireContext()).apply {
                            viewBinding.cameraViewContainer.addView(this)
                        }
                    }
                    imageViewFrameResult?.setImageBitmap(bitmap)
                }
            }
            else
                Log.e("LOG_TAG","Failed - yolov8Ncnn detect objects ")
        }
    }
    
    private fun yolov8NcnnLoadModel()
    {
        if(!yolov8Ncnn.loadModel(assets, currentModel, currentProcessor))
        {
            Log.e("LOG_TAG", "Failed - yolov8Ncnn loadModel ")
        }
    }
}
