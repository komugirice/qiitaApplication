package com.example.qiitaapplication

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qiitaapplication.activity.SearchActivity
import com.example.qiitaapplication.activity.WebViewActivity
import com.example.qiitaapplication.dataclass.QiitaResponse
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * ArticleAdapterクラス
 *
 */
class ArticleAdapter(private val context: Context) : RecyclerView.Adapter<ArticleAdapter.RowViewHolder>() {
    private val SEARCH_TAG = 1

    private val items = mutableListOf<QiitaResponse>()

    /**
     * onCreateViewHolderメソッド
     *
     * @param parent
     * @param viewType
     * @return RowViewHolder
     *
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleAdapter.RowViewHolder {
        // レイアウトインフレータを取得。
        val inflater = LayoutInflater.from(context)
        // row.xmlをインフレートし、1行分の画面部品とする。
        val view = inflater.inflate(com.example.qiitaapplication.R.layout.row, parent, false)
        // ビューホルダオブジェクトを生成。
        val holder = RowViewHolder(view)

        // クリックリスナを搭載
        view.setOnClickListener (object : View.OnClickListener{
            override fun onClick(view: View) {

                val position = holder.adapterPosition // positionを取得
                // クリック時の処理
                val url = items[position].url
                val id = items[position].id
                val title = items[position].title
                val intent = Intent(context, WebViewActivity::class.java)
                intent.putExtra("url", url)
                intent.putExtra("id", id)
                intent.putExtra("title", title)
                context.startActivity(intent)
            }
        })

        // タグのクリックリスナ
        view.articleTag.setOnClickListener {

            val position = holder.adapterPosition // positionを取得
            // SearchActivityに遷移
            val intent = Intent(context, SearchActivity::class.java)
            // 押下したタグごとに取得
            intent.putExtra("query", items[position].tags[0].name)
            intent.putExtra("searchType", SEARCH_TAG)
            context.startActivity(intent)
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
    override fun onBindViewHolder(holder: ArticleAdapter.RowViewHolder, position: Int) {
        val data = items[position]
        // プロフィール画像
        Picasso.get().load(data.user.profile_image_url).into(holder.profileImage);

        holder.articleTitle.text = data.title   // タイトル
        holder.userName.text = if(data.user.name.isEmpty()) "Non-Name" else data.user.name.trim()   // ユーザ名
        holder.likesCount.text = data.likes_count.toString()   // お気に入り数
        holder.commentCount.text = data.comments_count.toString()   // お気に入り数
        holder.tag.text = data.tags[0].name.toString()

        // 作成日
        val existingUTCFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val requiredFormat = SimpleDateFormat("yyyy/MM/dd")
        val getDate = existingUTCFormat.parse(data.created_at)
        val dateStr = requiredFormat.format(getDate ?: Date())
        holder.createdAt.text = dateStr
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
    fun refresh(list: List<QiitaResponse>) {
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
    fun addItems(list: List<QiitaResponse>) {
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
        var profileImage = itemView.findViewById(com.example.qiitaapplication.R.id.profileImage) as ImageView
        var articleTitle = itemView.findViewById(com.example.qiitaapplication.R.id.articleTitle) as TextView
        var userName = itemView.findViewById(com.example.qiitaapplication.R.id.userName) as  TextView
        var likesCount = itemView.findViewById(com.example.qiitaapplication.R.id.likesCount) as TextView
        var createdAt = itemView.findViewById(com.example.qiitaapplication.R.id.createdAt) as TextView
        var commentCount = itemView.findViewById(com.example.qiitaapplication.R.id.commentCount) as TextView
        var tag = itemView.findViewById(com.example.qiitaapplication.R.id.articleTag) as TextView

    }
}