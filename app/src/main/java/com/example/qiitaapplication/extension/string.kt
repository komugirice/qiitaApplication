package com.example.qiitaapplication.extension

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*




/**
 * UTC日付をUTC無し日付に変換
 * @return システム日付
 *
 */
fun String.utcDateToDate(): String =
     SimpleDateFormat("yyyy/MM/dd").format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(this)  ?: Date())

/**
 * システム日付取得
 * @return システム日付
 *
 */
fun String.getSystemDate(): String {

     val date = Date()
     val syoriYmd = SimpleDateFormat("yyyyMMdd")

     return syoriYmd.format(date)
}

/**
* システム時刻取得
* @return システム時刻
*
*/
fun String.getSystemTIME(): String {

     val date = Date()
     val syorihms = SimpleDateFormat("HHmmss")

     return syorihms.format(date)
}

/**
 * Date型をStringに変換
 * @return yyyyMMdd形式
 *
 */
fun String.formatYmd(dateObj: Date): String {
     val df = SimpleDateFormat("yyyyMMdd")
     return df.format(dateObj)
}

/**
 * 日付を加算・減算した結果をyyyyMMdd形式で取得します。
 *
 * @param dateText 日付文字列
 * @param dayCount 減算・加算日数
 * @return yyyyMMdd形式
 * @throws ParseException
 */
fun String.addDate(dateText: String?, dayCount: Int): String? {

     if (dateText == null)
          return null

     val sdf = SimpleDateFormat("yyyyMMdd")
     sdf.isLenient = false

     var baseDate: Date? = null
     try {
          baseDate = sdf.parse(dateText)
     } catch (e: ParseException) {
          e.printStackTrace()
          return null
     }

     val calendar = Calendar.getInstance()
     calendar.time = baseDate
     calendar.add(Calendar.DAY_OF_MONTH, dayCount)

     val afterDate = calendar.time

     return sdf.format(afterDate)
}

/**
 * 時刻を加算・減算した結果をHHmm形式で取得します。
 * @param timeText 時刻文字列
 * @param hoursCount 減算・加算時間
 * @return HHmm形式
 * @throws ParseException
 */
fun String.addHours(timeText: String?, hoursCount: Int): String? {
     if (timeText == null) {
          return null
     }
     val sdf = SimpleDateFormat("HHmm")
     sdf.isLenient = false

     var baseTime: Date? = null
     try {
          baseTime = sdf.parse(timeText)
     } catch (e: ParseException) {
          e.printStackTrace()
          return null
     }

     val calendar = Calendar.getInstance()
     calendar.time = baseTime
     calendar.add(Calendar.HOUR, hoursCount)

     val afterTime = calendar.time

     return sdf.format(afterTime)
}


/**
 * 日付文字列が指定書式と一致しているか判定する
 *
 * @param dateStr 日付文字列
 * @param format 日付書式
 * @return 日付文字列が指定書式と一致している場合true
 */
fun String.isDateStrValid(dateStr: String, format: String): Boolean {
     val sdf = SimpleDateFormat(format)
     sdf.isLenient = false
     var parsedDate: Date? = null
     try {
          parsedDate = sdf.parse(dateStr)
     } catch (e: ParseException) {
          return false
     }

     return sdf.format(parsedDate) == dateStr
}

/**
 * 日付文字列dateStrInが"/"なしの場合、"/"付きでフォーマットし、返却する。 日付文字列dateStrInが"/"付きの場合、そのまま返却する。
 * 日付チェックはしない。
 *
 * @param dateStrIn フォーマットする前の日付文字列
 * @return フォーマットした日付文字列
 */
fun String.formatDateStrWithoutValidate(dateStrIn: String?): String? {
     var dateStrOut: String? = null
     if (dateStrIn != null && dateStrIn.length == 8) {
          dateStrOut = dateStrIn.substring(0, 4) + "/" + dateStrIn.substring(
               4,
               6
          ) + "/" + dateStrIn.substring(6, 8)
     } else {
          dateStrOut = dateStrIn
     }
     return dateStrOut
}

/**
 * 2つの日付の差分日数を算出する。FROM > TOの場合、マイナス値が返却されます。日付に誤りがある場合、0が返却されます。
 *
 * @param from(yyyyMMdd)
 * @param to(yyyyMMdd)
 * @return 差分日数
 */
//fun String.getDateSabun(from: String, to: String): Long {
//     /** Date format: yyyyMMdd */
//     val DATE_FORMAT_yyyyMMdd = "yyyyMMdd"
//
//     var sabun: Long = 0
//     if (!isDateStrValid(from, DATE_FORMAT_yyyyMMdd) || !isDateStrValid(to, DATE_FORMAT_yyyyMMdd)) {
//          return sabun
//     }
//     val dateFrom = LocalDate.parse(from, DateTimeFormatter.ofPattern(DATE_FORMAT_yyyyMMdd))
//     val dateTo = LocalDate.parse(to, DateTimeFormatter.ofPattern(DATE_FORMAT_yyyyMMdd))
//     sabun = ChronoUnit.DAYS.between(dateFrom, dateTo)
//     return sabun
//}

