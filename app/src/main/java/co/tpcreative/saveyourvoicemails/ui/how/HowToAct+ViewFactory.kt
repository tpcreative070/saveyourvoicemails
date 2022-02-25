package co.tpcreative.saveyourvoicemails.ui.how

import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.ui.trim.TrimAct

fun HowToAct.initUI(){
    setSupportActionBar(binding.toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    title = getString(R.string.how_to)
}