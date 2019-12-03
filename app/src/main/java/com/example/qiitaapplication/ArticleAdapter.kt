package com.example.qiitaapplication

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cunoraz.tagview.Tag
import com.example.qiitaapplication.activity.SearchActivity
import com.example.qiitaapplication.activity.WebViewActivity
import com.example.qiitaapplication.databinding.RowBinding
import com.example.qiitaapplication.dataclass.ArticleRow
import com.example.qiitaapplication.extension.toggle


/**
 * ArticleAdapterクラス
 *
 */
class ArticleAdapter(private val context: Context?, private val isFavorite: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<QiitaData>()

    // スワイプ更新中に「検索結果が0件です」を出さない為の対応
    private var hasCompletedFirstRefresh = false

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
            // ビューホルダオブジェクトを生成。
            val holder = RowViewHolder(RowBinding.inflate(inflater, parent, false))

            // row.xmlをインフレートし、1行分の画面部品とする。
            // val view = inflater.inflate(R.layout.row, parent, false)

            // クリックリスナを搭載
            holder.binding.root.setOnClickListener(object : View.OnClickListener {
            //view.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {

                    val position = holder.adapterPosition // positionを取得
                    // クリック時の処理
                    val bundle = Bundle()
                    bundle.apply {
                        putString(WebViewActivity.KEY_ID, items[position].row.id)
                        putString(WebViewActivity.KEY_URL, items[position].row.url)
                        putString(WebViewActivity.KEY_TITLE, items[position].row.title)
                        putString(WebViewActivity.KEY_PROFILE_IMAGE_URL, items[position].row.profileImageUrl)
                        putString(WebViewActivity.KEY_USER_NAME, items[position].row.userName)
                        putString(WebViewActivity.KEY_CREATED_AT, items[position].row.createdAt)
                        putString(WebViewActivity.KEY_LIKES_COUNT, items[position].row.likesCount)
                        putString(WebViewActivity.KEY_COMMENT_COUNT, items[position].row.commentCount)
                        putString(WebViewActivity.KEY_TAGS, items[position].row.tags)

                    }

                    WebViewActivity.start(context, bundle)
                }
            })

            // タグのクリックリスナ
            holder.binding.tagGroup.setOnTagClickListener { tag, position ->

                // 押下したタグごとに遷移
                val itemsPos = holder.adapterPosition // positionを取得
                // SearchActivityに遷移
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
        else if (holder is EmptyViewHolder) {
            onBindEmptyViewHolder(holder, position)
        }
    }

    /**
     * itemsが取得成功のViewHolder
     *
     * @param holder
     * @param position
     */
    private fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        val data = items[position]

        // プロフィール画像
        // タイトル
        // いいね数
        // コメント数
        // 登録日（お気に入り画面で使用）
        //holder.binding.bindProfileImage = Picasso.get().load(data.row.profileImageUrl).get() → utilに移行
        holder.binding.articleRow = data.row

        // ユーザ名 + " が" + 登録日 + " に投稿しました"
        var userInfo = if(data.row.userName.isEmpty()) "Non-Name" else data.row.userName.trim()
        userInfo += context?.getString( R.string.label_user_name ) + data.row.createdAt + context?.getString( R.string.label_created_at )
        holder.binding.bindUserInfo = userInfo

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
        holder.binding.tagGroup.addTags(tagList)

        // 登録日の表示切り替え
        holder.binding.updDateLabel.toggle(data.isFavorite)
        holder.binding.updDate.toggle(data.isFavorite)
        //holder.rootView.setBackgroundColor(ContextCompat.getColor(context, if (position % 2 == 0) R.color.light_blue else R.color.light_yellow))
    }

    /**
     * itemsが0件のViewHolder
     *
     * @param holder
     * @param position
     */
    private fun onBindEmptyViewHolder(holder: EmptyViewHolder, position: Int) {
        if(isFavorite) {
            holder.searchZeroText.text = context?.getString(R.string.favorite_count_zero)
        } else {
            holder.searchZeroText.text = context?.getString(R.string.search_count_zero)
        }
    }

    /**
     * getItemCountメソッド
     *
     * @return Int
     */
    override fun getItemCount(): Int {
        // リストデータ中の件数をリターン。
        return if (items.isEmpty()) {
            if (hasCompletedFirstRefresh)
                1
            else
                0
        } else items.size
    }
    /**
     * itemsの数によってVIEW_TYPEを振り分け
     *
     * @param position
     * @return VIEW_TYPE: Int
     */

    override fun getItemViewType(position: Int): Int {
        return if (items.isEmpty() ) VIEW_TYPE_EMPTY else VIEW_TYPE_ITEM
    }

    /**
     * refreshメソッド
     *
     * @param qiitaList
     */
    fun refresh(qiitaList: List<QiitaData>) {
        // リフレッシュ実行フラグON
        hasCompletedFirstRefresh = true
        items.apply {
            clear()
            addAll(qiitaList)
        }

        notifyDataSetChanged()
    }

    /**
     * clearメソッド
     *
     */
    fun clear() {
        // 上スワイプで「検索結果0件」表示のバグ対応
        hasCompletedFirstRefresh = false
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
    class RowViewHolder(val binding: RowBinding): RecyclerView.ViewHolder(binding.root)


    /**
     * EmptyViewHolderクラス
     * 検索結果が0件の場合のViewHolder
     *
     * @param itemView
     */
    class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var searchZeroText = itemView.findViewById(R.id.searchZeroText) as TextView
    }

    /**
     * QiitaDataクラス
     * お気に入りから取得とそうでないものを区別する為に使う
     * ※ArticleAdapterコンストラクタの引数にisFavoriteを設定しているから不要だが、
     * 　一応記事にお気に入りアイコンつけるかもしれないので残す
     * 　
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

        private const val SEARCH_TAG = 1
    }
}