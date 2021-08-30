package co.tpcreative.saveyourvoicemails.common

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import co.tpcreative.saveyourvoicemails.common.view.progressing.SpotsDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SingletonManagerProcessing {
    private var dialog: AlertDialog? = null
    fun onStartProgressing(activity: Activity?, res: Int, listener : SingletonManagerProgressingListener? = null) = CoroutineScope(
        Dispatchers.Main).launch  {
        try {
            if (dialog == null) {
                dialog = SpotsDialog.Builder()
                    .setContext(activity)
                    .setDotColor(R.color.colorAccent)
                    .setTheme(R.style.CustomDialog)
                    .setMessage(SaveYourVoiceMailsApplication.getInstance().getString(res))
                    .setCancelable(true)
                    .setCancelListener { }
                    .build()
            }
            dialog?.let {
                if (!it.isShowing){
                    it.show()
                    listener?.onShow()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            clear()
        }
    }

    fun onStopProgressing()  = CoroutineScope(Dispatchers.Main).launch{
        try {
            dialog?.dismiss()
            dialog = null
        } catch (e: Exception) {
            clear()
        }
    }

    private fun clear(){
        instance = null
    }

    companion object {
        private val TAG = SingletonManagerProcessing::class.java.simpleName
        private var instance: SingletonManagerProcessing? = null
        fun getInstance(): SingletonManagerProcessing? {
            if (instance == null) {
                instance = SingletonManagerProcessing()
            }
            return instance
        }
    }
}

interface SingletonManagerProgressingListener {
    fun onShow()
}