package co.tpcreative.saveyourvoicemails.ui.user.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import co.tpcreative.common.Logger
import co.tpcreative.domain.models.EnType
import co.tpcreative.domain.models.EnumValidationKey
import co.tpcreative.domain.models.GitHubUser
import co.tpcreative.domain.models.request.Mail365Request
import co.tpcreative.domain.models.request.UserRequest
import co.tpcreative.domain.usecases.*
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.EmailOutlookViewModel
import co.tpcreative.saveyourvoicemails.common.Event
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.base.BaseViewModel
import co.tpcreative.saveyourvoicemails.common.extension.*
import co.tpcreative.saveyourvoicemails.common.network.Resource
import co.tpcreative.saveyourvoicemails.common.network.Status
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class UserViewModel (
        private val getLatestOutlookUseCase: GetLatestOutlookUseCase,
        private val emailOutlookService: EmailOutlookViewModel,
        private val getUserUseCase: GetUserUseCase,
        private val signUpUsersUseCase: SignUpUsersUseCase,
        private val signInUsersUseCase : SignInUsersUseCase,
        private val searchUsersUseCase: SearchUsersUseCase,
        private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
        private val logger: Logger,
        private val ioDispatcher: CoroutineDispatcher,
        private val mainDispatcher: CoroutineDispatcher
) : BaseViewModel<GitHubUser>() {

    val requestSignUp = MutableLiveData<Event<Boolean>>()

    var isFacebook : Boolean = false
        set(value){
            field = value
        }

    var user_id : String = ""
        set(value){
            field = value
        }

    var email : String = ""
        set(value) {
            field = value
            validationEmail(value)
        }

    var password : String  = ""
        set(value){
            field = value
            validationPassword(value)
        }

    var confirmPassword : String = ""
        set(value){
            field = value
            validationConfirmPassword(value)
        }

    var phoneNumber : String = ""
        set(value){
            field = value
            validationPhoneNumber(value)
        }

    override val errorResponseMessage: MutableLiveData<MutableMap<String, String?>?>
        get() = super.errorResponseMessage

    private fun validationPhoneNumber(mValue : String){
        if (mValue.isEmpty()){
            putError(EnumValidationKey.EDIT_TEXT_PHONE_NUMBER, "Request enter phone number")
        }
        else if(mValue.length < 6){
            putError(EnumValidationKey.EDIT_TEXT_PHONE_NUMBER, "Phone number at least 6 digits")
        }
        else{
            putError(EnumValidationKey.EDIT_TEXT_PHONE_NUMBER)
        }
    }

    private fun validationConfirmPassword(mValue : String){
        if (mValue.isEmpty()){
            putError(EnumValidationKey.EDIT_TEXT_CONFIRM_PASSWORD, "Request enter password")
        }
        else if(mValue.length < 6){
            putError(EnumValidationKey.EDIT_TEXT_CONFIRM_PASSWORD, "Password at least 6 characters")
        }
        else if (mValue != password){
            putError(EnumValidationKey.EDIT_TEXT_CONFIRM_PASSWORD, "The password don't match")
        }
        else{
            putError(EnumValidationKey.EDIT_TEXT_CONFIRM_PASSWORD)
        }
    }

    private fun validationEmail(mValue : String){
        if (mValue.isEmpty()){
            putError(EnumValidationKey.EDIT_TEXT_EMAIL, "Request enter email")
        }else if (!Utils.isValidEmail(mValue)){
            putError(EnumValidationKey.EDIT_TEXT_EMAIL, "Email invalid")
        }
        else{
            putError(EnumValidationKey.EDIT_TEXT_EMAIL)
        }
    }

    private fun validationPassword(mValue : String){
        if (mValue.isEmpty()){
            putError(EnumValidationKey.EDIT_PASSWORD, "Request enter password")
        }
        else if(mValue.length < 6){
            putError(EnumValidationKey.EDIT_PASSWORD, "Password at least 6 characters")
        }
        else{
            putError(EnumValidationKey.EDIT_PASSWORD)
        }
    }

    fun getUser() = liveData(Dispatchers.IO){
        try {
            val mUser = UserRequest(user_id,"null","null",null,null,SaveYourVoiceMailsApplication.getInstance().getDeviceId())
            val result = getUserUseCase(mUser)
            logger.debug("result: ${Gson().toJson(result)}")
            if (result.error){
                emit(Resource.error(Utils.CODE_EXCEPTION, result.message ?: "",null))
            }else{
                emit(Resource.success(result))
            }
        } catch (e: Exception) {
            logger.warn( "An error occurred while login user", e)
            emit(Resource.error(Utils.CODE_EXCEPTION, e.message ?: "",null))
        }
    }

    fun signIn() = liveData(Dispatchers.IO){
        try {
            if (!isOnline()){
                emit(Resource.error(Utils.CODE_EXCEPTION, getString(R.string.no_connections),null))
                return@liveData
            }
            val mUser = UserRequest(user_id,email,password,null,null,SaveYourVoiceMailsApplication.getInstance().getDeviceId())
            val result = signInUsersUseCase(mUser)
            logger.debug("result: ${Gson().toJson(result)}")
            if (result.error){
                emit(Resource.error(Utils.CODE_EXCEPTION, result.message ?: "",null))
            }else{
                result.user?.isFacebook = isFacebook
                Utils.putUserPreShare(result.user)
                Utils.putSessionTokenPreShare(result.session_token)
                Utils.putMail365PreShare(result.mail365)
                SaveYourVoiceMailsApplication.getInstance().initXLog()
                emit(Resource.success(result))
            }
        } catch (e: Exception) {
            logger.warn( "An error occurred while login user", e)
            emit(Resource.error(Utils.CODE_EXCEPTION, e.message ?: "",null))
        }
    }

    fun signUp() = liveData(Dispatchers.IO){
        try {
            if (!isOnline()){
                emit(Resource.error(Utils.CODE_EXCEPTION, getString(R.string.no_connections),null))
                return@liveData
            }
            val mUser = UserRequest(user_id,email,password,null,phoneNumber,SaveYourVoiceMailsApplication.getInstance().getDeviceId())
            Utils.log(this@UserViewModel.javaClass,mUser)
            val result = signUpUsersUseCase(mUser)
            logger.debug("result ${Gson().toJson(result)}")
            if (result.error){
                emit(Resource.error(Utils.CODE_EXCEPTION, result.message ?: "",null))
            }else{
                emit(Resource.success(result))
            }
        } catch (e: Exception) {
            logger.warn( "An error occurred while sign up user", e)
            emit(Resource.error(Utils.CODE_EXCEPTION, e.message ?: "",null))
        }
    }

    fun getLatestOutlook() = liveData(Dispatchers.IO){
        try {
            val mMail365Request = Mail365Request("null",email,"","")
            val result = getLatestOutlookUseCase(mMail365Request)
            logger.debug("result ${Gson().toJson(result)}")
            if (result.error){
                emit(Resource.error(Utils.CODE_EXCEPTION, result.message ?: "",null))
            }else{
                Utils.putMail365PreShare(result.data)
                Utils.putRequestCode(result.code)
                emit(Resource.success(result))
            }
        } catch (e: Exception) {
            logger.warn( "An error occurred while get latest outlook", e)
            emit(Resource.error(Utils.CODE_EXCEPTION, e.message ?: "",null))
        }
    }

    fun sendEmailOutlook() = liveData(Dispatchers.IO){
        try {
            if (!isOnline()){
                emit(Resource.error(Utils.CODE_EXCEPTION, getString(R.string.no_connections),null))
                return@liveData
            }
            val result = emailOutlookService.sendEmail(EnType.FORGOT_PASSWORD,user_id)
            when(result.status){
                Status.SUCCESS->{
                    emit(Resource.success(result.message ?:""))
                }else -> {
                  emit(Resource.error(Utils.CODE_EXCEPTION, result.message ?: "",null))
                }
            }
        } catch (e: Exception) {
            logger.warn( "An error occurred while get latest outlook", e)
            emit(Resource.error(Utils.CODE_EXCEPTION, e.message ?: "",null))
        }
    }
}