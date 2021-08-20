package co.tpcreative.saveyourvoicemails.common.base
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.tpcreative.saveyourvoicemails.common.Utils


open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState )
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy")
    }

    override fun onResume() {
        super.onResume()
        log("onResume")
    }
}

fun BaseActivity.log(message : Any){
    Utils.log(this::class.java,message)
}