package co.tpcreative.saveyourvoicemails.ui.share
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.InputType
import android.widget.EditText
import co.tpcreative.domain.models.EnumFormatType
import co.tpcreative.domain.models.ImportFilesModel
import co.tpcreative.domain.models.MimeTypeFile
import co.tpcreative.domain.models.UploadBody
import co.tpcreative.saveyourvoicemails.BuildConfig
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.PathUtil
import co.tpcreative.saveyourvoicemails.common.SingletonManagerProcessing
import co.tpcreative.saveyourvoicemails.common.SizeUnit
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.base.log
import co.tpcreative.saveyourvoicemails.common.controller.ServiceManager
import co.tpcreative.saveyourvoicemails.common.extension.*
import co.tpcreative.saveyourvoicemails.common.network.Status
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.elvishew.xlog.XLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

fun ShareAct.initUI(){
    onHandlerIntent()
    SaveYourVoiceMailsApplication.getInstance().initXLog()
}

fun ShareAct.onHandlerIntent() {
    try {
        val action: String? = intent.action
        val type: String? = intent.type
        log("original type :$type")
        val imageUri : Uri? = intent.getParcelableExtra(Intent.EXTRA_STREAM)
        if (Intent.ACTION_SEND == action && type != null) {
            handleSendSingleItem(intent)
        }
        else if (imageUri!=null){
            handleSendSingleItem(intent)
        }
        else {
            log("Sending items is not existing")
        }
    } catch (e: Exception) {
        viewModel.isLoading.postValue(false)
        finish()
        e.printStackTrace()
    }
}

fun ShareAct.handleSendSingleItem(intent: Intent) {
    XLog.d("-----------------------------------------------Start ${Utils.getCurrentDateTime()} Version ${BuildConfig.VERSION_NAME}------------------------------------------")
    try {
        val imageUri : Uri? = intent.getParcelableExtra(Intent.EXTRA_STREAM)
        if (imageUri != null) {
            var response : String? = ""
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                log("value path :" + imageUri.path)
                log("Path existing " + imageUri.path?.isFileExist())
                response = PathUtil.getRealPathFromUri(this, imageUri)
                if (response == null) {
                    response = PathUtil.getFilePathFromURI(this, imageUri)
                }
                XLog.d("getRealPathFromUri");
            } else {
                response = PathUtil.getPath(this, imageUri)
                if (response == null) {
                    response = PathUtil.getFilePathFromURI(this, imageUri)
                }
                XLog.d("getFilePathFromURI");
            }
            if (response == null) {
                viewModel.isLoading.postValue(false)
                finish()
            } else {
                val mFile = File(response)
                if (mFile.exists()) {
                    val path = mFile.absolutePath
                    val name = mFile.name.removedSpecialCharacters()
                    val fileExtension: String = Utils.getFileExtension(path)
                    val mimeType: String? = intent.type
                    log("file extension $fileExtension")
                    log("Path file :$path")
                    var mimeTypeFile: MimeTypeFile? = Utils.mediaTypeSupport().get(fileExtension)
                    if (mimeTypeFile == null) {
                        val mMimeTypeSupport: MimeTypeFile? = Utils.mimeTypeSupport().get(mimeType)
                        if (mMimeTypeSupport != null) {
                            when (mMimeTypeSupport.formatType) {
                                EnumFormatType.IMAGE -> {
                                    mimeTypeFile = MimeTypeFile(mMimeTypeSupport.extension, EnumFormatType.IMAGE, mimeType)
                                    mimeTypeFile.name = Utils.getCurrentDateTime(Utils.FORMAT_TIME_FILE_NAME) + mMimeTypeSupport.extension
                                }
                                EnumFormatType.VIDEO -> {
                                    mimeTypeFile = MimeTypeFile(mMimeTypeSupport.extension, EnumFormatType.VIDEO, mimeType)
                                    mimeTypeFile.name = Utils.getCurrentDateTime(Utils.FORMAT_TIME_FILE_NAME) + mMimeTypeSupport.extension
                                }
                                EnumFormatType.AUDIO -> {
                                    mimeTypeFile = MimeTypeFile(mMimeTypeSupport.extension, EnumFormatType.AUDIO, mimeType)
                                    mimeTypeFile.name = Utils.getCurrentDateTime(Utils.FORMAT_TIME_FILE_NAME) + mMimeTypeSupport.extension
                                }
                                else -> {
                                    mimeTypeFile = MimeTypeFile(mMimeTypeSupport.extension, EnumFormatType.FILES, mimeType)
                                    mimeTypeFile.name = Utils.getCurrentDateTime(Utils.FORMAT_TIME_FILE_NAME) + mMimeTypeSupport.extension
                                }
                            }
                        } else {
                            mimeTypeFile = MimeTypeFile(".$fileExtension", EnumFormatType.FILES, mimeType)
                            mimeTypeFile.name = name
                            log("type file $mimeType")
                        }
                    }
                    if (mimeTypeFile.name == null || mimeTypeFile.name == "") {
                        mimeTypeFile.name = name
                    }
                    XLog.d(path +" => intent size ${mFile.getSize(SizeUnit.KB)}");
                    val importFiles = ImportFilesModel( mimeTypeFile, path, 0, false,Utils.getUUId())
                    log(importFiles)
                    importingData(importFiles)
                } else {
                    viewModel.isLoading.postValue(false)
                    finish()
                }
            }
        } else {
            log("Nothing to do at single item")
            viewModel.isLoading.postValue(false)
            finish()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        viewModel.isLoading.postValue(false)
        finish()
    }
}

private fun ShareAct.alertAskRecording(message : String) {
    val builder: MaterialDialog = MaterialDialog(this)
            .title(text = getString(R.string.alert))
            .message(text = message)
            .positiveButton(text = getString(R.string.ok))
            .negativeButton(text = getString(R.string.cancel))
            .cancelable(false)
            .positiveButton {
                finish()
            }

    builder.show()
}
fun ShareAct.importingData(mData:ImportFilesModel) = CoroutineScope(Dispatchers.Main).launch{
    val mResult = ServiceManager.getInstance()?.onImportData(mData)
    when(mResult?.status){
        Status.SUCCESS -> {
            enterVoiceMails(File(mResult.data),mData)
        }
        else -> mResult?.message?.let { log(it) }
    }
}


fun ShareAct.uploadFile(mFile : File,mImport : ImportFilesModel,title: String){
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

fun ShareAct.enterVoiceMails(mFile : File,mImport : ImportFilesModel) {
    val mMessage = "Voice Mails"
    val builder: MaterialDialog = MaterialDialog(this)
        .title(text = mMessage)
        .negativeButton(R.string.cancel)
        .cancelable(true)
        .cancelOnTouchOutside(false)
        .negativeButton {
            finish()
        }
        .positiveButton(R.string.send)
        .input(hintRes = R.string.enter_title, inputType = (InputType.TYPE_CLASS_TEXT),maxLength = 100, allowEmpty = false){ dialog, text->
            SingletonManagerProcessing.getInstance()?.onStartProgressing(this,R.string.uploading)
            uploadFile(mFile,mImport,text.toString())
        }
    val input: EditText = builder.getInputField()
    input.setPadding(0,50,0,20)
    builder.show()
}

fun ShareAct.savedVoiceMails() {
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
            XLog.d("-----------------------------------------------End ${Utils.getCurrentDateTime()} Version ${BuildConfig.VERSION_NAME}------------------------------------------")
        }
    builder.show()
}













