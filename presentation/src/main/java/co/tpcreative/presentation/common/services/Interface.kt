package co.tpcreative.presentation.common.services


interface StateRecorderListener{
    fun onChangeState(state:String)
    fun onNetWorkChange(isNetWorks: Boolean)
    fun onSyncChange(state:String)
    fun onSyncImageChange(state:String)
}