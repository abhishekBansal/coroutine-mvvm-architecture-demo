package com.abhishek.mvvmdemo.api

import com.abhishek.mvvmdemo.model.Error
import com.abhishek.mvvmdemo.model.LoginResponse
import com.abhishek.mvvmdemo.model.User
import kotlinx.coroutines.delay

class MockApiService {
    suspend fun login(user: User): LoginResponse {
        val result = user.validate()
        return if (result == User.ValidationResult.NO_ERROR) {
            return if (user.username == "abhishek@gmail.com" && user.password == "1234") {
                delay(3000)
                LoginResponse("accessToken", "Abhishek", "uuid", null)
            } else {
                delay(3000)
                LoginResponse(null, null, null, Error("Invalid Username or Password", 11))
            }
        } else {
            delay(3000)
            LoginResponse(null, null, null, Error(result.toString(), result.ordinal))
        }
    }
}