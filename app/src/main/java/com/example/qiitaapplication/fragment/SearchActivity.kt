package com.example.qiitaapplication.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.qiitaapplication.EndlessScrollListener
import com.example.qiitaapplication.R
import com.example.qiitaapplication.activity.WebViewActivity
import com.example.qiitaapplication.dataclass.QiitaResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_web_view.toolbar
import kotlinx.android.synthetic.main.fragment_article.articleListView
import kotlinx.android.synthetic.main.fragment_article.swipeRefreshLayout
import kotlinx.android.synthetic.main.row.view.*
import okhttp3.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class SearchActivity : AppCompatActivity() {

    val SEARCH_BODY = 0
    val SEARCH_TAG = 1

    /** Realmインスタンス */
    lateinit var mRealm: Realm
    /** Handlerインスタンス */
    private val handler = Handler()
    /** RecyclerListAdapter */
    private val customAdapter by lazy { RecyclerListAdapter() }
    /** Qiita記事リスト */
    private val items = mutableListOf<QiitaResponse>()

    /** 検索タイプ */
    // TODO _で警告が発生する
    private val SEARCH_TYPE by lazy { intent.getIntExtra("searchType", 9) }
    /** 検索クエリ */
    private val QUERY by lazy { intent.getStringExtra("query") }

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
            // 引っ張って離した時に呼ばれます。
            swipeRefreshLayout.isRefreshing = false
        })
    }

    /**
     * RecyclerListAdapterクラス
     *
     */
    private inner class RecyclerListAdapter :
        RecyclerView.Adapter<RecyclerListViewHolder>() {


        /**
         * onCreateViewHolderメソッド
         *
         * @param parent
         * @param viewType
         * @return RecyclerListViewHolder
         *
         */
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerListViewHolder {
            // レイアウトインフレータを取得。
            val inflater = LayoutInflater.from(this@SearchActivity)
            // row.xmlをインフレートし、1行分の画面部品とする。
            val view = inflater.inflate(com.example.qiitaapplication.R.layout.row, parent, false)
            // ビューホルダオブジェクトを生成。
            val holder = RecyclerListViewHolder(view)

            // クリックリスナを搭載
            view.setOnClickListener (object : View.OnClickListener{
                override fun onClick(view: View) {

                    val position = holder.adapterPosition // positionを取得
                    // クリック時の処理
                    val url = items[position].url
                    val id = items[position].id
                    val title = items[position].title
                    val intent = Intent(this@SearchActivity, WebViewActivity::class.java)
                    intent.putExtra("url", url)
                    intent.putExtra("id", id)
                    intent.putExtra("title", title)
                    startActivity(intent)
                }
            })

            // タグのクリックリスナ
            view.articleTag.setOnClickListener {

                val position = holder.adapterPosition // positionを取得
                // SearchActivityに遷移
                val intent = Intent(this@SearchActivity, SearchActivity::class.java)
                // TODO 押下したタグごとに取得する必要がある
                intent.putExtra("query", items[position].tags[0].name)
                intent.putExtra("searchType", SEARCH_TAG)
                startActivity(intent)
            }

            // 生成したビューホルダをリターン。
            return holder
        }

        /**
         * onBindViewHolderメソッド
         *
         * @param holder
         * @param position
         *
         */
        override fun onBindViewHolder(holder: RecyclerListViewHolder, position: Int) {
            val data = items[position]
            // プロフィール画像
            Picasso.get().load(data.user.profile_image_url).into(holder.profileImage);

            holder.articleTitle.text = data.title   // タイトル
            holder.userName.text = if(data.user.name.isEmpty()) "Non-Name" else data.user.name.trim()   // ユーザ名
            holder.likesCount.text = data.likes_count.toString()   // お気に入り数
            holder.commentCount.text = data.comments_count.toString()   // お気に入り数
            holder.tag.text = data.tags[0].name.toString()

            // 作成日
            val existingUTCFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val requiredFormat = SimpleDateFormat("yyyy/MM/dd")
            val getDate = existingUTCFormat.parse(data.created_at)
            val dateStr = requiredFormat.format(getDate ?: Date())
            holder.createdAt.text = dateStr
            //holder.rootView.setBackgroundColor(ContextCompat.getColor(context, if (position % 2 == 0) R.color.light_blue else R.color.light_yellow))

        }

        /**
         * getItemCountメソッド
         *
         * @return Int
         */
        override fun getItemCount(): Int {
            // リストデータ中の件数をリターン。
            return items.size
        }

        /**
         * addItemsメソッド
         *
         * @param list
         */
        fun addItems(list: List<QiitaResponse>) {
            items.apply {
                addAll(list)
            }
            notifyDataSetChanged()
        }
    }

    /**
     * RecyclerListViewHolderクラスs
     *
     * @param itemView
     */
    private inner class RecyclerListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // リスト1行分中でメニュー名を表示する画面部品
        var profileImage: ImageView
        var articleTitle: TextView
        var userName: TextView
        var likesCount: TextView
        var createdAt: TextView
        var commentCount: TextView
        var tag: TextView


        init {
            // 引数で渡されたリスト1行分の画面部品中から表示に使われるTextViewを取得。
            profileImage = itemView.findViewById(com.example.qiitaapplication.R.id.profileImage)
            articleTitle = itemView.findViewById(com.example.qiitaapplication.R.id.articleTitle)
            userName = itemView.findViewById(com.example.qiitaapplication.R.id.userName)
            likesCount = itemView.findViewById(com.example.qiitaapplication.R.id.likesCount)
            createdAt = itemView.findViewById(com.example.qiitaapplication.R.id.createdAt)
            commentCount = itemView.findViewById(com.example.qiitaapplication.R.id.commentCount)
            tag = itemView.findViewById(com.example.qiitaapplication.R.id.articleTag)

        }
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
        val client = OkHttpClient()
        val request =
            when(type) {
                // 検索バー
                SEARCH_BODY -> {
                    Request.Builder()
                        .url("https://qiita.com/api/v2/items?page=${page}&per_page=20&query=body:${query}")
                    .build()
                }
                // タグ
                SEARCH_TAG -> {
                    Request.Builder()
                        .url("https://qiita.com/api/v2/tags/${query}/items?page=${page}&per_page=20")
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
                        customAdapter.addItems(mutableListOf())
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    handler.post {
                        //hideProgress()
                        swipeRefreshLayout.isRefreshing = false
                        response.body?.string()?.also {
                            val gson = Gson()
                            val type = object : TypeToken<List<QiitaResponse>>() {}.type
                            val list = gson.fromJson<List<QiitaResponse>>(it, type)
                            customAdapter.addItems(list)
                        } ?: run {
                            customAdapter.addItems(mutableListOf())
                        }
                    }
                }
            })
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

}
