package co.tpcreative.domain.models.request

data class UserRequest(
                        val user_id : String,
                        val email: String,
                        val password: String,
                        val newPassword: String ?= null,
                        val phone_number : String? = null,
                        val device_id: String)