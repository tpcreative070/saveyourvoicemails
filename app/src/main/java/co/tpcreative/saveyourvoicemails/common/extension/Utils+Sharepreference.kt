package co.tpcreative.saveyourvoicemails.common.extension
import co.tpcreative.domain.models.GitHubUser
import co.tpcreative.domain.models.response.Mail365
import co.tpcreative.domain.models.response.SessionToken
import co.tpcreative.domain.models.response.User
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.helper.AppPrefs
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import com.google.gson.Gson

fun Utils.getUserInfo(): User? {
    try {
        val value: String? = AppPrefs.encryptedPrefs.read(getString(R.string.key_user), "")
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
     user?.isSignIn = true
     AppPrefs.encryptedPrefs.write(getString(R.string.key_user), Gson().toJson(user))
}

fun Utils.putMail365PreShare(mail365 : Mail365?){
    AppPrefs.encryptedPrefs.write(getString(R.string.key_mail365), Gson().toJson(mail365))
}

fun Utils.putSessionTokenPreShare(sessionToken: SessionToken?){
    AppPrefs.encryptedPrefs.write(getString(R.string.key_session_token), Gson().toJson(sessionToken))
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