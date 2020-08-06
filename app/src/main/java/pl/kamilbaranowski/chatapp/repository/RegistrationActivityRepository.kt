package pl.kamilbaranowski.chatapp.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import pl.kamilbaranowski.chatapp.model.ConnectionInfo
import pl.kamilbaranowski.chatapp.network.ServerAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegistrationActivityRepository(val application: Application) {

    val registrationStatus = MutableLiveData<Boolean>()

    fun performRegistration(username: String, email: String, password: String) {

        val connectionInfo = ConnectionInfo()

        val retrofit = Retrofit.Builder().baseUrl(connectionInfo.getHost())
            .addConverterFactory(GsonConverterFactory.create())
            .client(connectionInfo.getUnsafeOkHttpClient()!!)
            .build()

        val service = retrofit.create(ServerAPI::class.java)

        service.registerUser(username, email, password, "online").enqueue(object : Callback<Void> {

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                registrationStatus.value = true
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                registrationStatus.value = false
            }

        })

    }
}