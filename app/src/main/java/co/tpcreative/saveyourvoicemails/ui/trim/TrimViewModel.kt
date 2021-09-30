package co.tpcreative.saveyourvoicemails.ui.trim

import androidx.lifecycle.liveData
import co.tpcreative.domain.models.UploadBody
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.base.BaseViewModel
import co.tpcreative.saveyourvoicemails.common.network.Resource
import co.tpcreative.saveyourvoicemails.common.network.Status
import co.tpcreative.saveyourvoicemails.common.services.UploadDownloadService
import co.tpcreative.saveyourvoicemails.common.services.upload.ProgressRequestBody
import kotlinx.coroutines.Dispatchers
import java.io.File

class TrimViewModel(private val uploadDownloadService: UploadDownloadService) : BaseViewModel<Any>() {


    fun insertVoiceMails(item : UploadBody, mFilePath: File?) = liveData(Dispatchers.IO ){
        try {
            val mResult = uploadDownloadService.uploadFile(item, mProgressUploading, mFilePath)
            log("Result ${mResult.data.toString()}")
            when(mResult.status){
                Status.SUCCESS ->{
                    emit(Resource.success(mResult))
                }else ->{
                emit(Resource.error(Utils.CODE_EXCEPTION, mResult.message ?: "",null))
            }
            }
        } catch (e: Exception) {
            log("An error occurred while login user ${e.message}")
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