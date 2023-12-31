package co.tpcreative.saveyourvoicemails.common.base

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.tpcreative.domain.models.EnumValidationKey
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication

open class BaseViewModel<T> : ViewModel() {
    open val isLoading : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    open val errorMessages : MutableLiveData<MutableMap<String, String?>?> by lazy {
        MutableLiveData<MutableMap<String,String?>?>()
    }

    open val errorResponseMessage  : MutableLiveData<MutableMap<String, String?>?> by lazy {
        MutableLiveData<MutableMap<String,String?>?>()
    }

    open val videos : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    open val photos : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    open val audios : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }
    open val others : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    open val count : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    open var isRequestSyncData : Boolean = false

    open var position : Int = 0

    open val isSelectAll : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    protected open val dataList : MutableList<T> = mutableListOf()

    open fun putErrorResponse(key: EnumValidationKey,value : String? = null){
        if (errorResponseMessage.value==null){
            try {
                errorResponseMessage.value = mutableMapOf(key.name to value)
            }catch (e : Exception){
                errorResponseMessage.postValue(mutableMapOf(key.name to value))
            }
        }else{
            if (value.isNullOrEmpty()){
                errorResponseMessage.value?.remove(key.name)
                errorResponseMessage.postValue(errorResponseMessage.value)
            }else{
                errorResponseMessage.value?.set(key.name,value)
                errorResponseMessage.postValue(errorResponseMessage.value)
            }
        }
    }

    open fun putError(key: EnumValidationKey, value : String? = null){
        if (errorMessages.value==null){
            try {
                errorMessages.value = mutableMapOf(key.name to value)
            }catch (e : Exception){
                errorMessages.postValue(mutableMapOf(key.name to value))
            }
        }else{
            if (value==null){
                errorMessages.value?.remove(key.name)
                errorMessages.postValue(errorMessages.value)
            }else{
                errorMessages.value?.set(key.name,value)
                errorMessages.postValue(errorMessages.value)
            }
        }
    }

    fun log(message : Any){
        Utils.log(this::class.java,message)
    }

    fun isOnline(): Boolean {
        val connectivityManager =
            SaveYourVoiceMailsApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }
}