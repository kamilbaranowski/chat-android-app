package pl.kamilbaranowski.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        signIn_button_login.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin() {
        val email = email_editText_login.text.toString()
        val password = password_editText_login.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter your email and password", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful)
                    return@addOnCompleteListener
                Log.d("LoginActivity", "Successfully logged in: ${it.result?.user?.uid}")
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                Log.d("LoginActivity", "Failed to login: ${it.message}")
            }
    }
}
