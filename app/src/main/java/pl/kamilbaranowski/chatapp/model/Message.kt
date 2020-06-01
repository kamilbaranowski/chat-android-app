package pl.kamilbaranowski.chatapp.model


class Message(val sender: String, val receiver: String, val messageContent: String, val timestamp: Long) {
    constructor(): this("", "", "", 0L)
}