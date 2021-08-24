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

class AudioViewModel(
    private val getVoiceMailsUseCase: GetVoiceMailsUseCase,
    private val logger: Logger,
    private val ioDispatcher: CoroutineDispatcher,
    private val mainDispatcher: CoroutineDispatcher
) : BaseViewModel<GitHubUser>() {

    fun getVoiceMail() = liveData(
        Dispatchers.IO ){
        try {
            val mRequest = VoiceMailsRequest()
            mRequest.user_id = Utils.getUserId()
            val mResult = getVoiceMailsUseCase(mRequest)
            if (mResult.error){
                emit(Resource.error(Utils.CODE_EXCEPTION, mResult.message ?: "",null))
            }else{
                emit(Resource.success(mResult))
            }
        } catch (e: Exception) {
            logger.warn( "An error occurred while login user", e)
            emit(Resource.error(Utils.CODE_EXCEPTION, e.message ?: "",null))
        }
    }
}