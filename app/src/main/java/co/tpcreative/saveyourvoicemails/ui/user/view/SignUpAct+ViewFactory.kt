package co.tpcreative.saveyourvoicemails.ui.user.view

import androidx.lifecycle.lifecycleScope
import co.tpcreative.domain.models.EnumValidationKey
import co.tpcreative.saveyourvoicemails.common.extension.textChanges
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce

fun SignUpAct.initUI(){

    lifecycleScope.launchWhenResumed {
        binding.textPutUserName.textChanges()
            .debounce(400)
            .collect {
                execute(it)
            }
    }
    lifecycleScope.launchWhenResumed {
        binding.textPutPassword.textChanges()
            .debounce(400)
            .collect {
                execute(it)
            }
    }
    viewModel.putError(EnumValidationKey.EDIT_TEXT_EMAIL,"")
    viewModel.putError(EnumValidationKey.EDIT_PASSWORD, "")
    binding.btnSignUp.setOnClickListener {
        signUp()
    }
}

private fun SignUpAct.signUp(){
    viewModel.isLoading.value = true
}

private fun SignUpAct.execute(s : CharSequence?) {
    if (binding.textPutUserName == currentFocus){
        viewModel.email = s.toString()
    }else{
        viewModel.password = s.toString()
    }
}