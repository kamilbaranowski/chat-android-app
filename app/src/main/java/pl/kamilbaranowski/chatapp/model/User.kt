package pl.kamilbaranowski.chatapp.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User( val uid: String, val username: String, val password: String, val email: String, val status: String) : Parcelable {
    constructor(): this("", "", "", "", "")
}