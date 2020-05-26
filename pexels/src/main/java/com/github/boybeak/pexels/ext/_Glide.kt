package com.github.boybeak.pexels.ext

import android.content.Context
import android.graphics.Bitmap
import android.text.TextUtils
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.TransitionOptions
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.bumptech.glide.request.target.Target
import com.github.boybeak.pexels.R
import org.jetbrains.anko.dip
import java.io.File

private const val TAG = "_Glide"

private val centerCropTrans = CenterCrop()

private val transWithBlur = bitmapTransform(MultiTransformation<Bitmap>(
    centerCropTrans
))
private val transNoBlur = bitmapTransform(centerCropTrans)
private val circleCrop = RequestOptions.circleCropTransform()

fun ImageView.thumb(
    url: String, roundedCorners: Boolean = false,
    onReady: ((bmp: Bitmap) -> Unit)? = null
) {
    val headers = LazyHeaders.Builder()
        .build()
    thumb(Glide.with(this).asBitmap().load(GlideUrl(url, headers)), roundedCorners, onReady)
}

fun ImageView.thumb(
    file: File, roundedCorners: Boolean = false,
    onReady: ((bmp: Bitmap) -> Unit)? = null
) {
    thumb(Glide.with(this).asBitmap().load(file), roundedCorners, onReady)
}

fun ImageView.thumb(
    @DrawableRes drawableRes: Int, roundedCorners: Boolean = false,
    onReady: ((bmp: Bitmap) -> Unit)? = null
) {
    thumb(Glide.with(this).asBitmap().load(drawableRes), roundedCorners, onReady)
}

private fun ImageView.thumb(
    requestBuilder: RequestBuilder<Bitmap>, roundedCorners: Boolean = false,
    onReady: ((bmp: Bitmap) -> Unit)? = null
) {
    val transformations = ArrayList<Transformation<Bitmap>>()
    transformations.add(centerCropTrans)
    if (roundedCorners) {
        transformations.add(RoundedCorners(dip(4)))
    }
    requestBuilder/*.placeholder(android.R.color.darker_gray)*/
        .placeholder(R.drawable.ic_launcher_background)
        .apply(
            bitmapTransform(MultiTransformation(transformations))
        )
//        .transition(BitmapTransitionOptions.withCrossFade())
        .apply {
            if (onReady != null) {
                addListener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Bitmap?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        resource?.run {
                            onReady.invoke(this)
                        }
                        return false
                    }
                })
            }
        }
        .into(this)
}

fun ImageView.round(@DrawableRes res: Int) {
    Glide.with(this).asBitmap()
        .circleCrop()
        .load(res)
        .into(this)
}