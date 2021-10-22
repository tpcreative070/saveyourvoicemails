package co.tpcreative.saveyourvoicemails.ui.changepassword

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import co.tpcreative.domain.models.EnumValidationKey
import co.tpcreative.domain.models.request.UserRequest
import co.tpcreative.domain.models.response.User
import co.tpcreative.domain.usecases.ChangePasswordUserUseCase
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.base.BaseViewModel
import co.tpcreative.saveyourvoicemails.common.extension.getUserInfo
import co.tpcreative.saveyourvoicemails.common.network.Resource
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import kotlinx.coroutines.Dispatchers

class ChangePasswordViewModel(private val changePasswordUserUseCase: ChangePasswordUserUseCase) : BaseViewModel<User>() {

    var oldPassword : String  = ""
        set(value){
            field = value
            validationOldPassword(value)
        }

    var newPassword : String  = ""
        set(value){
            field = value
            validationNewPassword(value)
        }

    var confirmPassword : String = ""
        set(value){
            field = value
            validationConfirmPassword(value)
        }

    override val errorResponseMessage: MutableLiveData<MutableMap<String, String?>?>
        get() = super.errorResponseMessage


    private fun validationOldPassword(mValue : String){
        if (mValue.isEmpty()){
            putError(EnumValidationKey.EDIT_OLD_PASSWORD, "Request enter password")
        }
        else if(mValue.length < 6){
            putError(EnumValidationKey.EDIT_OLD_PASSWORD, "Password at least 6 characters")
        }
        else{
            putError(EnumValidationKey.EDIT_OLD_PASSWORD)
        }
    }

    private fun validationNewPassword(mValue : String){
        if (mValue.isEmpty()){
            putError(EnumValidationKey.EDIT_NEW_PASSWORD, "Request enter password")
        }
        else if(mValue.length < 6){
            putError(EnumValidationKey.EDIT_NEW_PASSWORD, "Password at least 6 characters")
        }
        else{
            putError(EnumValidationKey.EDIT_NEW_PASSWORD)
        }
    }

    private fun validationConfirmPassword(mValue : String){
        if (mValue.isEmpty()){
            putError(EnumValidationKey.EDIT_TEXT_CONFIRM_PASSWORD, "Request enter password")
        }
        else if(mValue.length < 6){
            putError(EnumValidationKey.EDIT_TEXT_CONFIRM_PASSWORD, "Password at least 6 characters")
        }
        else if (mValue != newPassword){
            putError(EnumValidationKey.EDIT_TEXT_CONFIRM_PASSWORD, "The password don't match")
        }
        else{
            putError(EnumValidationKey.EDIT_TEXT_CONFIRM_PASSWORD)
        }
    }

    fun changePassword() = liveData(Dispatchers.IO){
        try {
            val mUser = Utils.getUserInfo()
            val mRequest = UserRequest(mUser?.user_id ?:"",mUser?.user_id ?:"",oldPassword,newPassword,"null",
                SaveYourVoiceMailsApplication.getInstance().getDeviceId())
            val result = changePasswordUserUseCase(mRequest)
            if (result.error){
                emit(Resource.error(Utils.CODE_EXCEPTION, result.message ?: "",null))
            }else{
                emit(Resource.success(result))
            }
        } catch (e: Exception) {
            log( "An error occurred while sign up user $e")
            emit(Resource.error(Utils.CODE_EXCEPTION, e.message ?: "",null))
        }
    }

}