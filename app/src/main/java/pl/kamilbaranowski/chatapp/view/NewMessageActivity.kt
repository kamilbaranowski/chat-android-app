package pl.kamilbaranowski.chatapp.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*
import okhttp3.*
import org.json.JSONObject
import pl.kamilbaranowski.chatapp.R
import pl.kamilbaranowski.chatapp.model.ConnectionInfo
import pl.kamilbaranowski.chatapp.model.User
import pl.kamilbaranowski.chatapp.model.row.UserItem
import pl.kamilbaranowski.chatapp.viewmodel.LoginActivityViewModel
import pl.kamilbaranowski.chatapp.viewmodel.NewMessageActivityViewModel
import java.io.IOException

class NewMessageActivity : AppCompatActivity() {

    companion object {
        val USER_KEY = "USER"
    }

    private lateinit var viewModel: NewMessageActivityViewModel
    private val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title = "Select User"
        viewModel = ViewModelProvider(this).get(NewMessageActivityViewModel::class.java)
        recyclerView_newMessage.adapter = adapter

        viewModel.fetchUsers()
        //fetchUsers()
        viewModel.listOfUsers.observe(this, Observer { it ->
            it.forEach {
                this.adapter.add(UserItem(it))
            }
        })

        adapter.setOnItemClickListener { item, view ->
            val userItem = item as UserItem
            val intent = Intent(view.context, ChatLogActivity::class.java)
            intent.putExtra(USER_KEY, userItem.user)
            startActivity(intent)
            finish()
        }

    }
}
