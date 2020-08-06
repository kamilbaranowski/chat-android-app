package pl.kamilbaranowski.chatapp.repository

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupieViewHolder
import pl.kamilbaranowski.chatapp.model.ConnectionInfo
import pl.kamilbaranowski.chatapp.model.Message
import pl.kamilbaranowski.chatapp.model.User
import pl.kamilbaranowski.chatapp.network.ServerAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LatestMessagesActivityRepository(val application: Application) {

    val latestMessages = MutableLiveData<HashMap<String, Message>>()
    val latestMessagesViewHolder = MutableLiveData<User>()
    private val latestMessagesMap = HashMap<String, Message>()
    fun firebaseSignOut() {
        FirebaseAuth.getInstance().signOut()
    }

    fun getCurrentUserFirebaseUid(): String? {
        return FirebaseAuth.getInstance().uid
    }

    fun listenForLatestMessages() {
        val receiver = getCurrentUserFirebaseUid()
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/${receiver}")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val message = p0.getValue(Message::class.java) ?: return
                latestMessagesMap[p0.key!!] = message
               // refreshLatestMessages()
                latestMessages.value = latestMessagesMap
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val message = p0.getValue(Message::class.java) ?: return
                latestMessagesMap[p0.key!!] = message
                //refreshLatestMessages()
                latestMessages.value = latestMessagesMap
            }
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }
            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })
    }

}