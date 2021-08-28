package co.tpcreative.saveyourvoicemails.ui.list
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import co.tpcreative.domain.models.request.DownloadFileRequest
import co.tpcreative.saveyourvoicemails.Navigator
import co.tpcreative.saveyourvoicemails.common.ViewModelFactory
import co.tpcreative.saveyourvoicemails.common.base.BaseFragment
import co.tpcreative.saveyourvoicemails.common.base.log
import co.tpcreative.saveyourvoicemails.common.services.DefaultServiceLocator
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import co.tpcreative.saveyourvoicemails.common.view.NpaGridLayoutManager
import co.tpcreative.saveyourvoicemails.databinding.FragmentAudioBinding

class AudioFragment : BaseFragment(), AudioAdapter.ItemSelectedListener {
    var gridLayoutManager: NpaGridLayoutManager? = null
    lateinit var binding: FragmentAudioBinding
    lateinit var adapter : AudioAdapter
    val viewModel: AudioFragmentViewModel by viewModels {
        ViewModelFactory(DefaultServiceLocator.getInstance(SaveYourVoiceMailsApplication.getInstance()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun work() {
        super.work()
        initRecycleView(this.layoutInflater)
        bindingEvent()
    }

    override fun getLayoutId(): Int {
        return  0
    }

    override fun getLayoutId(inflater: LayoutInflater?, viewGroup: ViewGroup?): View{
        binding = FragmentAudioBinding.inflate(layoutInflater)
        return binding.root
    }

    private fun bindingEvent(){
        viewModel.isLoading.observe(this, Observer {
            if (it){
                binding.progressBar.visibility = View.VISIBLE
                log("loading...")
            }else{
                binding.progressBar.visibility = View.GONE
                log("Hide loading...")
            }
        })
        getData()
    }

    override fun onClickItem(position: Int) {
        val mItem = dataSource[position]
        val mDownloadItem = DownloadFileRequest(mItem.voice,mItem.outputFolder,mItem.voice,mItem.title)
        context?.let { Navigator.moveToPlayer(it,mDownloadItem) }
    }

    override fun onLongClickItem(position: Int) {

    }

    override fun onDownloadItem(position: Int) {
        val mItem = dataSource[position]
        val mDownloadItem = DownloadFileRequest(mItem.voice,mItem.outputFolder,mItem.voice,mItem.title)
        downloadFile(mDownloadItem,false)
    }

    override fun onShare(position: Int) {
        val mItem = dataSource[position]
        val mDownloadItem = DownloadFileRequest(mItem.voice,mItem.outputFolder,mItem.voice,mItem.title)
        downloadFile(mDownloadItem,true)
    }

    override fun onEditItem(position: Int) {
        val mItem = dataSource[position]
        enterVoiceMails(mItem.id)
    }

    override fun onDeleteItem(position: Int) {
        val mItem = dataSource[position]
        askDeleteVoiceMail(mItem.id,mItem.voice)
    }

    private val dataSource : MutableList<AudioViewModel>
        get() {
            return adapter.getDataSource() ?: mutableListOf()
        }
}