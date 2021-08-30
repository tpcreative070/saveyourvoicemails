package co.tpcreative.saveyourvoicemails.ui.player

import androidx.lifecycle.Observer
import co.tpcreative.domain.models.request.DownloadFileRequest
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.SingletonManagerProcessing
import co.tpcreative.saveyourvoicemails.common.base.log
import co.tpcreative.saveyourvoicemails.common.network.Status


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