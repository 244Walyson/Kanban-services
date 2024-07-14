package com.waly.chat.models

class Board {

    var id: String = ""
    var title: String = ""
    var totalCards: Int = 0

    constructor(id: String, title: String, totalCards: Int) {
        this.id = id
        this.title = title
        this.totalCards = totalCards
    }

    constructor()
}