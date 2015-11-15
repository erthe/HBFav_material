package me.rei_m.hbfavkotlin.views.widgets.list

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.widget.RelativeLayout

import com.squareup.picasso.Picasso

import me.rei_m.hbfavkotlin.R
import me.rei_m.hbfavkotlin.models.Bookmark
import me.rei_m.hbfavkotlin.utils.BookmarkUtils
import me.rei_m.hbfavkotlin.views.widgets.bookmark.BookmarkLayout
import me.rei_m.hbfavkotlin.views.widgets.graphics.RoundedTransformation

class FavoriteItemLayout : RelativeLayout {

    companion object {
        private class ViewHolder {
            var name: AppCompatTextView? = null
            var iconImage: AppCompatImageView? = null
            var bookmarkLayout: BookmarkLayout? = null
            var timing: AppCompatTextView? = null
        }
    }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onFinishInflate() {
        super.onFinishInflate()
        val holder = ViewHolder()
        holder.name = findViewById(R.id.text_user_name) as AppCompatTextView
        holder.iconImage = findViewById(R.id.image_user_icon) as AppCompatImageView
        holder.bookmarkLayout = findViewById(R.id.layout_bookmark) as BookmarkLayout
        holder.timing = findViewById(R.id.text_add_bookmark_timing) as AppCompatTextView
        tag = holder
    }

    fun bindView(bookmark: Bookmark) {
        val holder = tag as ViewHolder
        holder.name?.text = bookmark.creator
        Picasso.with(context)
                .load(bookmark.bookmarkIconUrl)
                .transform(RoundedTransformation())
                .into(holder.iconImage)
        holder.bookmarkLayout?.bindView(bookmark)
        holder.timing?.text = BookmarkUtils.getAddBookmarkTimeString(bookmark.date)
    }
}