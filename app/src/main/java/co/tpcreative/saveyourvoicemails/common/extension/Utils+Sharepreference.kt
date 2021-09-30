package co.tpcreative.saveyourvoicemails.common.extension
import co.tpcreative.domain.models.GitHubUser
import co.tpcreative.domain.models.response.Mail365
import co.tpcreative.domain.models.response.SessionToken
import co.tpcreative.domain.models.response.User
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.helper.AppPrefs
import co.tpcreative.saveyourvoicemails.common.helper.EncryptDecryptFilesHelper
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import com.google.gson.Gson

fun Utils.getUserInfo(): User? {
    try {
        val value: String? = AppPrefs.encryptedPrefs.read(getString(R.string.key_user), "")
        if (value != null) {
            val mUser: User? = Gson().fromJson(value, User::class.java)
            if (mUser != null) {
                log(this::class.java,mUser)
                return mUser
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

fun Utils.getSessionTokenObject(): SessionToken? {
    try {
        val value: String? = AppPrefs.encryptedPrefs.read(getString(R.string.key_session_token), "")
        if (value != null) {
            val mSessionToken: SessionToken? = Gson().fromJson(value, SessionToken::class.java)
            if (mSessionToken != null) {
                return mSessionToken
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

fun Utils.getMail365(): Mail365? {
    try {
        val value: String? = AppPrefs.encryptedPrefs.read(getString(R.string.key_mail365), "")
        if (value != null) {
            val mMail365: Mail365? = Gson().fromJson(value, Mail365::class.java)
            if (mMail365 != null) {
                return mMail365
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

fun Utils.putRequestCode(code : String?){
    AppPrefs.encryptedPrefs.write(getString(R.string.key_request_code),code)
}

fun Utils.getRequestCode() : String?{
    return AppPrefs.encryptedPrefs.read(getString(R.string.key_request_code),"null")
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

fun Utils.signOut(){
    Utils.putUserPreShare(null)
    Utils.putMail365PreShare(null)
    Utils.putSessionTokenPreShare(null)
    EncryptDecryptFilesHelper.getInstance()?.cleanUp()
}

fun Utils.isAutoRecord() : Boolean {
    return AppPrefs.encryptedPrefs.read(getString(R.string.key_automatically_recorder_voice),false)
}

fun Utils.putRecord(isValue : Boolean)  {
    return AppPrefs.encryptedPrefs.write(getString(R.string.key_automatically_recorder_voice),isValue)
}

fun Utils.isAlreadyAskRecording() : Boolean {
    return AppPrefs.encryptedPrefs.read(getString(R.string.key_already_asked_recording),false)
}

fun Utils.putAlreadyAskRecording(isValue : Boolean)  {
    return AppPrefs.encryptedPrefs.write(getString(R.string.key_already_asked_recording),isValue)
}

fun Utils.putSubscription(isValue : Boolean){
    AppPrefs.encryptedPrefs.write(getString(R.string.key_subscription),isValue)
}

fun Utils.getIsSubscribed() : Boolean {
    return AppPrefs.encryptedPrefs.read(getString(R.string.key_subscription),false)
}

fun Utils.putSentSubscription(isValue : Boolean){
    AppPrefs.encryptedPrefs.write(getString(R.string.key_sent_subscribed_app),isValue)
}

fun Utils.getIsSentSubscribed() : Boolean {
    return AppPrefs.encryptedPrefs.read(getString(R.string.key_sent_subscribed_app),false)
}

fun Utils.putSentDownloaded(isValue : Boolean){
    AppPrefs.encryptedPrefs.write(getString(R.string.key_sent_downloaded_app),isValue)
}

fun Utils.getIsSentDownloaded() : Boolean {
    return AppPrefs.encryptedPrefs.read(getString(R.string.key_sent_downloaded_app),false)
}

fun getString(res: Int) : String{
    return SaveYourVoiceMailsApplication.getInstance().getString(res)
}