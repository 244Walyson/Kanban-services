package com.waly.chat.models

import com.google.gson.annotations.SerializedName

class FullTeam {

    @SerializedName("id")
    var id: String? = null
    @SerializedName("name")
    var name: String? = null
    @SerializedName("occupationArea")
    var occupationArea: String? = null
    @SerializedName("description")
    var description: String? = null
    @SerializedName("totalCollaborators")
    var totalCollaborators: Int? = null
    @SerializedName("imgUrl")
    var imgUrl: String? = null
    @SerializedName("collaborators")
    var members: List<User>? = null
    @SerializedName("totalBoards")
    var totalBoards: Int? = null
    @SerializedName("boards")
    var boards: List<Board>? = null
    @SerializedName("githubLink")
    var githubLink: String? = null

    constructor(
        id: String?,
        name: String?,
        occupationArea: String?,
        description: String?,
        totalCollaborators: Int?,
        imgUrl: String?,
        members: List<User>?,
        totalBoards: Int?,
        boards: List<Board>?,
        githubLink: String?
    ) {
        this.id = id
        this.name = name
        this.occupationArea = occupationArea
        this.description = description
        this.totalCollaborators = totalCollaborators
        this.imgUrl = imgUrl
        this.members = members
        this.totalBoards = totalBoards
        this.boards = boards
        this.githubLink = githubLink
    }

    constructor()
}
