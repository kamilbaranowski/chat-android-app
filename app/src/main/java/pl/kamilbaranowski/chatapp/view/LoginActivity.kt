package pl.kamilbaranowski.chatapp.view

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.*
import org.json.JSONObject
import pl.kamilbaranowski.chatapp.R
import pl.kamilbaranowski.chatapp.model.ConnectionInfo
import pl.kamilbaranowski.chatapp.viewmodel.LoginActivityViewModel
import java.io.IOException



class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewModel = ViewModelProvider(this).get(LoginActivityViewModel::class.java)

        signIn_button_login.setOnClickListener {
            val email = email_editText_login.text.toString()
            val password = password_editText_login.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter your email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.login(email, password)

        }
        viewModel.loginResponse.observe(this, Observer {
            if (it != null) {
                viewModel.loginFirebase(it.token)

            }
        })
        viewModel.firebaseToken.observe(this, Observer {
            viewModel.saveTokens()
            val intent = Intent(this.applicationContext, LatestMessagesActivity::class.java)
            startActivity(intent)
        })
    }

}
