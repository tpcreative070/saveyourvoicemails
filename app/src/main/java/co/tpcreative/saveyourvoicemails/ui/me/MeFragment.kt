package co.tpcreative.saveyourvoicemails.ui.me

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import co.tpcreative.saveyourvoicemails.Navigator
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.ViewModelFactory
import co.tpcreative.saveyourvoicemails.common.base.BaseFragment
import co.tpcreative.saveyourvoicemails.common.extension.getUserInfo
import co.tpcreative.saveyourvoicemails.common.services.DefaultServiceLocator
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import co.tpcreative.saveyourvoicemails.databinding.FragmentMeBinding
import co.tpcreative.saveyourvoicemails.ui.list.AudioFragmentViewModel


/**
 * A simple [Fragment] subclass.
 * Use the [MeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MeFragment : BaseFragment() {

    lateinit var binding: FragmentMeBinding

    val viewModel: MeViewModel by viewModels {
        ViewModelFactory(DefaultServiceLocator.getInstance(SaveYourVoiceMailsApplication.getInstance()))
    }

    override fun work() {
        super.work()
        binding.btnSignOut.setOnClickListener {
            signOut()
        }
        binding.btnChangePassword.setOnClickListener {
            Navigator.moveToChangePassword(requireContext())
        }

        binding.btnLiveChat.setOnClickListener {
            Navigator.openWebSites(getString(R.string.live_chat_url),requireActivity())
        }

        val mUser = Utils.getUserInfo()
        if (mUser?.isFacebook == true){
            binding.btnChangePassword.visibility = View.GONE
        }else{
            binding.tvUserInfo.text = mUser?.email
        }
    }

    override fun getLayoutId(): Int {
        return 0
    }

    override fun getLayoutId(inflater: LayoutInflater?, viewGroup: ViewGroup?): View? {
        binding = FragmentMeBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                MeFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }
}