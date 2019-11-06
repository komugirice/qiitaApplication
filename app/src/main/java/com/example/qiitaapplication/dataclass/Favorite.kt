package com.example.qiitaapplication.dataclass

import android.util.Log
import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class Favorite (
    @PrimaryKey open var id : String = "",
    @Required open var url : String = "",
    @Required open var title : String = "",
    open var delFlg : String = "0"
) : RealmObject() {

    override fun toString(): String =
        StringBuilder()
            .append("id:$id")
            .append(", url:$url")
            .append(", title:$title")
            .append(", delFlg:$delFlg")
            .toString()

    companion object {
        fun findAll(): List<Favorite> =
            Realm.getDefaultInstance().use { realm ->
                realm.where(Favorite::class.java)
                    .findAll()
                    .let { realm.copyFromRealm(it) }
            }

        fun showAll() {
            findAll().forEach {
                Log.d("Favorite", "$it")
            }
        }
    }
}