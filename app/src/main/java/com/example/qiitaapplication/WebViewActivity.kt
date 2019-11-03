package com.example.qiitaapplication

import android.graphics.Color
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_web_view.*


class WebViewActivity : AppCompatActivity() {

    lateinit var mRealm: Realm
    //var isClickedButton_favorite = false
    private val URL by lazy {  intent.getStringExtra("url") }
    private val TITLE by lazy {  intent.getStringExtra("title") }
    private val QiitaResponseID by lazy {  intent.getStringExtra("id") }
    lateinit var favorite: Favorite


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
        mRealm = Realm.getDefaultInstance()
        initData()
        initLayout()
    }

    /**
     * initDataメソッド
     *
     */
    private fun initData() {
        // realmからselect
        favorite = read(QiitaResponseID) ?:Favorite()
    }

    /**
     * initLayoutメソッド
     *
     */
    private fun initLayout() {
        initFavoriteButton()
        initClick()
        initWebView()
    }

    /**
     * initFavoriteButtonメソッド
     *
     */
    private fun initFavoriteButton() {
        // Favorite判定
        if (!favorite.id.isEmpty()) {
            button_favorite.setBackgroundColor(Color.GRAY)
        }
    }

    /**
     * initClickメソッド
     *
     */
    private fun initClick() {
        button_favorite.setOnClickListener {
            // お気に入り未登録の場合
            if (favorite.id.isEmpty() || favorite.del_flg == "1") {
                //realmにCreate
                insertOrUpdate(QiitaResponseID, URL, "0")
                button_favorite.setBackgroundColor(Color.GRAY)
                favorite.id = QiitaResponseID
                favorite.url = URL
                favorite.del_flg = "0"
            } else {
                // realmにupdate
                insertOrUpdate(QiitaResponseID, URL, "1")
                favorite.del_flg = "1"
                button_favorite.setBackgroundResource(android.R.drawable.btn_default);
            }
        }
        showAllRecordButton.setOnClickListener {
            Favorite.showAll()
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

    fun insertOrUpdate(id: String, url: String, del_flg: String) {
        mRealm.executeTransaction {realm ->
            realm.insertOrUpdate(favorite.apply {
                this.id = if(this.id.isEmpty()) id else this.id
                this.url = if(this.url.isEmpty()) url else this.url
                this.del_flg = del_flg
            })
        }
    }

    fun read(id: String): Favorite? {
        return mRealm.where(Favorite::class.java).equalTo("id", id).equalTo("del_flg", "0")
            .findFirst()
    }
}

