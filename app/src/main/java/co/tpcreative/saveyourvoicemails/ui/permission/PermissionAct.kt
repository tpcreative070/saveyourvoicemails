package co.tpcreative.saveyourvoicemails.ui.permission
import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.base.BaseActivity
import co.tpcreative.saveyourvoicemails.common.services.MyAccessibilityService
import co.tpcreative.saveyourvoicemails.databinding.ActivityPermissionBinding
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class PermissionAct : BaseActivity() {
    private lateinit var binding: ActivityPermissionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.btnGrantPermissions.setOnClickListener {
            requestPermissions()
        }
        if (isAccessibilityServiceEnabled(this@PermissionAct,  MyAccessibilityService::class.java)){
            binding.btnGrantPermissions.visibility = View.INVISIBLE
            binding.imgTurnOnRecorderAppConnectorToRecordCallsOnAndroid10.setImageDrawable(ContextCompat.getDrawable(this@PermissionAct,R.drawable.ic_baseline_check_circle_outline_24))
        }else{
            binding.imgTurnOnRecorderAppConnectorToRecordCallsOnAndroid10.setImageDrawable(ContextCompat.getDrawable(this@PermissionAct,R.drawable.ic_baseline_dangerous_24))
            binding.btnGrantPermissions.visibility = View.VISIBLE
        }
    }

    private fun requestPermissions() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_CALL_LOG
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (isAccessibilityServiceEnabled(this@PermissionAct,  MyAccessibilityService::class.java)){
                        binding.imgTurnOnRecorderAppConnectorToRecordCallsOnAndroid10.setImageDrawable(ContextCompat.getDrawable(this@PermissionAct,R.drawable.ic_baseline_check_circle_outline_24))
                    }else{
                        binding.imgTurnOnRecorderAppConnectorToRecordCallsOnAndroid10.setImageDrawable(ContextCompat.getDrawable(this@PermissionAct,R.drawable.ic_baseline_dangerous_24))
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

    private fun isAccessibilityServiceEnabled(context: Context, accessibilityService: Class<*>?): Boolean {
        val expectedComponentName = ComponentName(context, accessibilityService!!)
        val enabledServicesSetting =
            Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
                ?: return false
        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServicesSetting)
        while (colonSplitter.hasNext()) {
            val componentNameString = colonSplitter.next()
            val enabledService = ComponentName.unflattenFromString(componentNameString)
            if (enabledService != null && enabledService == expectedComponentName) return true
        }
        return false
    }

    private fun alertDialog() {
        val builder: MaterialDialog = MaterialDialog(this)
            .title(text = getString(R.string.find_enable_Voicemails))
            .customView(R.layout.dialog_custom)
            .positiveButton(text = getString(R.string.ok))
            .cancelable(false)
            .positiveButton {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                startActivity(intent)
                finish()
            }
        val customView: View = builder.getCustomView()
        val imgGuide : AppCompatImageView = customView.findViewById(R.id.imgGuide)
        Glide
            .with(this)
            .asGif()
            .load(R.raw.guide_turn_on_service_dark_animation)
            .into(imgGuide);
        builder.show()
    }


}