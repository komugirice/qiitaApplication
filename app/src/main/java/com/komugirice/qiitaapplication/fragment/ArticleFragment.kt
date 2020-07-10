package com.komugirice.qiitaapplication.fragment


import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.komugirice.qiitaapplication.ArticleAdapter
import com.komugirice.qiitaapplication.EndlessScrollListener
import com.komugirice.qiitaapplication.R
import com.komugirice.qiitaapplication.databinding.FragmentArticleBinding
import com.komugirice.qiitaapplication.viewModel.ArticleViewModel
import kotlinx.android.synthetic.main.fragment_article.*
import retrofit2.HttpException
import java.net.UnknownHostException


/**
 * A simple [Fragment] subclass.
 */
class ArticleFragment : Fragment() {

    private lateinit var binding: FragmentArticleBinding
    private lateinit var viewModel: ArticleViewModel

    private val handler = Handler()
    /** ArticleAdapter */
    private val customAdapter by lazy {
        ArticleAdapter(
            context,
            false
        )
    }
    /** EndlessScrollListenerインスタンス */
    private lateinit var mEndlessScrollListener: EndlessScrollListener


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //inflater.inflate(com.komugirice.qiitaapplication.R.layout.fragment_article, container, false)

        // initBinding
        binding = FragmentArticleBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        // initViewModel
        viewModel = ViewModelProviders.of(this).get(ArticleViewModel::class.java).apply {
            // QiitaApiが実行されて正常終了した
            items.observe(this@ArticleFragment, Observer {
                binding.apply {
                    // items = it
                    customAdapter.refresh(it)
                    swipeRefreshLayout.isRefreshing = false
                }
            })
            // QiitaAPIでExceptionが発生した
            isException.observe(this@ArticleFragment, Observer {
                when(it) {
                    is UnknownHostException -> {
                        showErrorDialog(
                            R.string.title_network_error,
                            R.string.message_network_error)
                    }
                    is HttpException -> {
                        showErrorDialog(
                            R.string.title_api_error,
                            R.string.message_api_error)
                    }
                    else -> Log.e("QiitaAPI", "UnExpected Error")
                }
                binding.swipeRefreshLayout.isRefreshing = false
            })
        }
        return binding.root
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
        viewModel.initData(false)
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
            mEndlessScrollListener = object:
                EndlessScrollListener(articleListView.layoutManager as LinearLayoutManager) {
                override fun onLoadMore(current_page: Int) {
                    swipeRefreshLayout.isRefreshing = true
                    // API実行
                    viewModel.updateData(current_page, false, true)
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

            // QiitaAPI実行
            viewModel.updateData(1, false, false)
        }
    }

    /**
     * initClickメソッド
     *
     */
    private fun initClick() {
    }

    private fun showErrorDialog(titleRes: Int, messageRes: Int) {
        context?.also {
            MaterialDialog(it)
                .title(res = titleRes)
                .message(res = messageRes)
                .show {
                    positiveButton(res = R.string.button_positive, click = {
                        viewModel.updateData(viewModel.currentPage, false, viewModel.isAddPrev)
                    })
                    negativeButton(res = R.string.button_negative)
                }
        }
    }

}
