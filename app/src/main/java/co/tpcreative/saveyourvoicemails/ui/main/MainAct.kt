package co.tpcreative.saveyourvoicemails.ui.main

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils.SimpleStringSplitter
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import co.tpcreative.saveyourvoicemails.Navigator
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.ViewModelFactory
import co.tpcreative.saveyourvoicemails.common.base.BaseActivity
import co.tpcreative.saveyourvoicemails.common.services.DefaultServiceLocator
import co.tpcreative.saveyourvoicemails.common.services.MyAccessibilityService
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import co.tpcreative.saveyourvoicemails.databinding.ActivityMainBinding
import co.tpcreative.saveyourvoicemails.ui.list.AudioFragment
import co.tpcreative.saveyourvoicemails.ui.me.MeFragment
import co.tpcreative.saveyourvoicemails.ui.settings.SettingsFragment
import com.afollestad.materialdialogs.MaterialDialog
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
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
            rootFragmentsFactory = mapOf(
                R.id.home to { AudioFragment() },
                R.id.log to { MeFragment() },
                R.id.setting to { SettingsFragment() }
            ),
            defaultTab = R.id.home,
            activity = this
        )
        bindingEvent()
        requestPermissions()
    }

    private fun requestPermissions() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.CAPTURE_AUDIO_OUTPUT,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_CALL_LOG
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    val enabled: Boolean = isAccessibilityServiceEnabled(
                            this@MainAct,
                            MyAccessibilityService::class.java
                        )
                    if (!enabled) {
                        alertDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                }
            })
            .check()
    }

    private fun alertDialog() {
        val builder: MaterialDialog = MaterialDialog(this)
            .title(text = getString(R.string.alert))
            .message(res = R.string.please_turn_on_service)
            .positiveButton(text = getString(R.string.ok))
            .cancelable(false)
            .positiveButton {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                startActivity(intent)
            }
        builder.show()
    }


    fun isAccessibilityServiceEnabled(context: Context, accessibilityService: Class<*>?): Boolean {
        val expectedComponentName = ComponentName(context, accessibilityService!!)
        val enabledServicesSetting =
            Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
                ?: return false
        val colonSplitter = SimpleStringSplitter(':')
        colonSplitter.setString(enabledServicesSetting)
        while (colonSplitter.hasNext()) {
            val componentNameString = colonSplitter.next()
            val enabledService = ComponentName.unflattenFromString(componentNameString)
            if (enabledService != null && enabledService == expectedComponentName) return true
        }
        return false
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
