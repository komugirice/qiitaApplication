package com.komugirice.qiitaapplication.extension

import java.text.SimpleDateFormat
import java.util.*

fun Date.getDateToString(): String =
    SimpleDateFormat("yyyy/MM/dd").format(this)