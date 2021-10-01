package co.tpcreative.saveyourvoicemails.ui.user.view
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import co.tpcreative.domain.models.EnumValidationKey
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.SingletonManagerProcessing
import co.tpcreative.saveyourvoicemails.common.ViewModelFactory
import co.tpcreative.saveyourvoicemails.common.base.BaseActivity
import co.tpcreative.saveyourvoicemails.common.base.log
import co.tpcreative.saveyourvoicemails.common.extension.onDone
import co.tpcreative.saveyourvoicemails.common.services.DefaultServiceLocator
import co.tpcreative.saveyourvoicemails.databinding.ActivitySignUpBinding
import co.tpcreative.saveyourvoicemails.ui.user.viewmodel.UserViewModel


class SignUpAct : BaseActivity() {

    lateinit var binding: ActivitySignUpBinding

    val viewModel: UserViewModel by viewModels {
        ViewModelFactory(DefaultServiceLocator.getInstance(application))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(
            binding.apply {
                viewmodel = this@SignUpAct.viewModel
                lifecycleOwner = this@SignUpAct
            }.root
        )
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initUI()
        bindingEvent()
    }

    private fun bindingEvent(){
        viewModel.errorMessages.observe( this,{ mResult->
            mResult?.let {
                log(mResult)
                if (it.isEmpty()){
                    binding.edtUsername.error = ""
                    binding.edtPassword.error = ""
                    binding.edtConfirmPassword.error = ""
                    binding.edtContactNo.error = ""
                    binding.btnSignUp.background = ContextCompat.getDrawable(this, R.drawable.bg_button_rounded)
                    binding.btnSignUp.setTextColor(ContextCompat.getColor(this,R.color.white))
                    binding.btnSignUp.isEnabled = true
                }else{
                    binding.edtUsername.error = it[EnumValidationKey.EDIT_TEXT_EMAIL.name]
                    binding.edtPassword.error = it[EnumValidationKey.EDIT_PASSWORD.name]
                    binding.edtConfirmPassword.error = it[EnumValidationKey.EDIT_TEXT_CONFIRM_PASSWORD.name]
                    binding.edtContactNo.error = it[EnumValidationKey.EDIT_TEXT_PHONE_NUMBER.name]
                    binding.btnSignUp.background = ContextCompat.getDrawable(this,R.drawable.bg_button_disable_rounded)
                    binding.btnSignUp.setTextColor(ContextCompat.getColor(this,R.color.white))
                    binding.btnSignUp.isEnabled = false
                }
            }
        })

        viewModel.isLoading.observe(this,{ mResult ->
            if (mResult){
                SingletonManagerProcessing.getInstance()?.onStartProgressing(this,R.string.waiting)
            }else{
                SingletonManagerProcessing.getInstance()?.onStopProgressing()
            }
        })

        binding.textPutPhoneNumber.onDone {
            hideSoftKeyBoard(currentFocus)
        }

        viewModel.putError(EnumValidationKey.EDIT_TEXT_EMAIL,"")
        viewModel.putError(EnumValidationKey.EDIT_PASSWORD, "")
        viewModel.putError(EnumValidationKey.EDIT_TEXT_CONFIRM_PASSWORD,"")
        viewModel.putError(EnumValidationKey.EDIT_TEXT_PHONE_NUMBER,"")
    }
}