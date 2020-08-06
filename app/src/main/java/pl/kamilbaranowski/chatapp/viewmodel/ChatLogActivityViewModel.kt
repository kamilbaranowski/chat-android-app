package pl.kamilbaranowski.chatapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_chat_log.*
import pl.kamilbaranowski.chatapp.model.Message
import pl.kamilbaranowski.chatapp.model.User
import pl.kamilbaranowski.chatapp.repository.ChatLogActivityRepository

class ChatLogActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val chatLogRepository = ChatLogActivityRepository(application)
    val chatMessage: LiveData<Message>
    val sendMessageStatus: LiveData<Boolean>
    init {
        this.chatMessage = chatLogRepository.chatMessage
        this.sendMessageStatus = chatLogRepository.sendMessageStatus
    }

    fun listenForMessages(user: User) {
        this.chatLogRepository.listenForMessages(user)
    }

    fun performSendMessageToFirebase(messageText: String, receiver: String) {
        this.chatLogRepository.performSendMessageToFirebase(messageText, receiver)
    }
}