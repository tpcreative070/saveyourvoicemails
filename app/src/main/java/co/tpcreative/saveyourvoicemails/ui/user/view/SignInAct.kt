package co.tpcreative.saveyourvoicemails.ui.user.view
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import co.tpcreative.saveyourvoicemails.Navigator
import co.tpcreative.saveyourvoicemails.common.ViewModelFactory
import co.tpcreative.saveyourvoicemails.common.services.DefaultServiceLocator
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import co.tpcreative.saveyourvoicemails.databinding.ActivitySignInBinding
import co.tpcreative.saveyourvoicemails.ui.user.viewmodel.UserViewModel

class SignInAct : AppCompatActivity() {
    //private lateinit var binding: ActivitySignInBinding
    private val viewModel : UserViewModel by viewModels {
        ViewModelFactory(DefaultServiceLocator.getInstance(SaveYourVoiceMailsApplication.getInstance()) )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            ActivitySignInBinding.inflate(layoutInflater).apply {
                viewmodel = this@SignInAct.viewModel
                lifecycleOwner = this@SignInAct
            }.root
        )
        bindingEvent()
    }

    private  fun bindingEvent(){
        viewModel.requestSignUp.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let { username ->
                Navigator.moveToSignUp(this)
            }
        })
    }

    private  fun registerOnClicked(){

    }
}