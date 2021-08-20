package co.tpcreative.saveyourvoicemails.helper

import co.tpcreative.presentation.common.Utils
import java.util.*
import java.util.concurrent.TimeUnit


enum class RecordType {
    PHONE_CALL,
    DEFAULT
}
class RecordHelper {
    companion object{
        @Volatile private var INSTANCE: RecordHelper? = null
        fun  instance(): RecordHelper {
            return INSTANCE?: synchronized(this){
                RecordHelper().also {
                    INSTANCE = it
                }
            }
        }
    }

    private var type : RecordType = RecordType.DEFAULT
    var path : String = ""
    init {
        init()
    }

    private fun init(){
    }

    fun startRecording(){
    }

    fun stopRecording(){

    }

    fun startRecordPhoneCall(){

    }

    fun pauseRecording(){

    }

    private fun response(message: String){
        log(message)
    }

    private fun formatTimeUnit(timeInMilliseconds: Long): String {
        return try {
            String.format(
                Locale.getDefault(),
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds),
                TimeUnit.MILLISECONDS.toSeconds(timeInMilliseconds) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds)
                )
            )
        } catch (e: Exception) {
            "00:00"
        }
    }
}

fun RecordHelper.log(message: Any){
   Utils.log(this::class.java, message)
}