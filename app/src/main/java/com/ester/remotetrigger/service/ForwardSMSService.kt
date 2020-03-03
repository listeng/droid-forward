package com.ester.remotetrigger.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.provider.Telephony
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.support.v4.app.NotificationCompat
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import com.ester.remotetrigger.config.Constants
import com.ester.remotetrigger.R
import com.ester.remotetrigger.activity.MainActivity

class ForwardSMSService : Service() {
    private val LOG_TAG = "ForwardSMSService"

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == action) {
                Log.i(LOG_TAG, "on receive," + intent.action!!)
                if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {
                    for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {

                        val messageBody = "短信 - " + smsMessage.messageBody
                        val tel = smsMessage.originatingAddress.replace("+86", "")
                        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                        val curDate = Date(System.currentTimeMillis())
                        val now = formatter.format(curDate)
                        val name = getPeople(tel)
                        val deviceid = getMyNumber()

                        Log.i(LOG_TAG, "body: " + messageBody)
                        Log.i(LOG_TAG, "address: " + tel)
                        Log.i(LOG_TAG, "contact: " + name)

                        val text=String.format("%s，来自：%s", messageBody, tel)
                        val desp=String.format(
                                        "%s<br/> 来自：%s（%s） <br/> 时间：%s<br/>  设备：%s<br/>",
                                        messageBody, tel, name, now, deviceid)

                        ForwardService(applicationContext).Send(text, desp)
                    }
                }
            } else {
                val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
                Log.d(LOG_TAG, "PhoneStateReceiver onReceive state: " + state)

                if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                    // 获取电话号码
                    val tel = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                    Log.d(LOG_TAG, "PhoneStateReceiver onReceive extraIncomingNumber: " + tel)
                    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                    val curDate = Date(System.currentTimeMillis())
                    val now = formatter.format(curDate)
                    val name = getPeople(tel)
                    val deviceid = getMyNumber()

                    val apiService = ApiService.create()
                    val text=String.format("%s 打入电话", tel)
                    val desp=String.format(
                                    "电话：%s（%s）<br/>  时间：%s<br/>  设备：%s<br/>",
                                    tel, name, now, deviceid)

                    ForwardService(applicationContext).Send(text, desp)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getMyNumber(): String {
        val id: String
        val mTelephony: TelephonyManager = this.applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        id = mTelephony.getLine1Number()
        return id
    }

    fun getPeople(tel: String): String {
        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER)

        val cursor = this.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, // Which columns to return.
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = '" + tel + "'",
                null,
                null)// WHERE clause.

        if (cursor == null) {
            Log.d(LOG_TAG, "tel of contact no found!")
            return "未知联系人"
        }
        Log.d(LOG_TAG, "contact cursor.getCount() = " + cursor.count)
        for (i in 0 until cursor.count) {
            cursor.moveToPosition(i)

            // 取得联系人名字
            val nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)
            val name = cursor.getString(nameFieldColumnIndex)
            Log.i("Contacts", "" + name + " .... " + nameFieldColumnIndex) // 这里提示 force close

            cursor.close()

            return name
        }

        cursor.close()

        return "未知联系人"
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.action == Constants.ACTION.STARTFOREGROUND_ACTION) {
            Log.i(LOG_TAG, "Received Start Foreground Intent ")

            val notificationIntent = Intent(this, MainActivity::class.java)
            notificationIntent.action = Constants.ACTION.MAIN_ACTION
            notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

            val icon = BitmapFactory.decodeResource(resources,
                    R.mipmap.ic_launcher)

            val notification = NotificationCompat.Builder(this, "smsforward")
                    .setContentTitle("事件触发器")
                    .setTicker("RemoteTrigger")
                    .setContentText("持续运行，请勿关闭")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .build()
            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification)
        } else if (intent.action == Constants.ACTION.STOPFOREGROUND_ACTION) {
            Log.i(LOG_TAG, "Received Stop Foreground Intent")
            stopForeground(true)
            stopSelf()
        }
        return Service.START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter()
        filter.addAction("android.provider.Telephony.SMS_RECEIVED")
        filter.addAction("android.intent.action.PHONE_STATE")
        registerReceiver(receiver, filter)
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
