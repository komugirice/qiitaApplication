package com.example.qiitaapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cunoraz.tagview.Tag
import com.cunoraz.tagview.TagView
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
class ArticleAdapter(private val context: Context?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == VIEW_TYPE_ITEM) {
            // レイアウトインフレータを取得。
            val inflater = LayoutInflater.from(context)
            // row.xmlをインフレートし、1行分の画面部品とする。
            val view = inflater.inflate(R.layout.row, parent, false)
            // ビューホルダオブジェクトを生成。
            val holder = RowViewHolder(view)

            // クリックリスナを搭載
            view.setOnClickListener(object : View.OnClickListener {
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
            view.tagGroup.setOnTagClickListener { tag, position ->

                // 押下したタグごとに遷移
                val itemsPos = holder.adapterPosition // positionを取得
                // SearchActivityに遷移
//            val intent = Intent(context, SearchActivity::class.java)
//            intent.putExtra("query", tag.text)
//            intent.putExtra("searchType", SEARCH_TAG)
//            context?.startActivity(intent)
                (context as? Activity)?.also {
                    SearchActivity.start(it, tag.text, true)
                }

            }

            // 生成したビューホルダをリターン。
            return holder
        } else {
            return EmptyViewHolder(LayoutInflater.from(context).inflate(R.layout.empty_row, parent, false))
        }
    }

    /**
     * onBindViewHolderメソッド
     * ViewHolderへデータをバインドする
     *
     * @param holder
     * @param position
     *
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RowViewHolder)
            onBindViewHolder(holder, position)
    }

    private fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        val data = items[position]
        // プロフィール画像
        Picasso.get().load(data.row.profileImageUrl).into(holder.profileImage)
        // タイトル
        holder.articleTitle.text = data.row.title
        // ユーザ名 + " が" + 登録日 + " に投稿しました"
        var userInfo = if(data.row.userName.isEmpty()) "Non-Name" else data.row.userName.trim()
        userInfo += context?.getString( R.string.label_user_name ) + data.row.createdAt + context?.getString( R.string.label_created_at )
        holder.userInfo.text = userInfo
        // いいね数
        holder.likesCount.text = data.row.likesCount
        // コメント数
        holder.commentCount.text = data.row.commentCount
        // 登録日（お気に入り画面で使用）
        holder.updDate.text = data.row.updDate

        // タググループ 5個まで
        var tagList: MutableList<Tag> = mutableListOf()
        val tagStrList = data.row.tags.split(",").withIndex().map { if(it.index <= 4) it.value else "" }.filterNot { it.isEmpty() }
        tagStrList.forEach {
            // 色：黒、テキストサイズ：10、背景画像：ic_label_gray_24dp
            val tag: Tag = Tag(it)
            tag.tagTextColor = Color.BLACK
            tag.tagTextSize = 10.0f
            tag.background = context?.getDrawable(R.drawable.ic_label_gray_24dp)
            tagList.add(tag)
        }
        holder.tagGroup.addTags(tagList)

        // 登録日の表示切り替え
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
        return if (items.isEmpty()) 1 else items.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (items.isEmpty()) VIEW_TYPE_EMPTY else VIEW_TYPE_ITEM
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
     * clearメソッド
     *
     */
    fun clear() {
        items.apply {
            clear()
        }
        notifyDataSetChanged()
    }

    /**
     * RowViewHolderクラス
     * 画面の表示データを設定する
     *
     * @param itemView
     */
    class RowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // リスト1行分中でメニュー名を表示する画面部品
        var profileImage = itemView.findViewById(R.id.profileImage) as ImageView
        var articleTitle = itemView.findViewById(R.id.articleTitle) as TextView
        var userInfo = itemView.findViewById(R.id.userInfo) as  TextView
        var tagGroup = itemView.findViewById(R.id.tagGroup) as TagView
        var likesCount = itemView.findViewById(R.id.likesCount) as TextView
        var commentCount = itemView.findViewById(R.id.commentCount) as TextView
        var updDate = itemView.findViewById(R.id.updDate) as TextView
        var updDateLabel = itemView.findViewById(R.id.updDateLabel) as TextView
    }

    class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    /**
     * QiitaDataクラス
     * お気に入りから取得とそうでないものを区別する為に使う
     *
     */
    class QiitaData {
        var row : ArticleRow
        var isFavorite = false

        constructor(row: ArticleRow, isFavorite: Boolean) {
            this.row = row
            this.isFavorite = isFavorite
        }
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_ITEM = 1
    }
}