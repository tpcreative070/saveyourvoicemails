package co.tpcreative.saveyourvoicemails.ui.me

import androidx.lifecycle.Observer
import co.tpcreative.saveyourvoicemails.Navigator
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.extension.putSeeVideo
import co.tpcreative.saveyourvoicemails.ui.main.MainAct
import co.tpcreative.saveyourvoicemails.ui.main.alertDialog
import com.afollestad.materialdialogs.MaterialDialog

fun MeFragment.signOut(){
    viewModel.signOut().observe(this, Observer {
        context?.let { it1 -> Navigator.moveToSignIn(it1) }
    })
}


fun MeFragment.accessibilityServicesDescription() {
    val builder: MaterialDialog = MaterialDialog(requireContext())
        .title(text = getString(R.string.alert))
        .message(res = R.string.accessibilityServices)
        .positiveButton(text = getString(R.string.ok))
        .cancelable(false)
        .positiveButton {
        }
    builder.show()
}