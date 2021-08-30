package co.tpcreative.saveyourvoicemails.ui.changepassword

import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import co.tpcreative.saveyourvoicemails.common.base.log
import co.tpcreative.saveyourvoicemails.common.extension.textChanges
import co.tpcreative.saveyourvoicemails.common.network.Status
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce

fun ChangePasswordAct.initUI(){
    lifecycleScope.launchWhenResumed {
        binding.textPutOldPassword.textChanges()
            .debounce(200)
            .collect {
                execute(it)
            }
    }
    lifecycleScope.launchWhenResumed {
        binding.textPutNewPassword.textChanges()
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

    binding.btnChangePassword.setOnClickListener {
        changePassword()
    }
}

private fun ChangePasswordAct.changePassword(){
    viewModel.changePassword().observe(this, Observer { mResult ->
        when(mResult.status){
            Status.SUCCESS ->{
                onBasicAlertNotify("Alert",message = mResult.data?.message,exit = true)
            }else ->{
                onBasicAlertNotify(message = mResult.message?:"")
            }
        }
    })
}

private fun ChangePasswordAct.execute(s : CharSequence?) {
    if (binding.textPutOldPassword == currentFocus){
        viewModel.oldPassword = s.toString()
    }
    else if (binding.textPutNewPassword == currentFocus){
        viewModel.newPassword = s.toString()
    }
    else if (binding.textPutConfirmPassword == currentFocus){
        viewModel.confirmPassword = s.toString()
    }else{
        log("Nothing")
    }
}