package co.tpcreative.saveyourvoicemails.ui.main

import androidx.lifecycle.MutableLiveData
import co.tpcreative.domain.models.GitHubUser
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.base.BaseViewModel
import co.tpcreative.saveyourvoicemails.common.extension.isSignedIn

class MainActViewModel : BaseViewModel<GitHubUser>() {

    val onSignIn = MutableLiveData<Boolean>()

    fun checkSignedIn(){
//        if (!Utils.isSignedIn()){
//            onSignIn.postValue(true)
//        }
        onSignIn.postValue(true)
    }
}