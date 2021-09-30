package co.tpcreative.saveyourvoicemails.common.services
import android.Manifest
import android.app.*
import android.content.Intent
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.MediaRecorder
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import co.tpcreative.saveyourvoicemails.Navigator
import co.tpcreative.saveyourvoicemails.common.Constant
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.helper.NotificationBarHelper
import co.tpcreative.saveyourvoicemails.common.helper.ServiceHelper
import co.tpcreative.saveyourvoicemails.ui.share.ShareAct
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.naman14.androidlame.AndroidLame
import com.naman14.androidlame.LameBuilder
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class SaveYourVoiceMailsService : Service() {
    private val mBinder = LocalBinder() // Binder given to clients
    private val TAG = this::class.java

    private var minBuffer = 0
    private val inSampleRate = 44100
    private var isRecording = false
    private lateinit var audioRecord: AudioRecord
    private lateinit var androidLame: AndroidLame
    var outputStream: FileOutputStream? = null
    private var file = ""
    private var recordingThread: Thread? = null


    override fun onBind(intent: Intent?): IBinder {
       return  mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val phoneNumber = intent?.getStringExtra(Constant.ACTION.PHONE_CALL_NUMBER)
        val outputPath = intent?.getStringExtra(Constant.ACTION.CALL_RECORD_PATH)
        try {
            if (outputPath != null && phoneNumber!=null){
                val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0
                )
                audioManager.setStreamVolume(
                    AudioManager.STREAM_VOICE_CALL,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0
                )
                File(outputPath).createNewFile()
                isRecording = true
                file = outputPath
                recordingThread = Thread({ startRecording() }, "AudioRecorder Thread")
                recordingThread?.start()
                val notification =
                        NotificationBarHelper.getInstance().updatedNotificationBar()
                startForeground(Constant.ID_NOTIFICATION_FOREGROUND_SERVICE, notification)
                Utils.log(TAG, "start record $file")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Utils.log(TAG, "Please turn on voice mal")
        }

        when(intent?.action){
            Constant.ACTION.STOP_RECORDING_PHONE_CALL -> {
                isRecording = false
                recordingThread?.interrupt()
                exitApp()
            }
            Constant.ACTION.START_HOME -> {
               Navigator.moveToMain(this)
            }
        }
        return START_NOT_STICKY
    }

    inner class LocalBinder : Binder() {
        // Return this instance of SignalRService so clients can call public methods
        val service: SaveYourVoiceMailsService
            get() = this@SaveYourVoiceMailsService
    }

    override fun onCreate() {
        super.onCreate()
        log("Created service")
        val notification = NotificationBarHelper.getInstance().createNotificationBar()
        startForeground(Constant.ID_NOTIFICATION_FOREGROUND_SERVICE, notification)
    }

    fun exitApp(){
        stopForeground(true)
        stopSelf()
        ServiceHelper.getInstance().onStopService()
    }

    fun stopRecord(){

    }
    fun pauseRecord(){

    }
    fun resumeRecord(){

    }

    override fun onDestroy() {
        super.onDestroy()
        isRecording = false
        recordingThread?.interrupt()
        sendIntent()
        log("onDestroy")
    }

    private fun startRecording() {
        minBuffer = AudioRecord.getMinBufferSize(
            inSampleRate, AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        Utils.log(TAG, "Initialising audio recorder..")
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.VOICE_RECOGNITION, inSampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT, minBuffer * 2
        )

        //5 seconds data
        Utils.log(
            TAG, "creating short buffer array"
        )
        val buffer = ShortArray(inSampleRate * 2 * 5)

        // 'mp3buf' should be at least 7200 bytes long
        // to hold all possible emitted data.
        Utils.log(TAG, "creating mp3 buffer")
        val mp3buffer = ByteArray((7200 + buffer.size * 2 * 1.25).toInt())
        try {
            outputStream = FileOutputStream(File(file))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        Utils.log(TAG, "Initialising Andorid Lame")
        androidLame = LameBuilder()
            .setInSampleRate(inSampleRate)
            .setOutChannels(1)
            .setOutBitrate(128)
            .setOutSampleRate(inSampleRate)
            .setScaleInput(30f)
            .build()
        Utils.log(TAG, "started audio recording")
        audioRecord.startRecording()
        var bytesRead = 0
        while (isRecording) {
            Utils.log(TAG, "reading to short array buffer, buffer sze- $minBuffer")
            bytesRead = audioRecord.read(buffer, 0, minBuffer)
            Utils.log(
                TAG, "bytes read=$bytesRead"
            )
            if (bytesRead > 0) {
                Utils.log(TAG, "encoding bytes to mp3 buffer..")
                val bytesEncoded = androidLame.encode(buffer, buffer, bytesRead, mp3buffer)
                Utils.log(TAG, "bytes encoded=$bytesEncoded")
                if (bytesEncoded > 0) {
                    try {
                        Utils.log(
                            TAG, "writing mp3 buffer to outputstream with $bytesEncoded bytes"
                        )
                        outputStream!!.write(mp3buffer, 0, bytesEncoded)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        Utils.log(TAG, "stopped recording")
        Utils.log(TAG, "Recording stopped")
        Utils.log(TAG, "flushing final mp3buffer")
        val outputMp3buf = androidLame.flush(mp3buffer)
        Utils.log(TAG, "flushed $outputMp3buf bytes")
        if (outputMp3buf > 0) {
            try {
                Utils.log(TAG, "writing final mp3buffer to outputstream")
                outputStream!!.write(mp3buffer, 0, outputMp3buf)
                Utils.log(TAG, "closing output stream")
                outputStream!!.close()
                Utils.log(TAG, "Output recording saved in $file")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        Utils.log(TAG, "releasing audio recorder")
        audioRecord.stop()
        audioRecord.release()
        Utils.log(TAG, "closing android lame")
        androidLame.close()
    }

    private fun sendIntent(){
        val intent = Intent(this, ShareAct::class.java)
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(file)));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}

fun SaveYourVoiceMailsService.log(message: Any){
   Utils.log(this::class.java, message)
}