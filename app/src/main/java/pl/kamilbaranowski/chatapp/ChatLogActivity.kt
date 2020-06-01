package pl.kamilbaranowski.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import okhttp3.*
import pl.kamilbaranowski.chatapp.model.ConnectionInfo
import pl.kamilbaranowski.chatapp.model.Message
import pl.kamilbaranowski.chatapp.model.User
import java.io.IOException
import java.util.*

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
    }
    val adapter = GroupAdapter<GroupieViewHolder>()
    var user: User = User()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerView_chat_log.adapter = adapter

        user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        supportActionBar?.title = user.username

        listenForMessages()

        send_button_chat_log.setOnClickListener {
            Log.d(TAG, "Attempt to send message")
            //performSendMessage(user.email)
            performSendMessageToFirebase()
        }

    }

    private fun listenForMessages() {
        val sender = FirebaseAuth.getInstance().uid
        val receiver = user.uid
        FirebaseDatabase.getInstance().getReference("/messages/${sender}/${receiver}")
            .addChildEventListener(object: ChildEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    val message = p0.getValue(Message::class.java)
                    if(message != null) {
                        if(message.sender == FirebaseAuth.getInstance().uid){
                            adapter.add(ChatFromItem(message.messageContent))
                        }
                        else {
                            adapter.add(ChatToItem(message.messageContent))
                        }
                    }
                }

                override fun onChildRemoved(p0: DataSnapshot) {

                }

            })
    }

    private fun performSendMessageToFirebase() {
        val messageText = editText_chat_log.text.toString()
        val sender = FirebaseAuth.getInstance().uid
        if (sender != null){
            val receiver = user.uid

            val senderReference = FirebaseDatabase.getInstance().getReference("/messages/${sender}/${receiver}")
                .push()
            val receiverReference = FirebaseDatabase.getInstance().getReference("/messages/${receiver}/${sender}")
                .push()

            senderReference.setValue(Message(sender!!, receiver, messageText, System.currentTimeMillis()))
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully sent: ${senderReference.key}")
                }
                .addOnFailureListener {
                    Log.d(TAG, "Failed while sending message")
                }

            receiverReference.setValue(Message(sender!!, receiver, messageText, System.currentTimeMillis()))
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully sent: ${receiverReference.key}")
                    editText_chat_log.text.clear()
                    recyclerView_chat_log.scrollToPosition(adapter.itemCount - 1)
                }
                .addOnFailureListener {
                    Log.d(TAG, "Failed while sending message")
                }

            val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/${sender}/${receiver}")
            latestMessageRef.setValue(Message(sender!!, receiver, messageText, System.currentTimeMillis()))

            val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/${receiver}/${sender}")
            latestMessageToRef.setValue(Message(sender!!, receiver, messageText, System.currentTimeMillis()))
        }
        else{
            Log.d("UID: ", "null + ${FirebaseAuth.getInstance().uid}")
        }

    }


    private fun performSendMessage(email: String) {
        val connectionInfo = ConnectionInfo();

        val url = connectionInfo.getHost() + "/message"

        val messageContent = editText_chat_log.text.toString()
        val requestBody = FormBody.Builder()
                //TODO: change sender value
            .add("sender", "test@test.com")
            .add("receiver", email)
            .add("timestamp", System.currentTimeMillis().toString())
            .add("messageContent", messageContent)
            .build()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .method("POST", requestBody)
            .build()
        Log.d(TAG, request.headers.toString())
        val client = connectionInfo.getUnsafeOkHttpClient()
        client?.newCall(request)?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "Failed to execute request: " + e.message.toString())

            }

            override fun onResponse(call: Call, response: Response) {
                Log.d(TAG, response.code.toString())
                //getallmessages
                getMessages("test@test.com", email)
            }

        })
    }

    private fun getMessages(sender: String, receiver: String){
        val connectionInfo = ConnectionInfo();

        val url = connectionInfo.getHost() + "/message?sender=${sender}&receiver=${receiver}"

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .get()
            .build()
        Log.d(TAG, request.headers.toString())
        val client = connectionInfo.getUnsafeOkHttpClient()
        client?.newCall(request)?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "Failed to execute request: " + e.message.toString())

            }

            override fun onResponse(call: Call, response: Response) {
                Log.d(TAG, response.code.toString())
                //getallmessages
                Log.d(TAG, "Response: " + response.body?.string())
            }

        })
    }
}

class ChatFromItem(val text: String): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView_from_row.text = text
    }

}
class ChatToItem(val text: String): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView_to_row.text = text
    }

}