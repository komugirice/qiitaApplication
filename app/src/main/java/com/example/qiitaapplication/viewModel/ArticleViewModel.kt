package com.example.qiitaapplication.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.qiitaapplication.ArticleAdapter
import com.example.qiitaapplication.QiitaApi
import com.example.qiitaapplication.activity.SearchActivity
import com.example.qiitaapplication.dataclass.ArticleRow
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.HttpException
import java.net.URLEncoder
import java.net.UnknownHostException

class ArticleViewModel: ViewModel() {

    val items = MutableLiveData<List<ArticleAdapter.QiitaData>>()

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
                    val row = ArticleRow()
                    row.convertFromQiitaResponse(resp)
                    articleRowList.add(row)
                })
                // 取得データ反映
                setItems(articleRowList, isFavorite, isAdd)

            }, {
                items.postValue(listOf())
                when(it) {
                    is UnknownHostException -> {
//                        showErrorDialog(
//                            R.string.title_network_error,
//                            R.string.message_network_error, page)
                    }
                    is HttpException -> {
//                        showErrorDialog(
//                            R.string.title_api_error,
//                            R.string.message_api_error, page)
                    }
                    else -> Log.e("QiitaAPI", "UnExpected Error")
                }
                //swipeRefreshLayout.isRefreshing = false
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
                    val row = ArticleRow()
                    row.convertFromQiitaResponse(resp)
                    articleRowList.add(row)
                })
                // 取得データ反映
                setItems(articleRowList, false, isAdd)
            }, {
                //customAdapter.addItems(mutableListOf(), false)
                when(it) {
                    is UnknownHostException -> {
//                        showErrorDialog(
//                            R.string.title_network_error,
//                            R.string.message_network_error, page)
                    }
                    is HttpException -> {
//                        showErrorDialog(
//                            R.string.title_api_error,
//                            R.string.message_api_error, page)
                    }
                    else -> Log.e("QiitaAPI", "UnExpected Error")
                }
            })
    }

    // TODO エラーダイアログの表示
//    private fun showErrorDialog(titleRes: Int, messageRes: Int, page: Int) {
//        context?.also {
//            MaterialDialog(it)
//                .title(res = titleRes)
//                .message(res = messageRes)
//                .show {
//                    positiveButton(res = R.string.button_positive, click = {
//                        updateData(page){
//                            customAdapter.addItems(it, false)
//                        }
//                    })
//                    negativeButton(res = R.string.button_negative)
//                }
//        }
//    }
}