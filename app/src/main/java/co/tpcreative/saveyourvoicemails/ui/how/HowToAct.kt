package co.tpcreative.saveyourvoicemails.ui.how

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.databinding.ActivityHowToBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener

class HowToAct : AppCompatActivity() {
    lateinit var binding: ActivityHowToBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHowToBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initUI()
        binding.youtubePlayerView.initialize(object : YouTubePlayerListener {
            override fun onVideoLoadedFraction(youTubePlayer: YouTubePlayer, v: Float) {}
            override fun onVideoId(youTubePlayer: YouTubePlayer, s: String) {}
            override fun onVideoDuration(youTubePlayer: YouTubePlayer, v: Float) {}
            override fun onStateChange(youTubePlayer: YouTubePlayer, playerState: PlayerConstants.PlayerState) {}
            override fun onReady(youTubePlayer: YouTubePlayer) {
                val videoId = "_gRlRuf8Vts"
                youTubePlayer.loadVideo(videoId, 0f)
            }

            override fun onPlaybackRateChange(
                youTubePlayer: YouTubePlayer,
                playbackRate: PlayerConstants.PlaybackRate
            ) {
            }

            override fun onPlaybackQualityChange(
                youTubePlayer: YouTubePlayer,
                playbackQuality: PlayerConstants.PlaybackQuality
            ) {
            }

            override fun onError(youTubePlayer: YouTubePlayer, playerError: PlayerConstants.PlayerError) {}
            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, v: Float) {}
            override fun onApiChange(youTubePlayer: YouTubePlayer) {}
        }, true)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.youtubePlayerView.release()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> false
        }
    }

}