package com.waly.chat.models

class Collaborator {

    var id: String = ""
    var name: String = ""
    var email: String = ""

    constructor(id: String, name: String, email: String) {
        this.id = id
        this.name = name
        this.email = email
    }
    constructor()
}