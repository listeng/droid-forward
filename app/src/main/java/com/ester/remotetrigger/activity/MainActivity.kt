package com.ester.remotetrigger.activity

import android.Manifest
import android.app.Activity
import android.app.role.RoleManager
import android.content.Context
import android.os.Bundle
import android.os.Build
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.widget.Toast
import android.content.Intent
import android.preference.PreferenceManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.ester.remotetrigger.config.Constants
import com.ester.remotetrigger.service.ForwardSMSService
import com.ester.remotetrigger.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var REQUEST_CODE = 88;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupPermissions()

        val startServiceIntent = Intent(this, ForwardSMSService::class.java)
        startServiceIntent.action = Constants.ACTION.STARTFOREGROUND_ACTION



        btSettings.setOnClickListener {
            val intent : Intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        val sp = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        swEmail.setOnClickListener {
            val edit = sp.edit()
            edit.putBoolean("send.email", swEmail.isChecked)
            edit.apply()
        }

        swBark.setOnClickListener {
            val edit = sp.edit()
            edit.putBoolean("send.bark", swBark.isChecked)
            edit.apply()
        }

        btStop.setOnClickListener {

            if (btStop.text == "停止服务") {
                stopService(startServiceIntent)
                trigger_text.text = "监听已停止！"

                btStop.text = "启动服务"
            } else {
                startForegroundService(startServiceIntent)
                trigger_text.text = "事件监听中……"

                btStop.text = "停止服务"
            }
        }

        swEmail.isChecked = sp.getBoolean("send.email", false)
        swBark.isChecked = sp.getBoolean("send.bark", false)
    }

    private fun setupPermissions() {
        val permissions = arrayOf(
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.INTERNET
        )

        var needPermissions = false
        for (x in permissions) {
            if (ActivityCompat.checkSelfPermission(this, x) != PackageManager.PERMISSION_GRANTED) {
                needPermissions = true
                break;
            }
        }

        if (needPermissions) {
            requestPermissions(permissions, REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "没有允许足够的授权，触发器无法执行 ${grantResults[0]}", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
