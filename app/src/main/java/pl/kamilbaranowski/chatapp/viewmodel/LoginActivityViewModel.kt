package pl.kamilbaranowski.chatapp.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import pl.kamilbaranowski.chatapp.model.response.LoginResponse
import pl.kamilbaranowski.chatapp.repository.LoginActivityRepository


class LoginActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val loginRepository = LoginActivityRepository(application)

    val loginResponse : LiveData<LoginResponse>
    val firebaseToken : LiveData<String>
    init {
        this.loginResponse = loginRepository.token
        this.firebaseToken = loginRepository.firebaseToken
    }

    fun login(email: String, password: String) {
        this.loginRepository.login(email, password)
    }

    fun loginFirebase(customToken: String) {
        this.loginRepository.loginFirebase(customToken)
    }

    fun saveTokens() {
        val sharedPref = getApplication<Application>().applicationContext.getSharedPreferences("auth", Context.MODE_PRIVATE)
        sharedPref.edit().putString("token", loginResponse.value?.token).apply()
        sharedPref.edit().putString("idToken", firebaseToken.value).apply()
    }
}