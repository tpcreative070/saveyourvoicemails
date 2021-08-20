package co.tpcreative.saveyourvoicemails.common.services


interface StateRecorderListener{
    fun onChangeState(state:String)
    fun onNetWorkChange(isNetWorks: Boolean)
    fun onSyncChange(state:String)
    fun onSyncImageChange(state:String)
}