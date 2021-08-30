package co.tpcreative.saveyourvoicemails.ui.changepassword

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
import co.tpcreative.saveyourvoicemails.databinding.ActivityChangePasswordBinding


class ChangePasswordAct : BaseActivity() {
    lateinit var binding: ActivityChangePasswordBinding

    val viewModel: ChangePasswordViewModel by viewModels {
        ViewModelFactory(DefaultServiceLocator.getInstance(application))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(
                binding.root
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
                    binding.edtOldPassword.error = ""
                    binding.edtNewPassword.error = ""
                    binding.edtConfirmPassword.error = ""
                    binding.edtConfirmPassword.error = ""
                    binding.btnChangePassword.background = ContextCompat.getDrawable(this, R.drawable.bg_button_rounded)
                    binding.btnChangePassword.setTextColor(ContextCompat.getColor(this, R.color.white))
                    binding.btnChangePassword.isEnabled = true
                }else{
                    binding.edtOldPassword.error = it[EnumValidationKey.EDIT_OLD_PASSWORD.name]
                    binding.edtNewPassword.error = it[EnumValidationKey.EDIT_NEW_PASSWORD.name]
                    binding.edtConfirmPassword.error = it[EnumValidationKey.EDIT_TEXT_CONFIRM_PASSWORD.name]
                    binding.btnChangePassword.background = ContextCompat.getDrawable(this, R.drawable.bg_button_disable_rounded)
                    binding.btnChangePassword.setTextColor(ContextCompat.getColor(this, R.color.white))
                    binding.btnChangePassword.isEnabled = false
                }
            }
        })

        viewModel.isLoading.observe(this,{ mResult ->
            if (mResult){
                SingletonManagerProcessing.getInstance()?.onStartProgressing(this, R.string.waiting)
            }else{
                SingletonManagerProcessing.getInstance()?.onStopProgressing(this)
            }
        })

        binding.textPutConfirmPassword.onDone {
            hideSoftKeyBoard(currentFocus)
        }

        viewModel.putError(EnumValidationKey.EDIT_OLD_PASSWORD,"")
        viewModel.putError(EnumValidationKey.EDIT_NEW_PASSWORD, "")
        viewModel.putError(EnumValidationKey.EDIT_TEXT_CONFIRM_PASSWORD,"")
    }
}
