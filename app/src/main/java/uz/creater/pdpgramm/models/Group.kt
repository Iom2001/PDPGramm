package uz.creater.pdpgramm.models

class Group {

    var name: String? = null
    var desc: String? = null
    var groupKey: String? = null

    constructor()

    constructor(name: String?, desc: String?, groupKey: String?) {
        this.name = name
        this.desc = desc
        this.groupKey = groupKey
    }
}