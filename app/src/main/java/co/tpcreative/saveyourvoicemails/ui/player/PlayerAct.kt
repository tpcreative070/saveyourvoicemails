package co.tpcreative.saveyourvoicemails.ui.player
import android.os.Build
import android.os.Bundle
import co.tpcreative.presentation.ui.player.VideoViewContract
import co.tpcreative.saveyourvoicemails.common.base.BaseActivity
import co.tpcreative.saveyourvoicemails.databinding.ActivityPlayerBinding

class PlayerAct : BaseActivity(), VideoViewContract.View {

    companion object {
        const val VIDEO_URL_EXTRA = "video_url_extra"
    }

    private lateinit var presenter: VideoViewContract.Presenter
    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        init()
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            presenter.releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            presenter.releasePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.deactivate()
        presenter.setMediaSessionState(false)
    }

    private fun init() {
        presenter = VideoViewPresenter(this)

        val videoUrl = intent.getStringExtra(VIDEO_URL_EXTRA)

        //binding.playerView.player = presenter.getPlayer().getPlayerImpl(this)

        presenter.play(videoUrl!!)
    }
}
