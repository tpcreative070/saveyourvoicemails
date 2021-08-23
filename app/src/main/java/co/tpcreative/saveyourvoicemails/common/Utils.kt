package co.tpcreative.saveyourvoicemails.common
import android.annotation.SuppressLint
import android.app.Activity
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.core.content.ContextCompat
import co.tpcreative.domain.models.GitHubUser
import co.tpcreative.saveyourvoicemails.BuildConfig
import co.tpcreative.saveyourvoicemails.R
import com.google.gson.Gson
import com.tapadoo.alerter.Alerter

object Utils {
    const val CODE_EXCEPTION = 1111
     fun <T> log(clazz: Class<T>, content: Any?) {
         if (content is String) {
             logMessage(clazz.simpleName, content)
         } else {
             logMessage(clazz.simpleName, Gson().toJson(content))
         }
     }

     @SuppressLint("LogNotTimber")
     private fun logMessage(TAG: String, message: String?) {
         if (BuildConfig.DEBUG) {
             if (message != null) {
                 Log.d(TAG,message)
             }
         }
     }

    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target ?: "").matches()
    }

    fun onBasicAlertNotify(context: Activity, title: String? = "Warning", message: String) {
        Alerter.create(context)
            .setTitle(title!!)
            .setBackgroundColorInt(
                ContextCompat.getColor(context, R.color.colorAccent))
            .setText(message)
            .setDuration(1000)
            .show()
    }
 }