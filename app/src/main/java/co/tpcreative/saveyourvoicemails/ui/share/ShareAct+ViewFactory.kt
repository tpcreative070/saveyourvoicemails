package co.tpcreative.saveyourvoicemails.ui.share

import android.content.Intent
import android.net.Uri
import android.os.Build
import co.tpcreative.domain.models.EnumFormatType
import co.tpcreative.domain.models.ImportFilesModel
import co.tpcreative.domain.models.MimeTypeFile
import co.tpcreative.domain.models.UploadBody
import co.tpcreative.saveyourvoicemails.common.PathUtil
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.base.log
import co.tpcreative.saveyourvoicemails.common.controller.ServiceManager
import co.tpcreative.saveyourvoicemails.common.extension.isFileExist
import co.tpcreative.saveyourvoicemails.common.network.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

fun ShareAct.initUI(){
    onHandlerIntent()
}

fun ShareAct.onHandlerIntent() {
    try {
        val action: String? = intent.action
        val type: String? = intent.type
        log("original type :$type")
        if (Intent.ACTION_SEND == action && type != null) {
            handleSendSingleItem(intent)
        } else {
            log("Sending items is not existing")
        }
    } catch (e: Exception) {
        viewModel.isLoading.postValue(false)
        finish()
        e.printStackTrace()
    }
}

fun ShareAct.handleSendSingleItem(intent: Intent) {
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
            } else {
                response = PathUtil.getPath(this, imageUri)
                if (response == null) {
                    response = PathUtil.getFilePathFromURI(this, imageUri)
                }
            }
            if (response == null) {
                viewModel.isLoading.postValue(false)
                finish()
            } else {
                val mFile = File(response)
                if (mFile.exists()) {
                    val path = mFile.absolutePath
                    val name = mFile.name
                    val fileExtension: String = Utils.getFileExtension(path)
                    val mimeType: String? = intent.getType()
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
fun ShareAct.importingData(mData:ImportFilesModel) = CoroutineScope(Dispatchers.Main).launch{
    val mResult = ServiceManager.getInstance()?.onImportData(mData)
    when(mResult?.status){
        Status.SUCCESS -> {
            uploadFile(File(mResult.data),mData)
        }
        else -> mResult?.message?.let { log(it) }
    }
}


fun ShareAct.uploadFile(mFile : File,mImport : ImportFilesModel){
    val mutableMap = HashMap<String?,Any?>()
    mutableMap.put("session_token",Utils.getSessionToken())
    mutableMap.put("user_id",Utils.getUserId())
    mutableMap.put("fileTitle",mImport.mimeTypeFile?.name!!)
    val item = UploadBody()
    item.session_token = Utils.getSessionToken() ?: ""
    item.user_id = Utils.getUserId() ?: ""
    item.fileTitle = mImport.mimeTypeFile?.name!!
    item.mimeType = mImport.mimeTypeFile?.mimeType!!
    viewModel.insertVoiceMails(item,mutableMap,mFile).observe(this,{ mResult ->
        when(mResult.status){
            Status.SUCCESS -> {
                mResult.data?.let { log(it) }
            }else ->{
            mResult.message?.let { log(it) }
            }
        }
    })
}











