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
    private var mTypeSpinner: Spinner? = null
    private var mFilename: EditText? = null
    private var mResponse: Message? = null
    private val mOriginalName: String
    private val mTypeArray: ArrayList<String>
    private var mPreviousSelection: Int
    private fun setFilenameEditBoxFromName(onlyIfNotEdited: Boolean) {
        if (onlyIfNotEdited) {
            val currentText: Editable? = mFilename?.text
            val expectedText = mOriginalName + " " +
                    mTypeArray[mPreviousSelection]
            if (!currentText?.let { expectedText.contentEquals(it) }!!) {
                return
            }
        }
        val newSelection = mTypeSpinner?.selectedItemPosition
        val newSuffix = mTypeArray[newSelection!!]
        mFilename?.setText("$mOriginalName $newSuffix")
        mPreviousSelection = mTypeSpinner!!.selectedItemPosition
    }

    private val saveListener = View.OnClickListener {
        mResponse?.obj = mFilename?.text
        mResponse?.arg1 = mTypeSpinner!!.selectedItemPosition
        mResponse?.sendToTarget()
        dismiss()
    }
    private val cancelListener = View.OnClickListener { dismiss() }

    companion object {
        // File kinds - these should correspond to the order in which
        // they're presented in the spinner control
        const val FILE_KIND_MUSIC = 0
        const val FILE_KIND_ALARM = 1
        const val FILE_KIND_NOTIFICATION = 2
        const val FILE_KIND_RINGTONE = 3

        /**
         * Return a human-readable name for a kind (music, alarm, ringtone, ...).
         * These won't be displayed on-screen (just in logs) so they shouldn't
         * be translated.
         */
        fun KindToName(kind: Int): String {
            return when (kind) {
                FILE_KIND_MUSIC -> "Music"
                FILE_KIND_ALARM -> "Alarm"
                FILE_KIND_NOTIFICATION -> "Notification"
                FILE_KIND_RINGTONE -> "Ringtone"
                else -> "Unknown"
            }
        }
    }

    init {

        // Inflate our UI from its XML layout description.
        setContentView(R.layout.file_save)
        setTitle(resources.getString(R.string.file_save_title))
        mTypeArray = ArrayList()
        mTypeArray.add(resources.getString(R.string.type_music))
        mTypeArray.add(resources.getString(R.string.type_alarm))
        mTypeArray.add(resources.getString(R.string.type_notification))
        mTypeArray.add(resources.getString(R.string.type_ringtone))
        mFilename = findViewById<View>(R.id.filename) as EditText
        mOriginalName = originalName
        val adapter = ArrayAdapter(
            context!!, android.R.layout.simple_spinner_item, mTypeArray
        )
        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        mTypeSpinner = findViewById<View>(R.id.ringtone_type) as Spinner
        mTypeSpinner?.adapter = adapter
        mTypeSpinner?.setSelection(FILE_KIND_RINGTONE)
        mPreviousSelection = FILE_KIND_RINGTONE
        setFilenameEditBoxFromName(false)
        mTypeSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                v: View,
                position: Int,
                id: Long
            ) {
                setFilenameEditBoxFromName(true)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        val save = findViewById<View>(R.id.save) as Button
        save.setOnClickListener(saveListener)
        val cancel = findViewById<View>(R.id.cancel) as Button
        cancel.setOnClickListener(cancelListener)
        mResponse = response
    }
}
