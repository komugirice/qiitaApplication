package com.example.qiitaapplication.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.qiitaapplication.R
import com.example.qiitaapplication.dataclass.ArticleRow
import com.example.qiitaapplication.extension.getDateToString
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_web_view.*
import java.util.*


class WebViewActivity : AppCompatActivity() {

    /** Realmインスタンス */
    lateinit var mRealm: Realm
    /** 記事URL */
    private val mUrl by lazy { intent.getStringExtra(KEY_URL) }
    /** 記事タイトル */
    private val mTitle by lazy { intent.getStringExtra(KEY_TITLE) }
    /** 記事ID */
    private val mQiitaResponseID by lazy { intent.getStringExtra(KEY_ID) }
    /** お気に入りクラス */
    lateinit var favorite: ArticleRow


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
        favorite = read(mQiitaResponseID) ?: ArticleRow()
    }

    /**
     * initLayoutメソッド
     *
     */
    private fun initLayout() {
        // タイトル
        val title = this.findViewById(R.id.title) as TextView
        title.text = mTitle

        initToolbar()
        initFavoriteIcon()
        initClick()
        initWebView()
    }

    /**
     * initToolbarメソッド
     *
     */
    private fun initToolbar() {
        // アクションバーにツールバーを設定
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * onOptionsItemSelectedメソッド
     *
     * @param item
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // 戻るボタン押下時
        if (item.itemId == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)

    }

    /**
     * initFavoriteIconメソッド
     *
     */
    private fun initFavoriteIcon() {
        // favorite存在判定
        if (!favorite.id.isEmpty()) {
            val image = findViewById(R.id.icFavorite) as ImageView
            image.setImageResource(R.drawable.ic_favorite_red_24dp)
        }
    }


    /**
     * initClickメソッド
     *
     */
    private fun initClick() {
        // お気に入りアイコン
        icFavorite.setOnClickListener {
            // お気に入り未登録or1度登録したけど削除の場合
            if (favorite.id.isEmpty() || favorite.delFlg == "1") {
                //realmにinsertOrUpdate
                insertOrUpdate("0")
                // 画像の変更
                icFavorite.setImageResource(R.drawable.ic_favorite_red_24dp)
            } else {
                // 削除
                // realmにUpdate
                insertOrUpdate("1")
                // 画像の変更
                icFavorite.setImageResource(R.drawable.ic_favorite_border_red_24dp)
            }
        }

        // ログ出力ボタン
        showAllRecordButton.setOnClickListener {
            ArticleRow.showAll()
        }
    }

    /**
     * initWebViewメソッド
     *
     */
    private fun initWebView() {
        val myWebView = findViewById<WebView>(R.id.webView)
        myWebView.webViewClient = WebViewClient()
        myWebView.loadUrl(mUrl)
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
     * @param delFlg: String
     *
     */
    fun insertOrUpdate(delFlg: String) {
        mRealm.executeTransaction { realm ->
            realm.insertOrUpdate(
                favorite.apply {
                    if (id.isEmpty())
                        id = mQiitaResponseID
                    url = if (url.isEmpty()) mUrl else url
                    title = if (title.isEmpty()) mTitle else title
                    profileImageUrl = intent.getStringExtra(KEY_PROFILE_IMAGE_URL)
                    userName = intent.getStringExtra(KEY_USER_NAME)
                    createdAt = intent.getStringExtra(KEY_CREATED_AT)
                    likesCount = intent.getStringExtra(KEY_LIKES_COUNT)
                    commentCount = intent.getStringExtra(KEY_COMMENT_COUNT)
                    tags = intent.getStringExtra(KEY_TAGS)
                    updDate = Date().getDateToString()
                    this.delFlg = delFlg

                }
            )
        }
    }

    /**
     * readメソッド
     *
     * @param id: String
     */
    fun read(id: String): ArticleRow? {
        return mRealm.where(ArticleRow::class.java).equalTo("id", id).equalTo("delFlg", "0")
            .findFirst()
    }

    /**
     * ログ出力メソッド
     *
     */
    fun log_favorite() {
        Log.d("Favorite", "{$favorite}")
    }

    companion object {

        public const val KEY_ID = "key_id"
        public const val KEY_URL = "key_url"
        public const val KEY_TITLE = "key_title"
        public const val KEY_PROFILE_IMAGE_URL = "key_profileImageUrl"
        public const val KEY_USER_NAME = "key_userName"
        public const val KEY_CREATED_AT = "key_createdAt"
        public const val KEY_LIKES_COUNT = "key_likesCount"
        public const val KEY_COMMENT_COUNT = "key_commentCount"
        public const val KEY_TAGS = "key_tags"

        fun start(context: Context?, bundle: Bundle) = context?.startActivity(
                Intent(context, WebViewActivity::class.java)
                    .putExtras(bundle)

            )
    }
}

