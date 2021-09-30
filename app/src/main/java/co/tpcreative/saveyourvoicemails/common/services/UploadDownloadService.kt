package co.tpcreative.saveyourvoicemails.common.services

import co.tpcreative.domain.models.BaseResponse
import co.tpcreative.domain.models.UploadBody
import co.tpcreative.domain.models.request.DownloadFileRequest
import co.tpcreative.domain.models.request.VoiceMailsRequest
import co.tpcreative.domain.models.response.ResponseUpload
import co.tpcreative.domain.usecases.*
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.network.Resource
import co.tpcreative.saveyourvoicemails.common.services.upload.ProgressRequestBody
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okio.BufferedSink
import okio.buffer
import okio.sink
import java.io.File
import java.io.IOException

class UploadDownloadService(private val downloadFilePostVoiceMailsUseCase: DownloadFilePostVoiceMailsUseCase,private val downloadFileVoiceMailsUseCase: DownloadFileVoiceMailsUseCase,private val uploadFileFormDataVoiceMailsUseCase: UploadFileFormDataVoiceMailsUseCase,private val uploadFileLogVoiceMailsUseCase: UploadFileLogVoiceMailsUseCase) {
    val TAG = this::class.java.simpleName

    suspend fun downloadFile(downloadFile : DownloadFileRequest) : Resource<String>{
        return withContext(Dispatchers.IO) {
            try {
                val mResult = downloadFileVoiceMailsUseCase(downloadFile.id)
                onSaveFileToDisk(mResult,downloadFile)
                ResponseHandler.handleSuccess("Download successful")
            }
            catch (throwable : Exception){
                throwable.printStackTrace()
                ResponseHandler.handleException(throwable)
            }
        }
    }

    suspend fun downloadFilePost(downloadFile : DownloadFileRequest) : Resource<String>{
        return withContext(Dispatchers.IO) {
            try {
                val mRequest = VoiceMailsRequest()
                mRequest.voice = downloadFile.id
                mRequest.user_id = Utils.getUserId()
                val mResult = downloadFilePostVoiceMailsUseCase(mRequest)
                onSaveFileToDisk(mResult,downloadFile)
                ResponseHandler.handleSuccess("Download successful")
            }
            catch (throwable : Exception){
                throwable.printStackTrace()
                ResponseHandler.handleException(throwable)
            }
        }
    }

    suspend fun uploadFile(item : UploadBody, listener: ProgressRequestBody.UploadCallbacks, mFilePath: File?) : Resource<ResponseUpload> {
        return withContext(Dispatchers.IO){
            try {
                val dataPart: MultipartBody.Part = MultipartBody.Part.createFormData("uploaded_file",item.fileName,ProgressRequestBody(mFilePath,item.mimeType,listener))
                val userId : RequestBody = item.user_id.toRequestBody("text/plain".toMediaType())
                val sessionToken : RequestBody = item.session_token.toRequestBody("text/plain".toMediaType())
                val fileTitle : RequestBody = item.fileTitle.toRequestBody("text/plain".toMediaType())
                val mResult  = uploadFileFormDataVoiceMailsUseCase(userId,sessionToken,fileTitle,dataPart)
                Utils.log(this::class.java,item)
                Utils.log(this::class.java,mResult)
                ResponseHandler.handleSuccess(mResult)
            }catch (exception : Exception){
                Utils.log(this::class.java,"Running here ${exception.message}")
                ResponseHandler.handleException(exception)
            }
        }
    }

    suspend fun uploadFileLog(listener: ProgressRequestBody.UploadCallbacks, mFilePath: File?) : Resource<ResponseBody> {
        return withContext(Dispatchers.IO){
            try {
                val dataPart: MultipartBody.Part = MultipartBody.Part.createFormData("file_name",mFilePath?.name,ProgressRequestBody(mFilePath,"text/plain",listener))
                val mResult  = uploadFileLogVoiceMailsUseCase(dataPart)
                ResponseHandler.handleSuccess(mResult)
            }catch (exception : Exception){
                Utils.log(this::class.java,"Running here ${exception.message}")
                ResponseHandler.handleException(exception)
            }
        }
    }

    private fun onSaveFileToDisk(response: ResponseBody, request: DownloadFileRequest) {
        try {
            File(request.outputFolder).mkdirs()
            val destinationFile = File(request.outputFolder, request.fileName)
            if (!destinationFile.exists()) {
                destinationFile.createNewFile()
                Utils.log(this::class.java, "created file")
            }
            val bufferedSink: BufferedSink = destinationFile.sink().buffer()
            response.source().let { bufferedSink.writeAll(it) }
            bufferedSink.close()
            Utils.log(this::class.java,"Saved completely ${response.contentLength()}")
        } catch (e: IOException) {
            val destinationFile = File(request.outputFolder, request.fileName)
            if (destinationFile.isFile && destinationFile.exists()) {
                destinationFile.delete()
            }
            e.printStackTrace()
        }
    }

    private fun getString(res : Int) : String{
        return SaveYourVoiceMailsApplication.getInstance().applicationContext.getString(res)
    }
}