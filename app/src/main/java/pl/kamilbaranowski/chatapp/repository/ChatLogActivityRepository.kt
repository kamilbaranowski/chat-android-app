package pl.kamilbaranowski.chatapp.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_chat_log.*
import pl.kamilbaranowski.chatapp.model.Message
import pl.kamilbaranowski.chatapp.model.User
import pl.kamilbaranowski.chatapp.model.row.ChatFromItem
import pl.kamilbaranowski.chatapp.model.row.ChatToItem
import pl.kamilbaranowski.chatapp.view.ChatLogActivity

class ChatLogActivityRepository(val application: Application) {

    val chatMessage = MutableLiveData<Message>()
    val sendMessageStatus = MutableLiveData<Boolean>()
    fun listenForMessages(user: User) {
        val sender = FirebaseAuth.getInstance().uid
        val receiver = user.uid

        FirebaseDatabase.getInstance().getReference("/messages/${sender}/${receiver}")
            .addChildEventListener(object : ChildEventListener {

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    val message = p0.getValue(Message::class.java)
                    if (message != null) {
                        chatMessage.value = message
                    }

                }

                override fun onChildRemoved(p0: DataSnapshot) {

                }
                override fun onCancelled(p0: DatabaseError) {

                }
                override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                }
                override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                }

            })
    }

    fun performSendMessageToFirebase(messageText: String, receiver: String) {
        val sender = FirebaseAuth.getInstance().uid
        if (sender != null) {
            val senderReference =
                FirebaseDatabase.getInstance().getReference("/messages/${sender}/${receiver}")
                    .push()
            val receiverReference =
                FirebaseDatabase.getInstance().getReference("/messages/${receiver}/${sender}")
                    .push()

            senderReference.setValue(
                Message(sender, receiver, messageText, System.currentTimeMillis()
                )
            )
                .addOnSuccessListener {
                    Log.d(ChatLogActivity.TAG, "Successfully sent: ${senderReference.key}")
                }
                .addOnFailureListener {
                    Log.d(ChatLogActivity.TAG, "Failed while sending message")
                }

            receiverReference.setValue(
                Message(sender, receiver, messageText, System.currentTimeMillis()
                )
            )
                .addOnSuccessListener {
                    Log.d(ChatLogActivity.TAG, "Successfully sent: ${receiverReference.key}")
                    sendMessageStatus.value = true
                }
                .addOnFailureListener {
                    sendMessageStatus.value = false
                    Log.d(ChatLogActivity.TAG, "Failed while sending message")
                }

            val latestMessageRef = FirebaseDatabase.getInstance()
                .getReference("/latest-messages/${sender}/${receiver}")
            latestMessageRef.setValue(
                Message(sender, receiver, messageText, System.currentTimeMillis()
                )
            )

            val latestMessageToRef = FirebaseDatabase.getInstance()
                .getReference("/latest-messages/${receiver}/${sender}")
            latestMessageToRef.setValue(
                Message(sender, receiver, messageText, System.currentTimeMillis()
                )
            )
        } else {
            sendMessageStatus.value = false
            Log.d("UID: ", "null + ${FirebaseAuth.getInstance().uid}")
        }
    }
}