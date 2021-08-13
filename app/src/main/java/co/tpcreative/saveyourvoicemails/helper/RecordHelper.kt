package co.tpcreative.saveyourvoicemails.helper
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import com.github.squti.androidwaverecorder.RecorderState
import com.github.squti.androidwaverecorder.WaveRecorder

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
}

fun RecordHelper.log(message : Any){
    Utils.log(this::class.java,message)
}