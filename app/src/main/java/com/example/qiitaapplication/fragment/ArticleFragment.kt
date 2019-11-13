package com.example.qiitaapplication.fragment


import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qiitaapplication.ArticleAdapter
import com.example.qiitaapplication.EndlessScrollListener
import com.example.qiitaapplication.dataclass.ArticleRow
import com.example.qiitaapplication.dataclass.QiitaResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_article.*
import okhttp3.*
import java.io.IOException


/**
 * A simple [Fragment] subclass.
 */
class ArticleFragment : Fragment() {


    private val handler = Handler()
    /** ArticleAdapter */
    private val customAdapter by lazy { ArticleAdapter(context) }
    /** EndlessScrollListenerインスタンス */
    private lateinit var mEndlessScrollListener: EndlessScrollListener
    /** Qiita記事リスト */
    private val items = mutableListOf<QiitaResponse>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(com.example.qiitaapplication.R.layout.fragment_article, container, false)
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

            // EndlessScrollListenerのインスタンス化
            mEndlessScrollListener = object:EndlessScrollListener(articleListView.layoutManager as LinearLayoutManager) {
                override fun onLoadMore(current_page: Int) {
                    swipeRefreshLayout.isRefreshing = true
                    // API実行
                    updateData(current_page)
                }
            }
            // RecyclerViewスクロール対応
            articleListView.addOnScrollListener(mEndlessScrollListener)
        }
    }

    /**
     * initSwipeRefreshLayoutメソッド
     *
     */
    private fun initSwipeRefreshLayout() {
        // swiprefreshLayout対応
        swipeRefreshLayout.setOnRefreshListener {
            // 上にスワイプした時に呼ばれます。
            swipeRefreshLayout.isRefreshing = true
            customAdapter.clear()
            // ndlessScrollのバグ修正
            mEndlessScrollListener.reset()

            updateData(1)
        }
    }


    /**
     * initClickメソッド
     *
     */
    private fun initClick() {
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
                        customAdapter.addItems(mutableListOf(), false)
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    handler.post {
                        //hideProgress()
                        swipeRefreshLayout.isRefreshing = false
                        response.body?.string()?.also {
                            val gson = Gson()
                            val type = object : TypeToken<List<QiitaResponse>>() {}.type
                            val qiitaList = gson.fromJson<List<QiitaResponse>>(it, type)
                            // RecyclerViewのAdapter用に変換
                            var articleRowList : MutableList<ArticleRow>  = mutableListOf()
                            qiitaList.forEach({
                                    resp ->
                                        val row = ArticleRow()
                                        row.convertFromQiitaResponse(resp)
                                        articleRowList.add(row)
                            })
                            customAdapter.addItems(articleRowList, false)
                        } ?: run {
                            customAdapter.addItems(mutableListOf(), false)
                        }
                    }
                }
            })
        }
    }


}
