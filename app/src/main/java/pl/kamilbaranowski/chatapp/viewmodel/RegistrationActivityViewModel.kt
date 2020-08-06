package pl.kamilbaranowski.chatapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import pl.kamilbaranowski.chatapp.repository.RegistrationActivityRepository

class RegistrationActivityViewModel(application: Application) : AndroidViewModel(application)  {

    private val registrationRepository = RegistrationActivityRepository(application)
    val registrationStatus: LiveData<Boolean>

    init {
        registrationStatus = registrationRepository.registrationStatus
    }

    fun performRegistration(username: String, email: String, password: String) {
        registrationRepository.performRegistration(username, email, password)
    }
}