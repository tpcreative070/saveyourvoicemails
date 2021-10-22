package co.tpcreative.saveyourvoicemails.ui.player

import androidx.lifecycle.liveData
import co.tpcreative.common.Logger
import co.tpcreative.domain.models.Empty
import co.tpcreative.domain.models.request.DownloadFileRequest
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.base.BaseViewModel
import co.tpcreative.saveyourvoicemails.common.controller.ServiceManager
import co.tpcreative.saveyourvoicemails.common.network.Resource
import co.tpcreative.saveyourvoicemails.common.network.Status
import co.tpcreative.saveyourvoicemails.common.services.UploadDownloadService
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class PlayerViewModel(private val ioDispatcher: CoroutineDispatcher,
                     private val mainDispatcher: CoroutineDispatcher,
                     private val logger: Logger,
                     private val uploadDownloadService: UploadDownloadService,
) : BaseViewModel<Empty>() {


    private lateinit var downloadRequest : DownloadFileRequest

    fun setDownloadRequest(request: DownloadFileRequest){
        this.downloadRequest = request
    }

    fun downloadFile(downloadFileRequest: DownloadFileRequest) = liveData(
        Dispatchers.IO ){
        try {
            val mResult = uploadDownloadService.downloadFilePost(downloadFileRequest)
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

    fun trimFile() = liveData(Dispatchers.IO) {
        try {
            val mResult = ServiceManager.getInstance()?.exportingItems(downloadRequest,true)
            emit(Resource.success(TrimObject(mResult?.data?.absolutePath,downloadRequest.title)))
        }catch (e : Exception){
            emit(Resource.error(Utils.CODE_EXCEPTION, e.message ?: "",null))
        }
    }

    data class TrimObject(val path: String?, val title: String)
}

