package com.yuk.miuihome.module

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.yuk.miuihome.utils.Config
import com.yuk.miuihome.utils.LogUtil
import com.yuk.miuihome.utils.OwnSP
import com.yuk.miuihome.utils.ktx.callMethod
import com.yuk.miuihome.utils.ktx.findClass
import com.yuk.miuihome.utils.ktx.getObjectField
import com.yuk.miuihome.utils.ktx.hookAfterMethod
import kotlin.properties.Delegates

class ModifyIconTitleFontColor {

    fun init() {
        val value = OwnSP.ownSP.getFloat("iconTitleFontColor", -1f)
        val launcherClass = "com.miui.home.launcher.Launcher".findClass()
        val shortcutInfoClass = "com.miui.home.launcher.ShortcutInfo".findClass()
        if (value == -1f) return
        var color by Delegates.notNull<Int>()
        when (value) {
            1f -> {
                color = Color.WHITE
            }
            2f -> {
                color = Color.BLACK
            }
            3f -> {
                color = Color.BLUE
            }
            4f -> {
                color = Color.RED
            }
            5f -> {
                color = Color.GREEN
            }
            6f -> {
                color = Color.YELLOW
            }
        }
        try {
            "com.miui.home.launcher.ItemIcon".hookAfterMethod(
                "onFinishInflate"
            ) {
                val mTitle = it.thisObject.getObjectField("mTitle") as TextView
                mTitle.setTextColor(color)
            }
            "com.miui.home.launcher.maml.MaMlWidgetView".hookAfterMethod(
                "onFinishInflate"
            ) {
                val mTitle = it.thisObject.getObjectField("mTitleTextView") as TextView
                mTitle.setTextColor(color)
            }
            "com.miui.home.launcher.LauncherMtzGadgetView".hookAfterMethod(
                "onFinishInflate"
            ) {
                val mTitle = it.thisObject.getObjectField("mTitleTextView") as TextView
                mTitle.setTextColor(color)
            }
            "com.miui.home.launcher.LauncherWidgetView".hookAfterMethod(
                "onFinishInflate"
            ) {
                val mTitle = it.thisObject.getObjectField("mTitleTextView") as TextView
                mTitle.setTextColor(color)
            }
            "com.miui.home.launcher.ShortcutIcon".hookAfterMethod(
                "fromXml",
                Int::class.javaPrimitiveType,
                launcherClass,
                ViewGroup::class.java,
                shortcutInfoClass
            ) {
                val buddyIconView = it.args[3].callMethod("getBuddyIconView", it.args[2]) as View
                val mTitle = buddyIconView.getObjectField("mTitle") as TextView
                mTitle.setTextColor(color)
            }
            "com.miui.home.launcher.ShortcutIcon".hookAfterMethod(
                "createShortcutIcon",
                Int::class.javaPrimitiveType,
                launcherClass,
                ViewGroup::class.java
            ) {
                val buddyIcon = it.result as View
                val mTitle = buddyIcon.getObjectField("mTitle") as TextView
                mTitle.setTextColor(color)
            }
            "com.miui.home.launcher.common.Utilities".hookAfterMethod(
                "adaptTitleStyleToWallpaper",
                Context::class.java,
                TextView::class.java,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType
            ) {
                val mTitle = it.args[1] as TextView
                if (mTitle.id == mTitle.resources.getIdentifier("icon_title", "id", Config.hookPackage))
                    mTitle.setTextColor(color)
            }
        } catch (e: Throwable) {
            LogUtil.e(e)
        }
    }
}