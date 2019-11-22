package com.example.qiitaapplication.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.example.qiitaapplication.ArticleAdapter
import com.example.qiitaapplication.EndlessScrollListener
import com.example.qiitaapplication.QiitaApi
import com.example.qiitaapplication.R
import com.example.qiitaapplication.dataclass.ArticleRow
import com.example.qiitaapplication.dataclass.QiitaResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_web_view.toolbar
import kotlinx.android.synthetic.main.fragment_article.articleListView
import kotlinx.android.synthetic.main.fragment_article.swipeRefreshLayout
import okhttp3.OkHttpClient
import java.net.URLEncoder

class SearchActivity : AppCompatActivity() {

    /** Realmインスタンス */
    lateinit var mRealm: Realm
    /** Handlerインスタンス */
    private val handler = Handler()

    /** RecyclerListAdapter */
    private val customAdapter by lazy { ArticleAdapter(this, false) }
    /** Qiita記事リスト */
    private val items = mutableListOf<QiitaResponse>()

    /** 検索タイプ */
    private val searchType by lazy { if (intent.getBooleanExtra(KEY_IS_SEARCH_BY_TAG, false)) SEARCH_TAG else SEARCH_BODY }
    /** 検索クエリ */
    private val searchQuery by lazy { intent.getStringExtra(KEY_SEARCH_WORD) }

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
        toolbarTitle.text = searchQuery
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
                    search(searchType, current_page, searchQuery)
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
            customAdapter.clear()
            search(searchType, 1, searchQuery)
        })
    }


    /**
     * initDataメソッド
     *
     */
    private fun initData() {
        // QiitaAPI実行
        search(searchType, 1, searchQuery)
    }

    /**
     * search
     *
     * @param page
     * @param query
     *
     */
    fun search(type: Int, page: Int, query: String, onSuccess: (List<ArticleRow>) -> Unit = {}) {
        // searchQueryのエンコード
        val encodeQuery = URLEncoder.encode(query, "UTF-8");
        val client = OkHttpClient()
        val observable =
            when(type) {
                // 検索バー
                SEARCH_BODY -> {
                    QiitaApi.items.searchBody(page, encodeQuery)
                }
                // タグ
                SEARCH_TAG -> {
                    QiitaApi.tags.searchTag(encodeQuery, page)
                }
                else -> {
                    return
                }
            }

        observable.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                // RecyclerViewのAdapter用のMutableList<ArticleRow>に変換
                var articleRowList: MutableList<ArticleRow> = mutableListOf()
                it.forEach({ resp ->
                    val row = ArticleRow()
                    row.convertFromQiitaResponse(resp)
                    articleRowList.add(row)
                })
                customAdapter.addItems(articleRowList, false)
            }, {
                customAdapter.addItems(mutableListOf(), false)
                showErrorDialog(page)
                swipeRefreshLayout.isRefreshing = false
            }, {
                swipeRefreshLayout.isRefreshing = false
            })
    }

    private fun showErrorDialog(page: Int) {
        MaterialDialog(this)
            .title(res = R.string.title_network_error)
            .message(res = R.string.message_network_error)
            .show {
                positiveButton(res = R.string.button_positive, click = {
                    search(searchType, page, searchQuery)
                })
                negativeButton(res = R.string.button_negative)
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
