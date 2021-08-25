package co.tpcreative.saveyourvoicemails.common.encrypt

import com.google.android.exoplayer2.upstream.DataSource
import javax.crypto.Cipher

class EncryptedFileFromInternetDataSourceFactory(var dataSource: DataSource,var cipher: Cipher) : DataSource.Factory {

    override fun createDataSource(): DataSource {
        return EncryptedFileFromInternetDataSource(dataSource,cipher)
    }
}
