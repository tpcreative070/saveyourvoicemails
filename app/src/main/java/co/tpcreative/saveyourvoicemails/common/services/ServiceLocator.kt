package co.tpcreative.saveyourvoicemails.common.services

import co.tpcreative.common.Logger
import co.tpcreative.domain.interfaces.VoiceMailsDataSource
import co.tpcreative.domain.interfaces.SearchHistoryDataSource
import co.tpcreative.saveyourvoicemails.common.services.download.ProgressResponseBody
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.OkHttpClient
import java.util.HashMap
import kotlin.reflect.KClass

interface ServiceLocator {

    val voiceMailsDataSource: VoiceMailsDataSource

    val mail365DataSource: VoiceMailsDataSource

    fun voiceMailsDownloadDataSource(progressListener: ProgressResponseBody.ProgressResponseBodyListener?): VoiceMailsDataSource

    val searchHistoryDataSource: SearchHistoryDataSource

    val ioDispatcher: CoroutineDispatcher

    val mainDispatcher: CoroutineDispatcher

    fun getLogger(cls: KClass<*>): Logger

    fun getHeaders(): HashMap<String, String>

    fun provideOkHttpClientDefault(): OkHttpClient

    fun provideMail365OkHttpClientDefault(): OkHttpClient

    fun provideOkHttpClientDownload(progressListener: ProgressResponseBody.ProgressResponseBodyListener?): OkHttpClient
}