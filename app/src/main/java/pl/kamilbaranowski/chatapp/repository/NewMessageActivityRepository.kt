package pl.kamilbaranowski.chatapp.repository

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import org.json.JSONObject
import pl.kamilbaranowski.chatapp.model.ConnectionInfo
import pl.kamilbaranowski.chatapp.model.User
import pl.kamilbaranowski.chatapp.model.response.SingleUserResponse
import pl.kamilbaranowski.chatapp.network.ServerAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NewMessageActivityRepository(val application: Application) {

    val listOfUsers = MutableLiveData<List<User>>()

    fun fetchUsers() {
        val connectionInfo = ConnectionInfo()

        val idToken = application.getSharedPreferences("auth", Context.MODE_PRIVATE).getString("idToken", null)

        val retrofit = Retrofit.Builder().baseUrl(connectionInfo.getHost())
            .addConverterFactory(GsonConverterFactory.create())
            .client(connectionInfo.getUnsafeOkHttpClient()!!)
            .build()

        val service = retrofit.create(ServerAPI::class.java)

        service.getUsers("Bearer ${idToken!!}").enqueue(object : Callback<List<User>> {

            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
               listOfUsers.value = response.body()
               //Log.d("NewMessageActivity", response.body().toString())
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.d("NewMessageActivity", "Error while fetching users: ${t.message.toString()}")
            }
        })
    }
}