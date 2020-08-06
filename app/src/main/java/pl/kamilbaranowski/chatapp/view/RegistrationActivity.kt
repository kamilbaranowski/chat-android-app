package pl.kamilbaranowski.chatapp.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import pl.kamilbaranowski.chatapp.R
import pl.kamilbaranowski.chatapp.model.ConnectionInfo
import pl.kamilbaranowski.chatapp.viewmodel.NewMessageActivityViewModel
import pl.kamilbaranowski.chatapp.viewmodel.RegistrationActivityViewModel
import java.io.IOException


class RegistrationActivity : AppCompatActivity() {

    private lateinit var viewModel: RegistrationActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(RegistrationActivityViewModel::class.java)

        signUp_button_registration.setOnClickListener {
            val username = username_editText_registration.text.toString()
            val email = email_editText_registration.text.toString()
            val password = password_editText_registration.text.toString()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter username, email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.performRegistration(username, email, password)
        }

        viewModel.registrationStatus.observe(this, Observer {
            if (it) {
                Log.d("RegistrationActivity", "Success")
                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Something went wrong...", Toast.LENGTH_SHORT).show()
            }
        })
        alreadyHaveAccount_textView_registration.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }


}
