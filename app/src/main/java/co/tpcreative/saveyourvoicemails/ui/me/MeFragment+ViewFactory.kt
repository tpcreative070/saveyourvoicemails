package co.tpcreative.saveyourvoicemails.ui.me

import androidx.lifecycle.Observer
import co.tpcreative.saveyourvoicemails.Navigator

fun MeFragment.signOut(){
    viewModel.signOut().observe(this, Observer {
        context?.let { it1 -> Navigator.moveToSignIn(it1) }
    })
}