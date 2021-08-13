package co.tpcreative.saveyourvoicemails.presentationlayer.player

interface VideoViewContract {

    interface Presenter {

        fun deactivate()

        fun getPlayer(): MediaPlayer

        fun play(url: String)

        fun releasePlayer()

        fun setMediaSessionState(isActive: Boolean)
    }

    interface View
}