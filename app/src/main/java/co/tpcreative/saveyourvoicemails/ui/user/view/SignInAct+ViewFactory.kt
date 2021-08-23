package co.tpcreative.saveyourvoicemails.ui.user.view
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import co.tpcreative.saveyourvoicemails.Navigator
import co.tpcreative.saveyourvoicemails.common.extension.textChanges
import co.tpcreative.saveyourvoicemails.common.network.Status
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce

fun SignInAct.initUI(){
    lifecycleScope.launchWhenResumed {
        binding.textPutUserName.textChanges()
            .debounce(300)
            .collect {
                execute(it)
            }
    }
    lifecycleScope.launchWhenResumed {
        binding.textPutPassword.textChanges()
            .debounce(300)
            .collect {
                execute(it)
            }
    }

    binding.btnSignIn.setOnClickListener {
        signIn()
    }
}

private fun SignInAct.signIn(){
    viewModel.isLoading.value = true
    viewModel.signIn().observe(this, Observer { result ->
        viewModel.isLoading.value = false
        when(result.status){
            Status.SUCCESS ->{
                Navigator.moveToMain(this)
            }else ->{
                onBasicAlertNotify(message = result.message)
            }
        }
    })
}

private fun SignInAct.execute(s : CharSequence?) {
    if (binding.textPutUserName == currentFocus){
        viewModel.email = s.toString()
    }else{
        viewModel.password = s.toString()
    }
}