package co.tpcreative.saveyourvoicemails.ui.list
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import co.tpcreative.common.Logger
import co.tpcreative.domain.models.request.DownloadFileRequest
import co.tpcreative.domain.models.request.VoiceMailsRequest
import co.tpcreative.domain.usecases.DeleteVoiceMailUseCase
import co.tpcreative.domain.usecases.GetVoiceMailsUseCase
import co.tpcreative.domain.usecases.UpdateVoiceMailsUseCase
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.base.BaseViewModel
import co.tpcreative.saveyourvoicemails.common.extension.deleteFile
import co.tpcreative.saveyourvoicemails.common.extension.getString
import co.tpcreative.saveyourvoicemails.common.network.Resource
import co.tpcreative.saveyourvoicemails.common.network.Status
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import co.tpcreative.saveyourvoicemails.common.services.UploadDownloadService
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class AudioFragmentViewModel(
    private val getVoiceMailsUseCase: GetVoiceMailsUseCase,
    private val downloadService: UploadDownloadService,
    private val updateVoiceMailsUseCase: UpdateVoiceMailsUseCase,
    private val deleteVoiceMailUseCase: DeleteVoiceMailUseCase,
    private val logger: Logger,
    private val ioDispatcher: CoroutineDispatcher,
    private val mainDispatcher: CoroutineDispatcher
) : BaseViewModel<AudioViewModel>() {

    override val dataList: MutableList<AudioViewModel>
        get() = super.dataList

    var title : String = ""
        set(value) {
            field = value
        }

    var id : String = ""
        set(value){
            field = value
        }
    var voice : String = ""
        set(value){
            field = value
        }

    @ExperimentalStdlibApi
    var searchText : String = ""
        set(value){
            field = value
            searchText()
        }

    var searchData : MutableLiveData<MutableList<AudioViewModel>> =  MutableLiveData<MutableList<AudioViewModel>>()

    @ExperimentalStdlibApi
    private fun searchText(){
        val mResult = dataList.filter { it.title.lowercase().contains(searchText.lowercase())}
        if (searchText.isEmpty()){
            searchData.value = dataList
        }else{
            searchData.value = mResult.toMutableList()
        }
    }

    fun downloadFile(downloadFileRequest: DownloadFileRequest) = liveData(Dispatchers.IO ){
        try {
            if (!isOnline()){
                emit(Resource.error(Utils.CODE_EXCEPTION, getString(R.string.no_connections),null))
                return@liveData
            }
            val mResult = downloadService.downloadFilePost(downloadFileRequest)
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

    fun getVoiceMail() = liveData(Dispatchers.IO){
        dataList.clear()
        try {
            if (!isOnline()){
                emit(Resource.error(Utils.CODE_EXCEPTION, getString(R.string.no_connections),null))
                return@liveData
            }
            val mRequest = VoiceMailsRequest()
            mRequest.user_id = Utils.getUserId()
            val mResult = getVoiceMailsUseCase(mRequest)
            if (mResult.error){
                emit(Resource.error(Utils.CODE_EXCEPTION, mResult.message ?: "",null))
            }else{
                val audioList = mResult.data?.map {
                     AudioViewModel(it)
                }
                dataList.addAll(audioList!!)
                emit(Resource.success(audioList.toMutableList()))
            }
        } catch (e: Exception) {
            logger.warn( "An error occurred while login user", e)
            emit(Resource.error(Utils.CODE_EXCEPTION, e.message ?: "",null))
        }
    }

    fun updatedVoiceMail() = liveData(Dispatchers.IO){
        try {
            if (!isOnline()){
                emit(Resource.error(Utils.CODE_EXCEPTION, getString(R.string.no_connections),null))
                return@liveData
            }
            val mRequest = VoiceMailsRequest()
            mRequest.user_id = Utils.getUserId()
            mRequest.title = title
            mRequest.id = id
            log("request ${Gson().toJson(mRequest)}")
            val mResult = updateVoiceMailsUseCase(mRequest)
            if (mResult.error){
                emit(Resource.error(Utils.CODE_EXCEPTION, mResult.message ?: "",null))
            }else{
                emit(Resource.success(mResult.message))
            }
        } catch (e: Exception) {
            logger.warn( "An error occurred while update voicem mail", e)
            emit(Resource.error(Utils.CODE_EXCEPTION, e.message ?: "",null))
        }
    }

    fun deleteVoiceMail() = liveData(Dispatchers.IO){
        try {
            if (!isOnline()){
                emit(Resource.error(Utils.CODE_EXCEPTION, getString(R.string.no_connections),null))
                return@liveData
            }
            val mRequest = VoiceMailsRequest()
            mRequest.user_id = Utils.getUserId()
            mRequest.title = title
            mRequest.id = id
            mRequest.voice = voice
            log("request ${Gson().toJson(mRequest)}")
            val mResult = deleteVoiceMailUseCase(mRequest)
            if (mResult.error){
                log(mResult.message ?: "")
                emit(Resource.error(Utils.CODE_EXCEPTION, mResult.message ?: "",null))
            }else{
                log(mResult.message ?: "")
                (SaveYourVoiceMailsApplication.getInstance().getPrivate()+voice).deleteFile()
                emit(Resource.success(mResult.message))
            }
        } catch (e: Exception) {
            logger.warn( "An error occurred while update voicem mail", e)
            emit(Resource.error(Utils.CODE_EXCEPTION, e.message ?: "",null))
        }
    }
}