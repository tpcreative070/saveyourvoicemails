package co.tpcreative.presentation.common
import android.annotation.SuppressLint
import android.util.Log
import co.tpcreative.presentation.BuildConfig
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