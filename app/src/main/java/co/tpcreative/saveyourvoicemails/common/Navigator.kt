package co.tpcreative.saveyourvoicemails.common

import android.content.Context
import android.content.Intent
import co.tpcreative.saveyourvoicemails.presentationlayer.player.PlayerAct


object Navigator {

    fun movePlayer(context: Context,url : String) {
        val intent =  Intent(context, PlayerAct::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra(PlayerAct.VIDEO_URL_EXTRA, url)
        context.startActivity(intent)
    }

}