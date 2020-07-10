package com.komugirice.qiitaapplication.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.komugirice.qiitaapplication.ArticleAdapter
import com.komugirice.qiitaapplication.R
import com.komugirice.qiitaapplication.dataclass.ArticleRow
import io.realm.Realm
import io.realm.Sort
import kotlinx.android.synthetic.main.fragment_favorite.*

/**
 * A simple [Fragment] subclass.
 */
class FavoriteFragment : Fragment() {

    /** RecyclerListAdapter */
    private val customAdapter by lazy {
        ArticleAdapter(
            context,
            true
        )
    }
    /** Realmインスタンス */
    lateinit var mRealm: Realm
    /** お気に入りリスト */
    lateinit var favoriteList : List<ArticleRow>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    /**
     * initializeメソッド
     *
     */
    private fun initialize() {
        Realm.init(context)
        mRealm = Realm.getDefaultInstance()


    }

    /**
     * onResumeメソッド
     *
     */
    override fun onResume() {
        super.onResume()
        initData()
        initLayout()
    }

    /**
     * initDataメソッド
     *
     */
    private fun initData() {
        // realmから取得
        favoriteList = readAll() ?: mutableListOf()

        // favoriteList → ArticleAdapter.QiitaDataにコンバート
        val qiitaList : MutableList<ArticleAdapter.QiitaData> = mutableListOf()
        favoriteList.forEach({ row -> qiitaList.add(ArticleAdapter.QiitaData(row, true))})

        customAdapter.refresh(qiitaList)
    }

    /**
     * readAllメソッド
     *
     */
    fun readAll(): List<ArticleRow>? {
        val results = mRealm.where(ArticleRow::class.java).equalTo("delFlg", "0")
            .sort("updDate", Sort.DESCENDING)
        .findAll().let { mRealm.copyFromRealm(it)}
        return  results
    }

    /**
     * initLayoutメソッド
     *
     */
    private fun initLayout() {
        initClick()
        initRecyclerView()
    }

    /**
     * initClickメソッド
     *
     */
    private fun initClick() {

    }

    /**
     * initRecyclerViewメソッド
     *
     */
    private fun initRecyclerView() {
        // RecyclerViewを取得。
        favoriteListView.apply {
            // LinearLayoutManagerオブジェクトを生成。
            val layout = LinearLayoutManager(context)
            // RecyclerViewにレイアウトマネージャとしてLinearLayoutManagerを設定。
            layoutManager = layout
            // RecyclerViewにアダプタオブジェクトを設定。
            adapter = customAdapter
        }

    }


}
