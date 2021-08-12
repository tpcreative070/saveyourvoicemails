package co.tpcreative.saveyourvoicemails.presentationlayer.home.view
import android.os.Bundle
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.activity.BaseActivity
import co.tpcreative.saveyourvoicemails.common.activity.log
import co.tpcreative.saveyourvoicemails.helper.ServiceHelper

class HomeAct : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        ServiceHelper.getInstance().onStartService()
        log("HomeAct")
    }
}