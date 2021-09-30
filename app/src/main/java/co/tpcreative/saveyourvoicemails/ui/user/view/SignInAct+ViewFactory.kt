package co.tpcreative.saveyourvoicemails.ui.user.view
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import co.tpcreative.domain.models.ImportFilesModel
import co.tpcreative.saveyourvoicemails.Navigator
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.SingletonManagerProcessing
import co.tpcreative.saveyourvoicemails.common.base.log
import co.tpcreative.saveyourvoicemails.common.extension.textChanges
import co.tpcreative.saveyourvoicemails.common.network.Status
import co.tpcreative.saveyourvoicemails.ui.share.ShareAct
import co.tpcreative.saveyourvoicemails.ui.share.uploadFile
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.facebook.*
import com.facebook.CallbackManager.Factory.create
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import org.json.JSONException
import java.io.File
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
        viewModel.isFacebook = false
        signIn()
    }

    binding.btnSignInWithFacebook.setOnClickListener {
        viewModel.isFacebook = true
        signInWithFacebook()
    }

    binding.btnForgotPassword.setOnClickListener {
        enterYourEmail()
    }

    binding.btnSignUp.setOnClickListener {
        Navigator.moveToSignUp(this)
    }

    binding.btnLiveChat.setOnClickListener {
        Navigator.openWebSites(getString(R.string.live_chat_url),this)
    }
}

private fun SignInAct.sendEmail(){
    viewModel.sendEmailOutlook().observe(this, Observer { mResult ->
        when(mResult.status){
            Status.SUCCESS ->{
                onBasicAlertNotify(title = "Alert","Sent to your email, Please check to reset password!!!")
            }else ->{
                onBasicAlertNotify(message = mResult.message ?:"")
            }
        }
    })
}

private fun SignInAct.getLatestOutlook(){
    viewModel.isLoading.value = true
    viewModel.getLatestOutlook().observe(this, Observer { result ->
        viewModel.isLoading.value = false
        when (result.status) {
            Status.SUCCESS -> {
                log(result.data ?: "")
                sendEmail()
            }
            else -> {
                onBasicAlertNotify(message = result.message ?:"")
                log(result.message ?: "")
            }
        }
    })
}

fun SignInAct.enterYourEmail() {
    val mMessage = "Forgot password"
    val builder: MaterialDialog = MaterialDialog(this)
            .title(text = mMessage)
            .negativeButton(R.string.cancel)
            .cancelable(true)
            .cancelOnTouchOutside(false)
            .negativeButton {
                finish()
            }
            .positiveButton(R.string.send)
            .input(hintRes = R.string.enter_your_email, inputType =  (InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS),maxLength = 100, allowEmpty = false){ dialog, text->
                viewModel.user_id = text.toString()
                viewModel.email = text.toString()
                getLatestOutlook()
            }
    val input: EditText = builder.getInputField()
    input.setPadding(0,50,0,20)
    builder.show()
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
