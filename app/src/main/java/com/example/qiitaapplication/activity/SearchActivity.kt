package com.example.qiitaapplication.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.example.qiitaapplication.ArticleAdapter
import com.example.qiitaapplication.EndlessScrollListener
import com.example.qiitaapplication.R
import com.example.qiitaapplication.dataclass.ArticleRow
import com.example.qiitaapplication.dataclass.QiitaResponse
import com.example.qiitaapplication.extension.toggle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_web_view.toolbar
import kotlinx.android.synthetic.main.fragment_article.articleListView
import kotlinx.android.synthetic.main.fragment_article.swipeRefreshLayout
import okhttp3.*
import java.io.IOException
import java.net.URLEncoder

class SearchActivity : AppCompatActivity() {

    /** Realmインスタンス */
    lateinit var mRealm: Realm
    /** Handlerインスタンス */
    private val handler = Handler()

    /** RecyclerListAdapter */
    private val customAdapter by lazy { ArticleAdapter(this) }
    /** Qiita記事リスト */
    private val items = mutableListOf<QiitaResponse>()

    /** 検索タイプ */
    // TODO _で警告が発生する
    private val SEARCH_TYPE by lazy { if (intent.getBooleanExtra(KEY_IS_SEARCH_BY_TAG, false)) SEARCH_TAG else SEARCH_BODY }
    /** 検索クエリ */
    private val QUERY by lazy { intent.getStringExtra(KEY_SEARCH_WORD) }

    /**
     * onCreateメソッド
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initialize()
    }

    /**
     * initializeメソッド
     *
     */
    private fun initialize() {
        Realm.init(this)
        mRealm = Realm.getDefaultInstance()
        initLayout()
        initData()
    }

    /**
     * initLayoutメソッド
     *
     */
    private fun initLayout() {
        initToolbar()
        initRecyclerView()
        initSwipeRefreshLayout()
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

        // ツールバータイトル設定
        toolbarTitle.text = QUERY
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
     * initRecyclerViewメソッド
     *
     */
    private fun initRecyclerView() {
        // RecyclerViewを取得。
        articleListView.apply {
            // LinearLayoutManagerオブジェクトを生成。
            val layout = LinearLayoutManager(context)
            // RecyclerViewにレイアウトマネージャとしてLinearLayoutManagerを設定。
            layoutManager = layout
            // RecyclerViewにアダプタオブジェクトを設定。
            adapter = customAdapter
            // コンテンツの大きさが変わらないとき、trueを設定するとパフォーマンスが向上する
            setHasFixedSize(true)

            // スクロール対応
            articleListView.addOnScrollListener(object :
                EndlessScrollListener(articleListView.layoutManager as LinearLayoutManager) {
                override fun onLoadMore(current_page: Int) {
                    swipeRefreshLayout.isRefreshing = true
                    search(SEARCH_TYPE, current_page, QUERY)
                }
            })
        }
    }

    /**
     * initSwipeRefreshLayoutメソッド
     *
     */
    private fun initSwipeRefreshLayout() {
        // swiprefreshLayout対応
        swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            // 上にスワイプした時に呼ばれます。
            swipeRefreshLayout.isRefreshing = true
            // TODO EndlessScrollのバグ修正が必要
            customAdapter.clear()
            search(SEARCH_TYPE, 1, QUERY)
        })
    }


    /**
     * initDataメソッド
     *
     */
    private fun initData() {
        // QiitaAPI実行
        search(SEARCH_TYPE, 1, QUERY)
    }

    /**
     * search
     *
     * @param page
     * @param query
     *
     */
    fun search(type: Int, page: Int, query: String) {
        // QUERYのエンコード
        val encordQuery = URLEncoder.encode(query, "UTF-8");
        val client = OkHttpClient()
        val request =
            when(type) {
                // 検索バー
                SEARCH_BODY -> {
                    Request.Builder()
                        .url("https://qiita.com/api/v2/items?page=${page}&per_page=20&query=body:${encordQuery}")
                    .build()
                }
                // タグ
                SEARCH_TAG -> {
                    Request.Builder()
                        .url("https://qiita.com/api/v2/tags/${encordQuery}/items?page=${page}&per_page=20")
                        .build()
                }
                else -> {
                    // TODO エラー処理？
                    return
                }
            }
        client.run {
            newCall(request).enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    handler.post {
                        //hideProgress()
                        swipeRefreshLayout.isRefreshing = false
                        customAdapter.addItems(mutableListOf(), false)
                        showErrorDialog()
//                        // TODO:Error時の処理
//                        Toast.makeText(this@SearchActivity, "エラーです。もどります。", Toast.LENGTH_SHORT).show()
//                        finish()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    handler.post {
                        //hideProgress()
                        swipeRefreshLayout.isRefreshing = false
                        response.body?.string()?.also {
                            // json取得
                            val gson = Gson()
                            val type = object : TypeToken<List<QiitaResponse>>() {}.type
                            val qiitaList = gson.fromJson<List<QiitaResponse>>(it, type)

                            // RecyclerViewのAdapter用のMutableList<ArticleRow>に変換
                            var articleRowList: MutableList<ArticleRow> = mutableListOf()
                            qiitaList.forEach({ resp ->
                                val row = ArticleRow()
                                row.convertFromQiitaResponse(resp)
                                articleRowList.add(row)
                            })
                            customAdapter.addItems(articleRowList, false)

                            if(!qiitaList.isEmpty()) {
                                // 取得結果あり
                                textSearchZero.toggle(false)
                            } else {
                                // 取得結果0件
                                textSearchZero.toggle(true)
                            }

                        } ?: run {
                            customAdapter.addItems(mutableListOf(), false)
                        }
                    }
                }
            })
        }
    }

    private fun showErrorDialog() {
        MaterialDialog(this)
            .title(text = "Errorです")
            .message(text = "もう一度やり直してください")
            .show {
            positiveButton(text = "リトライ", click = {
                search(SEARCH_TYPE, 1, QUERY)
            })
            negativeButton(text = "キャンセル")
        }
    }

    /**
     * onDestroyメソッド
     *
     */
    override fun onDestroy() {
        super.onDestroy()
        mRealm.close()
    }
    companion object { // comapnion object はstaticです
        private const val SEARCH_BODY = 0
        private const val SEARCH_TAG = 1

        private const val KEY_SEARCH_WORD = "key_search_word"
        private const val KEY_IS_SEARCH_BY_TAG = "key_is_search_by_tag"

        fun start(activity: Activity, searchWord: String, isSearchByTag: Boolean) =
            activity.startActivity(
                Intent(activity, SearchActivity::class.java)
                    .putExtra(KEY_SEARCH_WORD, searchWord)
                    .putExtra(KEY_IS_SEARCH_BY_TAG, isSearchByTag)
            )
    }
}
