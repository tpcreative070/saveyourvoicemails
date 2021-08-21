package co.tpcreative.saveyourvoicemails.common
import android.annotation.SuppressLint
import android.util.Log
import co.tpcreative.domain.models.User
import co.tpcreative.saveyourvoicemails.BuildConfig
import co.tpcreative.saveyourvoicemails.common.helper.AppPrefs
import com.google.gson.Gson

object Utils {

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
 }