package pl.kamilbaranowski.chatapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
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
import pl.kamilbaranowski.chatapp.NewMessageActivity.Companion.USER_KEY
import pl.kamilbaranowski.chatapp.model.Message
import pl.kamilbaranowski.chatapp.model.User
import pl.kamilbaranowski.chatapp.model.row.LatestMessageRow
import java.lang.Exception
import kotlin.collections.HashMap

class LatestMessagesActivity : AppCompatActivity() {

    companion object {
        var user: User? = null
    }

    private val adapter = GroupAdapter<GroupieViewHolder>()
    private val latestMessagesMap = HashMap<String, Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        recyclerView_latest_messages.adapter = adapter
        recyclerView_latest_messages.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(this, ChatLogActivity::class.java)
            intent.putExtra(USER_KEY, user)
            startActivity(intent)
        }

        Log.d("LatestMessagess token: ", "token")
        verifyIfUserIsLoggedIn()

        listenForLatestMessages()

    }

    private fun listenForLatestMessages() {
        val receiver = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/${receiver}")

        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val message = p0.getValue(Message::class.java) ?: return
                    latestMessagesMap[p0.key!!] = message
                    refreshLatestMessages()
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val message = p0.getValue(Message::class.java) ?: return
                    latestMessagesMap[p0.key!!] = message
                    refreshLatestMessages()
            }
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }
            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })
    }

    private fun refreshLatestMessages(){
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(LatestMessageRow(it, this@LatestMessagesActivity))
        }
    }

    private fun verifyIfUserIsLoggedIn() {
        val sharedPref = this.getSharedPreferences("token", Context.MODE_PRIVATE)
        val token = sharedPref.getString("token", null)

        if (token != null) {
            Log.d("LatestMessagess token: ", token)

            try {
                var expirationTime = JWT(token).getClaim("exp").asLong()
                val uid = FirebaseAuth.getInstance().uid

                if (expirationTime!! <= (System.currentTimeMillis() / 1000) || uid == null) {
                    Log.d("LatestMessages: ", "Token expired! + UID: ${uid}")
                    Toast.makeText(this, "Your session expired.\nPlease sign in.", Toast.LENGTH_SHORT).show()
                    clearUserAuthData()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            } catch (e: Exception) {
                clearUserAuthData()
                e.printStackTrace()

                return
            }
        } else {
            Log.d("LatestMessages: ", "Token is null")
            val intent = Intent(this, RegistrationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
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
                clearUserAuthData()
                val intent = Intent(this, RegistrationActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }


        return super.onOptionsItemSelected(item)
    }

    private fun clearUserAuthData(){
        FirebaseAuth.getInstance().signOut()
        this.getSharedPreferences("token", Context.MODE_PRIVATE).edit().clear().apply()
    }


}
