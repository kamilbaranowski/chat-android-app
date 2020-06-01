package pl.kamilbaranowski.chatapp.model.row

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_message_row.view.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import pl.kamilbaranowski.chatapp.ChatLogActivity
import pl.kamilbaranowski.chatapp.LatestMessagesActivity
import pl.kamilbaranowski.chatapp.R
import pl.kamilbaranowski.chatapp.model.ConnectionInfo
import pl.kamilbaranowski.chatapp.model.Message
import pl.kamilbaranowski.chatapp.model.User
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class LatestMessageRow(private val message: Message, private val activity: LatestMessagesActivity): Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val chatPartnerUid: String
        if (message.sender == FirebaseAuth.getInstance().uid){
            chatPartnerUid = message.receiver
        }
        else{
            chatPartnerUid = message.sender
        }


        viewHolder.itemView.textView_messageContent_latest_message.text = message.messageContent
        setViewHolderUsername(chatPartnerUid, viewHolder, activity)

        viewHolder.itemView.textView_date_latest_message.text = convertLongToTime(message.timestamp)
    }

    private fun setViewHolderUsername(uid: String, viewHolder: GroupieViewHolder, activity: LatestMessagesActivity){
        val connectionInfo = ConnectionInfo();

        val url = connectionInfo.getHost() + "/users?uid=${uid}"
        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .get()
            .build()
        Log.d(ChatLogActivity.TAG, request.headers.toString())
        val client = connectionInfo.getUnsafeOkHttpClient()
        client?.newCall(request)?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d(ChatLogActivity.TAG, "Failed to execute request: " + e.message.toString())

            }

            override fun onResponse(call: Call, response: Response) {
                val userJson = response.body?.string()
                LatestMessagesActivity.user = User(
                    JSONObject(userJson!!).getString("uid"),
                    JSONObject(userJson).getString("username"),
                    JSONObject(userJson).getString("password"),
                    JSONObject(userJson).getString("email"),
                    JSONObject(userJson).getString("status"))
                Log.d("LatestMessageActivity", userJson + "\nUser object: " + LatestMessagesActivity.user.toString())
                activity.runOnUiThread {
                    viewHolder.itemView.textView_username_latest_message.text = LatestMessagesActivity.user!!.username
                }
            }
        })
    }
    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
        return format.format(date)
    }

}