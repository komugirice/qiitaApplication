package com.example.qiitaapplication.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.qiitaapplication.R
import com.example.qiitaapplication.fragment.ArticleFragment
import com.example.qiitaapplication.fragment.FavoriteFragment
import kotlinx.android.synthetic.main.activity_main.*


/**
 * メインアクティビティ.
 */
class MainActivity : AppCompatActivity() {

    private val customAdapter by lazy { CustomAdapter(supportFragmentManager) }

    /**
     * onCreateメソッド
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
     *
     * @param menu
     */
    override fun onCreateOptionsMenu(menu: Menu) : Boolean {
        // オプションメニュー用xmlファイルをインフレイト
        menuInflater.inflate(R.menu.menu_options_menu_list, menu)
        // 親クラスの同名メソッドを呼び出し、その戻り値を返却
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * onOptionsItemSelectedメソッド
     *
     * @param item
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            // 終了
            R.id.menuListOptionsFinish -> finish()
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

        override fun getCount(): Int = 2

        override fun getItem(position: Int): Fragment {
            when(position) {
                0 -> return ArticleFragment()
                1 -> return FavoriteFragment()
                else -> return ArticleFragment()
            }
        }
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