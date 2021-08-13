package co.tpcreative.saveyourvoicemails.presentationlayer.player
import android.os.Build
import android.os.Bundle
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.activity.BaseActivity
import kotlinx.android.synthetic.main.activity_player.*

class PlayerAct : BaseActivity(), VideoViewContract.View {

    companion object {
        const val VIDEO_URL_EXTRA = "video_url_extra"
    }

    private lateinit var presenter: VideoViewContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
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

        playerView.player = presenter.getPlayer().getPlayerImpl(this)

        presenter.play(videoUrl!!)
    }
}
