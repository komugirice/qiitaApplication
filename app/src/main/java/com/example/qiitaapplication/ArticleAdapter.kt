package com.example.qiitaapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qiitaapplication.activity.SearchActivity
import com.example.qiitaapplication.activity.WebViewActivity
import com.example.qiitaapplication.dataclass.ArticleRow
import com.example.qiitaapplication.dataclass.QiitaResponse
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row.view.*

/**
 * ArticleAdapterクラス
 *
 */
class ArticleAdapter(private val context: Context?) : RecyclerView.Adapter<ArticleAdapter.RowViewHolder>() {
    private val SEARCH_TAG = 1

    private val items = mutableListOf<ArticleRow>()

    /**
     * onCreateViewHolderメソッド
     *
     * @param parent
     * @param viewType
     * @return RowViewHolder
     *
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        // レイアウトインフレータを取得。
        val inflater = LayoutInflater.from(context)
        // row.xmlをインフレートし、1行分の画面部品とする。
        val view = inflater.inflate(R.layout.row, parent, false)
        // ビューホルダオブジェクトを生成。
        val holder = RowViewHolder(view)

        // クリックリスナを搭載
        view.setOnClickListener (object : View.OnClickListener{
            override fun onClick(view: View) {

                val position = holder.adapterPosition // positionを取得
                // クリック時の処理
                val bundle = Bundle()
                bundle.apply {
                    putString("id", items[position].id)
                    putString("url", items[position].url)
                    putString("title", items[position].title)
                    putString("profileImageUrl", items[position].profileImageUrl)
                    putString("userName", items[position].userName)
                    putString("createdAt", items[position].createdAt)
                    putString("likesCount", items[position].likesCount)
                    putString("commentCount", items[position].commentCount)
                    putString("tags", items[position].tags)

                }

                val intent = Intent(context, WebViewActivity::class.java)
                intent.putExtras(bundle)
                context?.startActivity(intent)
            }
        })

        // タグのクリックリスナ
        view.articleTag.setOnClickListener {

            val position = holder.adapterPosition // positionを取得
            // SearchActivityに遷移
            val intent = Intent(context, SearchActivity::class.java)
            // TODO 押下したタグごとに取得
            intent.putExtra("query", items[position].tags.split(",")[0])
            intent.putExtra("searchType", SEARCH_TAG)
            context?.startActivity(intent)
        }

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
    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        val data = items[position]
        // プロフィール画像
        Picasso.get().load(data.profileImageUrl).into(holder.profileImage)

        holder.articleTitle.text = data.title   // タイトル
        holder.userName.text = if(data.userName.isEmpty()) "Non-Name" else data.userName.trim()   // ユーザ名
        holder.likesCount.text = data.likesCount   // お気に入り数
        holder.commentCount.text = data.commentCount   // お気に入り数
        holder.tag.text = data.tags.split(",")[0]
        holder.createdAt.text = data.createdAt
        holder.updDate.text = data.updDate

        //holder.rootView.setBackgroundColor(ContextCompat.getColor(context, if (position % 2 == 0) R.color.light_blue else R.color.light_yellow))

    }

    /**
     * getItemCountメソッド
     *
     * @return Int
     */
    override fun getItemCount(): Int {
        // リストデータ中の件数をリターン。
        return items.size
    }


    /**
     * refreshメソッド
     *
     * @param list
     */
    fun refresh(list: List<ArticleRow>) {
        items.apply {
            clear()
            addAll(list)
        }
        notifyDataSetChanged()
    }

    /**
     * addItemsメソッド
     *
     * @param list
     */
    fun addItems(list: List<ArticleRow>) {
        items.apply {
            addAll(list)
        }
        notifyDataSetChanged()
    }

    /**
     * RowViewHolderクラス
     *
     * @param itemView
     */
    class RowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // リスト1行分中でメニュー名を表示する画面部品
        var profileImage = itemView.findViewById(R.id.profileImage) as ImageView
        var articleTitle = itemView.findViewById(R.id.articleTitle) as TextView
        var userName = itemView.findViewById(R.id.userName) as  TextView
        var likesCount = itemView.findViewById(R.id.likesCount) as TextView
        var createdAt = itemView.findViewById(R.id.createdAt) as TextView
        var commentCount = itemView.findViewById(R.id.commentCount) as TextView
        var tag = itemView.findViewById(R.id.articleTag) as TextView
        var updDate = itemView.findViewById(R.id.updDate) as TextView

    }

    class QiitaData {
        lateinit var response : QiitaResponse
        var isFavorite = false
    }
}