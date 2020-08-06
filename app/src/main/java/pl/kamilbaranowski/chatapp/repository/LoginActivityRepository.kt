package pl.kamilbaranowski.chatapp.repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import pl.kamilbaranowski.chatapp.model.ConnectionInfo
import pl.kamilbaranowski.chatapp.model.response.LoginResponse
import pl.kamilbaranowski.chatapp.network.ServerAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivityRepository(val application: Application) {

    val token = MutableLiveData<LoginResponse>()
    val firebaseToken =  MutableLiveData<String>()

    fun login(email: String, password: String) {
        val connectionInfo = ConnectionInfo()

        val retrofit = Retrofit.Builder().baseUrl(connectionInfo.getHost())
            .addConverterFactory(GsonConverterFactory.create())
            .client(connectionInfo.getUnsafeOkHttpClient()!!)
            .build()

        val service = retrofit.create(ServerAPI::class.java)

        service.loginPost(email, password).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                Log.d("LoginActivity", "Token: ${response.body()?.token}")
                token.value = response.body()
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.d("LoginActivity", "${t.message.toString()}")
            }
        })
    }

    fun loginFirebase(customToken: String) {
        FirebaseAuth.getInstance().signInWithCustomToken(customToken)
            .addOnSuccessListener {
                getFirebaseIdToken(it.user!!)
            }
            .addOnFailureListener {
                Toast.makeText(application, "Login failed! ${it.message.toString()}", Toast.LENGTH_SHORT).show()
            }
    }

    fun getFirebaseIdToken(user: FirebaseUser) {
        user.getIdToken(false)
            .addOnSuccessListener {
                firebaseToken.value = it.token
            }
            .addOnFailureListener {
                Log.d("LoginActivity", "Failed getting firebase id token:\n${it.message.toString()}")
            }
    }
}