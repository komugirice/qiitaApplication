package com.example.qiitaapplication.extension

import java.text.SimpleDateFormat
import java.util.*

fun String.utcDate2Date(): String =
     SimpleDateFormat("yyyy/MM/dd").format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(this)  ?: Date())
