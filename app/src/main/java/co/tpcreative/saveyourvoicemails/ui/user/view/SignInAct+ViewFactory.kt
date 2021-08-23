package co.tpcreative.saveyourvoicemails.ui.user.view
import androidx.lifecycle.Observer
import co.tpcreative.saveyourvoicemails.common.network.Status

fun SignInAct.initUI(){

}

fun SignInAct.signIn(){
    viewModel.isLoading.value = true
    viewModel.signIn().observe(this, Observer { result ->
        viewModel.isLoading.value = false
        when(result.status){
            Status.SUCCESS ->{

            }else ->{
                onBasicAlertNotify(message = result.message)
            }
        }
    })
}