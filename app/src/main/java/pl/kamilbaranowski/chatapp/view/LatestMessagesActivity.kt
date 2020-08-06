package pl.kamilbaranowski.chatapp.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.auth0.android.jwt.JWT
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*
import pl.kamilbaranowski.chatapp.view.NewMessageActivity.Companion.USER_KEY
import pl.kamilbaranowski.chatapp.R
import pl.kamilbaranowski.chatapp.model.Message
import pl.kamilbaranowski.chatapp.model.User
import pl.kamilbaranowski.chatapp.model.row.LatestMessageRow
//import pl.kamilbaranowski.chatapp.model.row.LatestMessageRow.Companion.user
import pl.kamilbaranowski.chatapp.viewmodel.LatestMessagesActivityViewModel
import pl.kamilbaranowski.chatapp.viewmodel.LoginActivityViewModel
import java.lang.Exception
import kotlin.collections.HashMap

class LatestMessagesActivity : AppCompatActivity() {

    private lateinit var viewModel: LatestMessagesActivityViewModel
    private val adapter = GroupAdapter<GroupieViewHolder>()
    private val latestMessagesMap = HashMap<String, Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        viewModel = ViewModelProvider(this).get(LatestMessagesActivityViewModel::class.java)
        recyclerView_latest_messages.adapter = adapter
        recyclerView_latest_messages.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        adapter.setOnItemClickListener { item , view ->
            val latestMessageRow = item as LatestMessageRow
            val intent = Intent(this, ChatLogActivity::class.java)
            intent.putExtra(USER_KEY, latestMessageRow.getUser()
            )
            startActivity(intent)
        }

        Log.d("LatestMessagess token: ", "token")
        if(!viewModel.verifyIfUserIsLoggedIn()) {
            Toast.makeText(this, "Your session expired.\nPlease sign in.", Toast.LENGTH_SHORT).show()
            viewModel.clearUserAuthData()
            val intent = Intent(this, RegistrationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        viewModel.listenForLatestMessages()
        //listenForLatestMessages()
        viewModel.latestMessages.observe(this, Observer {
            adapter.clear()
            it.values.forEach {
                adapter.add(LatestMessageRow(it, this@LatestMessagesActivity))
            }
        })
        //viewModel.getUsername(chatPartnerUid/*, viewHolder, activity*/)
        //viewModel.latestMessagesViewHolder.observe(this, Observer {
         //   Log.d("LatestMessages dfgdfg", it.username)
        //})

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.menu_new_message -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out -> {
                viewModel.clearUserAuthData()
                val intent = Intent(this, RegistrationActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }




}
