package co.tpcreative.saveyourvoicemails.ui.player

import co.tpcreative.presentation.ui.player.VideoViewContract
import java.lang.ref.WeakReference

class VideoViewPresenter(videoViewView: VideoViewContract.View) : VideoViewContract.Presenter {

    private val view = WeakReference(videoViewView)

    private val mediaPlayer = MediaPlayerImpl()

    override fun getPlayer() = mediaPlayer

    override fun play(url: String) = mediaPlayer.play(url)

    override fun releasePlayer() = mediaPlayer.releasePlayer()

}