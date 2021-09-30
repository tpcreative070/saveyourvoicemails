package co.tpcreative.trimmerlibrary


class WAVHeader(// sampling frequency in Hz (e.g. 44100).
    private val mSampleRate: Int, // number of channels.
    private val mChannels: Int, // total number of samples per channel.
    private val mNumSamples: Int
) {
    var wAVHeader // the complete header.
            : ByteArray?
        private set
    private val mNumBytesPerSample // number of bytes per sample, all channels included.
            : Int

    override fun toString(): String {
        var str = ""
        if (wAVHeader == null) {
            return str
        }
        val num_32bits_per_lines = 8
        var count = 0
        for (b in wAVHeader!!) {
            val break_line = count > 0 && count % (num_32bits_per_lines * 4) == 0
            val insert_space = count > 0 && count % 4 == 0 && !break_line
            if (break_line) {
                str += '\n'
            }
            if (insert_space) {
                str += ' '
            }
            str += String.format("%02X", b)
            count++
        }
        return str
    }

    private fun setHeader() {
        val header = ByteArray(46)
        var offset = 0
        var size: Int

        // set the RIFF chunk
        System.arraycopy(
            byteArrayOf('R'.toByte(), 'I'.toByte(), 'F'.toByte(), 'F'.toByte()),
            0,
            header,
            offset,
            4
        )
        offset += 4
        size = 36 + mNumSamples * mNumBytesPerSample
        header[offset++] = (size and 0xFF).toByte()
        header[offset++] = (size shr 8 and 0xFF).toByte()
        header[offset++] = (size shr 16 and 0xFF).toByte()
        header[offset++] = (size shr 24 and 0xFF).toByte()
        System.arraycopy(
            byteArrayOf('W'.toByte(), 'A'.toByte(), 'V'.toByte(), 'E'.toByte()),
            0,
            header,
            offset,
            4
        )
        offset += 4

        // set the fmt chunk
        System.arraycopy(
            byteArrayOf('f'.toByte(), 'm'.toByte(), 't'.toByte(), ' '.toByte()),
            0,
            header,
            offset,
            4
        )
        offset += 4
        System.arraycopy(byteArrayOf(0x10, 0, 0, 0), 0, header, offset, 4) // chunk size = 16
        offset += 4
        System.arraycopy(byteArrayOf(1, 0), 0, header, offset, 2) // format = 1 for PCM
        offset += 2
        header[offset++] = (mChannels and 0xFF).toByte()
        header[offset++] = (mChannels shr 8 and 0xFF).toByte()
        header[offset++] = (mSampleRate and 0xFF).toByte()
        header[offset++] = (mSampleRate shr 8 and 0xFF).toByte()
        header[offset++] = (mSampleRate shr 16 and 0xFF).toByte()
        header[offset++] = (mSampleRate shr 24 and 0xFF).toByte()
        val byteRate = mSampleRate * mNumBytesPerSample
        header[offset++] = (byteRate and 0xFF).toByte()
        header[offset++] = (byteRate shr 8 and 0xFF).toByte()
        header[offset++] = (byteRate shr 16 and 0xFF).toByte()
        header[offset++] = (byteRate shr 24 and 0xFF).toByte()
        header[offset++] = (mNumBytesPerSample and 0xFF).toByte()
        header[offset++] = (mNumBytesPerSample shr 8 and 0xFF).toByte()
        System.arraycopy(byteArrayOf(0x10, 0), 0, header, offset, 2)
        offset += 2

        // set the beginning of the data chunk
        System.arraycopy(
            byteArrayOf('d'.toByte(), 'a'.toByte(), 't'.toByte(), 'a'.toByte()),
            0,
            header,
            offset,
            4
        )
        offset += 4
        size = mNumSamples * mNumBytesPerSample
        header[offset++] = (size and 0xFF).toByte()
        header[offset++] = (size shr 8 and 0xFF).toByte()
        header[offset++] = (size shr 16 and 0xFF).toByte()
        header[offset++] = (size shr 24 and 0xFF).toByte()
        wAVHeader = header
    }

    companion object {
        fun getWAVHeader(sampleRate: Int, numChannels: Int, numSamples: Int): ByteArray? {
            return WAVHeader(sampleRate, numChannels, numSamples).wAVHeader
        }
    }

    init {
        mNumBytesPerSample = 2 * mChannels // assuming 2 bytes per sample (for 1 channel)
        wAVHeader = null
        setHeader()
    }
}
