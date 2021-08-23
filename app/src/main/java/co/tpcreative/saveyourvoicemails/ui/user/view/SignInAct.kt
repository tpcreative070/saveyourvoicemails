package co.tpcreative.saveyourvoicemails.ui.user.view
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import co.tpcreative.domain.models.EnumValidationKey
import co.tpcreative.saveyourvoicemails.Navigator
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.SingletonManagerProcessing
import co.tpcreative.saveyourvoicemails.common.ViewModelFactory
import co.tpcreative.saveyourvoicemails.common.base.BaseActivity
import co.tpcreative.saveyourvoicemails.common.base.log
import co.tpcreative.saveyourvoicemails.common.extension.textChanges
import co.tpcreative.saveyourvoicemails.common.services.DefaultServiceLocator
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import co.tpcreative.saveyourvoicemails.databinding.ActivitySignInBinding
import co.tpcreative.saveyourvoicemails.ui.user.viewmodel.UserViewModel
import kotlinx.coroutines.flow.*

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
                log(mResult)
                if (it.isEmpty()){
                    binding.edtUsername.error = ""
                    binding.edtPassword.error = ""
                    binding.btnSignIn.background = ContextCompat.getDrawable(this, R.drawable.bg_button_rounded)
                    binding.btnSignIn.setTextColor(ContextCompat.getColor(this,R.color.white))
                }else{
                    binding.edtUsername.error = it[EnumValidationKey.EDIT_TEXT_EMAIL.name]
                    binding.edtPassword.error = it[EnumValidationKey.EDIT_PASSWORD.name]
                    binding.btnSignIn.background = ContextCompat.getDrawable(this,R.drawable.bg_button_disable_rounded)
                    binding.btnSignIn.setTextColor(ContextCompat.getColor(this,R.color.white))
                }
            }
        })

        viewModel.isLoading.observe(this,{ mResult ->
            if (mResult){
                hideSoftKeyBoard(currentFocus)
                SingletonManagerProcessing.getInstance()?.onStartProgressing(this,R.string.waiting)
            }else{
                SingletonManagerProcessing.getInstance()?.onStopProgressing(this)
            }
        })

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
    }

    private fun execute(s : CharSequence?) {
        if (binding.textPutUserName == currentFocus){
            viewModel.email = s.toString()
        }else{
            viewModel.password = s.toString()
        }
    }

}