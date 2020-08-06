package pl.kamilbaranowski.chatapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import pl.kamilbaranowski.chatapp.model.User
import pl.kamilbaranowski.chatapp.repository.LatestMessageRowRepository
import java.text.SimpleDateFormat
import java.util.*

class LatestMessageRowViewModel(application: Application) : AndroidViewModel(application) {
    private val latestMessageRowRepository = LatestMessageRowRepository(application)
    val user: LiveData<User>

    init {
        user = latestMessageRowRepository.user
    }


    fun getUsername(uid: String/*, viewHolder: GroupieViewHolder, activity: LatestMessagesActivity*/) {
        this.latestMessageRowRepository.getUsername(uid)
    }
}