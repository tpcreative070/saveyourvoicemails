package co.tpcreative.saveyourvoicemails.ui.settings
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import co.tpcreative.saveyourvoicemails.BuildConfig
import co.tpcreative.saveyourvoicemails.Navigator
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.base.BaseFragment
import co.tpcreative.saveyourvoicemails.common.base.log
import co.tpcreative.saveyourvoicemails.common.controller.EncryptedPreferenceDataStore
import co.tpcreative.saveyourvoicemails.common.extension.instantiate
import co.tpcreative.saveyourvoicemails.common.preference.MyPreference
import co.tpcreative.saveyourvoicemails.databinding.FragmentAudioBinding
import co.tpcreative.saveyourvoicemails.databinding.FragmentSettingsBinding

class SettingsFragment : BaseFragment(){
    lateinit var binding: FragmentSettingsBinding
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("Calling apis")
    }


    override fun work() {
        super.work()
        val fragment = this.activity?.supportFragmentManager?.instantiate(SettingsFragment::class.java.name)
        val transaction: FragmentTransaction? = this.activity?.supportFragmentManager?.beginTransaction()
        if (fragment != null) {
            transaction?.replace(R.id.content_frame, fragment)
        }
        transaction?.commit()
    }

    override fun getLayoutId(): Int {
        return 0
    }

    override fun getLayoutId(inflater: LayoutInflater?, viewGroup: ViewGroup?): View? {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        private var mPerMission: MyPreference? = null
        private var mVersion: MyPreference? = null

        private fun createChangeListener(): Preference.OnPreferenceChangeListener? {
            return Preference.OnPreferenceChangeListener { preference, newValue -> true }
        }

        private fun createActionPreferenceClickListener(): Preference.OnPreferenceClickListener? {
            return Preference.OnPreferenceClickListener { preference ->
                if (preference is MyPreference){
                    if (preference.key == getString(R.string.key_permission)){
                        Navigator.moveToPermission(this.requireContext())
                    }
                }
                true
            }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            mPerMission = findPreference(getString(R.string.key_permission)) as MyPreference?
            mPerMission?.onPreferenceClickListener = createActionPreferenceClickListener()

            /*Version*/
            mVersion = findPreference(getString(R.string.key_version)) as MyPreference?
            mVersion?.onPreferenceChangeListener = createChangeListener()
            mVersion?.onPreferenceClickListener = createActionPreferenceClickListener()
            mVersion?.summary = String.format(getString(R.string.key_voicemails_version), BuildConfig.VERSION_NAME)
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.preferenceDataStore = EncryptedPreferenceDataStore.getInstance(requireContext())
            addPreferencesFromResource(R.xml.pref_general)
        }
    }
}