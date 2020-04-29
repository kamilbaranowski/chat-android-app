package pl.kamilbaranowski.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import pl.kamilbaranowski.chatapp.model.User
import kotlin.math.log

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        signUp_button_registration.setOnClickListener {
            performRegistration()

        }

        alreadyHaveAccount_textView_registration.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performRegistration() {
        val username = username_editText_registration.text.toString()
        val email = email_editText_registration.text.toString()
        val password = password_editText_registration.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter your email and password", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful){
                    return@addOnCompleteListener
                }
                val uid = it.result?.user?.uid
                Log.d("RegistrationActivity", "Completed registered: $uid")
                if (uid.isNullOrEmpty())
                    return@addOnCompleteListener
                saveUserInFirebase(uid, username, email)
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                Log.d("RegistrationActivity", "Error: ${it.message}")
            }
    }

    private fun saveUserInFirebase(uid: String, username: String, email: String) {


        val user = User(uid, username, email, "online")
        FirebaseDatabase.getInstance().getReference("users/$uid")
            .setValue(user)
            .addOnSuccessListener {
                Log.d("RegistrationActivity", "User added to database")
            }
            .addOnFailureListener {
                Log.d("RegistrationActivity", "Error while adding user to database: ${it.message}")
            }
    }
}
