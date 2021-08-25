package co.tpcreative.saveyourvoicemails.ui.player
import android.content.Context
import android.net.Uri
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.encrypt.EncryptedFileDataSourceFactory
import co.tpcreative.saveyourvoicemails.common.encrypt.EncryptedFileFromInternetDataSourceFactory
import co.tpcreative.saveyourvoicemails.common.encrypt.SecurityUtil
import co.tpcreative.saveyourvoicemails.common.helper.EncryptDecryptFilesHelper
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.crypto.AesCipherDataSink
import com.google.android.exoplayer2.util.Util
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class MediaPlayerImpl : MediaPlayer {

    companion object {
        private const val TAG = "MediaPlayerTag"
    }

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var context: Context
    private lateinit var mCipher: Cipher
    private lateinit var mSecretKeySpec: SecretKeySpec
    private lateinit var mIvParameterSpec: IvParameterSpec
    override fun play(url: String) {
        try {
            mCipher = EncryptDecryptFilesHelper.getInstance()?.getCipher(Cipher.DECRYPT_MODE)!!
            mSecretKeySpec = SecretKeySpec(EncryptDecryptFilesHelper.configurationFile?.secretKey, SecurityUtil.AES_ALGORITHM)
            mIvParameterSpec = IvParameterSpec(EncryptDecryptFilesHelper.configurationFile?.ivParameter)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val dsf = DefaultDataSourceFactory(context, Util.getUserAgent(context, "ExoPlayerInfo"))
        mCipher.init(Cipher.DECRYPT_MODE, mSecretKeySpec, mIvParameterSpec)
        val mediaSource = ProgressiveMediaSource.Factory(EncryptedFileFromInternetDataSourceFactory(dsf.createDataSource(), mCipher)).createMediaSource(MediaItem.fromUri(Uri.parse(url)))

//        val dataSourceFactory: DataSource.Factory = EncryptedFileDataSourceFactory(mCipher, mSecretKeySpec, mIvParameterSpec)
//        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(url)))

        //val userAgent = Util.getUserAgent(context, context.getString(R.string.record))
        //val mediaSource = ProgressiveMediaSource.Factory(DefaultDataSourceFactory(context,userAgent))
        //       .createMediaSource(MediaItem.fromUri(Uri.parse(url)))

        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    override fun getPlayerImpl(context: Context): ExoPlayer {
        this.context = context
        initializePlayer()
        return exoPlayer
    }

    override fun releasePlayer() {
        exoPlayer.stop()
        exoPlayer.release()
    }


    private fun initializePlayer() {
        val loadControl = DefaultLoadControl()
        val renderersFactory = DefaultRenderersFactory(context)
        exoPlayer = SimpleExoPlayer.Builder(context, renderersFactory).setLoadControl(loadControl).build()
    }
}