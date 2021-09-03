package co.tpcreative.saveyourvoicemails.ui.subscription
import android.content.Intent
import android.os.Bundle
import co.tpcreative.saveyourvoicemails.common.base.BaseActivity
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import co.tpcreative.saveyourvoicemails.databinding.ActivitySubscriptionBinding
import org.solovyev.android.checkout.Checkout
import org.solovyev.android.checkout.Inventory

class SubscriptionAct : BaseActivity() {

    lateinit var binding: ActivitySubscriptionBinding
    val mCheckout = Checkout.forActivity(
            this,
            SaveYourVoiceMailsApplication.getInstance().getBilling()
    )
    var mInventory: Inventory? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubscriptionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initUI()
        startSubscription()
    }

    override fun onDestroy() {
        mCheckout.stop()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mCheckout.onActivityResult(requestCode, resultCode, data)
    }

}