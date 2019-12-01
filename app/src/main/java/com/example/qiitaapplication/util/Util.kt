package com.example.qiitaapplication.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

object Util {

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun ImageView.loadImage(url: String?) {
        Picasso.get().load(url).into(this)
    }

//    fun showErrorDialog(context: Context, titleRes: Int, messageRes: Int, page: Int) {
//        context?.also {
//            MaterialDialog(it)
//                .title(res = titleRes)
//                .message(res = messageRes)
//                .show {
//                    positiveButton(res = R.string.button_positive, click = {
//                        updateData(page) {
//                            customAdapter.addItems(it, false)
//                        }
//                    })
//                    negativeButton(res = R.string.button_negative)
//                }
//        }
//    }

}