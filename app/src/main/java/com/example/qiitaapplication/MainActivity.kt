package com.example.qiitaapplication

import QiitaResponse
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException







class MainActivity : AppCompatActivity() {

    private val handler = Handler()
    private val customAdapter by lazy { RecyclerListAdapter() }
    private val swiprefreshLayout by lazy { findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)}

    private val items = mutableListOf<QiitaResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // RecyclerViewを取得。
        val articleListView = findViewById<RecyclerView>(R.id.articleList)
        // LinearLayoutManagerオブジェクトを生成。
        val layout = LinearLayoutManager(applicationContext)
        // RecyclerViewにレイアウトマネージャとしてLinearLayoutManagerを設定。
        articleListView.layoutManager = layout
        // RecyclerViewにアダプタオブジェクトを設定。
        articleListView.adapter = customAdapter
        // 区切り専用のオブジェクトを生成。
        val decorator = DividerItemDecoration(applicationContext, layout.orientation)
        // RecyclerViewに区切り線オブジェクトを設定
        articleListView.addItemDecoration(decorator)
        // QiitaAPI実行
        updateData(1)

        // スクロール対応
        articleListView.addOnScrollListener(object :
            EndlessScrollListener(articleListView.getLayoutManager() as LinearLayoutManager) {
            override fun onLoadMore(page: Int) {
                updateData(page)
            }
        })

        // swiprefreshLayout対応
        swiprefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            // 引っ張って離した時に呼ばれます。
            swiprefreshLayout.isRefreshing = false
        })
    }


    private inner class RecyclerListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // リスト1行分中でメニュー名を表示する画面部品
        var articleTitle: TextView


        init {
            // 引数で渡されたリスト1行分の画面部品中から表示に使われるTextViewを取得。
            articleTitle = itemView.findViewById(R.id.articleTitle)

        }
    }

    private inner class RecyclerListAdapter() :
        RecyclerView.Adapter<RecyclerListViewHolder>() {



        fun refresh(list: List<QiitaResponse>) {
            items.apply {
                //clear()
                addAll(list)
            }
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerListViewHolder {
            // レイアウトインフレータを取得。
            val inflater = LayoutInflater.from(applicationContext)
            // row.xmlをインフレートし、1行分の画面部品とする。
            val view = inflater.inflate(R.layout.row, parent, false)
            // インフレートされた1行分画面部品にリスナを設定
            //view.setOnClickListener(ItemClickListener())
            // ビューホルダオブジェクトを生成。
            val holder = RecyclerListViewHolder(view)

            // クリックリスナを搭載
            view.setOnClickListener (object : View.OnClickListener{
                override fun onClick(view:  View) {

                    val position = holder.adapterPosition // positionを取得
                    // 何かの処理をします
                    val url = items[position].url
                    val intent = Intent(applicationContext, WebViewActivity::class.java)
                    intent.putExtra("url", url)
                    startActivity(intent)
                }
            })

            // 生成したビューホルダをリターン。
            return holder
        }

        override fun onBindViewHolder(holder: RecyclerListViewHolder, position: Int) {
            val data = items[position]
            holder.articleTitle.text = data.title
            //holder.rootView.setBackgroundColor(ContextCompat.getColor(context, if (position % 2 == 0) R.color.light_blue else R.color.light_yellow))

        }

        override fun getItemCount(): Int {
            // リストデータ中の件数をリターン。
            return items.size
        }
    }

    public fun updateData(page: Int) {
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