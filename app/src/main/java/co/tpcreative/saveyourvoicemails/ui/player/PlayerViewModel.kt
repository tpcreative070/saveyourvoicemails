package co.tpcreative.saveyourvoicemails.ui.player

import androidx.lifecycle.liveData
import co.tpcreative.common.Logger
import co.tpcreative.domain.models.Empty
import co.tpcreative.domain.models.UploadBody
import co.tpcreative.domain.models.request.DownloadFileRequest
import co.tpcreative.domain.models.response.VoiceMail
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.base.BaseViewModel
import co.tpcreative.saveyourvoicemails.common.network.Resource
import co.tpcreative.saveyourvoicemails.common.network.Status
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import co.tpcreative.saveyourvoicemails.common.services.UploadDownloadService
import co.tpcreative.saveyourvoicemails.common.services.upload.ProgressRequestBody
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.io.File

class PlayerViewModel(private val ioDispatcher: CoroutineDispatcher,
                     private val mainDispatcher: CoroutineDispatcher,
                     private val logger: Logger,
                     private val uploadDownloadService: UploadDownloadService,
) : BaseViewModel<Empty>() {

    fun downloadFile(downloadFileRequest: DownloadFileRequest) = liveData(
        Dispatchers.IO ){
        try {
            val mResult = uploadDownloadService.downloadFile(downloadFileRequest)
            logger.debug("result: ${Gson().toJson(mResult.data.toString())}")
            when(mResult.status){
                Status.SUCCESS ->{
                    emit(Resource.success(mResult))
                }else ->{
                emit(Resource.error(Utils.CODE_EXCEPTION, mResult.message ?: "",null))
            }
            }
        } catch (e: Exception) {
            logger.warn( "An error occurred while login user", e)
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

