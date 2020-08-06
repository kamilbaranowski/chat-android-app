package pl.kamilbaranowski.chatapp.network

import com.google.gson.Gson
import org.json.JSONObject
import pl.kamilbaranowski.chatapp.model.User
import pl.kamilbaranowski.chatapp.model.response.LoginResponse
import pl.kamilbaranowski.chatapp.model.response.SingleUserResponse
import retrofit2.Call
import retrofit2.http.*

interface ServerAPI {

    @POST("/")
    @FormUrlEncoded
    fun loginPost(@Field("email") email: String, @Field("password") password: String) : Call<LoginResponse>

    @GET("/users")
    fun getUsers(@Header("Authorization") idToken: String) : Call<List<User>>

    @GET("/users?")
    fun getUserByUid(@Query(value = "uid") uid: String, @Header("Authorization") idToken: String) : Call<User>

    @POST("/register")
    //@Headers("Content-Type: application/json")
    @FormUrlEncoded
    fun registerUser(@Field("username") username: String, @Field("email") email: String, @Field("password") password: String, @Field("status") status: String) : Call<Void>
}