package co.tpcreative.saveyourvoicemails.common.controller

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ThumbnailUtils
import android.os.Build
import android.os.CancellationSignal
import android.provider.MediaStore
import android.util.Size
import co.tpcreative.domain.models.ImportFilesModel
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.extension.createCipherFile
import co.tpcreative.saveyourvoicemails.common.extension.createDirectory
import co.tpcreative.saveyourvoicemails.common.network.Resource
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
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

    /*Import data from gallery*/
    suspend fun onImportData(mData : ImportFilesModel) : Resource<String> {
        return withContext(Dispatchers.IO){
            try {
                val rootPath: String = SaveYourVoiceMailsApplication.getInstance().getTemporary()
                rootPath.createDirectory()
                val output = rootPath + mData.mimeTypeFile?.name
                try {
                    val createdOriginal = File(mData.path).createCipherFile(File(output), File(mData.path), Cipher.ENCRYPT_MODE)
                    if (createdOriginal) {
                        Utils.log(this::class.java, "CreatedFile successful")
                    } else {
                        Utils.log(this::class.java, "CreatedFile failed")
                    }
                    Resource.success(output)
                } catch (e: Exception) {
                    Utils.log(this::class.java, "Cannot write to $e")
                    Resource.error(Utils.CODE_EXCEPTION, e.message ?: "", null)
                } finally {
                    Utils.log(this::class.java, "Finally")
                }
            }
            catch (e : Exception) {
                e.printStackTrace()
                Resource.error(Utils.CODE_EXCEPTION, e.message ?: "", null)
            }
        }
    }

}