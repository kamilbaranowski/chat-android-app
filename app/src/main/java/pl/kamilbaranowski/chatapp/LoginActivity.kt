package pl.kamilbaranowski.chatapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.*
import pl.kamilbaranowski.chatapp.model.ConnectionInfo
import java.io.IOException



class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        signIn_button_login.setOnClickListener {
            performLogin(this)
        }
    }

    private fun performLogin(context: Context) {
        val email = email_editText_login.text.toString()
        val password = password_editText_login.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter your email and password", Toast.LENGTH_SHORT).show()
            return
        }
        val connectionInfo = ConnectionInfo();

        val url = connectionInfo.getHost()

        val requestBody = FormBody.Builder()
            .add("email", email)
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .method("POST", requestBody)
            .build()
        Log.d("LoginActivity", request.headers.toString())
        val client = connectionInfo.getUnsafeOkHttpClient()
        client?.newCall(request)?.enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("LoginActivity", "Failed to execute request: " + e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val token = response.body?.string()
                val sharedPref = context.getSharedPreferences("token", Context.MODE_PRIVATE)
                sharedPref.edit().putString("token", token).apply()
                Log.d("LoginActivity", "Success: " + token)
                FirebaseAuth.getInstance().signInWithCustomToken(token!!)
                    .addOnSuccessListener {
                        Log.d("Login UID: ", it.user?.uid)
                        val intent = Intent(context, LatestMessagesActivity::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener {
                        Log.d("LoginActivity", "Failed: " + token + "\nMessage: " + it.message)
                    }

            }

        })


        /*
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

         */
    }
}
