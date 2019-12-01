package com.example.qiitaapplication

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * EndlessScrollListenerクラス
 *
 * @param mLinearLayoutManager
 */
abstract class EndlessScrollListener(private val mLinearLayoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {

    internal var firstVisibleItem: Int = 0
    internal var visibleItemCount: Int = 0
    internal var totalItemCount: Int = 0
    private var visibleThreshold = 10
    private var previousTotal = 0
    private var loading = true
    private var current_page = 1
    // スクロール条件件数を指定してください。取得件数を下回る場合はスクロールしません。
    private val scrollOnItemCount: Int = 20


    /**
     * onScrolledメソッド
     *
     * @param recyclerView
     * @param dx
     * @param dy
     *
     */
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        visibleItemCount = recyclerView.childCount
        // 取得件数
        totalItemCount = mLinearLayoutManager.itemCount
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition()

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false
                previousTotal = totalItemCount
            }
        }

        // 取得件数がスクロール条件件数を上回る場合だけ実行する
        if (scrollOnItemCount <= totalItemCount) {
            if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
                current_page++

                onLoadMore(current_page)

                loading = true
            }
        }
    }

    /**
     * onLoadMoreメソッド
     *
     * @param current_page
     *
     */
    abstract fun onLoadMore(current_page: Int)

    /**
     * resetメソッド
     *
     */
    fun reset() {
        current_page = 1
        previousTotal = 0
        totalItemCount = 0
        loading = true
    }

}