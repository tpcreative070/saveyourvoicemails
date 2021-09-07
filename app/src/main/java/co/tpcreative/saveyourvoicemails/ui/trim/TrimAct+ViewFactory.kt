package co.tpcreative.saveyourvoicemails.ui.trim

import android.text.InputType
import android.widget.EditText
import co.tpcreative.domain.models.ImportFilesModel
import co.tpcreative.domain.models.UploadBody
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.SingletonManagerProcessing
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.base.log
import co.tpcreative.saveyourvoicemails.common.controller.ServiceManager
import co.tpcreative.saveyourvoicemails.common.extension.createDirectory
import co.tpcreative.saveyourvoicemails.common.extension.deleteDirectory
import co.tpcreative.saveyourvoicemails.common.network.Status
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import co.tpcreative.saveyourvoicemails.ui.share.ShareAct
import co.tpcreative.saveyourvoicemails.ui.share.enterVoiceMails
import co.tpcreative.saveyourvoicemails.ui.share.savedVoiceMails
import co.tpcreative.saveyourvoicemails.ui.share.uploadFile
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

fun TrimAct.initUI(){
    setSupportActionBar(binding.toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
}

fun TrimAct.enterTrimTitle(fileName : String) {
    val mMessage = "Voice Mails"
    val builder: MaterialDialog = MaterialDialog(this)
            .title(text = mMessage)
            .negativeButton(R.string.cancel)
            .cancelable(true)
            .cancelOnTouchOutside(false)
            .positiveButton(R.string.send)
            .input(prefill = fileName,hintRes = R.string.enter_title, inputType = (InputType.TYPE_CLASS_TEXT),maxLength = 100, allowEmpty = false){ dialog, text->
                saveRingtone(text.toString())
            }
    val input: EditText = builder.getInputField()
    input.setPadding(0,50,0,20)
    builder.show()
}


fun TrimAct.importingData(mData:ImportFilesModel) = CoroutineScope(Dispatchers.Main).launch{
    val mResult = ServiceManager.getInstance()?.onImportData(mData)
    when(mResult?.status){
        Status.SUCCESS -> {
            SingletonManagerProcessing.getInstance()?.onStartProgressing(this@importingData,R.string.uploading)
            uploadFile(File(mData.path),mData,mData.mimeTypeFile?.name!!)
        }
        else -> mResult?.message?.let { log(it) }
    }
}

fun TrimAct.uploadFile(mFile : File, mImport : ImportFilesModel, title: String){
    val item = UploadBody()
    item.session_token = Utils.getSessionToken() ?: ""
    item.user_id = Utils.getUserId() ?: ""
    item.fileTitle = title
    item.mimeType = mImport.mimeTypeFile?.mimeType!!
    item.fileName = mImport.mimeTypeFile?.name!!
    item.extension = mImport.mimeTypeFile?.extension!!
    viewModel.insertVoiceMails(item,mFile).observe(this,{ mResult ->
        when(mResult.status){
            Status.SUCCESS -> {
                val inputPath = SaveYourVoiceMailsApplication.getInstance().getTemporary()
                val inputFileName = item.fileName
                val outputPath  = SaveYourVoiceMailsApplication.getInstance().getPrivate()
                val outputFileName = mResult.data?.data?.name ?: ""
                CoroutineScope(Dispatchers.Main).launch {
                    ServiceManager.getInstance()?.moveFile(inputPath,inputFileName,outputPath,outputFileName)
                    mResult.data?.let { log(it) }
                    savedVoiceMails()
                    mFile.delete()
                    SingletonManagerProcessing.getInstance()?.onStopProgressing()
                }
            }else ->{
            mResult.message?.let { log(it) }
            finish()
        }
        }
    })
}

fun TrimAct.savedVoiceMails() {
    val builder: MaterialDialog = MaterialDialog(this)
            .title(text = getString(R.string.saved_voice_mails))
            .message(res = R.string.saved_voice_mails_successfully)
            .positiveButton(text = getString(R.string.ok))
            .cancelable(false)
            .positiveButton {
                SaveYourVoiceMailsApplication.getInstance().getTemporary().deleteDirectory()
                SaveYourVoiceMailsApplication.getInstance().getRecorder().deleteDirectory()
                SaveYourVoiceMailsApplication.getInstance().getTemporary().createDirectory()
                SaveYourVoiceMailsApplication.getInstance().getRecorder().createDirectory()
                finish()
            }
    builder.show()
}


