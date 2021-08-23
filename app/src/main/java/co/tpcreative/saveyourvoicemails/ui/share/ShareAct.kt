package co.tpcreative.saveyourvoicemails.ui.share

import android.os.Bundle
import androidx.activity.viewModels
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.ViewModelFactory
import co.tpcreative.saveyourvoicemails.common.base.BaseActivity
import co.tpcreative.saveyourvoicemails.common.services.DefaultServiceLocator
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import co.tpcreative.saveyourvoicemails.databinding.ActivityShareBinding

class ShareAct : BaseActivity() {

    lateinit var binding: ActivityShareBinding
    val viewModel : ShareViewModel by viewModels {
        ViewModelFactory(DefaultServiceLocator.getInstance(SaveYourVoiceMailsApplication.getInstance()) )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        setContentView(
            binding.apply {
            }.root
        )
    }
}