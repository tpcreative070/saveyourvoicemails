package co.tpcreative.presentation.ui.user.view
import android.os.Bundle
import co.tpcreative.presentation.R
import co.tpcreative.presentation.common.activity.BaseActivity
import co.tpcreative.presentation.common.services.NetworkConnectivityHelper
import javax.inject.Inject

class SignUpAct : BaseActivity() {
    @Inject
    lateinit var networkConnectivity: NetworkConnectivityHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
    }
}