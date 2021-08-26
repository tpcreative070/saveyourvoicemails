package co.tpcreative.saveyourvoicemails.common
import android.annotation.SuppressLint
import android.app.Activity
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.webkit.MimeTypeMap
import androidx.core.content.ContextCompat
import co.tpcreative.domain.models.EnumFormatType
import co.tpcreative.domain.models.MimeTypeFile
import co.tpcreative.saveyourvoicemails.BuildConfig
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.extension.getSessionTokenObject
import co.tpcreative.saveyourvoicemails.common.extension.getUserInfo
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import com.google.gson.Gson
import com.tapadoo.alerter.Alerter
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    const val FORMAT_TIME: String = "yyyy-MM-dd HH:mm:ss"
    const val FORMAT_TIME_FILE_NAME: String = "yyyyMMdd_HHmmss"
    const val FORMAT_SERVER_DATE_TIME = "MM/dd/yyyy hh:mm:ss a"
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
                 Log.d(TAG, message)
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
                ContextCompat.getColor(context, R.color.colorAccent)
            )
            .setText(message)
            .setDuration(1000)
            .show()
    }


    fun getMimeType(url: String?): String? {
        var type: String? = null
        val extension: String? = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    fun getFileExtension(url: String?): String{
        return FilenameUtils.getExtension(url).toLowerCase(Locale.ROOT)
    }

    fun mediaTypeSupport(): HashMap<String, MimeTypeFile> {
        val hashMap = HashMap<String, MimeTypeFile>()
        hashMap["mp4"] = MimeTypeFile(".mp4", EnumFormatType.VIDEO, "video/mp4")
        hashMap["3gp"] = MimeTypeFile(".3gp", EnumFormatType.VIDEO, "video/3gp")
        hashMap["wmv"] = MimeTypeFile(".wmv", EnumFormatType.VIDEO, "video/wmv")
        hashMap["mkv"] = MimeTypeFile(".mkv", EnumFormatType.VIDEO, "video/mkv")
        hashMap["m4a"] = MimeTypeFile(".m4a", EnumFormatType.AUDIO, "audio/m4a")
        hashMap["aac"] = MimeTypeFile(".aac", EnumFormatType.AUDIO, "audio/aac")
        hashMap["mp3"] = MimeTypeFile(".mp3", EnumFormatType.AUDIO, "audio/mp3")
        hashMap["wav"] = MimeTypeFile(".wav", EnumFormatType.AUDIO, "audio/wav")
        hashMap["jpg"] = MimeTypeFile(".jpg", EnumFormatType.IMAGE, "image/jpeg")
        hashMap["jpeg"] = MimeTypeFile(".jpeg", EnumFormatType.IMAGE, "image/jpeg")
        hashMap["png"] = MimeTypeFile(".png", EnumFormatType.IMAGE, "image/png")
        hashMap["gif"] = MimeTypeFile(".gif", EnumFormatType.IMAGE, "image/gif")
        return hashMap
    }

    fun mimeTypeSupport(): HashMap<String, MimeTypeFile> {
        val hashMap = HashMap<String, MimeTypeFile>()
        hashMap["video/mp4"] = MimeTypeFile(".mp4", EnumFormatType.VIDEO, "video/mp4")
        hashMap["video/3gp"] = MimeTypeFile(".3gp", EnumFormatType.VIDEO, "video/3gp")
        hashMap["video/wmv"] = MimeTypeFile(".wmv", EnumFormatType.VIDEO, "video/wmv")
        hashMap["video/mkv"] = MimeTypeFile(".mkv", EnumFormatType.VIDEO, "video/mkv")
        hashMap["audio/m4a"] = MimeTypeFile(".m4a", EnumFormatType.AUDIO, "audio/m4a")
        hashMap["audio/aac"] = MimeTypeFile(".aac", EnumFormatType.AUDIO, "audio/aac")
        hashMap["audio/mp3"] = MimeTypeFile(".mp3", EnumFormatType.AUDIO, "audio/mp3")
        hashMap["audio/mpeg"] = MimeTypeFile(".mp3", EnumFormatType.AUDIO, "audio/mpeg")
        hashMap["audio/wav"] = MimeTypeFile(".wav", EnumFormatType.AUDIO, "audio/wav")
        hashMap["image/jpeg"] = MimeTypeFile(".jpg", EnumFormatType.IMAGE, "image/jpeg")
        hashMap["image/png"] = MimeTypeFile(".png", EnumFormatType.IMAGE, "image/png")
        hashMap["image/gif"] = MimeTypeFile(".gif", EnumFormatType.IMAGE, "image/gif")
        hashMap["application/msword"] = MimeTypeFile(
            ".doc",
            EnumFormatType.FILES,
            "application/msword"
        )
        hashMap["application/vnd.openxmlformats-officedocument.wordprocessingml.document"] = MimeTypeFile(
            ".docx",
            EnumFormatType.FILES,
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        )
        hashMap["application/vnd.openxmlformats-officedocument.wordprocessingml.template"] = MimeTypeFile(
            ".dotx",
            EnumFormatType.FILES,
            "application/vnd.openxmlformats-officedocument.wordprocessingml.template"
        )
        hashMap["application/vnd.ms-word.document.macroEnabled.12"] = MimeTypeFile(
            ".dotm",
            EnumFormatType.FILES,
            "application/vnd.ms-word.document.macroEnabled.12"
        )
        hashMap["application/vnd.ms-excel"] = MimeTypeFile(
            ".xls",
            EnumFormatType.FILES,
            "application/vnd.ms-excel"
        )
        hashMap["application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"] = MimeTypeFile(
            ".xlsx",
            EnumFormatType.FILES,
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        )
        hashMap["application/vnd.openxmlformats-officedocument.spreadsheetml.template"] = MimeTypeFile(
            ".xltx",
            EnumFormatType.FILES,
            "application/vnd.openxmlformats-officedocument.spreadsheetml.template"
        )
        hashMap["application/vnd.ms-excel.sheet.macroEnabled.12"] = MimeTypeFile(
            ".xlsm",
            EnumFormatType.FILES,
            "application/vnd.ms-excel.sheet.macroEnabled.12"
        )
        hashMap["application/vnd.ms-excel.template.macroEnabled.12"] = MimeTypeFile(
            ".xltm",
            EnumFormatType.FILES,
            "application/vnd.ms-excel.template.macroEnabled.12"
        )
        hashMap["application/vnd.ms-excel.addin.macroEnabled.12"] = MimeTypeFile(
            ".xlam",
            EnumFormatType.FILES,
            "application/vnd.ms-excel.addin.macroEnabled.12"
        )
        hashMap["application/vnd.ms-excel.sheet.binary.macroEnabled.12"] = MimeTypeFile(
            ".xlsb",
            EnumFormatType.FILES,
            "application/vnd.ms-excel.sheet.binary.macroEnabled.12"
        )
        hashMap["application/vnd.ms-powerpoint"] = MimeTypeFile(
            ".ppt",
            EnumFormatType.FILES,
            "application/vnd.ms-powerpoint"
        )
        hashMap["application/vnd.openxmlformats-officedocument.presentationml.presentation"] = MimeTypeFile(
            ".pptx",
            EnumFormatType.FILES,
            "application/vnd.openxmlformats-officedocument.presentationml.presentation"
        )
        hashMap["application/vnd.openxmlformats-officedocument.presentationml.template"] = MimeTypeFile(
            ".potx",
            EnumFormatType.FILES,
            "application/vnd.openxmlformats-officedocument.presentationml.template"
        )
        hashMap["application/vnd.ms-powerpoint.addin.macroEnabled.12"] = MimeTypeFile(
            ".ppsx",
            EnumFormatType.FILES,
            "application/vnd.ms-powerpoint.addin.macroEnabled.12"
        )
        hashMap["application/vnd.ms-powerpoint.presentation.macroEnabled.12t"] = MimeTypeFile(
            ".pptm",
            EnumFormatType.FILES,
            "application/vnd.ms-powerpoint.presentation.macroEnabled.12"
        )
        hashMap["application/vnd.ms-powerpoint.template.macroEnabled.12"] = MimeTypeFile(
            ".potm",
            EnumFormatType.FILES,
            "application/vnd.ms-powerpoint.template.macroEnabled.12"
        )
        hashMap["application/vnd.ms-powerpoint.slideshow.macroEnabled.12"] = MimeTypeFile(
            ".ppsm",
            EnumFormatType.FILES,
            "application/vnd.ms-powerpoint.slideshow.macroEnabled.12"
        )
        hashMap["application/vnd.ms-access"] = MimeTypeFile(
            ".mdb",
            EnumFormatType.FILES,
            "application/vnd.ms-access"
        )
        return hashMap
    }

    fun getUUId(): String? {
        return try {
            UUID.randomUUID().toString()
        } catch (e: Exception) {
            "" + System.currentTimeMillis()
        }
    }

    fun getUserId() : String? {
        val mUser =  getUserInfo()
        return mUser?.email
    }

    fun getUserUUID() : String? {
        val mUser =  getUserInfo()
        return mUser?.uuid
    }

    fun getSessionToken() : String?{
        val mSessionToken = getSessionTokenObject()
        log(this::class.java, mSessionToken)
        return mSessionToken?.session_token
    }

    fun getCurrentDateTime(): String? {
        val date = Date()
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        return dateFormat.format(date)
    }

    fun getCurrentDateTime(formatName: String?): String? {
        val date = Date()
        val dateFormat = SimpleDateFormat(formatName, Locale.getDefault())
        return dateFormat.format(date)
    }

    fun getCurrentDate(value: String?): String? {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        try {
            val mDate = sdf.parse(value ?: "")
            val dateFormat = SimpleDateFormat("EE dd MMM, yyyy", Locale.getDefault())
            return dateFormat.format(mDate ?: "")
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }

    private fun getCurrentDateTimeFormat(): String? {
        val date = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(date)
    }

    fun getDate(): String? {
        val cal = Calendar.getInstance()
        val year = cal[Calendar.YEAR]
        val month = cal[Calendar.MONTH] + 1
        val day = cal[Calendar.DATE]
        val date = "$day/$month/$year"
        return date
    }


    fun getTime(): String? {
        val cal = Calendar.getInstance()
        val sec = cal[Calendar.SECOND]
        val min = cal[Calendar.MINUTE]
        val hr = cal[Calendar.HOUR_OF_DAY]
        val time = hr.toString() + min.toString() + sec.toString()
        return time
    }

    fun getClearTime(): String? {
        val cal = Calendar.getInstance()
        val sec = cal[Calendar.SECOND]
        val min = cal[Calendar.MINUTE]
        val hr = cal[Calendar.HOUR_OF_DAY]
        val time = "$hr:$min:$sec"
        return time
    }

    fun getPath(): String? {
        val file =
            File(SaveYourVoiceMailsApplication.getInstance().getRecorder())
        if (!file.exists()) {
            file.mkdir()
        }
        return file.absolutePath
    }

 }