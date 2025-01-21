package com.dynamicapk

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class bootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if(intent?.action == Intent.ACTION_BOOT_COMPLETED){
            val serviceIntent = Intent(context, uninstallerService::class.java)
            ContextCompat.startForegroundService(ww, serviceIntent)
        }
    }
}