package com.example.qiitaapplication.fragment


import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.example.qiitaapplication.ArticleAdapter
import com.example.qiitaapplication.EndlessScrollListener
import com.example.qiitaapplication.QiitaApi
import com.example.qiitaapplication.R
import com.example.qiitaapplication.dataclass.ArticleRow
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_article.*


/**
 * A simple [Fragment] subclass.
 */
class ArticleFragment : Fragment() {


    private val handler = Handler()
    /** ArticleAdapter */
    private val customAdapter by lazy { ArticleAdapter(context, false) }
    /** EndlessScrollListenerインスタンス */
    private lateinit var mEndlessScrollListener: EndlessScrollListener

    /** 作業用Qiita記事リスト */
    private var workArticleRowList: MutableList<ArticleRow> = mutableListOf()


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
        updateData(1, true)
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
                    updateData(current_page, false)
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
            // endlessScrollのバグ修正
            mEndlessScrollListener.reset()

            // TODO API取得とrefresh or addItemと処理を分けること
            updateData(1, true)
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
    fun updateData(page: Int, isRefresh: Boolean) {
        QiitaApi.items.getItem(page)
            .observeOn(mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                var articleRowList : MutableList<ArticleRow>  = mutableListOf()
                it.forEach({
                        resp ->
                    val row = ArticleRow()
                    row.convertFromQiitaResponse(resp)
                    articleRowList.add(row)
                })
                if (isRefresh)
                    customAdapter.refresh(articleRowList, false)
                else
                    customAdapter.addItems(articleRowList, false)

            }, {
                if (isRefresh)
                    customAdapter.refresh(mutableListOf(), false)
                else
                    customAdapter.addItems(mutableListOf(), false)

                showErrorDialog()
                swipeRefreshLayout.isRefreshing = false
            },{
                swipeRefreshLayout.isRefreshing = false
        })

    }
    private fun showErrorDialog() {
        context?.also {
            MaterialDialog(it)
                .title(res = R.string.title_network_error)
                .message(res = R.string.message_network_error)
                .show {
                    positiveButton(res = R.string.button_positive, click = {
                        updateData(1, true)
                    })
                    negativeButton(res = R.string.button_negative)
                }
        }
    }

}
