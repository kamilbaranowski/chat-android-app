package pl.kamilbaranowski.chatapp.model.response

data class SingleUserResponse(
    val email: String,
    val password: String,
    val registrationDate: RegistrationDate,
    val status: String,
    val uid: String,
    val username: String
)