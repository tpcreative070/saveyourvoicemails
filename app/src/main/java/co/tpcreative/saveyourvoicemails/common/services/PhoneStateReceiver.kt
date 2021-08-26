package co.tpcreative.saveyourvoicemails.common.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import co.tpcreative.saveyourvoicemails.common.Constant
import co.tpcreative.saveyourvoicemails.common.Utils

class PhoneStateReceiver : BroadcastReceiver() {
    private val TAG = PhoneStateReceiver::class.java
    override fun onReceive(context: Context, intent: Intent) {
        Utils.log(TAG, "PhoneStateReceiver - onReceive")
        try {
            val extras = intent.extras
            val state = extras!!.getString(TelephonyManager.EXTRA_STATE)
             if (state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                val phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                val recPath = startRecording(context, phoneNumber)
                Utils.log(TAG, "recPath : $recPath")
            } else if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                val phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                stopRecording(context, phoneNumber)
            }
            Utils.log(TAG, "PhoneStateReceiver - onReceive: $state")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startRecording(context: Context, number: String?): String {
        Utils.log(TAG, "startRecording - trimNumber: $number")

        val time: String? = Utils.getTime()
        val date: String? = Utils.getDate()
        val path: String? = Utils.getPath()
        val outputPath = path + "/" + Utils.getUserId() + "_" + time + ".mp3"
        //        String outputPath = path + "/" + trimNumber + "_" + time + ".mp4";
        Utils.log(TAG, "outputPath: $outputPath")
        val recordService = Intent(context, SaveYourVoiceMailsService::class.java)
        recordService.putExtra(Constant.ACTION.PHONE_CALL_NUMBER, number)
        recordService.putExtra(Constant.ACTION.CALL_RECORD_PATH, outputPath)
        context.startService(recordService)
        return outputPath
    }

    private fun stopRecording(context: Context, phoneNo: String?) {
        Utils.log(TAG, "stopRecording: $phoneNo")
        context.stopService(Intent(context, SaveYourVoiceMailsService::class.java))
    }
}
