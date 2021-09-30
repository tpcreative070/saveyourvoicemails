package co.tpcreative.presentation.ui.player

import co.tpcreative.saveyourvoicemails.ui.player.MediaPlayer

interface VideoViewContract {

    interface Presenter {

        fun getPlayer(): MediaPlayer

        fun play(url: String)

        fun releasePlayer()

    }

    interface View
}