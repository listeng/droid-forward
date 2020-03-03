package com.ester.remotetrigger.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.SpannableStringBuilder
import com.ester.remotetrigger.R
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val sp = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        txtFTkey.text = SpannableStringBuilder(sp.getString("settings.ftkey", ""))
        txtSMTPHost.text = SpannableStringBuilder(sp.getString("settings.smtp_host", ""))
        txtSMTPUser.text = SpannableStringBuilder(sp.getString("settings.smtp_user", ""))
        txtSMTPPwd.text = SpannableStringBuilder(sp.getString("settings.smtp_pwd", ""))
        txtReceiveMail.text = SpannableStringBuilder(sp.getString("settings.smtp_receive", ""))

        btCancel.setOnClickListener {
            this.finish()
        }

        btSave.setOnClickListener {
            val edit = sp.edit()
            edit.putString("settings.ftkey", txtFTkey.text.toString())
            edit.putString("settings.smtp_host", txtSMTPHost.text.toString())
            edit.putString("settings.smtp_user", txtSMTPUser.text.toString())
            edit.putString("settings.smtp_pwd", txtSMTPPwd.text.toString())
            edit.putString("settings.smtp_receive", txtReceiveMail.text.toString())
            edit.apply()

            this.finish()
        }
    }
}
