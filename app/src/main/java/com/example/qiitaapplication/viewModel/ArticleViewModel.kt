package com.example.qiitaapplication.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.qiitaapplication.ArticleAdapter
import com.example.qiitaapplication.QiitaApi
import com.example.qiitaapplication.dataclass.ArticleRow
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.net.UnknownHostException

class ArticleViewModel: ViewModel() {

    val items = MutableLiveData<List<ArticleAdapter.QiitaData>>()

    fun initData(isFavorite: Boolean) {
//        updateData(1) {
//            customAdapter.refresh(it, false)
//        }
        updateData(1, isFavorite)
    }

    /**
     * updateData
     *
     * @param page
     * @param isFavorite
     * @param isAdd
     *
     */
    fun updateData(page: Int, isFavorite: Boolean, isAdd: Boolean = false) {
        QiitaApi.items.getItem(page)
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
            },{
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