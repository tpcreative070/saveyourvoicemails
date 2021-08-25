package co.tpcreative.saveyourvoicemails.ui.player
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import co.tpcreative.domain.models.request.DownloadFileRequest
import co.tpcreative.saveyourvoicemails.common.ViewModelFactory
import co.tpcreative.saveyourvoicemails.common.base.BaseActivity
import co.tpcreative.saveyourvoicemails.common.base.log
import co.tpcreative.saveyourvoicemails.common.encrypt.EncryptedFileDataSourceFactory
import co.tpcreative.saveyourvoicemails.common.encrypt.SecurityUtil
import co.tpcreative.saveyourvoicemails.common.helper.EncryptDecryptFilesHelper
import co.tpcreative.saveyourvoicemails.common.services.DefaultServiceLocator
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import co.tpcreative.saveyourvoicemails.common.services.download.ProgressResponseBody
import co.tpcreative.saveyourvoicemails.databinding.ActivityPlayerBinding
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.gson.Gson
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class PlayerAct : BaseActivity() {

    companion object {
        const val AUDIO_URL_EXTRA = "audio_url_extra"
    }

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var mCipher: Cipher
    private lateinit var mSecretKeySpec: SecretKeySpec
    private lateinit var mIvParameterSpec: IvParameterSpec
    val viewModel : PlayerViewModel by viewModels {
        ViewModelFactory(DefaultServiceLocator.getInstance(SaveYourVoiceMailsApplication.getInstance()),mProgressListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        init()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        exoPlayer.stop()
        exoPlayer.release()
    }

    private fun init() {
        initializePlayer()
        val result = intent.getStringExtra(AUDIO_URL_EXTRA)
        val downloadRequest = Gson().fromJson(result,DownloadFileRequest::class.java)
        if (downloadRequest.isDownloaded){
            play(downloadRequest.fullLocalPath)
        }else{
            downloadFile(downloadRequest)
        }
    }

    fun play(url : String) {
        log(url)
        try {
            mCipher = EncryptDecryptFilesHelper.getInstance()?.getCipher(Cipher.DECRYPT_MODE)!!
            mSecretKeySpec = SecretKeySpec(EncryptDecryptFilesHelper.configurationFile?.secretKey, SecurityUtil.AES_ALGORITHM)
            mIvParameterSpec = IvParameterSpec(EncryptDecryptFilesHelper.configurationFile?.ivParameter)
        } catch (e: Exception) {
            e.printStackTrace()
        }

//        val dsf = DefaultDataSourceFactory(this, Util.getUserAgent(this, "ExoPlayerInfo"))
//        mCipher.init(Cipher.DECRYPT_MODE, mSecretKeySpec, mIvParameterSpec)
//        val mediaSource = ProgressiveMediaSource.Factory(EncryptedFileFromInternetDataSourceFactory(dsf.createDataSource(), mCipher)).createMediaSource(
//            MediaItem.fromUri(Uri.parse(url)))

        val dataSourceFactory: DataSource.Factory = EncryptedFileDataSourceFactory(mCipher, mSecretKeySpec, mIvParameterSpec)
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(url)))

        //val userAgent = Util.getUserAgent(context, context.getString(R.string.record))
        //val mediaSource = ProgressiveMediaSource.Factory(DefaultDataSourceFactory(context,userAgent))
        //       .createMediaSource(MediaItem.fromUri(Uri.parse(url)))

        binding.playerView.player = exoPlayer
        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    private fun initializePlayer() {
        val loadControl = DefaultLoadControl()
        val renderersFactory = DefaultRenderersFactory(this)
        exoPlayer = SimpleExoPlayer.Builder(this, renderersFactory).setLoadControl(loadControl).build()
    }

    private val mProgressListener = object : ProgressResponseBody.ProgressResponseBodyListener {
        override fun onAttachmentDownloadedSuccess() {
            log("onAttachmentDownloadedSuccess")
        }

        override fun onAttachmentDownloadedError(message: String?) {
            log("onAttachmentDownloadedSuccess $message")
        }

        override fun onAttachmentDownloadUpdate(percent: Int) {
            log("onAttachmentDownloadUpdate $percent")
        }

        override fun onAttachmentElapsedTime(elapsed: Long) {
            log("onAttachmentElapsedTime $elapsed")
        }

        override fun onAttachmentAllTimeForDownloading(all: Long) {
            log("onAttachmentAllTimeForDownloading $all")
        }

        override fun onAttachmentRemainingTime(all: Long) {

        }

        override fun onAttachmentSpeedPerSecond(all: Double) {

        }

        override fun onAttachmentTotalDownload(totalByte: Long, totalByteDownloaded: Long) {
            log("onAttachmentTotalDownload $totalByteDownloaded")
        }

    }

}
