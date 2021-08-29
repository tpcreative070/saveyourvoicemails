package co.tpcreative.saveyourvoicemails.common

import androidx.lifecycle.ViewModel
import co.tpcreative.domain.models.EmailToken
import co.tpcreative.domain.models.EnType
import co.tpcreative.domain.models.EnumResponseCode
import co.tpcreative.domain.models.request.Mail365Request
import co.tpcreative.domain.models.response.Mail365
import co.tpcreative.domain.models.response.Mail365Response
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.base.BaseViewModel
import co.tpcreative.saveyourvoicemails.common.extension.getMail365
import co.tpcreative.saveyourvoicemails.common.extension.getRequestCode
import co.tpcreative.saveyourvoicemails.common.extension.getUserInfo
import co.tpcreative.saveyourvoicemails.common.extension.putMail365PreShare
import co.tpcreative.saveyourvoicemails.common.network.Resource
import co.tpcreative.saveyourvoicemails.common.network.Status
import co.tpcreative.saveyourvoicemails.common.services.EmailOutlookService
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.HashMap

class EmailOutlookViewModel(private val service : EmailOutlookService) : BaseViewModel<Mail365Response>() {
    private val REFRESH_TOKEN: String = "https://login.microsoftonline.com/common/oauth2/v2.0/token"
    private val SEND_MAIL: String = "https://graph.microsoft.com/v1.0/me/sendMail"
    private val FORGOT_PASSWORD = SaveYourVoiceMailsApplication.getInstance().getUrl() + "saveyourvoicemails/voiceApp/vmsv2/v1/user/active/"
    suspend fun sendEmail(enumStatus : EnType,user_id : String) : Resource<String> {
        return withContext(Dispatchers.IO){
            try {
                val mMicRequest = getEmailContent(enumStatus,user_id)
                val mMail365 = Utils.getMail365()
                val mResultSentEmail = service.sendMail(SEND_MAIL,mMail365?.access_token,mMicRequest)
                when(mResultSentEmail.status){
                    Status.SUCCESS -> {
                        mResultSentEmail
                    }
                    else -> {
                        if (EnumResponseCode.INVALID_AUTHENTICATION.code==mResultSentEmail.code){
                            val mRequestEmailToken = getRefreshContent(mMail365)
                            log("Request refresh token ${Gson().toJson(mRequestEmailToken)}")
                            val mResultRefreshToken = service.refreshEmailToken(REFRESH_TOKEN,mRequestEmailToken)
                            when(mResultRefreshToken.status){
                                Status.SUCCESS ->{
                                    Utils.putMail365PreShare(mResultRefreshToken.data)
                                    val mResultAddedMailToken = service.addEmailToken(getAddedEmailToken(user_id))
                                    when(mResultAddedMailToken.status){
                                        Status.SUCCESS ->{
                                            val mSentEmail = service.sendMail(SEND_MAIL,mResultRefreshToken.data?.access_token ?:"",getEmailContent(enumStatus,user_id))
                                            when(mSentEmail.status){
                                                Status.SUCCESS ->{
                                                    mSentEmail
                                                }
                                                else ->  Resource.error(mSentEmail.code?:Utils.CODE_EXCEPTION, mSentEmail.message ?:"",null)
                                            }
                                        }
                                        else -> Resource.error(mResultAddedMailToken.code?:Utils.CODE_EXCEPTION, mResultAddedMailToken.message ?:"",null)
                                    }
                                }
                                else ->  Resource.error(mResultRefreshToken.code?:Utils.CODE_EXCEPTION, mResultRefreshToken.message ?:"",null)
                            }
                        }else{
                            Resource.error(mResultSentEmail.code?:Utils.CODE_EXCEPTION, mResultSentEmail.message ?:"",null)
                        }
                    }
                }
            }catch (e : Exception){
                e.printStackTrace()
                Resource.error(Utils.CODE_EXCEPTION, e.message ?:"",null)
            }
        }
    }

    private fun getEmailContent(enumStatus: EnType,user_id : String) : EmailToken {
        val mMail365 = Utils.getMail365()
        val urlForgotPassword = FORGOT_PASSWORD + Utils.getRequestCode()
        val mResult = EmailToken.getInstance()?.convertObject(mMail365, enumStatus,user_id,urlForgotPassword)
        mResult?.let {
            log("content email ${Gson().toJson(it)}")
            return it
        }
        log("content email empty")
        return EmailToken()
    }

    private fun getRefreshContent(request: Mail365?) : MutableMap<String?,Any?>{
        val hash: MutableMap<String?, Any?> = HashMap()
        hash[getString(R.string.key_client_id)] = request?.client_id
        hash[getString(R.string.key_redirect_uri)] = request?.redirect_uri
        hash[getString(R.string.key_grant_type)] = request?.grant_type
        hash[getString(R.string.key_refresh_token)] = request?.refresh_token
        return hash
    }

    private fun getAddedEmailToken(user_id : String) : Mail365Request {
        val email365 = Utils.getMail365()
        return Mail365Request(user_id ,user_id, email365?.refresh_token,email365?.access_token)
    }

    fun getString(res : Int) : String{
        return SaveYourVoiceMailsApplication.getInstance().getString(res)
    }
}