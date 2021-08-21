package co.tpcreative.saveyourvoicemails.ui.user.view
import android.os.Bundle
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import co.tpcreative.saveyourvoicemails.Navigator
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.ViewModelFactory
import co.tpcreative.saveyourvoicemails.common.base.BaseActivity
import co.tpcreative.saveyourvoicemails.common.base.log
import co.tpcreative.saveyourvoicemails.common.services.DefaultServiceLocator
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import co.tpcreative.saveyourvoicemails.databinding.ActivitySignInBinding
import co.tpcreative.saveyourvoicemails.ui.user.viewmodel.UserViewModel

class SignInAct : BaseActivity() {

    private lateinit var binding: ActivitySignInBinding
    private val viewModel : UserViewModel by viewModels {
        ViewModelFactory(DefaultServiceLocator.getInstance(SaveYourVoiceMailsApplication.getInstance()) )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(
            binding.apply {
                viewmodel = this@SignInAct.viewModel
                lifecycleOwner = this@SignInAct
            }.root
        )
        bindingEvent()
        binding.textPutUserName.addTextChangedListener(mTextWatcher)
        //binding.edtUsername.error = "This is error"
    }

    private fun bindingEvent(){
        viewModel.requestSignUp.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let { username ->
                Navigator.moveToSignUp(this)
            }
        })

        viewModel.requestForgotPassword.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let { value ->
               log(value)
            }
        })

        viewModel.errorMessages.observe( this,{ mResult->
            mResult?.let {
                if (it.isEmpty()){
                    binding.btnSignIn.background = ContextCompat.getDrawable(this, R.drawable.bg_button_rounded)
                    binding.btnSignIn.setTextColor(ContextCompat.getColor(this,R.color.white))
                }else{
                    binding.btnSignIn.background = ContextCompat.getDrawable(this,R.drawable.bg_button_disable_rounded)
                    binding.btnSignIn.setTextColor(ContextCompat.getColor(this,R.color.white))
                }
            }
        })
    }

    /*Detecting textWatch*/
    val mTextWatcher: TextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            viewModel.email = s.toString()
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: android.text.Editable?) {}
    }
}