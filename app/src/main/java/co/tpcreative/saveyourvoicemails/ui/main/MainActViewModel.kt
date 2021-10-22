package co.tpcreative.saveyourvoicemails.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import co.tpcreative.domain.models.EnType
import co.tpcreative.domain.models.GitHubUser
import co.tpcreative.domain.models.UploadBody
import co.tpcreative.saveyourvoicemails.common.EmailOutlookViewModel
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.base.BaseViewModel
import co.tpcreative.saveyourvoicemails.common.extension.isSignedIn
import co.tpcreative.saveyourvoicemails.common.extension.putSentDownloaded
import co.tpcreative.saveyourvoicemails.common.extension.putSentSubscription
import co.tpcreative.saveyourvoicemails.common.network.Resource
import co.tpcreative.saveyourvoicemails.common.network.Status
import co.tpcreative.saveyourvoicemails.common.services.UploadDownloadService
import co.tpcreative.saveyourvoicemails.common.services.upload.ProgressRequestBody
import com.elvishew.xlog.XLog
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import java.io.File

class MainActViewModel(private val emailOutlookService: EmailOutlookViewModel, private val uploadDownloadService: UploadDownloadService) : BaseViewModel<GitHubUser>() {

    val onSignIn = MutableLiveData<Boolean>()

    fun checkSignedIn(){
        if (!Utils.isSignedIn()){
            onSignIn.postValue(true)
        }
    }

    fun sendEmailOutlook(type : EnType) = liveData(Dispatchers.IO){
        try {
            val result = emailOutlookService.sendEmail(type,Utils.getEmail() ?: Utils.getUserId() ?: "")
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

    fun uploadFileLog(mFilePath: File?) = liveData(Dispatchers.IO ){
        try {
            val mResult = uploadDownloadService.uploadFileLog(mProgressUploading, mFilePath)
            when(mResult.status){
                Status.SUCCESS ->{
                    XLog.d("Uploaded successfully")
                    emit(Resource.success(mResult))
                }else ->{
                    XLog.d("Uploaded failure")
                    emit(Resource.error(Utils.CODE_EXCEPTION, mResult.message ?: "",null))
                }
            }
        } catch (e: Exception) {
            log( "An error occurred while login user ${e.message}")
            emit(Resource.error(Utils.CODE_EXCEPTION, e.message ?: "",null))
        }
    }

    private val mProgressUploading = object : ProgressRequestBody.UploadCallbacks {
        override fun onProgressUpdate(percentage: Int) {
            Utils.log(this::class.java, "Progressing uploaded $percentage%")
        }
        override fun onError() {
            Utils.log(this::class.java, "onError")
        }
        override fun onFinish() {
            Utils.log(this::class.java, "onFinish")
        }
    }

}