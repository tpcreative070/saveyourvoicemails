package co.tpcreative.saveyourvoicemails.common.extension

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.annotation.CheckResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart

@ExperimentalCoroutinesApi
@CheckResult
fun EditText.textChanges(): Flow<Editable?> = callbackFlow {
    val watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            offer(s)
        }
        override fun beforeTextChanged(s: CharSequence?,
                                       start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?,
                                   start: Int, before: Int, count: Int) {}
    }
    addTextChangedListener(watcher)
    awaitClose { removeTextChangedListener(watcher) }
}