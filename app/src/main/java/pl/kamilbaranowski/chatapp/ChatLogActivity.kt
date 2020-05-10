package pl.kamilbaranowski.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import okhttp3.*
import pl.kamilbaranowski.chatapp.model.ConnectionInfo
import pl.kamilbaranowski.chatapp.model.User
import java.io.IOException
import java.util.*

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        supportActionBar?.title = user.username


        send_button_chat_log.setOnClickListener {
            Log.d(TAG, "Attempt to send message")
            performSendMessage(user.email)
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