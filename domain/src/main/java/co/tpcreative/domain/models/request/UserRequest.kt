package co.tpcreative.domain.models.request

data class UserRequest( val email: String,
                        val password: String,
                        val newPassword: String ?= null,
                        val device_id: String)