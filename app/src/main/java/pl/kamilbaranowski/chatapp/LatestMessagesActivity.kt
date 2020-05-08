package pl.kamilbaranowski.chatapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

class LatestMessagesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

       // verifyIfAuthenticatedUser()
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
                val sharedPref = this.getSharedPreferences("token", Context.MODE_PRIVATE)
                sharedPref.edit().remove("token").apply()
                val intent = Intent(this, RegistrationActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }


        return super.onOptionsItemSelected(item)
    }

    private fun verifyIfAuthenticatedUser() {
        val sharedPref = this.getSharedPreferences("token", Context.MODE_PRIVATE)
        val token = sharedPref.getString("token", null)
        if (token.isNullOrEmpty()){
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }
}
