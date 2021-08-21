package co.tpcreative.saveyourvoicemails.ui.user.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import co.tpcreative.common.Logger
import co.tpcreative.domain.models.User
import co.tpcreative.domain.usecases.GetSearchHistoryUseCase
import co.tpcreative.domain.usecases.SearchUsersUseCase
import co.tpcreative.saveyourvoicemails.common.Event
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
}