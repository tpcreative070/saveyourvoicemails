package co.tpcreative.saveyourvoicemails.ui.subscription

import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.extension.getIsSubscribed
import co.tpcreative.saveyourvoicemails.common.extension.getString
import co.tpcreative.saveyourvoicemails.common.extension.putSubscription
import com.google.gson.Gson
import org.solovyev.android.checkout.*


fun SubscriptionAct.initUI(){
    initCheckout()
}

private fun SubscriptionAct.initCheckout(){
    mCheckout.start()
    mCheckout.createPurchaseFlow(PurchaseListener())
    mInventory = mCheckout.makeInventory()
    mInventory?.load(
        Inventory.Request.create()
            .loadAllPurchases()
            .loadSkus(ProductTypes.SUBSCRIPTION, getString(R.string.key_subscription_one_year)),
        InventoryCallback()
    )
}

fun SubscriptionAct.startSubscription(){
    if (Utils.getIsSubscribed()){
        return
    }
    mCheckout.whenReady(object : Checkout.EmptyListener() {
        override fun onReady(requests: BillingRequests) {
            requests.purchase(ProductTypes.SUBSCRIPTION,getString(R.string.key_subscription_one_year), null, mCheckout.purchaseFlow)
        }
    })
}

private class PurchaseListener : EmptyRequestListener<Purchase>() {
    override fun onSuccess(purchase: Purchase) {
        // here you can process the loaded purchase
        Utils.putSubscription(true)
        Utils.log(this::class.java, "result ${Gson().toJson(purchase)}")
    }

    override fun onError(response: Int, e: Exception) {
        // handle errors here
        Utils.log(this::class.java, "Error occurred ${e.message}")
    }
}

private class InventoryCallback : Inventory.Callback {
    override fun onLoaded(products: Inventory.Products) {
        // your code here
        val mProduct = products.get(ProductTypes.SUBSCRIPTION)
        val mPurchased = mProduct.getPurchaseInState(
            getString(R.string.key_subscription_one_year),
            Purchase.State.PURCHASED)
        if (mPurchased!=null){
            Utils.putSubscription(true)
        }else{
            Utils.putSubscription(false)
        }
        Utils.log(this::class.java, "this is product ${Gson().toJson(mProduct)}")
        Utils.log(this::class.java, products)
    }
}