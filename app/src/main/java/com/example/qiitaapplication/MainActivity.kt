package com.example.qiitaapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // RecyclerViewを取得。
        val articleListView = findViewById<RecyclerView>(R.id.articleList)
        // LinearLayoutManagerオブジェクトを生成。
        val layout = LinearLayoutManager(applicationContext)
        // RecyclerViewにレイアウトマネージャとしてLinearLayoutManagerを設定。
        articleListView.layoutManager = layout
        // 定食メニューリストデータを生成。
        val articleList = createArticleList()
        // アダプタオブジェクトを生成。
        val adapter = RecyclerListAdapter(articleList)
        // RecyclerViewにアダプタオブジェクトを設定。
        articleListView.adapter = adapter
        // 区切り専用のオブジェクトを生成。
        val decorator = DividerItemDecoration(applicationContext, layout.orientation)
        // RecyclerViewに区切り線オブジェクトを設定
        articleListView.addItemDecoration(decorator)
        articleListView.addItemDecoration(decorator)
    }

    private fun createArticleList(): MutableList<MutableMap<String, String>> {
        // 記事リスト用のListオブジェクトを用意。
        val articleList: MutableList<MutableMap<String, String>> = mutableListOf()
        var article = mutableMapOf(
            "name" to "記事１"
        )
        articleList.add(article)
        article = mutableMapOf(
            "name" to "記事２"
        )
        articleList.add(article)
        article = mutableMapOf(
            "name" to "記事３"
        )
        articleList.add(article)
        article = mutableMapOf(
            "name" to "記事４"
        )
        articleList.add(article)
        article = mutableMapOf(
            "name" to "記事５"
        )
        articleList.add(article)
        article = mutableMapOf(
            "name" to "記事６"
        )
        articleList.add(article)
        article = mutableMapOf(
            "name" to "記事７"
        )
        articleList.add(article)
        article = mutableMapOf(
            "name" to "記事８"
        )
        articleList.add(article)
        article = mutableMapOf(
            "name" to "記事９"
        )
        articleList.add(article)
        article = mutableMapOf(
            "name" to "記事１０"
        )

        articleList.add(article)


        return articleList
    }

    private inner class RecyclerListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // リスト1行分中でメニュー名を表示する画面部品
        var articleTitle: TextView


        init {
            // 引数で渡されたリスト1行分の画面部品中から表示に使われるTextViewを取得。
            articleTitle = itemView.findViewById(R.id.articleTitle)

        }
    }

    private inner class RecyclerListAdapter(private val _listData: MutableList<MutableMap<String, String>>) :
        RecyclerView.Adapter<RecyclerListViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerListViewHolder {
            // レイアウトインフレータを取得。
            val inflater = LayoutInflater.from(applicationContext)
            // row.xmlをインフレートし、1行分の画面部品とする。
            val view = inflater.inflate(R.layout.row, parent, false)
            // インフレートされた1行分画面部品にリスナを設定
            view.setOnClickListener(ItemClickListener())
            // ビューホルダオブジェクトを生成。
            val holder = RecyclerListViewHolder(view)
            // 生成したビューホルダをリターン。
            return holder
        }

        override fun onBindViewHolder(holder: RecyclerListViewHolder, position: Int) {
            // リストデータから該当1行分のデータを取得。
            val item = _listData[position]
            // メニュー名文字列を取得。
            val menuName = item["name"] as String
            // メニュー名と金額をビューホルダ中のTextViewに設定。
            holder.articleTitle.text = menuName
        }

        override fun getItemCount(): Int {
            // リストデータ中の件数をリターン。
            return _listData.size
        }
    }

    private inner class ItemClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            // タップされたLinearLayout内にあるメニュー名表示TextViewを取得。
            val articleTitle = view.findViewById<TextView>(R.id.articleTitle)
            // メニュー名表示TextViewから表示されているメニュー名文字列を取得。
            val titleStr = articleTitle.text.toString()
            // トーストに表示する文字列を生成。
            val msg = titleStr
            // トーストを表示。
            Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
        }
    }


}