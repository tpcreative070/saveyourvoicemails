package co.tpcreative.saveyourvoicemails.common.encrypt
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.TransferListener
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class EncryptedFileDataSourceFactory(private val mCipher: Cipher?, private val mSecretKeySpec: SecretKeySpec?, private val mIvParameterSpec: IvParameterSpec?, listener: TransferListener<in DataSource?>?) : DataSource.Factory {
    private val mTransferListener: TransferListener<in DataSource?>? = listener
    override fun createDataSource() : EncryptedFileDataSource {
        return EncryptedFileDataSource(mCipher, mSecretKeySpec, mIvParameterSpec, mTransferListener)
    }
    companion object {
        private val TAG = EncryptedFileDataSourceFactory::class.java.simpleName
    }
}