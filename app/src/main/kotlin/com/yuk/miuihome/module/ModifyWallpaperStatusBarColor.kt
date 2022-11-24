package com.yuk.miuihome.module

import android.util.Log
import com.yuk.miuihome.utils.OwnSP
import com.yuk.miuihome.utils.ktx.findClass
import com.yuk.miuihome.utils.ktx.hookAfterMethod

class ModifyWallpaperStatusBarColor {
    private val LOG_TAG = "miuihome"

    fun init() {
        val value = OwnSP.ownSP.getString("statusBarColorMode", "")
        if (value == "") return
        try{
            val intVal = value?.toInt();
        }
        catch (e:Throwable){
            return
        }
        try {
            "com.miui.home.launcher.wallpaper.DesktopWallpaperInfo".hookAfterMethod("getStatusBarColorMode"
            ) {
                val mode = (it.result);
                if(mode is Int){
                    Log.i(LOG_TAG, "getStatusBarColorMode:$mode");
                }
                val intVal = value?.toInt();
                it.result = intVal;
            }
            Log.i(LOG_TAG,"Hook finish");
        } catch (e: Throwable) {
            Log.e(LOG_TAG,e.toString());
        }
    }
}