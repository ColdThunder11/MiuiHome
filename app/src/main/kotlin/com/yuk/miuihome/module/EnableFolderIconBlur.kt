package com.yuk.miuihome.module

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.github.kyuubiran.ezxhelper.init.InitFields
import com.github.kyuubiran.ezxhelper.utils.*
import com.yuk.miuihome.utils.Config
import com.yuk.miuihome.utils.OwnSP
import com.yuk.miuihome.utils.ktx.dp2px
import com.zhenxiang.blur.WindowBlurFrameLayout
import com.zhenxiang.blur.model.CornersRadius
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

class EnableFolderIconBlur {

    fun init() {
        if (!OwnSP.ownSP.getBoolean("folderBlur", false) || Build.VERSION.SDK_INT < 31) return
        XposedHelpers.findAndHookMethod( // TODO
            "com.miui.home.launcher.Launcher",
            InitFields.ezXClassLoader,
            "onCreate",
            Bundle::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val activity = param.thisObject as Activity
                    val view = activity.findViewById(activity.resources.getIdentifier("recents_container", "id", Config.hostPackage)) as View
                    val isFolderShowing = activity.invokeMethodAuto("isFolderShowing") as Boolean
                    val isInEditing = activity.invokeMethodAuto("isInEditing") as Boolean
                    val value = OwnSP.ownSP.getFloat("folderBlurCorner", 40f)
                    findMethod(loadClass("com.miui.home.launcher.FolderIcon"), true) {
                        name == "onFinishInflate"
                    }.hookAfter {
                        val mIconImageView = it.thisObject.getObjectAs<ImageView>("mIconImageView")
                        val mIconContainer = mIconImageView.parent as FrameLayout
                        val child0 = mIconContainer.getChildAt(0) as ImageView //LauncherIconImageView -> 文件夹的背景
                        val child2 = mIconContainer.getChildAt(2) as ImageView //LauncherIconImageView -> 不知道是啥
                        val blur = WindowBlurFrameLayout(mIconContainer.context)
                        blur.blurController.apply {
                            backgroundColour = Color.parseColor("#44FFFFFF")
                            cornerRadius = CornersRadius.all(value)
                        }
                        mIconContainer.removeView(child0)
                        mIconContainer.removeView(child2)
                        val lp = child2.layoutParams as FrameLayout.LayoutParams
                        lp.setMargins(dp2px(-1.5f), dp2px(-1.5f), dp2px(-1.5f), dp2px(-1.5f))
                        blur.addView(child2)
                        if (view.visibility == View.GONE && !isInEditing && !isFolderShowing) mIconContainer.addView(blur, 0)
                    }
                }
            })
    }
}