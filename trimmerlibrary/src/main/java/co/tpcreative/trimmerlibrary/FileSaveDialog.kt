package co.tpcreative.trimmerlibrary

import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.os.Message
import android.text.Editable
import android.view.View
import android.widget.*
import java.util.*

class FileSaveDialog(
    context: Context?,
    resources: Resources,
    originalName: String,
    response: Message
) : Dialog(context!!) {
    private var mFilename: EditText? = null
    private var mResponse: Message? = null
    private val mOriginalName: String
    private fun setFilenameEditBoxFromName(onlyIfNotEdited: Boolean) {
        if (onlyIfNotEdited) {
            val currentText: Editable? = mFilename?.text
            val expectedText = mOriginalName
            if (!currentText?.let { expectedText.contentEquals(it) }!!) {
                return
            }
        }
        mFilename?.setText("$mOriginalName")
    }

    private val saveListener = View.OnClickListener {
        mResponse?.obj = mFilename?.text
        mResponse?.sendToTarget()
        dismiss()
    }
    private val cancelListener = View.OnClickListener { dismiss() }

    companion object {
    }

    init {

        // Inflate our UI from its XML layout description.
        setContentView(R.layout.file_save)
        setTitle(resources.getString(R.string.file_save_title))
        mFilename = findViewById<View>(R.id.filename) as EditText
        mOriginalName = originalName
        setFilenameEditBoxFromName(false)
        val save = findViewById<View>(R.id.save) as Button
        save.setOnClickListener(saveListener)
        val cancel = findViewById<View>(R.id.cancel) as Button
        cancel.setOnClickListener(cancelListener)
        mResponse = response
    }
}
