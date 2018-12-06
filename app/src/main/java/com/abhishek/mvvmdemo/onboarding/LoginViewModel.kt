package com.abhishek.mvvmdemo.onboarding

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abhishek.mvvmdemo.R
import com.abhishek.mvvmdemo.api.MockApiService
import com.abhishek.mvvmdemo.model.Error
import com.abhishek.mvvmdemo.model.User
import kotlinx.coroutines.*

class LoginViewModel(private val apiService: MockApiService) : ViewModel() {
    val mutableLiveData = MutableLiveData<LoginState>()

    fun login(email: String, password: String) {
        val user = User(email, password)
        val validationResult = user.validate()

        if (validationResult == User.ValidationResult.NO_ERROR) {
            // correct input go ahead and make api call
            mutableLiveData.value = LoadingState()
            GlobalScope.launch {
                val apiResponse = performLogin(email, password)
                if (apiResponse.error != null) {
                    apiError(apiResponse.error)
                } else {
                    mutableLiveData.postValue(LoginSuccessState())
                }
            }
        } else {
            validationError(validationResult)
        }

        return
    }

    private suspend fun CoroutineScope.performLogin(
        email: String,
        password: String
    ) = async { apiService.login(User(email, password)) }.await()

    private fun apiError(error: Error?) {
        when (error?.code) {
            11 ->
                mutableLiveData.postValue(
                    LoginErrorState(
                        error = R.string.invalid_username_or_password,
                        errorType = ErrorType.EMAIL_AND_PASSWORD
                    )
                )
        }
    }

    private fun validationError(validationResult: User.ValidationResult) {
        when (validationResult) {
            User.ValidationResult.INVALID_EMAIL ->
                mutableLiveData.value = LoginErrorState(error = R.string.invalid_email, errorType = ErrorType.EMAIL)
            User.ValidationResult.EMPTY_EMAIL ->
                mutableLiveData.value = LoginErrorState(error = R.string.empty_email, errorType = ErrorType.EMAIL)
            User.ValidationResult.EMPTY_PASSWORD ->
                mutableLiveData.value = LoginErrorState(error = R.string.empty_password, errorType = ErrorType.PASSWORD)
            User.ValidationResult.NO_ERROR ->
                mutableLiveData.value = IdleState()
        }
    }
}