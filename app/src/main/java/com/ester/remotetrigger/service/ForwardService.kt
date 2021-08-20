package com.ester.remotetrigger.service

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.ester.remotetrigger.config.BarkNotifyResult
import com.ester.remotetrigger.config.NotifyResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.URLEncoder
import java.util.*
import javax.mail.Address
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress

import javax.mail.internet.MimeMessage

class ForwardService(context: Context) {
    private var context: Context
    private val LOG_TAG = "ForwardService"

    init {
        this.context = context
    }

    fun Send(text:String, desp:String) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)

        if (sp.getBoolean("send.ftqq", false)) {
            try {
                SendFTQQ(text, desp, sp)

            } catch (e: Exception) {
            }
        }

        if (sp.getBoolean("send.email", false)) {
            try {
                SendEmail(text, desp, sp)
            } catch (e: Exception) {
            }
        }

        if (sp.getBoolean("send.bark", false)) {
            try {
                SendBark(text, desp, sp)
            } catch (e: Exception) {
            }
        }
    }

    fun SendEmail(text:String, desp:String, sp:SharedPreferences) {
        Log.d(LOG_TAG, "Notify send email")

        Thread {
            val properties = Properties()
            properties.setProperty("mail.smtp.auth", "true")
            properties.setProperty("mail.transport.protocol", "smtp")

            val session: Session = Session.getInstance(properties)
            val message: Message = MimeMessage(session)
            val me:String? = sp.getString("settings.smtp_user", "")
            val to:String? = sp.getString("settings.smtp_receive", "")
            val host:String? = sp.getString("settings.smtp_host", "")
            val user:String? = sp.getString("settings.smtp_user", "")
            val pwd:String? = sp.getString("settings.smtp_pwd", "")

            message.subject = "$text【转发提醒】"
            message.setText(desp)
            message.setFrom(InternetAddress(me))
            message.addRecipients(Message.RecipientType.TO, arrayOf<Address>(InternetAddress(to)))
            message.saveChanges()

            val transport: Transport = session.getTransport()
            transport.connect(
                    host,
                    25,
                    user,
                    pwd)
            transport.sendMessage(message, message.allRecipients)

            transport.close()
        }.start()
    }

    fun SendFTQQ(text:String, desp:String, sp:SharedPreferences) {
        Log.d(LOG_TAG, "Notify send ftqq")

        val apiService = FTQQApiService.create()
        val call = apiService.sendNotify(sp.getString("settings.ftkey", "") + ".send", text, desp)
        call.enqueue(object : Callback<NotifyResult> {
            override fun onResponse(call: Call<NotifyResult>?, response: Response<NotifyResult>?) {
                if (response?.code() == 200) {
                    Log.d(LOG_TAG, "Tel Notify sent ${response.body()?.status}")
                } else {
                    Log.d(LOG_TAG, "Tel Notify sent error ${response?.errorBody()}")
                }
            }

            override fun onFailure(call: Call<NotifyResult>?, t: Throwable?) {
            }
        })
    }

    fun SendBark(text:String, desp:String, sp:SharedPreferences) {
        Log.d(LOG_TAG, "Notify send bark")

        val apiService = BarkApiService.create()
        val key = sp.getString("settings.barkkey", "")

        if (key != null) {
            val call = apiService.sendNotify(key, text, desp)
            call.enqueue(object : Callback<BarkNotifyResult> {
                override fun onResponse(call: Call<BarkNotifyResult>?, response: Response<BarkNotifyResult>?) {
                    if (response?.code() == 200) {
                        Log.d(LOG_TAG, "Tel Bark Notify sent ${response.body()?.code}")
                    } else {
                        Log.e(LOG_TAG, "Tel Bark Notify sent error ${response?.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<BarkNotifyResult>, t: Throwable) {
                    Log.e(LOG_TAG, "Tel Bark Notify sent error ${t.message}")
                }
            })
        }
    }
}