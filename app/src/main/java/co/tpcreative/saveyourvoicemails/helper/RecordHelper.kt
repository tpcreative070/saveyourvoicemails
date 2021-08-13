package co.tpcreative.saveyourvoicemails.helper

import android.media.MediaRecorder
import android.widget.Chronometer
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import com.aykuttasil.callrecord.CallRecord
import com.aykuttasil.callrecord.CallRecord.Builder
import com.github.squti.androidwaverecorder.RecorderState
import com.github.squti.androidwaverecorder.WaveRecorder
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
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
    private lateinit var waveRecorder: WaveRecorder
    private lateinit var callRecord: CallRecord
    private lateinit var filePath: String
    init {
        init()
    }

    private fun init(){
        filePath = SaveYourVoiceMailsApplication.getInstance().externalCacheDir?.absolutePath + "/audioFile001.wav"
        waveRecorder = WaveRecorder(filePath)
        waveRecorder.onStateChangeListener = {
            when (it) {
                RecorderState.RECORDING -> response("recording")
                RecorderState.STOP -> response("stop")
                RecorderState.PAUSE -> response("pause")
            }
        }
        waveRecorder.onTimeElapsed = {
            log("onCreate: time elapsed $it")
            val mResult = formatTimeUnit(it * 1000)
            log(mResult)
        }

        //Phone call
        callRecord = Builder(SaveYourVoiceMailsApplication.getInstance())
            .setLogEnable(true)
            .setRecordFileName("audioFile002")
            .setRecordDirName("voice")
            .setRecordDirPath(SaveYourVoiceMailsApplication.getInstance().externalCacheDir?.absolutePath)
            .setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            .setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
            .setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
            .setShowSeed(true)
            .build()
        callRecord.enableSaveFile()
    }

    fun startRecording(){
        waveRecorder.startRecording()
        type = RecordType.DEFAULT
        path = SaveYourVoiceMailsApplication.getInstance().externalCacheDir?.absolutePath + "/audioFile001.wav"
    }

    fun stopRecording(){
        if (type ==RecordType.DEFAULT){
            waveRecorder.stopRecording()
        }else{
            callRecord.stopCallReceiver()
        }
    }

    fun startRecordPhoneCall(){
        callRecord.startCallRecordService()
        type = RecordType.PHONE_CALL
        path = SaveYourVoiceMailsApplication.getInstance().externalCacheDir?.absolutePath + "/voice/audioFile002.amr"
    }

    fun pauseRecording(){
        waveRecorder.pauseRecording()
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