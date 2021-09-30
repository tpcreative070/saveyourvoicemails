package co.tpcreative.saveyourvoicemails.common.services

import android.app.Application
import co.tpcreative.data.BuildConfig
import co.tpcreative.data.voicemails.RetrofitVoiceMailsDataSource
import co.tpcreative.data.logger.AndroidLogger
import co.tpcreative.data.searchhistory.SqliteSearchHistoryDataSource
import co.tpcreative.domain.interfaces.VoiceMailsDataSource
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.services.download.ProgressResponseBody
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.util.HashMap
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

class DefaultServiceLocator private constructor(application: Application) : ServiceLocator {

    companion object {

        private var instance: DefaultServiceLocator? = null

        fun getInstance(application: Application) = instance ?: DefaultServiceLocator(application).also { instance = it }
    }

    private var mInterceptor = HttpLoggingInterceptor().apply {
        if (BuildConfig.DEBUG){
            this.level = HttpLoggingInterceptor.Level.BASIC
        }
    }

    override fun voiceMailsDownloadDataSource(progressListener: ProgressResponseBody.ProgressResponseBodyListener?): VoiceMailsDataSource {
        return RetrofitVoiceMailsDataSource(SaveYourVoiceMailsApplication.getInstance().getUrl(),provideOkHttpClientDownload(progressListener),provideMail365OkHttpClientDefault())
    }

    override val voiceMailsDataSource by lazy { RetrofitVoiceMailsDataSource(SaveYourVoiceMailsApplication.getInstance().getUrl(),provideOkHttpClientDefault(),provideMail365OkHttpClientDefault()) }

    override val mail365DataSource: VoiceMailsDataSource by lazy { RetrofitVoiceMailsDataSource(SaveYourVoiceMailsApplication.getInstance().getUrl(),provideMail365OkHttpClientDefault(),provideMail365OkHttpClientDefault()) }

    override val searchHistoryDataSource by lazy { SqliteSearchHistoryDataSource(application) }

    override val ioDispatcher = Dispatchers.IO

    override val mainDispatcher = Dispatchers.Main.immediate

    private val loggers = mutableMapOf<KClass<*>, AndroidLogger>()

    override fun getLogger(cls: KClass<*>) = loggers[cls] ?: AndroidLogger(cls).also { loggers[cls] = it }

    override fun getHeaders(): HashMap<String, String> {
        val hashMap = HashMap<String, String>()
        hashMap["Content-Type"] = "application/json"
        hashMap["Authorization"] = Utils.getSessionToken() ?: ""
        return hashMap
    }

    override fun provideOkHttpClientDownload(progressListener: ProgressResponseBody.ProgressResponseBodyListener?): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.MINUTES)
            .writeTimeout(10, TimeUnit.MINUTES)
            .readTimeout(10, TimeUnit.MINUTES).addInterceptor(Interceptor { chain ->
                val request = chain.request()
                val builder = request.newBuilder()
                val headers = getHeaders()
                headers.let {
                    for ((key, value) in it) {
                        Timber.d("%s : %s", key, value)
                        builder.addHeader(key, value)
                    }
                }
                if (progressListener == null) return@Interceptor chain.proceed(builder.build())
                val originalResponse = chain.proceed(builder.build())
                originalResponse.newBuilder()
                    .body(ProgressResponseBody(originalResponse.body, progressListener))
                    .build()
            }).addInterceptor(mInterceptor).build()
    }

    override fun provideOkHttpClientDefault() : OkHttpClient {
        val timeout = 1
        return OkHttpClient.Builder()
            .readTimeout(timeout.toLong(), TimeUnit.MINUTES)
            .writeTimeout(timeout.toLong(), TimeUnit.MINUTES)
            .connectTimeout(timeout.toLong(), TimeUnit.MINUTES)
            .addInterceptor { chain ->
                val request = chain.request()
                val builder = request.newBuilder()
                val headers = getHeaders()
                headers.let {
                    for ((key, value) in it) {
                        Timber.d("%s : %s", key, value)
                        builder.addHeader(key, value)
                    }
                }
                chain.proceed(builder.build())
            }.addInterceptor(mInterceptor).build()
    }

    override fun provideMail365OkHttpClientDefault(): OkHttpClient {
        val timeout = 1
        return OkHttpClient.Builder()
            .readTimeout(timeout.toLong(), TimeUnit.MINUTES)
            .writeTimeout(timeout.toLong(), TimeUnit.MINUTES)
            .connectTimeout(timeout.toLong(), TimeUnit.MINUTES)
            .addInterceptor { chain ->
                val request = chain.request()
                val builder = request.newBuilder()
                chain.proceed(builder.build())
            }.addInterceptor(mInterceptor).build()
    }

}