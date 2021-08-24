package co.tpcreative.saveyourvoicemails.common.services

import co.tpcreative.domain.models.BaseResponse
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.network.Resource
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.IOException

enum class ErrorCodes(val code: Int) {
    SocketTimeOut(-1),
}
open class ResponseHandler {
    companion object{
        val TAG = ResponseHandler::class.java.simpleName
        fun <T : Any> handleSuccess(data: T): Resource<T> {
            return Resource.success(data)
        }
        fun <T : Any> handleException(e: Exception? = null): Resource<T> {
            return if (e is HttpException) {
                val mBody: ResponseBody? = (e as HttpException?)?.response()?.errorBody()
                val mCode = (e as HttpException?)?.response()?.code()
                try {
                    val mMessage = mBody?.string()
                    Utils.log(this::class.java,mMessage)
                    Resource.error(mCode!!,mMessage!!, null)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Resource.error(mCode!!,e.message!!, null)
                }
            } else {
                val mMessage = getErrorMessage(ErrorCodes.SocketTimeOut.code)
                Utils.log(this::class.java,mMessage)
                Resource.error(ErrorCodes.SocketTimeOut.code,mMessage, null)
            }
        }

        private fun getErrorMessage(code: Int): String {
            return when (code) {
                ErrorCodes.SocketTimeOut.code -> "Timeout"
                400 -> "Bad request"
                401 -> "Unauthorised"
                403 -> "Forbidden"
                404 -> "Not found"
                405 -> "Method not allowed"
                500 -> "Internal server error"
                else -> "Something went wrong"
            }
        }

        private fun getErrorCode(code: Int){
            when (code) {
                400 -> Utils.log(this::class.java ,"Bad request")
                401 -> Utils.log(this::class.java ,"Unauthorised")
                403 -> Utils.log(this::class.java,"Forbidden")
                404 -> Utils.log(this::class.java ,"Not found")
                405 -> Utils.log(this::class.java ,"Method not allowed")
                500 -> Utils.log(this::class.java ,"Internal server error")
                else -> Utils.log(this::class.java ,"Something went wrong")
            }
        }
    }
}