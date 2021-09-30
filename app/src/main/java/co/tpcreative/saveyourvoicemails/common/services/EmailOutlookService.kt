package co.tpcreative.saveyourvoicemails.common.services
import co.tpcreative.domain.models.BaseResponse
import co.tpcreative.domain.models.EmailToken
import co.tpcreative.domain.models.request.Mail365Request
import co.tpcreative.domain.models.response.Mail365
import co.tpcreative.domain.usecases.AddEmailTokenUseCase
import co.tpcreative.domain.usecases.RefreshEmailOutlookUseCase
import co.tpcreative.domain.usecases.SendEmailOutlookUseCase
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EmailOutlookService(private val sendEmailOutlookUseCase: SendEmailOutlookUseCase,private val refreshEmailOutlookUseCase: RefreshEmailOutlookUseCase,private val addEmailTokenUseCase: AddEmailTokenUseCase) {
    suspend fun sendMail(url : String?,accessToken : String?,emailToken : EmailToken) : Resource<String> {
        return withContext(Dispatchers.IO) {
            try {
                val mResult = sendEmailOutlookUseCase(url,accessToken,emailToken)
                if (mResult == 202){
                    ResponseHandler.handleSuccess("Sent mail successfully")
                }else {
                    ResponseHandler.handleErrorCode(mResult)
                }
            }
            catch (throwable : Exception){
                Utils.log(this::class.java,"response error ${throwable.message}")
                ResponseHandler.handleException(throwable)
            }
        }
    }

    suspend fun refreshEmailToken(url : String,request : MutableMap<String?,Any?>) : Resource<Mail365> {
        return withContext(Dispatchers.IO) {
            try {
                val mResult = refreshEmailOutlookUseCase(url,request)
                ResponseHandler.handleSuccess(mResult)
            }
            catch (throwable : Exception){
                ResponseHandler.handleException(throwable)
            }
        }
    }

    suspend fun addEmailToken(request : Mail365Request) : Resource<BaseResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val mResult = addEmailTokenUseCase(request)
                ResponseHandler.handleSuccess(mResult)
            }
            catch (throwable : Exception){
                ResponseHandler.handleException(throwable)
            }
        }
    }
}