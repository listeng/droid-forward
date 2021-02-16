package com.ester.remotetrigger.activity

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Build
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import android.content.Intent
import android.preference.PreferenceManager
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
        startService(startServiceIntent)

        btSettings.setOnClickListener {
            var intent : Intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        val sp = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        swFTQQ.setOnClickListener {
            val edit = sp.edit()
            edit.putBoolean("send.ftqq", swFTQQ.isChecked)
            edit.apply()
        }

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

        swFTQQ.isChecked = sp.getBoolean("send.ftqq", false)
        swEmail.isChecked = sp.getBoolean("send.email", false)
        swBark.isChecked = sp.getBoolean("send.bark", false)
    }

    private fun setupPermissions() {
        val permissions = arrayOf(
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE,
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, REQUEST_CODE)
            }
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
