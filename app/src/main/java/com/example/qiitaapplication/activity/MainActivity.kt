package com.example.qiitaapplication.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.qiitaapplication.R
import com.example.qiitaapplication.extension.toggle
import com.example.qiitaapplication.fragment.ArticleFragment
import com.example.qiitaapplication.fragment.FavoriteFragment
import kotlinx.android.synthetic.main.activity_main.*






/**
 * メインアクティビティ.
 */
class MainActivity : AppCompatActivity() {

    val SEARCH_BODY = 0

    private val customAdapter by lazy { CustomAdapter(this, supportFragmentManager) }

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
        initClick()
        initEditText()
        //initToolbar()
        initViewPager()
        initTabLayout()
    }

    private fun initClick() {
        searchImageView.setOnClickListener {
            changeSearchView(true)
            // Edit Textにフォーカス設定
            searchEditText.setFocusable(true)
            searchEditText.setFocusableInTouchMode(true)
            searchEditText.requestFocus()
            showKeybord()
        }
        deleteImageView.setOnClickListener {
//            if (searchEditText.text.isEmpty())
//                changeSearchView(false)
//            else
//                searchEditText.setText("")

            searchEditText.setText("")
            changeSearchView(false)
        }
    }

    private fun changeSearchView(isSearch: Boolean) {
        headerTextView.toggle(!isSearch)
        searchImageView.toggle(!isSearch)
        headerSearchView.toggle(isSearch)
        if (!isSearch)
            hideKeybord()
    }

    private fun initEditText() {
        // 検索実行
        searchEditText.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // EditTextに値がある場合
                if(!textView.text.toString().isEmpty()) {
                    // SearchActivityに遷移
                    val intent = Intent(this@MainActivity, SearchActivity::class.java)
                    intent.putExtra("query", textView.text.toString())
                    intent.putExtra("searchType", SEARCH_BODY)
                    // SearchActivityに遷移
                    startActivity(intent)
                }
                //true
            }
            false
        }
    }


    /**
     * initToolbarメソッド
     *
     */
//    private fun initToolbar() {
//        // アクションバーにツールバーを設定
//        setSupportActionBar(toolber)
//        supportActionBar?.setDisplayShowTitleEnabled(false)
//    }


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
                    searchView.onActionViewCollapsed()
                    // Focusを外す
                    searchView.clearFocus()
                }
                return  false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
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
            addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
                override fun onPageSelected(position: Int) {
                    hideKeybord()
                }
            })
        }
    }

    private fun hideKeybord() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(searchEditText.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun showKeybord() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(searchEditText, 0)
    }

    private fun initTabLayout() {
        tabLayout.setupWithViewPager(viewPager)
    }

    class CustomAdapter(private val context: Context, fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager) {

        inner class Item(val fragment: Fragment, val title:Int)

        val fragments = listOf(Item(ArticleFragment(), R.string.tab_article)
            , Item(FavoriteFragment(), R.string.tab_favorite))

        override fun getCount(): Int = 2

        override fun getItem(position: Int) = fragments[position].fragment

        override fun getPageTitle(position: Int) = context.getString( fragments[position].title )
    }

}