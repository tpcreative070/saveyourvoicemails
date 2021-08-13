package co.tpcreative.saveyourvoicemails.presentationlayer.home.view

import android.Manifest
import android.os.Bundle
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.activity.BaseActivity
import co.tpcreative.saveyourvoicemails.common.activity.log
import co.tpcreative.saveyourvoicemails.helper.ServiceHelper
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener


class HomeAct : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        ServiceHelper.getInstance().onStartService()
        log("HomeAct")

        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.BIND_ACCESSIBILITY_SERVICE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.PROCESS_OUTGOING_CALLS,
                Manifest.permission.MODIFY_AUDIO_SETTINGS
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) { /* ... */
                    finish()
                }
                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) { /* ... */
                }
            }).check()
    }
}