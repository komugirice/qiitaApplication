package com.example.qiitaapplication

import android.util.Log
import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*

open class Favorite (
    @PrimaryKey open var id : String = UUID.randomUUID().toString(),
    @Required open var url : String = "",
    open var del_flg : String = "0"
) : RealmObject() {

    override fun toString(): String =
        StringBuilder()
            .append("id:$id")
            .append(", url:$url")
            .append(", del_flg:$del_flg")
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