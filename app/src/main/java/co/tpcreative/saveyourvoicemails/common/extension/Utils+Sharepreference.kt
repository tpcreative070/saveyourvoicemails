package co.tpcreative.saveyourvoicemails.common.extension
import co.tpcreative.domain.models.User
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.helper.AppPrefs
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import com.google.gson.Gson

fun Utils.getUserInfo(): User? {
    try {
        val value: String? = AppPrefs.encryptedPrefs.read(getString(R.string.user_key), "")
        if (value != null) {
            val mUser: User? = Gson().fromJson(value, User::class.java)
            if (mUser != null) {
                return mUser
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

fun Utils.putUserPreShare(user: User?) {
     AppPrefs.encryptedPrefs.write(getString(R.string.user_key), Gson().toJson(user))
}

fun Utils.isSignedIn() : Boolean {
    val mUser = getUserInfo()
    mUser?.let {
        return it.isSignIn
    }
    return false
}

fun getString(res: Int) : String{
    return SaveYourVoiceMailsApplication.getInstance().getString(res)
}