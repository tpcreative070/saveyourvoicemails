package co.tpcreative.saveyourvoicemails.ui.user.view
import android.os.Bundle
import androidx.activity.viewModels
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.ViewModelFactory
import co.tpcreative.saveyourvoicemails.common.base.BaseActivity
import co.tpcreative.saveyourvoicemails.common.services.DefaultServiceLocator
import co.tpcreative.saveyourvoicemails.ui.user.viewmodel.UserViewModel


class SignUpAct : BaseActivity() {
    private val viewModel: UserViewModel by viewModels {
        ViewModelFactory(DefaultServiceLocator.getInstance(application))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        viewModel.doSearch()
    }
}