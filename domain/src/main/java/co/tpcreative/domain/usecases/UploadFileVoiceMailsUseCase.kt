package co.tpcreative.domain.usecases

import co.tpcreative.domain.interfaces.VoiceMailsDataSource
import co.tpcreative.domain.models.UploadBody
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Part

class UploadFileVoiceMailsUseCase(private val dataSource: VoiceMailsDataSource) {
    operator fun invoke(@Part metaPart: MultipartBody.Part?,
                                @Part dataPart: MultipartBody.Part?,) = dataSource.uploadFile(metaPart,dataPart)
}