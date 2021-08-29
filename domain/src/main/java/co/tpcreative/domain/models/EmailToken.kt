package co.tpcreative.domain.models

import co.tpcreative.domain.models.response.Mail365
import java.io.Serializable
import java.util.*

class EmailToken  {
    var message: Message? = null
    var saveToSentItems = false
    var client_id: String? = null
    var redirect_uri: String? = null
    var grant_type: String? = null
    var refresh_token: String? = null
    var access_token: String? = null
    var user_id: String? = null


    fun convertObject(mMail365: Mail365?, status: EnType?, email: String?, urlForgotPassword: String?): EmailToken? {
        instance?.access_token = mMail365?.access_token
        instance?.refresh_token = mMail365?.refresh_token
        instance?.grant_type = mMail365?.grant_type
        instance?.client_id = mMail365?.client_id
        instance?.redirect_uri = mMail365?.redirect_uri
        instance?.saveToSentItems = true
        instance?.user_id = email
        val messages = Message()
        val subject = "VoiceMailSaver"
        messages.subject = subject
        val body = Body()
        body.contentType = "TEXT"
        var content = ""
        val emailAddress = EmailAddress()
        emailAddress.address = email
        when (status) {
            EnType.NEW_USER -> {
                content = "We have a new customer downloaded our android app!!! - User account $email"
                emailAddress.address = "voicemailsaver@gmail.com"
            }
            EnType.NEW_STORE_FILES_SUBSCRIPTION -> {
                content = "We have a new customer that has subscribed to the Upload Picture/Video Feature using an android phone!!! - User account $email"
                emailAddress.address = "voicemailsaver@gmail.com"
            }
            EnType.FORGOT_PASSWORD -> {
                val mCode: String = urlForgotPassword ?: ""
                content = "Please click the link to reset the password \n $mCode"
            }
            else -> {

            }
        }
        body.content = content
        messages.body = body
        val emailObject = EmailObject()
        emailObject.emailAddress = emailAddress
        messages.toRecipients.add(emailObject)
        instance!!.message = messages
        return instance
    }

    class Message : Serializable {
        var subject: String? = null
        var body: Body? = null
        var toRecipients = ArrayList<EmailObject>() // Getter Methods
    }

    class Body : Serializable {
        var contentType: String? = null
        var content: String? = null
    }

    class EmailObject : Serializable {
        var emailAddress: EmailAddress? = null
    }

    class EmailAddress : Serializable {
        var address: String? = null
    }

    companion object {
        private var instance: EmailToken? = null
        fun getInstance(): EmailToken? {
            if (instance == null) {
                instance = EmailToken()
            }
            return instance
        }
    }
}