package co.tpcreative.saveyourvoicemails

import android.content.Context
import android.content.Intent
import co.tpcreative.domain.models.request.DownloadFileRequest
import co.tpcreative.saveyourvoicemails.ui.changepassword.ChangePasswordAct
import co.tpcreative.saveyourvoicemails.ui.main.MainAct
import co.tpcreative.saveyourvoicemails.ui.permission.PermissionAct
import co.tpcreative.saveyourvoicemails.ui.player.PlayerAct
import co.tpcreative.saveyourvoicemails.ui.user.view.SignInAct
import co.tpcreative.saveyourvoicemails.ui.user.view.SignUpAct
import com.google.gson.Gson

object Navigator {
    fun moveToSignIn(context : Context){
        val intent : Intent = Intent(context,SignInAct::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun moveToMain(context : Context){
        val intent : Intent = Intent(context,MainAct::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun moveToSignUp(context : Context){
        val intent : Intent = Intent(context,SignUpAct::class.java)
        context.startActivity(intent)
    }


    fun moveToPlayer(context : Context,itemRequest : DownloadFileRequest){
        val intent = Intent(context,PlayerAct::class.java)
        intent.putExtra(PlayerAct.AUDIO_URL_EXTRA,Gson().toJson(itemRequest))
        context.startActivity(intent)
    }

    fun moveToPermission(context: Context){
        val intent = Intent(context,PermissionAct::class.java)
        context.startActivity(intent)
    }

    fun moveToChangePassword(context: Context){
        val intent = Intent(context,ChangePasswordAct::class.java)
        context.startActivity(intent)
    }

}