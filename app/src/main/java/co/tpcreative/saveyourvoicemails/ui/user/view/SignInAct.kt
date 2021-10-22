package co.tpcreative.saveyourvoicemails.ui.user.view
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import co.tpcreative.domain.models.EnumValidationKey
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.SingletonManagerProcessing
import co.tpcreative.saveyourvoicemails.common.ViewModelFactory
import co.tpcreative.saveyourvoicemails.common.base.BaseActivity
import co.tpcreative.saveyourvoicemails.common.base.log
import co.tpcreative.saveyourvoicemails.common.services.DefaultServiceLocator
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import co.tpcreative.saveyourvoicemails.databinding.ActivitySignInBinding
import co.tpcreative.saveyourvoicemails.ui.user.viewmodel.UserViewModel
import com.facebook.CallbackManager
import kotlinx.coroutines.flow.*

class SignInAct : BaseActivity() {
    var callbackmanager: CallbackManager? = null

    lateinit var binding: ActivitySignInBinding
    val viewModel : UserViewModel by viewModels {
        ViewModelFactory(DefaultServiceLocator.getInstance(SaveYourVoiceMailsApplication.getInstance()))
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
        initUI()
    }

    private fun bindingEvent(){
        viewModel.errorMessages.observe(this, { mResult ->
            mResult?.let {
                log(mResult)
                if (it.isEmpty()) {
                    binding.edtUsername.error = ""
                    binding.edtPassword.error = ""
                    binding.btnSignIn.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_button_rounded
                    )
                    binding.btnSignIn.setTextColor(ContextCompat.getColor(this, R.color.white))
                    binding.btnSignIn.isEnabled = true
                } else {
                    binding.edtUsername.error = it[EnumValidationKey.EDIT_TEXT_EMAIL.name]
                    binding.edtPassword.error = it[EnumValidationKey.EDIT_PASSWORD.name]
                    binding.btnSignIn.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.bg_button_disable_rounded
                    )
                    binding.btnSignIn.setTextColor(ContextCompat.getColor(this, R.color.white))
                    binding.btnSignIn.isEnabled = false
                }
            }
        })

        viewModel.isLoading.observe(this, { mResult ->
            if (mResult) {
                hideSoftKeyBoard(currentFocus)
                SingletonManagerProcessing.getInstance()?.onStartProgressing(this, R.string.waiting)
            } else {
                SingletonManagerProcessing.getInstance()?.onStopProgressing()
            }
        })

        viewModel.putError(EnumValidationKey.EDIT_TEXT_EMAIL, "")
        viewModel.putError(EnumValidationKey.EDIT_PASSWORD, "")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        log("request code $requestCode, result code $resultCode")
        callbackmanager?.onActivityResult(
            requestCode,
            resultCode,
            data
        )
    }
}