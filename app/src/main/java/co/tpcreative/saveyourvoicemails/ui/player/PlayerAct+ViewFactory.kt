package co.tpcreative.saveyourvoicemails.ui.player

import androidx.lifecycle.Observer
import co.tpcreative.domain.models.request.DownloadFileRequest
import co.tpcreative.saveyourvoicemails.Navigator
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.SingletonManagerProcessing
import co.tpcreative.saveyourvoicemails.common.base.log
import co.tpcreative.saveyourvoicemails.common.network.Status


fun PlayerAct.initUI(){
    binding.imgTrim.setOnClickListener {
        trimFile()
    }
}

fun PlayerAct.trimFile(){
    SingletonManagerProcessing.getInstance()?.onStartProgressing(this, R.string.exo_download_downloading)
    viewModel.trimFile().observe(this, Observer { mResult ->
        when(mResult.status){
            Status.SUCCESS ->{
                SingletonManagerProcessing.getInstance()?.onStopProgressing()
                val mPath = mResult.data?.path
                val mTitle = mResult.data?.title
                Navigator.moveToTrim(this,mPath?:"",mTitle ?:"")
                log(mPath ?:"")
            }else ->{
            log("Error occurred downloading")
        }
        }
    })
}

fun PlayerAct.downloadFile(downloadFileRequest: DownloadFileRequest){
    SingletonManagerProcessing.getInstance()?.onStartProgressing(this, R.string.exo_download_downloading)
    viewModel.downloadFile(downloadFileRequest).observe(this, Observer {  mResult ->
        when(mResult.status){
            Status.SUCCESS ->{
                SingletonManagerProcessing.getInstance()?.onStopProgressing()
                play(downloadFileRequest.fullLocalPath)
            }else ->{
                log("Error occurred downloading")
            }
        }
        log(mResult.status)
    })
}