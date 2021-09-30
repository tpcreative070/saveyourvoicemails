package co.tpcreative.domain.models

import java.io.Serializable

class ImportFilesModel(mimeTypeFile: MimeTypeFile?, path: String?, position: Int, isImport: Boolean,uuId : String?) : Serializable {
    var path: String?
    var mimeTypeFile: MimeTypeFile?
    var position: Int
    var isImport: Boolean
    var unique_id: String?

    init {
        this.mimeTypeFile = mimeTypeFile
        this.path = path
        this.position = position
        this.isImport = isImport
        this.unique_id = uuId
    }
}