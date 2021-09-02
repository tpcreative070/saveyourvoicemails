package co.tpcreative.saveyourvoicemails.ui.list

import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import co.tpcreative.domain.models.request.DownloadFileRequest
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.SingletonManagerProcessing
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.base.log
import co.tpcreative.saveyourvoicemails.common.controller.ServiceManager
import co.tpcreative.saveyourvoicemails.common.extension.getIsSubscribed
import co.tpcreative.saveyourvoicemails.common.extension.getString
import co.tpcreative.saveyourvoicemails.common.extension.putSubscription
import co.tpcreative.saveyourvoicemails.common.extension.textChanges
import co.tpcreative.saveyourvoicemails.common.network.Status
import co.tpcreative.saveyourvoicemails.common.view.NpaGridLayoutManager
import co.tpcreative.saveyourvoicemails.common.view.addListOfDecoration
import co.tpcreative.saveyourvoicemails.ui.main.MainAct
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.solovyev.android.checkout.*

@ExperimentalStdlibApi
fun AudioFragment.initUI(){
    lifecycleScope.launchWhenResumed {
        binding.edtSearch.textChanges()
            .debounce(200)
            .collect {
                execute(it)
            }
    }

    binding.edtSearch.setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            hideSoftKeyBoard(activity?.currentFocus)
        }
        true
    }

    binding.imgClear.setOnClickListener {
        binding.edtSearch.setText("")
        hideSoftKeyBoard(activity?.currentFocus)
    }
    initCheckout()
}
fun AudioFragment.initRecycleView(layoutInflater: LayoutInflater) {
    try {
        gridLayoutManager = NpaGridLayoutManager(this.context, AudioAdapter.SPAN_COUNT_ONE)
        adapter =
            AudioAdapter(gridLayoutManager, layoutInflater, this.context, this@initRecycleView)
        binding.recyclerView.layoutManager = gridLayoutManager
        this.context?.let { binding.recyclerView.addListOfDecoration(it) }
        binding.recyclerView.adapter = adapter
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun AudioFragment.getData() {
    viewModel.isLoading.postValue(true)
    viewModel.getVoiceMail().observe(this, Observer { mResult ->
        CoroutineScope(Dispatchers.Main).launch {
            if (mResult.data == null) {
                binding.tvNoVoiceMails.visibility = View.VISIBLE
            } else {
                binding.tvNoVoiceMails.visibility = View.GONE
            }
            adapter.setDataSource(mResult.data)
            viewModel.isLoading.postValue(false)
        }
    })
}


fun AudioFragment.downloadFile(request: DownloadFileRequest,isShared : Boolean) {
    if (request.isDownloaded) {
        SingletonManagerProcessing.getInstance()?.onStartProgressing(activity, R.string.exporting)
        CoroutineScope(Dispatchers.Main).launch {
            val mExport = ServiceManager.getInstance()?.exportingItems(request,isShared)
            when (mExport?.status) {
                Status.SUCCESS -> {
                    val mPath = mExport.data?.absolutePath
                    log(mPath ?: "")
                    if (isShared){
                        context?.let { mExport.data?.let { it1 ->
                            Utils.shareMultiple(
                                it1, it)
                        } }
                    }else{
                        onBasicAlertNotify(message = "Download folder: ${mExport.data?.name}")
                    }
                }
                else -> {
                    log(mExport?.message ?: "")
                }
            }
        }
        SingletonManagerProcessing.getInstance()?.onStopProgressing()
    } else {
        SingletonManagerProcessing.getInstance()?.onStartProgressing(activity, R.string.exo_download_downloading)
        viewModel.downloadFile(request).observe(this, Observer { mResult ->
            when (mResult.status) {
                Status.SUCCESS -> {
                    log("Download successfully ${request.fullLocalPath}")
                    CoroutineScope(Dispatchers.Main).launch {
                        val mExport = ServiceManager.getInstance()?.exportingItems(request,isShared)
                        when (mExport?.status) {
                            Status.SUCCESS -> {
                                val mPath = mExport.data?.absolutePath
                                log(mPath ?: "")
                                if (isShared){
                                    context?.let { mExport.data?.let { it1 ->
                                        Utils.shareMultiple(
                                            it1, it)
                                    } }
                                }else{
                                    onBasicAlertNotify(message = "Download folder: ${mExport.data?.name}")
                                }
                            }
                            else -> {
                                log(mExport?.message ?: "")
                            }
                        }
                    }
                }
                else -> {
                    log(mResult.message ?: "")
                }
            }
            SingletonManagerProcessing.getInstance()?.onStopProgressing()
        })
    }
}

fun AudioFragment.updateTitle() {
    viewModel.isLoading.postValue(true)
    viewModel.updatedVoiceMail().observe(this, Observer { mResult ->
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.isLoading.postValue(false)
            getData()
        }
    })
}

fun AudioFragment.deleteVoiceMail() {
    viewModel.isLoading.postValue(true)
    viewModel.deleteVoiceMail().observe(this, Observer { mResult ->
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.isLoading.postValue(false)
            getData()
        }
    })
}

fun AudioFragment.enterVoiceMails(id: String) {
    val mMessage = "Voice Mails"
    val builder: MaterialDialog = MaterialDialog(this.requireContext())
        .title(text = mMessage)
        .negativeButton(R.string.cancel)
        .cancelable(true)
        .cancelOnTouchOutside(false)
        .negativeButton {

        }
        .positiveButton(R.string.change)
        .input(
            hintRes = R.string.enter_title,
            inputType = (InputType.TYPE_CLASS_TEXT),
            maxLength = 100,
            allowEmpty = false
        ) { dialog, text ->
            viewModel.title = text.toString()
            viewModel.id = id
            updateTitle()
        }
    val input: EditText = builder.getInputField()
    input.setPadding(0, 50, 0, 20)
    builder.show()
}

fun AudioFragment.askDeleteVoiceMail(id: String, voice: String) {
    val builder: MaterialDialog = MaterialDialog(requireContext())
        .title(text = getString(R.string.confirm))
        .message(res = R.string.are_you_sure_you_want_delete_item)
        .negativeButton(text = getString(R.string.cancel))
        .positiveButton(text = getString(R.string.ok))
        .cancelable(false)
        .positiveButton {
            viewModel.id = id
            viewModel.voice = voice
            deleteVoiceMail()
        }
        .negativeButton {
        }
    builder.show()
}


@ExperimentalStdlibApi
private fun AudioFragment.execute(s: CharSequence?) {
    if (binding.edtSearch == activity?.currentFocus){
        viewModel.searchText = s.toString()
        if (viewModel.searchText.isNotEmpty()){
            binding.imgClear.visibility = View.VISIBLE
        }else{
            binding.imgClear.visibility = View.INVISIBLE
        }
    }else{
        log("Nothing")
    }
}


private fun AudioFragment.initCheckout(){
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

fun AudioFragment.startSubscription(){
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
