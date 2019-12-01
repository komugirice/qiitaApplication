package com.example.qiitaapplication.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.qiitaapplication.ArticleAdapter
import com.example.qiitaapplication.EndlessScrollListener
import com.example.qiitaapplication.R
import com.example.qiitaapplication.databinding.ActivitySearchBinding
import com.example.qiitaapplication.dataclass.QiitaResponse
import com.example.qiitaapplication.viewModel.ArticleViewModel
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_web_view.toolbar
import kotlinx.android.synthetic.main.fragment_article.articleListView
import kotlinx.android.synthetic.main.fragment_article.swipeRefreshLayout

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: ArticleViewModel

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
        initBinding()
        initViewModel()
        initLayout()
        initData()
    }

    /**
     * MVVMのBinding
     *
     */
    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_search
        )
        binding.lifecycleOwner = this
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(ArticleViewModel::class.java).apply {
            items.observe(this@SearchActivity, Observer {
                binding.apply {
                    customAdapter.refresh(it)
                    swipeRefreshLayout.isRefreshing = false
                }
            })
        }
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
                    viewModel.search(searchType, current_page, searchQuery, true)
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
            viewModel.initSearch(searchType, searchQuery)
        })
    }

    /**
     * initDataメソッド
     *
     */
    private fun initData() {
        // QiitaAPI実行
        viewModel.initSearch(searchType, searchQuery)
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
        val SEARCH_BODY = 0
        val SEARCH_TAG = 1

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
