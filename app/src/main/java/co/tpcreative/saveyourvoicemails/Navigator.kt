package co.tpcreative.saveyourvoicemails

import android.content.Context
import android.content.Intent
import co.tpcreative.saveyourvoicemails.ui.user.view.SignInAct
import co.tpcreative.saveyourvoicemails.ui.user.view.SignUpAct

object Navigator {
    fun moveToSignIn(context : Context){
        val intent : Intent = Intent(context,SignInAct::class.java)
        context.startActivity(intent)
    }

    fun moveToSignUp(context : Context){
        val intent : Intent = Intent(context,SignUpAct::class.java)
        context.startActivity(intent)
    }
}