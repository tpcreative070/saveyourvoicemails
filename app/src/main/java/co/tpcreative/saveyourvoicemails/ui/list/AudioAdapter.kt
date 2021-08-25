package co.tpcreative.saveyourvoicemails.ui.list
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.GridLayoutManager
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.adapter.BaseAdapter
import co.tpcreative.saveyourvoicemails.common.adapter.BaseHolder

class AudioAdapter (private val mLayoutManager: GridLayoutManager? = null, inflater: LayoutInflater, private val context: Context?, private val itemSelectedListener: ItemSelectedListener?) : BaseAdapter<AudioViewModel, BaseHolder<AudioViewModel>>(inflater) {

    companion object {
        const val SPAN_COUNT_ONE = 1
        const val SPAN_COUNT_THREE = 3
    }

    private val VIEW_TYPE_SMALL = 1
    private val VIEW_TYPE_BIG = 2

    override fun getItemCount(): Int {
        return mDataSource.size
    }


    override fun getItemViewType(position: Int): Int {
        val spanCount = mLayoutManager?.spanCount
        return if (spanCount == SPAN_COUNT_THREE) {
            VIEW_TYPE_BIG
        } else {
            VIEW_TYPE_SMALL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<AudioViewModel> {
        return ItemHolder(inflater!!.inflate(R.layout.audio_items, parent, false))
    }

    interface ItemSelectedListener {
        fun onClickItem(position: Int)
        fun onLongClickItem(position: Int)
    }

    inner class ItemHolder(view: View) : BaseHolder<AudioViewModel>(view) {
        private val tvTitle : AppCompatTextView = itemView.findViewById(R.id.tvTitle)
        private val tvCreatedDateTime : AppCompatTextView = itemView.findViewById(R.id.tvCreatedDateTime)
        private val rlItem : RelativeLayout = itemView.findViewById(R.id.rlItem)
        override fun bind(data: AudioViewModel, position: Int) {
            super.bind(data, position)
            tvTitle.text = data.title
            tvCreatedDateTime.text = data.createdDateTime
            rlItem.setOnClickListener {
                itemSelectedListener?.onClickItem(position)
            }
        }
    }


}