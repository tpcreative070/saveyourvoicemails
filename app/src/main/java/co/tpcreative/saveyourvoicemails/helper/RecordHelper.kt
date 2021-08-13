package co.tpcreative.saveyourvoicemails.helper
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import com.github.squti.androidwaverecorder.RecorderState
import com.github.squti.androidwaverecorder.WaveRecorder
import org.greenrobot.eventbus.EventBus
import java.util.*
import java.util.concurrent.TimeUnit

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

    private lateinit var waveRecorder: WaveRecorder
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
            val mResult = formatTimeUnit(it*1000)
            EventBus.getDefault().post(mResult)
        }
        log("init data")
    }

    fun startRecording(){
        waveRecorder.startRecording()
    }

    fun stopRecording(){
        waveRecorder.stopRecording()
    }

    fun pauseRecording(){
        waveRecorder.pauseRecording()
    }

    private fun response(message : String){
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

fun RecordHelper.log(message : Any){
    Utils.log(this::class.java,message)
}