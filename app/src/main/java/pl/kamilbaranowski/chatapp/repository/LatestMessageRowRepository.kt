package pl.kamilbaranowski.chatapp.repository

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import pl.kamilbaranowski.chatapp.model.ConnectionInfo
import pl.kamilbaranowski.chatapp.model.User
import pl.kamilbaranowski.chatapp.network.ServerAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LatestMessageRowRepository(val application: Application) {

    val user = MutableLiveData<User>()


    fun getUsername(uid: String) {
        //....
        val connectionInfo = ConnectionInfo();
        val idToken = application.getSharedPreferences("auth", Context.MODE_PRIVATE).getString("idToken", null)

        val retrofit = Retrofit.Builder().baseUrl(connectionInfo.getHost())
            .addConverterFactory(GsonConverterFactory.create())
            .client(connectionInfo.getUnsafeOkHttpClient()!!)
            .build()

        val service = retrofit.create(ServerAPI::class.java)
        service.getUserByUid(uid, "Bearer ${idToken}").enqueue(object : Callback<User> {

            override fun onResponse(call: Call<User>, response: Response<User>) {
                Log.d("LatestMessages", "${response.body().toString()}")
                user.value = response.body()
            }


            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d("LatestMessages", "${t.message}")
            }
        })
    }
}