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

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Typeface
import android.hardware.usb.UsbDevice
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.children
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.jiangdg.ausbc.MultiCameraClient
import com.jiangdg.ausbc.base.BaseBottomDialog
import com.jiangdg.ausbc.base.CameraFragment
import com.jiangdg.ausbc.callback.ICameraStateCallBack
import com.jiangdg.demo.databinding.FragmentDemoBinding
import com.jiangdg.ausbc.callback.ICaptureCallBack
import com.jiangdg.ausbc.callback.IPlayCallBack
import com.jiangdg.ausbc.camera.CameraUVC
import com.jiangdg.ausbc.render.effect.EffectBlackWhite
import com.jiangdg.ausbc.render.effect.EffectSoul
import com.jiangdg.ausbc.render.effect.EffectZoom
import com.jiangdg.ausbc.render.effect.bean.CameraEffect
import com.jiangdg.ausbc.utils.*
import com.jiangdg.ausbc.utils.bus.BusKey
import com.jiangdg.ausbc.utils.bus.EventBus
import com.jiangdg.utils.imageloader.ILoader
import com.jiangdg.utils.imageloader.ImageLoaders
import com.jiangdg.ausbc.widget.*
import com.jiangdg.demo.EffectListDialog.Companion.KEY_ANIMATION
import com.jiangdg.demo.EffectListDialog.Companion.KEY_FILTER
import com.jiangdg.demo.databinding.DialogMoreBinding
import com.jiangdg.demo.databinding.FragmentYolov8Binding
import com.jiangdg.utils.MMKVUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/** CameraFragment Usage Demo
 *
 * @author Created by jiangdg on 2022/1/28
 */
class Yolov8Fragment : CameraFragment(), View.OnClickListener, CaptureMediaView.OnViewClickListener {

    private lateinit var mViewBinding: FragmentYolov8Binding
    
    override fun onCameraState(
        self: MultiCameraClient.ICamera,
        code: ICameraStateCallBack.State,
        msg: String?
    ) {
        when (code) {
            ICameraStateCallBack.State.OPENED -> handleCameraOpened()
            ICameraStateCallBack.State.CLOSED -> handleCameraClosed()
            ICameraStateCallBack.State.ERROR -> handleCameraError(msg)
        }
    }
    
    private fun handleCameraError(msg: String?)
    {
        ToastUtils.show("camera opened error: $msg")
    }
    
    private fun handleCameraClosed() {

        ToastUtils.show("camera closed success")
    }
    
    private fun handleCameraOpened() {

        ToastUtils.show("camera opened success")
    }
    
    override fun getCameraView(): IAspectRatio {
        return AspectRatioTextureView(requireContext())
    }
    
    override fun getCameraViewContainer(): ViewGroup {
        return mViewBinding.cameraViewContainer
    }
    
    override fun getRootView(inflater: LayoutInflater, container: ViewGroup?): View {
        mViewBinding = FragmentYolov8Binding.inflate(inflater, container, false)
        return mViewBinding.root
    }
    
    override fun getGravity(): Int = Gravity.CENTER
    
    
    override fun onClick(p0: View?)
    {
        TODO("Not yet implemented")
    }
    
    override fun onViewClick(mode: CaptureMediaView.CaptureMode?)
    {
        TODO("Not yet implemented")
    }
}
