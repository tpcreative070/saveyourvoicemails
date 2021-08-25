package co.tpcreative.domain.models.request

import java.io.File

class DownloadFileRequest {
    val id : String
    val outputFolder : String
    val fileName : String
    val isDownloaded : Boolean
    val fullLocalPath : String
    constructor(id : String,outputFolder : String,fileName : String){
        this.id = id
        this.outputFolder = outputFolder
        this.fileName = fileName
        this.fullLocalPath = outputFolder + id
        this.isDownloaded = File(this.fullLocalPath).exists()
    }
}