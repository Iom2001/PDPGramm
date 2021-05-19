package uz.creater.pdpgramm.models

import java.io.Serializable

class User : Serializable {

    var name: String? = null
    var photoUrl: String? = null
    var gmail: String? = null
    var uid: String? = null


    constructor(name: String?, photoUrl: String?, gmail: String?, uid: String?) {
        this.name = name
        this.photoUrl = photoUrl
        this.gmail = gmail
        this.uid = uid
    }

    constructor()

}