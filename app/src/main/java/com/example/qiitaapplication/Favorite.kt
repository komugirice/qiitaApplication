package com.example.qiitaapplication

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*

open class Favorite (
    @PrimaryKey open var id : String = UUID.randomUUID().toString(),
    @Required open var url : String = "",
    open var del_flg : String = "0"
) : RealmObject() {}