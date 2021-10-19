package com.example.choice

class Message {
    var message: String? = ""
    var senderId: String? = ""

    constructor(){}

    constructor(message: String?, senderId: String?){
        this.message = message
        this.senderId = senderId
    }
}