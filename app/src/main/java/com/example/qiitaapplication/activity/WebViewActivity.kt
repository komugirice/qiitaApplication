package com.example.qiitaapplication.activity

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.qiitaapplication.R
import com.example.qiitaapplication.dataclass.Favorite
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_web_view.*


class WebViewActivity : AppCompatActivity() {

    /** Realmインスタンス */
    lateinit var mRealm: Realm
    /** 記事URL */
    private val URL by lazy {  intent.getStringExtra("url") }
    /** 記事タイトル */
    private val TITLE by lazy {  intent.getStringExtra("title") }
    /** 記事ID */
    private val QiitaResponseID by lazy {  intent.getStringExtra("id") }
    /** お気に入りクラス */
    lateinit var favorite: Favorite


    /**
     * onCreateメソッド
     *
     * @param savedInstanceState
     */
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
        favorite = read(QiitaResponseID) ?: Favorite()
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
        // favorite存在判定
        if (!favorite.id.isEmpty()) {
            button_favorite.setBackgroundColor(Color.GRAY)
        }
        // log_favorite()
    }

    fun log_favorite() {
            Log.d("Favorite", "{$favorite}")
    }

    /**
     * initClickメソッド
     *
     */
    private fun initClick() {
        // お気に入りボタン
        button_favorite.setOnClickListener {
            // お気に入り未登録の場合
            if (favorite.id.isEmpty() || favorite.del_flg == "1") {
                //realmにinsertOrUpdate
                insertOrUpdate(QiitaResponseID, URL, TITLE, "0")
                button_favorite.setBackgroundColor(Color.GRAY)
            } else {
                // realmにUpdate
                insertOrUpdate(QiitaResponseID, URL, TITLE, "1")
                button_favorite.setBackgroundResource(android.R.drawable.btn_default)
            }
        }
        // ログ出力ボタン
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
        myWebView.loadUrl(URL)
    }

    /**
     * onDestroyメソッド
     *
     */
    override fun onDestroy() {
        super.onDestroy()
        mRealm.close()
    }

    /**
     * insertOrUpdateメソッド
     *
     * @param id: String
     * @param url: String
     * @param title: String
     * @param del_flg: String
     *
     */
    fun insertOrUpdate(id: String, url: String, title: String, del_flg: String) {
        mRealm.executeTransaction {realm ->
            realm.insertOrUpdate(favorite.apply {
                if(this.id.isEmpty()) this.id = id
                this.url = if(this.url.isEmpty()) url else this.url
                this.title = if(this.title.isEmpty()) title else this.title
                this.del_flg = del_flg
            })
        }
    }

    /**
     * readメソッド
     *
     * @param id: String
     */
    fun read(id: String): Favorite? {
        return mRealm.where(Favorite::class.java).equalTo("id", id).equalTo("del_flg", "0")
            .findFirst()
    }
}

