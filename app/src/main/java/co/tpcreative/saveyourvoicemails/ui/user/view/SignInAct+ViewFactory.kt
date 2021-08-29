package co.tpcreative.saveyourvoicemails.ui.user.view
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import co.tpcreative.saveyourvoicemails.Navigator
import co.tpcreative.saveyourvoicemails.common.base.log
import co.tpcreative.saveyourvoicemails.common.extension.textChanges
import co.tpcreative.saveyourvoicemails.common.network.Status
import com.facebook.*
import com.facebook.CallbackManager.Factory.create
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import org.json.JSONException
import java.util.*

fun SignInAct.initUI(){
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

    binding.btnSignIn.setOnClickListener {
        viewModel.user_id = viewModel.email
        signIn()
    }

    binding.btnSignInWithFacebook.setOnClickListener {
        signInWithFacebook()
    }
}

private fun SignInAct.signIn(){
    viewModel.isLoading.value = true
    viewModel.signIn().observe(this, Observer { result ->
        viewModel.isLoading.value = false
        when (result.status) {
            Status.SUCCESS -> {
                Navigator.moveToMain(this)
            }
            else -> {
                onBasicAlertNotify(message = result.message)
            }
        }
    })
}

private fun SignInAct.getUser(){
    viewModel.isLoading.value = true
    viewModel.getUser().observe(this, Observer { result ->
        when (result.status) {
            Status.SUCCESS -> {
               log(result.data ?: "")
                signIn()
            }
            else -> {
                viewModel.signUp().observe(this, Observer { mResultSignUp ->
                    when(mResultSignUp.status){
                        Status.SUCCESS ->{
                            signIn()
                        }else ->{
                            log(mResultSignUp.message ?:"")
                        }
                    }
                })
            }
        }
    })
}

private fun SignInAct.execute(s: CharSequence?) {
    if (binding.textPutUserName == currentFocus){
        viewModel.email = s.toString()
    }else{
        viewModel.password = s.toString()
    }
}

private fun SignInAct.signInWithFacebook(){
    callbackmanager = create()
    LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile","email"))
    LoginManager.getInstance()
        .registerCallback(callbackmanager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    log( "Success login")
                    val request = GraphRequest.newMeRequest(
                        loginResult.accessToken
                    ) { mObject, response ->
                        if (response!!.error != null) {
                            // handle error
                            log("error ???")
                        } else {
                            log("success")
                            try {
                                val jsonResult = mObject.toString()
                                log("JSON Result$jsonResult")
                                val id  = mObject?.getString("id")
                                val email = mObject?.getString("email")
                                if (email != null) {
                                    log("id $id")
                                    viewModel.user_id = id ?: email
                                    viewModel.email = email
                                    viewModel.password = id ?:""
                                    viewModel.confirmPassword = id ?:""
                                    viewModel.phoneNumber = "null"
                                    getUser()
                                } else {
                                    Toast.makeText(this@signInWithFacebook, "Could not find email", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                log("error" + e.message)
                            }
                        }
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id,first_name,last_name,email,gender,birthday")
                    request.parameters = parameters
                    request.executeAsync()
                }
                override fun onCancel() {
                    log("On cancel")
                }
                override fun onError(error: FacebookException) {
                   log(error.toString())
                }
            })
}
