package co.tpcreative.saveyourvoicemails.ui.list
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import co.tpcreative.domain.models.EnumEventBus
import co.tpcreative.domain.models.request.DownloadFileRequest
import co.tpcreative.saveyourvoicemails.Navigator
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.ViewModelFactory
import co.tpcreative.saveyourvoicemails.common.base.BaseFragment
import co.tpcreative.saveyourvoicemails.common.base.log
import co.tpcreative.saveyourvoicemails.common.extension.getIsSubscribed
import co.tpcreative.saveyourvoicemails.common.services.DefaultServiceLocator
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import co.tpcreative.saveyourvoicemails.common.view.NpaGridLayoutManager
import co.tpcreative.saveyourvoicemails.databinding.FragmentAudioBinding
import com.afollestad.materialdialogs.MaterialDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

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

    @OptIn(ExperimentalStdlibApi::class)
    override fun work() {
        super.work()
        initUI()
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

        viewModel.searchData.observe(this, Observer { mResult ->
            if (mResult.size==0) {
                binding.tvNoVoiceMails.visibility = View.VISIBLE
            } else {
                binding.tvNoVoiceMails.visibility = View.GONE
            }
            adapter.setDataSource(mResult)

        })
        getData()
    }

    override fun onClickItem(position: Int) {
        if (!Utils.getIsSubscribed() && Utils.isRequestingSubscription()){
            alertDialog()
            return
        }
        val mItem = dataSource[position]
        val mDownloadItem = DownloadFileRequest(mItem.voice,mItem.outputFolder,mItem.voice,mItem.title)
        context?.let { Navigator.moveToPlayer(it,mDownloadItem) }
    }

    override fun onLongClickItem(position: Int) {

    }

    override fun onDownloadItem(position: Int) {
        if (!Utils.getIsSubscribed() && Utils.isRequestingSubscription()){
            alertDialog()
            return
        }
        val mItem = dataSource[position]
        val mDownloadItem = DownloadFileRequest(mItem.voice,mItem.outputFolder,mItem.voice,mItem.title)
        downloadFile(mDownloadItem,false)
    }

    override fun onShare(position: Int) {
        if (!Utils.getIsSubscribed() && Utils.isRequestingSubscription()){
            alertDialog()
            return
        }
        val mItem = dataSource[position]
        val mDownloadItem = DownloadFileRequest(mItem.voice,mItem.outputFolder,mItem.voice,mItem.title)
        downloadFile(mDownloadItem,true)
    }

    override fun onEditItem(position: Int) {
        if (!Utils.getIsSubscribed() && Utils.isRequestingSubscription()){
            alertDialog()
            return
        }
        val mItem = dataSource[position]
        enterVoiceMails(mItem.id)
    }

    override fun onDeleteItem(position: Int) {
        if (!Utils.getIsSubscribed() && Utils.isRequestingSubscription()){
            alertDialog()
            return
        }
        val mItem = dataSource[position]
        askDeleteVoiceMail(mItem.id,mItem.voice)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this);
        log("Destroy...")
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: EnumEventBus?) {
        getData()
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    private fun alertDialog() {
        val builder: MaterialDialog = MaterialDialog(requireActivity())
                .title(text = getString(R.string.confirm))
                .message(res = R.string.description_subscription)
                .positiveButton(text = getString(R.string.subscribe))
                .negativeButton(text = getText(R.string.cancel))
                .cancelable(false)
                .positiveButton {
                   Navigator.moveSubscription(requireContext())
                }
        builder.show()
    }

    private val dataSource : MutableList<AudioViewModel>
        get() {
            return adapter.getDataSource() ?: mutableListOf()
        }
}