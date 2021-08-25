package co.tpcreative.saveyourvoicemails.ui.list
import android.view.LayoutInflater
import androidx.lifecycle.Observer
import co.tpcreative.saveyourvoicemails.common.view.NpaGridLayoutManager
import co.tpcreative.saveyourvoicemails.common.view.addListOfDecoration
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