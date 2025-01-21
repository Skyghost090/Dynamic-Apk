package com.dynamicapk

import android.app.TimePickerDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.tabs.TabLayout
import com.jaredrummler.ktsh.Shell

class MainActivity : AppCompatActivity() {
    lateinit var idtext: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val tablayout = findViewById<TabLayout>(R.id.tabLayout)
        val titletext = findViewById<TextView>(R.id.titleText)
        val btn = findViewById<Button>(R.id.button)
        val timecalendar = Calendar.getInstance()
        val hometext = findViewById<TextView>(R.id.homeText)
        val edittext = findViewById<EditText>(R.id.editTextText)

        if ((resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES){
            hometext.setBackgroundResource(R.drawable.textview)
            edittext.setBackgroundResource(R.drawable.textview)
        } else {
            hometext.setBackgroundResource(R.drawable.textviewlight)
            edittext.setBackgroundResource(R.drawable.textviewlight)
        }

        val serviceIntent = Intent(this, uninstallerService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)

        Shell.SU.run("echo oi")

        btn.setOnClickListener{
            idtext = edittext.text.toString()
            val urlIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=${edittext.text}")
            )
            startActivityForResult(urlIntent, 100)
        }

        fun countDigitis(Number_: Int): Int {
            var Counter = Number_
            var digits = 0
            while (Counter != 0) {
                Counter /= 10
                ++digits
            }
            return digits
        }

        fun detectTab() {
            when(tablayout.selectedTabPosition) {
                1 -> {
                    val hour = timecalendar.get(Calendar.HOUR_OF_DAY)
                    val minute = timecalendar.get(Calendar.MINUTE)
                    val timePickerDialog = TimePickerDialog(
                        this,
                        { view, hourOfDay, minute ->
                            val sharedPrefs = getSharedPreferences("UninstallTime", MODE_PRIVATE)
                            val tasksPrefs = sharedPrefs.edit()
                            var hourstring = ""
                            var minutestring = ""
                            Toast.makeText(this,"${countDigitis(hourOfDay)}", Toast.LENGTH_SHORT).show()
                            if (countDigitis(hourOfDay) == 0 || countDigitis(hourOfDay) == 1){
                                hourstring = "0$hourOfDay"
                            } else {
                                hourstring = "$hourOfDay"
                            }
                            if (countDigitis(minute) == 0 || countDigitis(minute) == 1) {
                                minutestring = "0$minute"
                            } else {
                                minutestring = "$minute"
                            }
                            tasksPrefs.putString("time","$hourstring:$minutestring")
                            tasksPrefs.apply()
                        },
                        hour,
                        minute,
                        true
                    )
                    timePickerDialog.show()
                    tablayout.selectTab(tablayout.getTabAt(0))
                }
                2 -> {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("About")
                        .setMessage(R.string.motivation_text)
                        .setNeutralButton("Github"){ dialog, id ->
                            val builder = CustomTabsIntent.Builder()
                            builder.setInstantAppsEnabled(true)
                            builder.setDownloadButtonEnabled(false)
                            val customBuilder = builder.build()
                            customBuilder.intent.setPackage("com.android.chrome")
                            customBuilder.launchUrl(this, Uri.parse("https://github.com/Skyghost090"))
                        }
                        .setOnCancelListener {
                            tablayout.selectTab(tablayout.getTabAt(0))
                        }

                    builder.show()
                }
            }
        }

        detectTab()

        tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {detectTab()}
            override fun onTabUnselected(tab: TabLayout.Tab) {detectTab()}
            override fun onTabReselected(tab: TabLayout.Tab) {detectTab()}
        })
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 100){
            val sharedPrefs = getSharedPreferences("AppId", MODE_PRIVATE)
            val tasksPrefs = sharedPrefs.edit()
            tasksPrefs.putString("id", idtext)
            tasksPrefs.apply()
            Toast.makeText(this,"command",Toast.LENGTH_SHORT).show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}