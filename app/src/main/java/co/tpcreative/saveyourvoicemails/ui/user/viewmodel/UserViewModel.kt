package co.tpcreative.saveyourvoicemails.ui.user.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import co.tpcreative.common.Logger
import co.tpcreative.domain.models.EnumValidationKey
import co.tpcreative.domain.models.User
import co.tpcreative.domain.usecases.GetSearchHistoryUseCase
import co.tpcreative.domain.usecases.SearchUsersUseCase
import co.tpcreative.saveyourvoicemails.common.Event
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.base.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class UserViewModel (
    private val searchUsersUseCase: SearchUsersUseCase,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val logger: Logger,
    private val ioDispatcher: CoroutineDispatcher,
    private val mainDispatcher: CoroutineDispatcher
) : BaseViewModel<User>() {

    val requestSignUp = MutableLiveData<Event<Boolean>>()

    val requestSignIn = MutableLiveData<Event<Boolean>>()

    val requestSignInWithGoogle = MutableLiveData<Event<Boolean>>()

    var requestForgotPassword = MutableLiveData<Event<Boolean>>()

    var requestLiveChat = MutableLiveData<Event<Boolean>>()

    var email : String = ""
        set(value) {
            field = value
            validationEmail(value)
        }

    override val errorResponseMessage: MutableLiveData<MutableMap<String, String?>?>
        get() = super.errorResponseMessage

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

    fun doSearch() {
        viewModelScope.launch(ioDispatcher) {
            try {
                val result = searchUsersUseCase("tp")

                logger.debug("result: $result")

                launch(mainDispatcher) {

                }
            } catch (e: Exception) {
                logger.warn( "An error occurred while searching users", e)

                launch(mainDispatcher) {

                }
            }

            launch(mainDispatcher) {

            }
        }
    }

    fun onSignUpClicked() = viewModelScope.launch(mainDispatcher) {
        requestSignUp.value =  Event(true)
    }

    fun onForgotPasswordClicked() = viewModelScope.launch(mainDispatcher) {
        requestForgotPassword.value = Event(true)
    }

    fun onLiveChatClicked() = viewModelScope.launch(mainDispatcher) {
        requestLiveChat.value = Event(true)
    }

    fun onSignInClicked() = viewModelScope.launch(mainDispatcher) {
        requestSignIn.value = Event(true)
    }

    fun onSignInWithGoogleClicked() = viewModelScope.launch(mainDispatcher) {
        requestSignInWithGoogle.value = Event(true)
    }
}