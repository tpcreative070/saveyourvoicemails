package co.tpcreative.saveyourvoicemails.common.controller

import co.tpcreative.domain.models.ImportFilesModel
import co.tpcreative.domain.models.request.DownloadFileRequest
import co.tpcreative.saveyourvoicemails.common.SizeUnit
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.extension.createCipherFile
import co.tpcreative.saveyourvoicemails.common.extension.createDirectory
import co.tpcreative.saveyourvoicemails.common.extension.getSize
import co.tpcreative.saveyourvoicemails.common.extension.isDirectoryExists
import co.tpcreative.saveyourvoicemails.common.network.Resource
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import com.elvishew.xlog.XLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import javax.crypto.Cipher

class ServiceManager {
    companion object {
        private val TAG = ServiceManager::class.java.simpleName
        private var instance: ServiceManager? = null
        fun getInstance(): ServiceManager? {
            if (instance == null) {
                instance = ServiceManager()
            }
            return instance
        }
    }

    fun log(message : Any?){
        Utils.log(this::class.java,message)
    }

    /*Import data from gallery*/
    suspend fun onImportData(mData: ImportFilesModel) : Resource<String> {
        return withContext(Dispatchers.IO){
            try {
                val rootPath: String = SaveYourVoiceMailsApplication.getInstance().getTemporary()
                rootPath.createDirectory()
                val output = rootPath + mData.mimeTypeFile?.name
                try {
                    val createdOriginal = File(mData.path).createCipherFile(
                        File(output),
                        File(mData.path),
                        Cipher.ENCRYPT_MODE
                    )
                    if (createdOriginal) {
                        Utils.log(this::class.java, "CreatedFile successful")
                    } else {
                        Utils.log(this::class.java, "CreatedFile failed")
                    }
                    XLog.d("onInput ${mData.path} ==> ${File(mData.path).getSize(SizeUnit.KB)}");
                    XLog.d("onOutput $output ==> ${File(output).getSize(SizeUnit.KB)}");
                    Resource.success(output)
                } catch (e: Exception) {
                    Utils.log(this::class.java, "Cannot write to $e")
                    XLog.d(e.message);
                    Resource.error(Utils.CODE_EXCEPTION, e.message ?: "", null)
                } finally {
                    Utils.log(this::class.java, "Finally")
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
                Resource.error(Utils.CODE_EXCEPTION, e.message ?: "", null)
            }
        }
    }

    suspend fun exportingItems(mData : DownloadFileRequest,isShared : Boolean) : Resource<File>{
        return withContext(Dispatchers.IO){
            try {
                val mInput  = File(mData.fullLocalPath)
                var rootOutput = SaveYourVoiceMailsApplication.getInstance().getDownload()
                if (isShared){
                    rootOutput = SaveYourVoiceMailsApplication.getInstance().getRecorder()
                }
                var mOutPut = File(rootOutput + "/ ${mData.title}.${mInput.extension}")
                if (mOutPut.exists()){
                   mOutPut =  File(rootOutput + "/ ${mData.title}_${Utils.getCurrentDateTime()}.${mInput.extension}")
                }
                if (!SaveYourVoiceMailsApplication.getInstance().getDownload().isDirectoryExists()){
                    SaveYourVoiceMailsApplication.getInstance().getDownload().createDirectory()
                }
                try {
                    mOutPut.createCipherFile(mOutPut,mInput,Cipher.DECRYPT_MODE)
                    log("Exported completely")
                    Resource.success(mOutPut)
                } catch (e: Exception) {
                    log("Cannot write to $e")
                    Resource.error(Utils.CODE_EXCEPTION,e.message ?:"",null)
                } finally {
                    log("Finally")
                }
            }
            catch (e : Exception){
                e.printStackTrace()
                Resource.error(Utils.CODE_EXCEPTION,e.message ?:"",null)
            }
        }
    }


    suspend fun moveFile(inputPath: String, inputFile: String, outputPath: String,outputFile : String) : Resource<Boolean>{
        return withContext(Dispatchers.IO) {
            var mIn: InputStream? = null
            var mOut: OutputStream? = null
            try {
                //create output directory if it doesn't exist
                val dir = File(outputPath)
                if (!dir.exists()) {
                    dir.mkdirs()
                }
                mIn = FileInputStream(inputPath + inputFile)
                mOut = FileOutputStream(outputPath + outputFile)
                val buffer = ByteArray(1024)
                var read: Int
                while (mIn.read(buffer).also { read = it } != -1) {
                    mOut.write(buffer, 0, read)
                }
                mIn.close()
                mIn = null
                // write the output file
                mOut.flush()
                mOut.close()
                mOut = null
                // delete the original file
                File(inputPath + inputFile).delete()
                Resource.success(true)
            } catch (fileNotFound: FileNotFoundException) {
                log(fileNotFound.message)
                Resource.error(Utils.CODE_EXCEPTION, fileNotFound.message ?: "", null)
            } catch (e: java.lang.Exception) {
                log(e.message)
                Resource.error(Utils.CODE_EXCEPTION, e.message ?: "", null)
            }
        }
    }

}