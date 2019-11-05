package com.example.qiitaapplication.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qiitaapplication.R
import com.example.qiitaapplication.activity.WebViewActivity
import com.example.qiitaapplication.dataclass.Favorite
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_favorite.*

/**
 * A simple [Fragment] subclass.
 */
class FavoriteFragment : Fragment() {

    /** RecyclerListAdapter */
    private val customAdapter by lazy { RecyclerListAdapter() }
    /** Realmインスタンス */
    lateinit var mRealm: Realm
    /** お気に入りリスト */
    lateinit var favoriteList : List<Favorite>

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
    }

    /**
     * readAllメソッド
     *
     */
    fun readAll(): List<Favorite>? {
        val results = mRealm.where(Favorite::class.java).equalTo("del_flg", "0")
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
            // row_favoriteをインフレートし、1行分の画面部品とする。
            val view = inflater.inflate(R.layout.row_favorite, parent, false)
            // ビューホルダオブジェクトを生成。
            val holder = RecyclerListViewHolder(view)

            // クリックリスナを搭載
            view.setOnClickListener (object : View.OnClickListener{
                override fun onClick(view: View) {

                    val position = holder.adapterPosition // positionを取得
                    // クリック時の処理
                    val url = favoriteList[position].url
                    val id = favoriteList[position].id
                    val title = favoriteList[position].title
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
            val data = favoriteList[position]
            holder.favoriteTitle.text = data.title   // タイトル

        }

        /**
         * getItemCountメソッド
         *
         * @return Int
         */
        override fun getItemCount(): Int {
            // リストデータ中の件数をリターン。
            return favoriteList.size
        }


    }

    /**
     * RecyclerListViewHolderクラス
     *
     * @param itemView
     */
    private inner class RecyclerListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // リスト1行分中でメニュー名を表示する画面部品
        var favoriteTitle: TextView


        init {
            // 引数で渡されたリスト1行分の画面部品中から表示に使われるTextViewを取得。
            favoriteTitle = itemView.findViewById(R.id.favoriteTitle)

        }
    }


}
