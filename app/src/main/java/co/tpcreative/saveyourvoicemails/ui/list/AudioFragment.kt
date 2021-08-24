package co.tpcreative.saveyourvoicemails.ui.list
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.ViewModelFactory
import co.tpcreative.saveyourvoicemails.common.base.BaseFragment
import co.tpcreative.saveyourvoicemails.common.base.log
import co.tpcreative.saveyourvoicemails.common.services.DefaultServiceLocator
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication

class AudioFragment : BaseFragment() {

    private val viewModel: AudioViewModel by viewModels {
        ViewModelFactory(DefaultServiceLocator.getInstance(SaveYourVoiceMailsApplication.getInstance()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingEvent()
    }

    override fun work() {
        super.work()
    }

    override fun getLayoutId(): Int {
        return  0
    }

    override fun getLayoutId(inflater: LayoutInflater?, viewGroup: ViewGroup?): View?{
        return inflater?.inflate(
            R.layout.fragment_audio, viewGroup, false) as ConstraintLayout
    }

    private fun bindingEvent(){
        viewModel.getVoiceMail().observe(this, Observer { mResult ->
            log(mResult)
        })
    }

}