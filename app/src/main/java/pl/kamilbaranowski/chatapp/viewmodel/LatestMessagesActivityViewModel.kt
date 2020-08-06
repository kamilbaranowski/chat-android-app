package pl.kamilbaranowski.chatapp.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.auth0.android.jwt.JWT
import com.google.firebase.auth.FirebaseAuth
import com.xwray.groupie.GroupieViewHolder
import pl.kamilbaranowski.chatapp.model.Message
import pl.kamilbaranowski.chatapp.model.User
import pl.kamilbaranowski.chatapp.repository.LatestMessagesActivityRepository
import pl.kamilbaranowski.chatapp.view.LatestMessagesActivity
import pl.kamilbaranowski.chatapp.view.LoginActivity
import pl.kamilbaranowski.chatapp.view.RegistrationActivity
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class LatestMessagesActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val latestMessagesRepository = LatestMessagesActivityRepository(application)
    val latestMessages: LiveData<HashMap<String, Message>>
    val latestMessagesViewHolder: LiveData<User>

    init {
        latestMessages = latestMessagesRepository.latestMessages
        latestMessagesViewHolder = latestMessagesRepository.latestMessagesViewHolder

    }


    fun verifyIfUserIsLoggedIn(): Boolean {
        val sharedPref = getApplication<Application>().applicationContext.getSharedPreferences("auth", Context.MODE_PRIVATE)
        val token = sharedPref.getString("token", null)
        var isLoggedIn = true
        if (token != null) {
            Log.d("LatestMessagess token: ", token)

            try {
                var expirationTime = JWT(token).getClaim("exp").asLong()
                val uid = this.latestMessagesRepository.getCurrentUserFirebaseUid()

                if (expirationTime!! <= (System.currentTimeMillis() / 1000) || uid == null) {
                    Log.d("LatestMessages: ", "Token expired! + UID: ${uid}")
                    isLoggedIn = false
                }
            } catch (e: Exception) {
                isLoggedIn = false
                e.printStackTrace()
            }
        } else {
            isLoggedIn = false;
        }
        return isLoggedIn
    }


    fun listenForLatestMessages() {
        this.latestMessagesRepository.listenForLatestMessages()
    }

    fun clearUserAuthData() {
        this.latestMessagesRepository.firebaseSignOut()
        getApplication<Application>().applicationContext.getSharedPreferences("auth", Context.MODE_PRIVATE).edit().clear().apply()
    }


}