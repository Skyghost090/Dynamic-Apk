package com.dynamicapk

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock.sleep
import android.util.Log
import android.widget.Toast
import com.jaredrummler.ktsh.Shell
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class uninstallerService : Service() {
    private fun run_(){
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val current = LocalDateTime.now().format(formatter)
        try {
            val uninstalltime = getSharedPreferences("UninstallTime", Context.MODE_PRIVATE)
            val uninstallapp = getSharedPreferences("AppId", Context.MODE_PRIVATE)
            var value_ = uninstalltime.all.values.toTypedArray()
            var appid = uninstallapp.all.values.toTypedArray()
            if (current == value_[0]){
                Toast.makeText(this, "App uninstalled", Toast.LENGTH_SHORT).show()
                Shell.SU.run("pm uninstall $appid")
            }
        } catch (e: Exception){
            Log.d("DEBUG", "Sharedprefs not created")
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { onTaskRemoved(it) }
        run_()
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        Thread{
            sleep(5000)
            val restartServiceIntent = Intent(applicationContext, this.javaClass)
            restartServiceIntent.setPackage(packageName)
            startService(restartServiceIntent)
            super.onTaskRemoved(rootIntent)
        }.start()
    }
}