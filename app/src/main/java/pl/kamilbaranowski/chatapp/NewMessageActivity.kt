package pl.kamilbaranowski.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*
import okhttp3.*
import org.json.JSONObject
import pl.kamilbaranowski.chatapp.model.ConnectionInfo
import pl.kamilbaranowski.chatapp.model.User
import java.io.IOException

class NewMessageActivity : AppCompatActivity() {

    companion object {
        val USER_KEY = "USER"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"

        fetchUsers()
    }

    private fun fetchUsers() {
        val connectionInfo = ConnectionInfo();

        val url = connectionInfo.getHost() + "/users"

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .get()
            .build()
        Log.d("NewMessageActivity", request.headers.toString())
        val client = connectionInfo.getUnsafeOkHttpClient()
        client?.newCall(request)?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("NewMessageActivity", "Failed to execute request: " + e.message.toString())

            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (!responseBody.isNullOrEmpty())
                    updateUI(responseBody)
            }

        })


    }

    private fun updateUI(response: String?) {
        this@NewMessageActivity.runOnUiThread {
            val adapter = GroupAdapter<GroupieViewHolder>()

            val users = JSONObject(response!!)
            val keys: Iterator<String> = users.keys();
            while (keys.hasNext()) {
                var key = keys.next()
                var uid = key
                var email = users.getJSONObject(key).getString("email")
                var username = users.getJSONObject(key).getString("username")
                var password = users.getJSONObject(key).getString("password")
                var status = users.getJSONObject(key).getString("status")

                Log.d("NewMessageActivity", key)
                Log.d("NewMessageActivity", users.getJSONObject(key).toString())

                adapter.add(UserItem(User(uid, username, password, email, status)))
            }
            adapter.setOnItemClickListener { item, view ->
                val userItem = item as UserItem
                val intent = Intent(view.context, ChatLogActivity::class.java)
                intent.putExtra(USER_KEY, userItem.user)
                startActivity(intent)
                finish()
            }

            recyclerView_newMessage.adapter = adapter
        }

    }
}
class UserItem(val user: User): Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
       return R.layout.user_row_new_message
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.username_textView_user_row_new_message.text = user.username
    }
}
