package co.tpcreative.saveyourvoicemails.common.helper

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import co.tpcreative.saveyourvoicemails.common.encrypt.SecurityUtil

object AppPrefs {
    lateinit var encryptedPrefs: Prefs
    lateinit var prefs: Prefs
    fun initEncryptedPrefs(context: Context) {
        val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS+ SecurityUtil.key_password_default)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        encryptedPrefs =
            Prefs(
                "${context.packageName}_ENCRYPTED_PREFS",
                context,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
    }
    fun initPrefs(context: Context) {
        prefs = Prefs("${context}_PREFS", context)
    }
}