package co.tpcreative.presentation.ui.player

import co.tpcreative.saveyourvoicemails.ui.player.MediaPlayer

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