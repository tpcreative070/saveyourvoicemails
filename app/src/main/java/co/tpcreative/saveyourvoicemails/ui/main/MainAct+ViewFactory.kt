package co.tpcreative.saveyourvoicemails.ui.main

import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.Utils.log
import co.tpcreative.saveyourvoicemails.common.extension.getIsSubscribed
import co.tpcreative.saveyourvoicemails.common.extension.getString
import co.tpcreative.saveyourvoicemails.common.extension.putSubscription
import com.google.gson.Gson
import org.solovyev.android.checkout.*
import org.solovyev.android.checkout.Checkout.EmptyListener


fun MainAct.initUI(){
    initCheckout()
}

private fun MainAct.initCheckout(){
    mCheckout.start()
    mInventory = mCheckout.makeInventory()
    mInventory?.load(
        Inventory.Request.create()
            .loadAllPurchases()
            .loadSkus(ProductTypes.SUBSCRIPTION, getString(R.string.key_subscription_one_year)),
        InventoryCallback()
    )
}

private class InventoryCallback : Inventory.Callback {
    override fun onLoaded(products: Inventory.Products) {
        // your code here
        val mProduct = products.get(ProductTypes.SUBSCRIPTION)
        val mPurchased = mProduct.getPurchaseInState(getString(R.string.key_subscription_one_year),Purchase.State.PURCHASED)
        if (mPurchased!=null){
            Utils.putSubscription( mPurchased.autoRenewing)
        }else{
            Utils.putSubscription(false)
        }
        log(this::class.java,"this is product ${Gson().toJson(mProduct)}")
        log(this::class.java,products)
    }
}