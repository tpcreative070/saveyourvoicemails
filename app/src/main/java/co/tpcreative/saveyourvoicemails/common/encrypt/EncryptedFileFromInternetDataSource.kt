package co.tpcreative.saveyourvoicemails.common.encrypt
import android.net.Uri
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSourceInputStream
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.TransferListener
import com.google.android.exoplayer2.util.Assertions
import java.io.IOException
import javax.crypto.Cipher
import javax.crypto.CipherInputStream

class EncryptedFileFromInternetDataSource(upstream: DataSource,mCipher : Cipher) : DataSource {

    private var upstream: DataSource = upstream
    private var cipherInputStream: CipherInputStream? = null
    private var cipher = mCipher

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        Assertions.checkNotNull<Any>(cipherInputStream)
        val bytesRead = cipherInputStream!!.read(buffer, offset, length)
        return if (bytesRead < 0) {
            C.RESULT_END_OF_INPUT
        } else bytesRead
    }

    override fun addTransferListener(transferListener: TransferListener) {

    }

    override fun open(dataSpec: DataSpec): Long {
        val inputStream = DataSourceInputStream(upstream, dataSpec)
        cipherInputStream = CipherInputStream(inputStream, cipher)
        inputStream.open()
        return C.LENGTH_UNSET.toLong()
    }

    override fun getUri(): Uri {
        return upstream.uri!!
    }

    @Throws(IOException::class)
    override fun close() {
        if (cipherInputStream != null) {
            cipherInputStream = null
            upstream.close()
        }
    }
}
