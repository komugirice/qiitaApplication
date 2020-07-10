package com.komugirice.qiitaapplication.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.komugirice.qiitaapplication.ArticleAdapter
import com.komugirice.qiitaapplication.QiitaApi
import com.komugirice.qiitaapplication.activity.SearchActivity
import com.komugirice.qiitaapplication.dataclass.ArticleRow
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import java.net.URLEncoder

class ArticleViewModel: ViewModel() {

    val items = MutableLiveData<List<ArticleAdapter.QiitaData>>()
    val isException = MutableLiveData<Throwable>()
    // 仕方なく現在ページをここに設置(エラー対応)
    var currentPage = 0
    // 仕方なく前回追加フラグをここに設置(エラー対応)
    var isAddPrev = false

    fun initData(isFavorite: Boolean) {
        updateData(1, isFavorite)
    }

    fun initSearch(type: Int, query: String) {
        search(type, 1, query)
    }

    /**
     * updateData
     *
     * @param page 検索するページ番号
     * @param isFavorite　お気に入りか
     * @param isAdd true:追加 false:クリア
     *
     */
    fun updateData(page: Int, isFavorite: Boolean, isAdd: Boolean = false) {
        QiitaApi.itemsIF.getItem(page)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                var articleRowList : MutableList<ArticleRow>  = mutableListOf()
                it.forEach({
                        resp ->
                    val row =
                        ArticleRow()
                    row.convertFromQiitaResponse(resp)
                    articleRowList.add(row)
                })
                // 取得データ反映
                setItems(articleRowList, isFavorite, isAdd)

            }, {
                currentPage = page
                isAddPrev = isAdd

                //items.postValue(listOf())
                isException.postValue(it)

            })

    }

    /**
     * setItems
     *
     * @param articleRowList
     * @param isFavorite
     * @param isAdd
     *
     */
    fun setItems(articleRowList : MutableList<ArticleRow>, isFavorite: Boolean, isAdd: Boolean = false) {

        // 引数：articleRowList → ArticleAdapter.QiitaDataにコンバート
        val qiitaList : MutableList<ArticleAdapter.QiitaData> = mutableListOf()
        articleRowList.forEach({ row -> qiitaList.add(ArticleAdapter.QiitaData(row, isFavorite))})

        // 引数：isAddによってデータ追加orクリア
        val tempList = mutableListOf<ArticleAdapter.QiitaData>()
        if (isAdd)
            tempList.addAll(items.value ?: listOf())
        tempList.addAll(qiitaList)
        items.postValue(tempList)
    }

    /**
     * search
     *
     * @param type 0:検索 1:タグ
     * @param page 検索するページ番号
     * @param query 検索クエリ
     * @param isAdd true:追加 false:クリア
     *
     */
    fun search(type: Int, page: Int, query: String, isAdd: Boolean = false) {
        // searchQueryのエンコード
        val encodeQuery = URLEncoder.encode(query, "UTF-8");
        val client = OkHttpClient()
        val observable =
            when(type) {
                // 検索バー
                SearchActivity.SEARCH_BODY -> {
                    QiitaApi.itemsIF.searchBody(page, encodeQuery)
                }
                // タグ
                SearchActivity.SEARCH_TAG -> {
                    QiitaApi.tagsIF.searchTag(encodeQuery, page)
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
                    val row =
                        ArticleRow()
                    row.convertFromQiitaResponse(resp)
                    articleRowList.add(row)
                })
                // 取得データ反映
                setItems(articleRowList, false, isAdd)
            }, {
                currentPage = page
                isAddPrev = isAdd

                //items.postValue(listOf())
                isException.postValue(it)
            })
    }

}