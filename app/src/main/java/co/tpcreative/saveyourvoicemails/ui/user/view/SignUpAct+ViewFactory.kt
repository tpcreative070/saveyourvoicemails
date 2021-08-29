package co.tpcreative.saveyourvoicemails.ui.user.view
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import co.tpcreative.saveyourvoicemails.common.extension.textChanges
import co.tpcreative.saveyourvoicemails.common.network.Status
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce

fun SignUpAct.initUI(){
    lifecycleScope.launchWhenResumed {
        binding.textPutUserName.textChanges()
            .debounce(200)
            .collect {
                execute(it)
            }
    }
    lifecycleScope.launchWhenResumed {
        binding.textPutPassword.textChanges()
            .debounce(200)
            .collect {
                execute(it)
            }
    }

    lifecycleScope.launchWhenResumed {
        binding.textPutConfirmPassword.textChanges()
            .debounce(200)
            .collect {
                execute(it)
            }
    }
    lifecycleScope.launchWhenResumed {
        binding.textPutPhoneNumber.textChanges()
            .debounce(200)
            .collect {
                execute(it)
            }
    }
    binding.btnSignUp.setOnClickListener {
        viewModel.user_id = viewModel.email
        signUp()
    }
}

private fun SignUpAct.signUp(){
    viewModel.isLoading.value = true
    viewModel.signUp().observe(this, Observer { result ->
        viewModel.isLoading.value = false
        when(result.status){
            Status.SUCCESS ->{
                onBasicAlertNotify(title = "Alert",message = "You are successfully registered user",exit = true)
            }else ->{
            onBasicAlertNotify(message = result.message)
        }
        }
    })
}

private fun SignUpAct.execute(s : CharSequence?) {
    if (binding.textPutUserName == currentFocus){
        viewModel.email = s.toString()
    }
    else if (binding.textPutPassword == currentFocus){
        viewModel.password = s.toString()
    }
    else if (binding.textPutConfirmPassword == currentFocus){
        viewModel.confirmPassword = s.toString()
    }
    else{
        viewModel.phoneNumber = s.toString()
    }
}