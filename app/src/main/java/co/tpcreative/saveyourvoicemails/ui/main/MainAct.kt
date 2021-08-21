package co.tpcreative.saveyourvoicemails.ui.main
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import co.tpcreative.saveyourvoicemails.Navigator
import co.tpcreative.saveyourvoicemails.ui.list.AudioFragment
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.ViewModelFactory
import co.tpcreative.saveyourvoicemails.common.base.BaseActivity
import co.tpcreative.saveyourvoicemails.common.services.DefaultServiceLocator
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import co.tpcreative.saveyourvoicemails.databinding.ActivityMainBinding
import co.tpcreative.saveyourvoicemails.ui.me.MeFragment
import co.tpcreative.saveyourvoicemails.ui.settings.SettingFragment
import com.pandora.bottomnavigator.BottomNavigator

class MainAct : BaseActivity() {
    private lateinit var navigator: BottomNavigator
    private lateinit var binding: ActivityMainBinding


    private val viewModel: MainActViewModel by viewModels {
        ViewModelFactory(DefaultServiceLocator.getInstance(SaveYourVoiceMailsApplication.getInstance()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        navigator = BottomNavigator.onCreate(
            fragmentContainer = R.id.fragment_container,
            bottomNavigationView = binding.bottomnavView,
            rootFragmentsFactory =  mapOf(
                R.id.home to { AudioFragment() },
                R.id.log to { MeFragment() },
                R.id.setting to { SettingFragment() }
            ),
            defaultTab = R.id.home,
            activity = this
        )
        bindingEvent()
    }

    private fun bindingEvent(){
        viewModel.onSignIn.observe(this, Observer {
            Navigator.moveToSignIn(this)
        })
        viewModel.checkSignedIn()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}

class SampleFragment : Fragment() {

}
