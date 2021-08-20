package co.tpcreative.saveyourvoicemails.ui.main
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import co.tpcreative.saveyourvoicemails.ui.list.AudioFragment
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.ViewModelFactory
import co.tpcreative.saveyourvoicemails.common.services.DefaultServiceLocator
import co.tpcreative.saveyourvoicemails.databinding.ActivityMainBinding
import co.tpcreative.saveyourvoicemails.ui.home.HomeViewModel
import com.pandora.bottomnavigator.BottomNavigator

class MainAct : AppCompatActivity() {

    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory(DefaultServiceLocator.getInstance(application))
    }
    private lateinit var navigator: BottomNavigator
    private lateinit var binding: ActivityMainBinding
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
                R.id.log to { AudioFragment() },
                R.id.setting to { AudioFragment() }
            ),
            defaultTab = R.id.home,
            activity = this
        )
        viewModel.doSearch()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}

class SampleFragment : Fragment() {

}
