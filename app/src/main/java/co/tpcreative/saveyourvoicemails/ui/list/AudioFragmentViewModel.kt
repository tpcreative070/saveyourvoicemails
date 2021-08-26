package co.tpcreative.saveyourvoicemails.ui.list
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import co.tpcreative.common.Logger
import co.tpcreative.domain.models.GitHubUser
import co.tpcreative.domain.models.request.VoiceMailsRequest
import co.tpcreative.domain.usecases.DeleteVoiceMailUseCase
import co.tpcreative.domain.usecases.GetVoiceMailsUseCase
import co.tpcreative.domain.usecases.UpdateVoiceMailsUseCase
import co.tpcreative.saveyourvoicemails.common.Event
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.base.BaseViewModel
import co.tpcreative.saveyourvoicemails.common.extension.deleteFile
import co.tpcreative.saveyourvoicemails.common.network.Resource
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class AudioFragmentViewModel(
    private val getVoiceMailsUseCase: GetVoiceMailsUseCase,
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

    fun getVoiceMail() = liveData(Dispatchers.IO){
        dataList.clear()
        try {
            val mRequest = VoiceMailsRequest()
            mRequest.user_id = Utils.getUserId()
            val mResult = getVoiceMailsUseCase(mRequest)
            if (mResult.error){
                emit(Resource.error(Utils.CODE_EXCEPTION, mResult.message ?: "",null))
            }else{
                val audioList = mResult.data?.map {
                     AudioViewModel(it)
                }
                emit(Resource.success(audioList?.toMutableList()))
            }
        } catch (e: Exception) {
            logger.warn( "An error occurred while login user", e)
            emit(Resource.error(Utils.CODE_EXCEPTION, e.message ?: "",null))
        }
    }

    fun updatedVoiceMail() = liveData(Dispatchers.IO){
        try {
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