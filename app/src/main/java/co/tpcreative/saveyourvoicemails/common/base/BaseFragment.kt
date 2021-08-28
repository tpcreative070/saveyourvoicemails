package co.tpcreative.saveyourvoicemails.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.Utils
import com.tapadoo.alerter.Alerter
import com.tapadoo.alerter.OnHideAlertListener
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

abstract class BaseFragment : Fragment() {
    var isLoaded = false
    var isDead = false
    private val lock = ReentrantLock()
    private val condition = lock.newCondition()
    protected abstract fun getLayoutId(): Int
    protected abstract fun getLayoutId(inflater: LayoutInflater?, viewGroup: ViewGroup?): View?
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        isDead = false
        val viewResponse = getLayoutId(inflater, container)
        return if (viewResponse != null) {
            //work()
            viewResponse
        } else {
            val view: View = inflater.inflate(getLayoutId(), container, false)
            //work()
            view
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lock.withLock {
            isLoaded = true
            condition.signalAll()
        }
        work()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        isDead = true
        super.onDestroyView()
        hide()
    }

    override fun onDestroy() {
        super.onDestroy()
        remove()
        isLoaded = false
    }

    protected fun remove() {}
    protected fun hide() {}
    protected open fun work() {}

    fun onBasicAlertNotify(title: String? = "Warning", message: String?) {
        activity?.let {
            Alerter.create(it)
                .setTitle(title!!)
                .setBackgroundColorInt(
                    ContextCompat.getColor(it, R.color.colorAccent))
                .setText(message ?: "")
                .setDuration(5000)
                .setOnHideListener(OnHideAlertListener {
                })
                .show()
        }
    }

    var TAG : String = this::class.java.simpleName

}

fun BaseFragment.log(message : Any){
    Utils.log(this::class.java,message)
}