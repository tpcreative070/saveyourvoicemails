package co.tpcreative.saveyourvoicemails.common.extension

import android.util.Base64
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.helper.EncryptDecryptFilesHelper
import java.io.File

/*This is for key pin*/
fun String.createPin(content: String?) : Boolean?{
    return EncryptDecryptFilesHelper.getInstance()?.createFile(this, content = content)
}

fun String.encryptTextByIdPKCS7(mode: Int) : String?{
    return EncryptDecryptFilesHelper.getInstance()?.encryptTextPKCS7(this, mode)
}

fun String.readPin() : String? {
    return EncryptDecryptFilesHelper.getInstance()?.readTextFile(this)
}

fun String.decode(): String {
    return Base64.decode(this, Base64.DEFAULT).toString(charset("UTF-8"))
}

fun String.encode(): String {
    return Base64.encodeToString(this.toByteArray(charset("UTF-8")), Base64.DEFAULT)
}

fun String.readFile() : ByteArray?{
    return EncryptDecryptFilesHelper.getInstance()?.readFile(this)
}

fun String.isFileExist(): Boolean {
    return File(this).exists()
}

fun String.createDirectory() : Boolean{
    val directory = File(this)
    if (directory.exists()) {
        Utils.log(this::class.java, "Directory $this already exists")
        return false
    }
    return directory.mkdirs()
}

fun String.deleteFile(){
    val file = File(this)
    file.delete()
}

fun String.deleteDirectory() : Boolean {
    return deleteDirectoryImpl(this)
}

fun String.createDirectory(override: Boolean): Boolean {
    if (override && this.isDirectoryExists()) {
        this.deleteDirectory()
    }
    return this.createDirectory()
}


private fun String.deleteDirectoryImpl(path: String): Boolean {
    val directory = File(path)
    // If the directory exists then delete
    if (directory.exists()) {
        val files = directory.listFiles() ?: return true
        // Run on all sub files and folders and delete them
        for (i in files.indices) {
            if (files[i].isDirectory) {
                deleteDirectoryImpl(files[i].absolutePath)
            } else {
                files[i].delete()
            }
        }
    }
    return directory.delete()
}

fun String.isDirectoryExists(): Boolean {
    return File(this).exists()
}

fun String.removedSpecialCharacters() : String{
    var mResult = ""
    mResult = this.replace(":","")
    return mResult
}
