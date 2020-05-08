package pl.kamilbaranowski.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_message.*
import okhttp3.*
import pl.kamilbaranowski.chatapp.model.ConnectionInfo
import java.io.IOException

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"

        val adapter = GroupAdapter<GroupieViewHolder>()

        adapter.add(UserItem())
        adapter.add(UserItem())
        adapter.add(UserItem())
        adapter.add(UserItem())
        adapter.add(UserItem())


        recyclerView_newMessage.adapter = adapter

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
        client?.newCall(request)?.enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("NewMessageActivity", "Failed to execute request: " + e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                //parse JWT
                //val sharedPref = context.getSharedPreferences("token", Context.MODE_PRIVATE)
                //sharedPref.edit().putString("token", response.body?.string()).apply()
                Log.d("NewMessageActivity", "Success: " + response.body?.string())
                //val intent = Intent(context, LatestMessagesActivity::class.java)
                //startActivity(intent)
            }

        })
    }
}

class UserItem: Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
       return R.layout.user_row_new_message
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }

}
