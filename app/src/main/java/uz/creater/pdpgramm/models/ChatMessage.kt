package uz.creater.pdpgramm.models

class ChatMessage {

    var message: String? = null
    var date: String? = null
    var sentUserUid: String? = null

    constructor()

    constructor(message: String?, date: String?, sentUserUid: String?) {
        this.message = message
        this.date = date
        this.sentUserUid = sentUserUid
    }
}