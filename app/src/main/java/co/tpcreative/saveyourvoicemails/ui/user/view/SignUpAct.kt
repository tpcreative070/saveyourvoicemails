package co.tpcreative.saveyourvoicemails.ui.user.view
import android.os.Bundle
import androidx.activity.viewModels
import co.tpcreative.domain.models.EnumValidationKey
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.ViewModelFactory
import co.tpcreative.saveyourvoicemails.common.base.BaseActivity
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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(
            binding.apply {
                viewmodel = this@SignUpAct.viewModel
                lifecycleOwner = this@SignUpAct
            }.root
        )
        initUI()
    }

    private fun bindingEvent(){
        viewModel.putError(EnumValidationKey.EDIT_TEXT_EMAIL,"")
        viewModel.putError(EnumValidationKey.EDIT_PASSWORD, "")
    }
}