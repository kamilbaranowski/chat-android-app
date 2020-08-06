package pl.kamilbaranowski.chatapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import pl.kamilbaranowski.chatapp.R
import pl.kamilbaranowski.chatapp.model.Message
import pl.kamilbaranowski.chatapp.model.User
import pl.kamilbaranowski.chatapp.model.row.ChatFromItem
import pl.kamilbaranowski.chatapp.model.row.ChatToItem
import pl.kamilbaranowski.chatapp.viewmodel.ChatLogActivityViewModel
import pl.kamilbaranowski.chatapp.viewmodel.LatestMessagesActivityViewModel

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
    }

    private lateinit var viewModel: ChatLogActivityViewModel
    val adapter = GroupAdapter<GroupieViewHolder>()
    var user: User = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        viewModel = ViewModelProvider(this).get(ChatLogActivityViewModel::class.java)
        recyclerView_chat_log.adapter = adapter

        user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)!!

        supportActionBar?.title = user.username


        send_button_chat_log.setOnClickListener {
            val messageText = editText_chat_log.text.toString()
            val receiver = user.uid
            viewModel.performSendMessageToFirebase(messageText, receiver)
        }

        viewModel.listenForMessages(user)
        viewModel.chatMessage.observe(this, Observer {
            if (it.sender == FirebaseAuth.getInstance().uid) {
                adapter.add(ChatToItem(it.messageContent))
            } else {
                adapter.add(ChatFromItem(it.messageContent))
            }
        })

        viewModel.sendMessageStatus.observe(this, Observer {
            if (it) {
                editText_chat_log.text.clear()
                recyclerView_chat_log.scrollToPosition(adapter.itemCount - 1)
            }
        })

    }
}