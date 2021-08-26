package co.tpcreative.saveyourvoicemails.common.preference
import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceViewHolder
import co.tpcreative.saveyourvoicemails.R

class MyPreferenceCategory @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int = 0
) : PreferenceCategory(context, attrs, defStyleAttr) {
    init {
        widgetLayoutResource = R.layout.preference_category_layout
    }
    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        with(holder.itemView) {
            val title = this.findViewById(android.R.id.title) as TextView
        }
    }
}