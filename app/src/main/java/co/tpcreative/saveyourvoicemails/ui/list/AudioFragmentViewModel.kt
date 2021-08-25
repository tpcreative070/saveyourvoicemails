package co.tpcreative.saveyourvoicemails.ui.list
import androidx.lifecycle.liveData
import co.tpcreative.common.Logger
import co.tpcreative.domain.models.GitHubUser
import co.tpcreative.domain.models.request.VoiceMailsRequest
import co.tpcreative.domain.usecases.GetVoiceMailsUseCase
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.base.BaseViewModel
import co.tpcreative.saveyourvoicemails.common.network.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class AudioFragmentViewModel(
    private val getVoiceMailsUseCase: GetVoiceMailsUseCase,
    private val logger: Logger,
    private val ioDispatcher: CoroutineDispatcher,
    private val mainDispatcher: CoroutineDispatcher
) : BaseViewModel<AudioViewModel>() {

    override val dataList: MutableList<AudioViewModel>
        get() = super.dataList

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
                     AudioViewModel(it.title ?: "")
                }
                emit(Resource.success(audioList?.toMutableList()))
            }
        } catch (e: Exception) {
            logger.warn( "An error occurred while login user", e)
            emit(Resource.error(Utils.CODE_EXCEPTION, e.message ?: "",null))
        }
    }
}