package pl.kamilbaranowski.chatapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import pl.kamilbaranowski.chatapp.model.User
import pl.kamilbaranowski.chatapp.model.response.SingleUserResponse
import pl.kamilbaranowski.chatapp.repository.NewMessageActivityRepository

class NewMessageActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val newMessageRepository = NewMessageActivityRepository(application)
    val listOfUsers : LiveData<List<User>>

    init {
        listOfUsers = newMessageRepository.listOfUsers
    }

    fun fetchUsers() {
        this.newMessageRepository.fetchUsers()
    }
}