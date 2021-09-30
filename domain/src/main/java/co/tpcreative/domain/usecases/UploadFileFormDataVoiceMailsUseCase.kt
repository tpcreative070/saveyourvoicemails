package co.tpcreative.domain.usecases

import co.tpcreative.domain.interfaces.VoiceMailsDataSource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Part

class UploadFileFormDataVoiceMailsUseCase(private val dataSource: VoiceMailsDataSource) {
    operator fun invoke(@Part("user_id") user_id: RequestBody,
                        @Part("session_token") session_token: RequestBody, @Part("fileTitle") fileTitle: RequestBody,
                        @Part dataPart: MultipartBody.Part?) = dataSource.uploadFileFormData(user_id,session_token,fileTitle,dataPart)
}