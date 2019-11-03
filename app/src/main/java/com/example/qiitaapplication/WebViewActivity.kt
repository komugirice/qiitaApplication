package com.example.qiitaapplication

import android.graphics.Color
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_web_view.*
import java.util.*


class WebViewActivity : AppCompatActivity() {

    val mRealm = Realm.getDefaultInstance()
    //var isClickedButton_favorite = false
    //var favorite : Favorite? = null
    val favorite: Favorite? by lazy { read(URL) }
    val URL = intent.getStringExtra("url")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        initialize()

    }

    /**
     * initializeメソッド
     *
     */
    private fun initialize() {
        Realm.init(this)
        initData()
        initLayout()
    }

    /**
     * initDataメソッド
     *
     */
    private fun initData() {
        // realmからセレクト
        //favorite = read(URL)
    }

    /**
     * initLayoutメソッド
     *
     */
    private fun initLayout() {
        initClick()
        initWebView()
    }

    /**
     * initClickメソッド
     *
     */
    private fun initClick() {
        button_favorite.setOnClickListener {
            // Favorite判定
            if (favorite == null) {
                //realmにCreate
                create(URL)
                button_favorite.setBackgroundColor(Color.GRAY)
            } else {
                // realmにupdate
                val del_flg = if (favorite!!.del_flg == "0") "1" else "0"
                update(URL, del_flg)
                button_favorite.setBackgroundResource(android.R.drawable.btn_default);
            }
        }
    }

    /**
     * initWebViewメソッド
     *
     */
    private fun initWebView() {
        val myWebView = findViewById<WebView>(R.id.webView)
        myWebView.setWebViewClient(WebViewClient())
        //val url = intent.getStringExtra("url")
        myWebView.loadUrl(URL)
    }

    override fun onDestroy() {
        super.onDestroy()
        mRealm.close()
    }

    fun create(url: String) {
        mRealm.executeTransaction {
            var favorite = mRealm.createObject(Favorite::class.java, UUID.randomUUID().toString())
            favorite.url = url
            favorite.del_flg = "0"

            mRealm.copyToRealm(favorite)
        }
    }

    fun update(url: String, del_flg: String) {
        mRealm.executeTransaction {
            var favorite = mRealm.where(Favorite::class.java).equalTo("url", url).findFirst()
            favorite!!.del_flg = del_flg
        }
    }

    fun read(url: String): Favorite? {
        return mRealm.where(Favorite::class.java).equalTo("url", url).equalTo("del_flg", "0")
            .findFirst()
    }
}

