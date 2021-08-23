package co.tpcreative.saveyourvoicemails.ui.share

import androidx.lifecycle.liveData
import co.tpcreative.common.Logger
import co.tpcreative.domain.models.Empty
import co.tpcreative.domain.models.request.VoiceMailsRequest
import co.tpcreative.domain.usecases.*
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.base.BaseViewModel
import co.tpcreative.saveyourvoicemails.common.network.Resource
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class ShareViewModel(private val insertVoiceMailsUseCase: InsertVoiceMailsUseCase,
                     private val ioDispatcher: CoroutineDispatcher,
                     private val mainDispatcher: CoroutineDispatcher,
                     private val logger: Logger,
) : BaseViewModel<Empty>() {

    fun insertVoiceMails() = liveData(Dispatchers.IO ){
        try {
            val mVoiceMails = VoiceMailsRequest()
            val result = insertVoiceMailsUseCase(mVoiceMails)
            logger.debug("result: ${Gson().toJson(result)}")
            if (result.error){
                emit(Resource.error(Utils.CODE_EXCEPTION, result.message ?: "",null))
            }else{
                emit(Resource.success(result))
            }
        } catch (e: Exception) {
            logger.warn( "An error occurred while login user", e)
        }
    }

}

