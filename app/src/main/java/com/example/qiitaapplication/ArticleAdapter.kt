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
import com.example.qiitaapplication.extension.toggle
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row.view.*

/**
 * ArticleAdapterクラス
 *
 */
class ArticleAdapter(private val context: Context?) : RecyclerView.Adapter<ArticleAdapter.RowViewHolder>() {
    private val SEARCH_TAG = 1

    private val items = mutableListOf<QiitaData>()

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
                    putString("id", items[position].row.id)
                    putString("url", items[position].row.url)
                    putString("title", items[position].row.title)
                    putString("profileImageUrl", items[position].row.profileImageUrl)
                    putString("userName", items[position].row.userName)
                    putString("createdAt", items[position].row.createdAt)
                    putString("likesCount", items[position].row.likesCount)
                    putString("commentCount", items[position].row.commentCount)
                    putString("tags", items[position].row.tags)

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
            intent.putExtra("query", items[position].row.tags.split(",")[0])
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
        Picasso.get().load(data.row.profileImageUrl).into(holder.profileImage)

        holder.articleTitle.text = data.row.title   // タイトル
        // ユーザ名 + " が" + 登録日 + " に投稿しました"
        var userInfo = if(data.row.userName.isEmpty()) "Non-Name" else data.row.userName.trim()
        userInfo += context?.getString( R.string.label_user_name ) + data.row.createdAt + context?.getString( R.string.label_created_at )

        holder.userInfo.text = userInfo
        holder.likesCount.text = data.row.likesCount   // お気に入り数
        holder.commentCount.text = data.row.commentCount   // お気に入り数
        holder.tag.text = data.row.tags.split(",")[0]
        holder.updDate.text = data.row.updDate
        // 登録日を表示
        holder.updDateLabel.toggle(data.isFavorite)
        holder.updDate.toggle(data.isFavorite)
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
    fun refresh(list: List<ArticleRow>, isFavorite: Boolean) {
        val qiitaList : MutableList<QiitaData> = mutableListOf()
        list.forEach({ row -> qiitaList.add(QiitaData(row, isFavorite))})
        items.apply {
            clear()
            addAll(qiitaList)
        }
        notifyDataSetChanged()
    }

    /**
     * addItemsメソッド
     *
     * @param list
     */
    fun addItems(list: List<ArticleRow>, isFavorite: Boolean) {
        val qiitaList : MutableList<QiitaData> = mutableListOf()
        list.forEach({ row -> qiitaList.add(QiitaData(row, isFavorite))})
        items.apply {
            addAll(qiitaList)
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
        var userInfo = itemView.findViewById(R.id.userInfo) as  TextView
//        var tagGroup = itemView.findViewById(R.id.tagGroup) as TagView
        var tag = itemView.findViewById(R.id.articleTag) as TextView
        var likesCount = itemView.findViewById(R.id.likesCount) as TextView
//        var createdAt = itemView.findViewById(R.id.createdAt) as TextView
        var commentCount = itemView.findViewById(R.id.commentCount) as TextView
        var updDate = itemView.findViewById(R.id.updDate) as TextView
        var updDateLabel = itemView.findViewById(R.id.updDateLabel) as TextView
    }

    class QiitaData {
        var row : ArticleRow
        var isFavorite = false

        constructor(row: ArticleRow, isFavorite: Boolean) {
            this.row = row
            this.isFavorite = isFavorite
        }
    }
}