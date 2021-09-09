package co.tpcreative.saveyourvoicemails.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import co.tpcreative.domain.models.EnType
import co.tpcreative.domain.models.GitHubUser
import co.tpcreative.saveyourvoicemails.common.EmailOutlookViewModel
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.base.BaseViewModel
import co.tpcreative.saveyourvoicemails.common.extension.isSignedIn
import co.tpcreative.saveyourvoicemails.common.extension.putSentDownloaded
import co.tpcreative.saveyourvoicemails.common.extension.putSentSubscription
import co.tpcreative.saveyourvoicemails.common.network.Resource
import co.tpcreative.saveyourvoicemails.common.network.Status
import kotlinx.coroutines.Dispatchers

class MainActViewModel(private val emailOutlookService: EmailOutlookViewModel) : BaseViewModel<GitHubUser>() {

    val onSignIn = MutableLiveData<Boolean>()

    fun checkSignedIn(){
        if (!Utils.isSignedIn()){
            onSignIn.postValue(true)
        }
    }

    fun sendEmailOutlook(type : EnType) = liveData(Dispatchers.IO){
        try {
            val result = emailOutlookService.sendEmail(type,Utils.getUserId()?:"")
            when(result.status){
                Status.SUCCESS->{
                    if (type == EnType.NEW_USER){
                        Utils.putSentDownloaded(true)
                    }else{
                        Utils.putSentSubscription(true)
                    }
                    emit(Resource.success(result.message ?:""))
                }else -> {
                emit(Resource.error(Utils.CODE_EXCEPTION, result.message ?: "",null))
            }
            }
        } catch (e: Exception) {
            log("An error occurred while get latest outlook ${e.message}")
            emit(Resource.error(Utils.CODE_EXCEPTION, e.message ?: "",null))
        }
    }
}