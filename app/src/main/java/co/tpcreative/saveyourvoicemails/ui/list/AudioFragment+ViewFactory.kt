package co.tpcreative.saveyourvoicemails.ui.list
import android.text.InputType
import android.view.LayoutInflater
import android.widget.EditText
import androidx.lifecycle.Observer
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.view.NpaGridLayoutManager
import co.tpcreative.saveyourvoicemails.common.view.addListOfDecoration
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun AudioFragment.initRecycleView(layoutInflater: LayoutInflater){
    try {
        gridLayoutManager = NpaGridLayoutManager(this.context, AudioAdapter.SPAN_COUNT_ONE)
        adapter = AudioAdapter(gridLayoutManager, layoutInflater, this.context, this@initRecycleView)
        binding.recyclerView.layoutManager = gridLayoutManager
        this.context?.let { binding.recyclerView.addListOfDecoration(it) }
        binding.recyclerView.adapter = adapter
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun AudioFragment.getData(){
    viewModel.isLoading.postValue(true)
    viewModel.getVoiceMail().observe(this, Observer { mResult ->
        CoroutineScope(Dispatchers.Main).launch {
            adapter.setDataSource(mResult.data)
            viewModel.isLoading.postValue(false)
        }
    })
}

fun AudioFragment.updateTitle(){
    viewModel.isLoading.postValue(true)
    viewModel.updatedVoiceMail().observe(this, Observer { mResult ->
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.isLoading.postValue(false)
            getData()
        }
    })
}

fun AudioFragment.enterVoiceMails(id : String) {
    val mMessage = "Voice Mails"
    val builder: MaterialDialog = MaterialDialog(this.requireContext())
        .title(text = mMessage)
        .negativeButton(R.string.cancel)
        .cancelable(true)
        .cancelOnTouchOutside(false)
        .negativeButton {

        }
        .positiveButton(R.string.send)
        .input(hintRes = R.string.enter_title, inputType = (InputType.TYPE_CLASS_TEXT),maxLength = 100, allowEmpty = false){ dialog, text->
            viewModel.title = text.toString()
            viewModel.id = id
            updateTitle()
        }
    val input: EditText = builder.getInputField()
    input.setPadding(0,50,0,20)
    builder.show()
}
