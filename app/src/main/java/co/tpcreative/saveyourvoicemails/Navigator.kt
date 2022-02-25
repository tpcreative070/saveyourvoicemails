package co.tpcreative.saveyourvoicemails

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import co.tpcreative.domain.models.request.DownloadFileRequest
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.ui.changepassword.ChangePasswordAct
import co.tpcreative.saveyourvoicemails.ui.how.HowToAct
import co.tpcreative.saveyourvoicemails.ui.main.MainAct
import co.tpcreative.saveyourvoicemails.ui.permission.PermissionAct
import co.tpcreative.saveyourvoicemails.ui.player.PlayerAct
import co.tpcreative.saveyourvoicemails.ui.subscription.SubscriptionAct
import co.tpcreative.saveyourvoicemails.ui.trim.TrimAct
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

    fun moveSubscription(context : Context){
        val intent = Intent(context,SubscriptionAct::class.java)
        context.startActivity(intent)
    }

    fun moveToTrim(context: Context,url : String,title : String){
        val intent = Intent(context,TrimAct::class.java)
        intent.putExtra(TrimAct.AUDIO_URL_EXTRA,url)
        intent.putExtra(TrimAct.AUDIO_TITLE_EXTRA,title)
        context.startActivity(intent)
    }

    fun openWebSites(url: String?,context: Activity) {
        val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.setPackage("com.android.chrome")
        try {
            context.startActivity(i)
        } catch (e: ActivityNotFoundException) {
            // Chrome is probably not installed
            // Try with the default browser
            try {
                i.setPackage(null)
                context.startActivity(i)
            } catch (ex: Exception) {
                Utils.onAlertNotify(context ,"Can not open the link")
            }
        }
    }

    fun onHowTo(activity: Activity) {
        val intent = Intent(activity, HowToAct::class.java)
        intent.putExtra("ActivityName", "inbox")
        activity.startActivity(intent)
    }

}