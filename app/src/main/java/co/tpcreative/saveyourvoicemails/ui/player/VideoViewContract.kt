package co.tpcreative.saveyourvoicemails.ui.player

interface VideoViewContract {

    interface Presenter {

        fun getPlayer(): MediaPlayer

        fun play(url: String)

        fun releasePlayer()

    }

    interface View
}