/**
 * YYMMDDDからYYYY/MM/DDに変換
 *
 * @param date yyyymmddd形式の日付
 * @return 変換結果
 */
fun String.dateFormatSlash(date: String): String {

     // 桁数がyyMMddではない場合、編集しない
     if (date.length != 8) {
          return date
     }
     // 年4桁
     val strYear = date.substring(0, 4)
     // 月2桁
     val strMon = date.substring(4, 6)
     // 日2桁
     val strDay = date.substring(6, 8)

     // YYYY/MM/DD
     return "$strYear/$strMon/$strDay"
}

/**
 * HHmmSSからHH:mm:ssに変換
 *
 * @param time HHmmSS形式の時分秒
 * @return 変換結果
 */
fun String.timeFormatColon(time: String): String {

     // 桁数がyyyyMMddではない場合、編集しない
     if (time.length != 6) {
          return time
     }
     // 時2桁
     val HH = time.substring(0, 2)
     // 分2桁
     val mm = time.substring(2, 4)
     // 秒2桁
     val ss = time.substring(4, 6)

     // HH:mm:ss
     return "$HH:$mm:$ss"
}

/**
 * 半角文字チェック
 *
 * @param s
 * @return true, 半角文字のみ; false, 全角文字が含まれている
 */
fun String.isHan(): Boolean {
     val chars = this.toCharArray()
     for (i in chars.indices) {
          val c = chars[i]
          return if (c <= '\u007e' || // 英数字

               c == '\u00a5' || // \記号

               c == '\u203e' || // ~記号

               c >= '\uff61' && c <= '\uff9f' // 半角カナ
          ) {
               continue
          } else {
               false
          }
     }
     return true
}

/**
 * 半角数値チェック
 *
 * @param s チェック対象文字列
 * @return true, 半角数値; false, それ以外
 */
fun String.isHanNum(): Boolean {
     return if (!this.matches("^[0-9]+$".toRegex())) {
          false
     } else true
}

/**
 * 半角英数チェック
 *
 * @param s チェック対象文字列
 * @return true, 半角英数; false, それ以外
 */
fun String.isHanStr(): Boolean {
     return if (!this.matches("^[0-9a-zA-Z]+$".toRegex())) {
          false
     } else true
}

/**
 * 半角英数（大文字のみ）チェック
 *
 * @param s チェック対象文字列
 * @return true, 半角英数; false, それ以外
 */
fun String.isHanStrBigOnly(): Boolean {
     return if (!this.matches("^[0-9A-Z]+$".toRegex())) {
          false
     } else true
}

/**
 * 半角大文字英字チェック
 *
 * @param s チェック対象文字列
 * @return true, 半角大文字英字; false, それ以外
 */
fun String.isHanBigStr(): Boolean {
     return if (!this.matches("^[A-Z]+$".toRegex())) {
          false
     } else true
}

/**
 * 全角チェック
 *
 * @param s チェック対象文字列
 * @return true, 全角文字のみ; false, 半角文字が含まれている
 */
fun String.isZenStr(): Boolean {
     return if (!this.matches("^[^ -~｡-ﾟ]+$".toRegex())) {
          false
     } else true
}

/**
 * 半角カナチェック
 *
 * @param str チェック対象文字列
 * @return true, 全角; false, 半角
 */
fun String.isHalfKatakanaOnly(): Boolean {
     val P_HALF_KATAKANA_ONLY = "^[\\uFF65-\\uFF9F]+$"
     return this.matches(P_HALF_KATAKANA_ONLY.toRegex())
}

/**
 * アスキーコードチェック（制御文字除く）
 *
 * @param str チェック対象文字列
 * @return true:アスキーコード , false:アスキーコード外
 */
fun String.isAsciiCode(): Boolean {
     val P_ASCII_ONLY = "^[\\u0020-\\u007e]+$"
     return this.matches(P_ASCII_ONLY.toRegex())
}

/**
 * 半角数値を全角数値に変換
 *
 * @return
 */
fun String.hankakuToZenkakuNumber(): String {
     if (this.isEmpty()) {
          return this
     }
     val sb = StringBuffer()
     for (i in 0 until this.length) {
          val c = this[i].toInt()
          if (0x30 <= c && c <= 0x39) {
               sb.append((c + 0xFEE0).toChar())
          } else {
               sb.append(c.toChar())
          }
     }
     return sb.toString()
}

/**
 * 全角英数字を半角英数字に変換
 *
 * @return
 */
fun String.zenkakuToHankaku(): String {
     var value = this
     val sb = StringBuilder(value)
     for (i in 0 until sb.length) {
          val c = sb[i].toInt()
          if (c >= 0xFF10 && c <= 0xFF19 || c >= 0xFF21 && c <= 0xFF3A || c >= 0xFF41 && c <= 0xFF5A) {
               sb.setCharAt(i, (c - 0xFEE0).toChar())
          }
     }
     value = sb.toString()
     return value
}