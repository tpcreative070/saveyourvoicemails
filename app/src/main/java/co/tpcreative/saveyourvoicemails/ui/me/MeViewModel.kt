package co.tpcreative.saveyourvoicemails.ui.me

import androidx.lifecycle.liveData
import co.tpcreative.domain.models.response.User
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.base.BaseViewModel
import co.tpcreative.saveyourvoicemails.common.extension.signOut
import co.tpcreative.saveyourvoicemails.common.network.Resource
import kotlinx.coroutines.Dispatchers

class MeViewModel : BaseViewModel<User>(){

    fun signOut() = liveData(Dispatchers.IO){
        Utils.signOut()
        emit(Resource.success(true))
    }
}