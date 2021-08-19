package co.tpcreative.presentation.ui.main
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import co.tpcreative.presentation.R
import co.tpcreative.presentation.databinding.ActivityMainBinding
import co.tpcreative.presentation.ui.list.AudioFragment
import com.pandora.bottomnavigator.BottomNavigator

class MainAct : AppCompatActivity() {
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
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}

class SampleFragment : Fragment() {

}
