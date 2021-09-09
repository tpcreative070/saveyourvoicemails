package co.tpcreative.saveyourvoicemails.ui.main

import androidx.lifecycle.Observer
import co.tpcreative.domain.models.EnType
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.Utils.log
import co.tpcreative.saveyourvoicemails.common.base.log
import co.tpcreative.saveyourvoicemails.common.extension.*
import co.tpcreative.saveyourvoicemails.common.network.Status
import com.google.gson.Gson
import org.solovyev.android.checkout.*


fun MainAct.initUI() {
    initCheckout()
}

fun MainAct.sendingEmail(enType: EnType) {
    if (!Utils.isSignedIn()) {
        log("Please sign in to using this feature")
        return
    }
    if (enType == EnType.NEW_USER) {
        if (Utils.getIsSentDownloaded()) {
            return
        }
    }
    if (enType == EnType.NEW_STORE_FILES_SUBSCRIPTION) {
        if (Utils.getIsSentSubscribed()) {
            return
        }
    }
    viewModel.sendEmailOutlook(enType).observe(this, Observer { mResult ->
        when (mResult.status) {
            Status.SUCCESS -> {
                log(mResult.data ?: "")
            }
            else -> {
                log(mResult.message ?: "")
            }
        }
    })
}

private fun MainAct.initCheckout() {
    mCheckout.start()
    mInventory = mCheckout.makeInventory()
    mInventory?.load(
            Inventory.Request.create()
                    .loadAllPurchases()
                    .loadSkus(ProductTypes.SUBSCRIPTION, getString(R.string.key_subscription_one_year)), object : Inventory.Callback {
        override fun onLoaded(products: Inventory.Products) {
            val mProduct = products.get(ProductTypes.SUBSCRIPTION)
            val mPurchased = mProduct.getPurchaseInState(co.tpcreative.saveyourvoicemails.common.extension.getString(R.string.key_subscription_one_year), Purchase.State.PURCHASED)
            if (mPurchased != null) {
                Utils.putSubscription(mPurchased.autoRenewing)
            } else {
                Utils.putSubscription(false)
            }
            if (Utils.getIsSubscribed()) {
                sendingEmail(EnType.NEW_STORE_FILES_SUBSCRIPTION)
            }
            log(this::class.java, "this is product ${Gson().toJson(mProduct)}")
            log(this::class.java, products)
        }
    })
}