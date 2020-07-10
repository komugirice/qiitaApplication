package com.komugirice.qiitaapplication.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

object Util {

    /**
     * xmlでImageViewに:imageUrlを設定すると画像が取得できる
     *
     * @param url
     *
     */
    @JvmStatic
    @BindingAdapter("imageUrl")
    fun ImageView.loadImage(url: String?) {
        Picasso.get().load(url).into(this)
    }

}