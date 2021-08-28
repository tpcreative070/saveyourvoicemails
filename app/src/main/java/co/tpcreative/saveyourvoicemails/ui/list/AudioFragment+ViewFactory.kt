package co.tpcreative.saveyourvoicemails.ui.list

import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.lifecycle.Observer
import co.tpcreative.domain.models.request.DownloadFileRequest
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.SingletonManagerProcessing
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.base.log
import co.tpcreative.saveyourvoicemails.common.controller.ServiceManager
import co.tpcreative.saveyourvoicemails.common.network.Status
import co.tpcreative.saveyourvoicemails.common.view.NpaGridLayoutManager
import co.tpcreative.saveyourvoicemails.common.view.addListOfDecoration
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        SingletonManagerProcessing.getInstance()?.onStopProgressing(activity)
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
            SingletonManagerProcessing.getInstance()?.onStopProgressing(activity)
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
        .positiveButton(R.string.send)
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
