package co.tpcreative.saveyourvoicemails.ui.list
import android.content.Context
import android.view.*
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.GridLayoutManager
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.adapter.BaseAdapter
import co.tpcreative.saveyourvoicemails.common.adapter.BaseHolder
import com.google.gson.Gson

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
        fun onEditItem(position: Int)
        fun onDeleteItem(position: Int)
    }

    inner class ItemHolder(view: View) : BaseHolder<AudioViewModel>(view) {
        private val tvTitle : AppCompatTextView = itemView.findViewById(R.id.tvTitle)
        private val tvCreatedDateTime : AppCompatTextView = itemView.findViewById(R.id.tvCreatedDateTime)
        private val rlItem : RelativeLayout = itemView.findViewById(R.id.rlItem)
        private val imgOverflow : AppCompatImageView = itemView.findViewById(R.id.imgOverflow)
        override fun bind(data: AudioViewModel, position: Int) {
            super.bind(data, position)
            tvTitle.text = data.title
            tvCreatedDateTime.text = data.createdDateTime
            rlItem.setOnClickListener {
                itemSelectedListener?.onClickItem(position)
            }

            imgOverflow.setOnClickListener {
                showPopupMenu(it, R.menu.menu_voice_mails, position)
            }
        }
    }

    private fun showPopupMenu(view: View, menu: Int, position: Int) {
        val popup = PopupMenu(context!!, view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(menu, popup.menu)
        popup.setOnMenuItemClickListener(MyMenuItemClickListener(position))
        popup.show()
    }

    internal inner class MyMenuItemClickListener(var position: Int) : PopupMenu.OnMenuItemClickListener {
        override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
            when (menuItem?.itemId) {
                R.id.action_edit -> {
                    itemSelectedListener?.onEditItem(position)
                    return true
                }
                R.id.action_delete -> {
                    itemSelectedListener?.onDeleteItem(position)
                    return true
                }
                else -> {
                }
            }
            return false
        }
    }
}