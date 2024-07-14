package com.waly.chat.models


class Card {

     var id: Long? = null
     var title: String? = null
     var description: String? = null
     var position: Int? = null
     var done = false
     var priority: Int? = null
     var collaborators: List<User> = ArrayList<User>()

    constructor(id: Long?, title: String?, description: String?, position: Int?, done: Boolean, priority: Int?, collaborators: List<User>) {
        this.id = id
        this.title = title
        this.description = description
        this.position = position
        this.done = done
        this.priority = priority
        this.collaborators = collaborators
    }

    constructor()
}