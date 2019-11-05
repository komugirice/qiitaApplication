package com.example.qiitaapplication.fragment


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.qiitaapplication.EndlessScrollListener
import com.example.qiitaapplication.R
import com.example.qiitaapplication.activity.WebViewActivity
import com.example.qiitaapplication.dataclass.QiitaResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main_bk.swipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_article.*
import okhttp3.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ArticleFragment : Fragment() {

    private val handler = Handler()
    /** RecyclerListAdapter */
    private val customAdapter by lazy { RecyclerListAdapter() }
    /** Qiita記事リスト */
    private val items = mutableListOf<QiitaResponse>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_article, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun initialize() {
        initLayout()
        initData()
    }

    /**
     * initDataメソッド
     *
     */
    private fun initData() {
        // QiitaAPI実行
        updateData(1)
    }

    /**
     * initLayoutメソッド
     *
     */
    private fun initLayout() {
        initClick()
        initRecyclerView()
        initSwipeRefreshLayout()
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
                    updateData(current_page)
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
     * initClickメソッド
     *
     */
    private fun initClick() {

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
            val inflater = LayoutInflater.from(context)
            // row.xmlをインフレートし、1行分の画面部品とする。
            val view = inflater.inflate(R.layout.row, parent, false)
            // ビューホルダオブジェクトを生成。
            val holder = RecyclerListViewHolder(view)

            // クリックリスナを搭載
            view.setOnClickListener (object : View.OnClickListener{
                override fun onClick(view:  View) {

                    val position = holder.adapterPosition // positionを取得
                    // クリック時の処理
                    val url = items[position].url
                    val id = items[position].id
                    val title = items[position].title
                    val intent = Intent(context, WebViewActivity::class.java)
                    intent.putExtra("url", url)
                    intent.putExtra("id", id)
                    intent.putExtra("title", title)
                    startActivity(intent)
                }
            })

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
            holder.articleTitle.text = data.title   // タイトル
            holder.userName.text = data.user.name   // ユーザ名
            holder.likes_count.text = data.likes_count.toString()   // お気に入り数
            val existingUTCFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val requiredFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val getDate = existingUTCFormat.parse(data.created_at)
            val dateStr = requiredFormat.format(getDate ?: Date())
            holder.created_at.text = dateStr   // 作成日
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
         * refreshメソッド
         *
         * @param list
         */
        fun refresh(list: List<QiitaResponse>) {
            items.apply {
                //clear()
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
        var articleTitle: TextView
        var userName: TextView
        var likes_count: TextView
        var created_at: TextView


        init {
            // 引数で渡されたリスト1行分の画面部品中から表示に使われるTextViewを取得。
            articleTitle = itemView.findViewById(R.id.articleTitle)
            userName = itemView.findViewById(R.id.userName)
            likes_count = itemView.findViewById(R.id.likes_count)
            created_at = itemView.findViewById(R.id.created_at)

        }
    }

    /**
     * updateData
     *
     * @param page
     *
     */
    fun updateData(page: Int) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://qiita.com/api/v2/items?page=${page}&per_page=20")
            .build()
        client.run {
            newCall(request).enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    handler.post {
                        //hideProgress()
                        swipeRefreshLayout.isRefreshing = false
                        customAdapter.refresh(mutableListOf())
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
                            customAdapter.refresh(list)
                        } ?: run {
                            customAdapter.refresh(mutableListOf())
                        }
                    }
                }
            })
        }
    }

}
