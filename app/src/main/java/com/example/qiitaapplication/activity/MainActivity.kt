package com.example.qiitaapplication.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.qiitaapplication.fragment.ArticleFragment
import com.example.qiitaapplication.fragment.FavoriteFragment
import com.example.qiitaapplication.fragment.SearchActivity
import kotlinx.android.synthetic.main.activity_main.*






/**
 * メインアクティビティ.
 */
class MainActivity : AppCompatActivity() {

    val SEARCH_BODY = 0

    private val customAdapter by lazy { CustomAdapter(supportFragmentManager) }

    /**
     * onCreateメソッド
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.qiitaapplication.R.layout.activity_main)

        initialize()
    }

    /**
     * initializeメソッド
     *
     */
    private fun initialize() {
        initLayout()
    }
    /**
     * initLayoutメソッド
     *
     */
    private fun initLayout() {
        initToolbar()
        initViewPager()
        initTabLayout()
    }


    /**
     * initToolbarメソッド
     *
     */
    private fun initToolbar() {
        // アクションバーにツールバーを設定
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }


    /**
     * onCreateOptionsMenuメソッド
     * menuのメソッド
     *
     * @param menu
     */
    override fun onCreateOptionsMenu(menu: Menu) : Boolean {
        // オプションメニュー用xmlファイルをインフレイト
        menuInflater.inflate(com.example.qiitaapplication.R.menu.menu_options_menu_list, menu)

        //=== searchView設定 ===
        val searchItem = menu.findItem(com.example.qiitaapplication.R.id.searchView) as MenuItem
        val searchView = searchItem.getActionView() as SearchView

        // SearchViewに何も入力していない時のテキストを設定
        searchView.setQueryHint(this.getResources().getString(com.example.qiitaapplication.R.string.label_search_title))

        // イベントリスナ設定
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // 検索ボタン押下時
            override fun onQueryTextSubmit(query: String?): Boolean {
                // 検索バーに入力がある場合
                if(!query!!.isEmpty()) {

                    // SearchActivityに遷移
                    val intent = Intent(this@MainActivity, SearchActivity::class.java)
                    intent.putExtra("query", query)
                    intent.putExtra("searchType", SEARCH_BODY)
                    startActivity(intent)

                    // SearchViewを隠す
                    searchView.onActionViewCollapsed();
                    // Focusを外す
                    searchView.clearFocus();
                }
                return  false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true;
            }

        })


        // 親クラスの同名メソッドを呼び出し、その戻り値を返却
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * onOptionsItemSelectedメソッド
     * menuのメソッド
     *
     * @param item
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            // 終了
            com.example.qiitaapplication.R.id.menuListOptionsFinish -> finish()
        }

        return super.onOptionsItemSelected(item)

    }

    private fun initViewPager() {
        viewPager.apply {
            adapter = customAdapter
            offscreenPageLimit = customAdapter.count
        }
    }

    private fun initTabLayout() {
        tabLayout.setupWithViewPager(viewPager)
    }

    class CustomAdapter(fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager) {

        val fragments = listOf(ArticleFragment(), FavoriteFragment())

        override fun getCount(): Int = 2

        override fun getItem(position: Int) = fragments[position]

        override fun getPageTitle(position: Int): CharSequence {
            when(position) {
                0 -> {
                    return "記事一覧"
                }
                1 -> {
                    return "お気に入り"
                }
                else -> {
                    return ""
                }
            }
        }
    }

}