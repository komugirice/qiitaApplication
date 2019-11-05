package com.example.qiitaapplication.activity

import android.os.Bundle
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
        initClick()
        initViewPager()
        initTabLayout()
    }

    /**
     * initClickメソッド
     *
     */
    private fun initClick() {
        closeImageView.setOnClickListener {
            finish()
        }
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