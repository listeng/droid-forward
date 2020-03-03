package com.ester.remotetrigger.config

/**
 * Created by tls on 2018/1/13.
 */
class Constants {
    interface ACTION {
        companion object {
            val MAIN_ACTION = "com.ester.remotetrigger.action.main"
            val STARTFOREGROUND_ACTION = "com.ester.remotetrigger.action.startforeground"
            val STOPFOREGROUND_ACTION = "com.ester.remotetrigger.action.stopforeground"
        }
    }

    interface NOTIFICATION_ID {
        companion object {
            val FOREGROUND_SERVICE = 101
        }
    }
}