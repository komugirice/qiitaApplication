package com.example.qiitaapplication.dataclass

import android.util.Log
import com.example.qiitaapplication.extension.getDateToString
import com.example.qiitaapplication.extension.utcDate2Date
import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*

open class ArticleRow (
    @PrimaryKey open var id : String = "",
    @Required open var url : String = "",
    @Required open var title : String = "",
    var profileImageUrl : String = "",
    var userName : String = "",
    var createdAt : String = "",
    var likesCount : String = "",
    var commentCount : String = "",
    var tags : String = "",
    var updDate : String = "",
    var delFlg : String = "0"

) : RealmObject() {

    override fun toString(): String =
        StringBuilder()
            .append("id:$id")
            .append(", url:$url")
            .append(", title:$title")
            .append(", profileImageUrl:$profileImageUrl")
            .append(", userName:$userName")
            .append(", createdAt:$createdAt")
            .append(", likesCount:$likesCount")
            .append(", commentCount:$commentCount")
            .append(", tags:$tags")
            .append(", updateDate:$updDate")
            .append(", delFlg:$delFlg")
            .toString()

    companion object {
        fun findAll(): List<ArticleRow> =
            Realm.getDefaultInstance().use { realm ->
                realm.where(ArticleRow::class.java)
                    .findAll()
                    .let { realm.copyFromRealm(it) }
            }

        fun showAll() {
            findAll().forEach {
                Log.d("Favorite", "$it")
            }
        }
    }
    fun convertFromQiitaResponse(resp: QiitaResponse) {
        id = resp.id
        url = resp.url
        title = resp.title
        profileImageUrl = resp.user.profile_image_url
        userName = resp.user.name
        createdAt = resp.created_at.utcDate2Date()
        likesCount = resp.likes_count.toString()
        commentCount = resp.comments_count.toString()
        tags = resp.tags.map { it.name }.joinToString (separator = ",")
        updDate = Date().getDateToString()
    }
}