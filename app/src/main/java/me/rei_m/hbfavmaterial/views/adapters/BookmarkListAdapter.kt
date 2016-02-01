package me.rei_m.hbfavmaterial.views.adapters

import android.content.Context
import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.squareup.picasso.Picasso
import me.rei_m.hbfavmaterial.databinding.ListItemBookmarkBinding
import me.rei_m.hbfavmaterial.entities.BookmarkEntity
import me.rei_m.hbfavmaterial.extensions.toggle
import me.rei_m.hbfavmaterial.utils.BookmarkUtil
import me.rei_m.hbfavmaterial.views.widgets.graphics.RoundedTransformation

/**
 * ブックマーク一覧を管理するAdaptor.
 */
class BookmarkListAdapter(context: Context,
                          resource: Int) : ArrayAdapter<BookmarkEntity>(context, resource) {

    companion object {
        private val BOOKMARK_COUNT_PER_PAGE = 20
    }

    private val mLayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        return convertView?.apply {
            val binding = DataBindingUtil.getBinding<ListItemBookmarkBinding>(this)
            bindEntity(binding, getItem(position))
        } ?: let {
            val binding = ListItemBookmarkBinding.inflate(mLayoutInflater, parent, false)
            bindEntity(binding, getItem(position))
            return binding.root
        }
    }

    val nextIndex: Int
        get() {
            if (BOOKMARK_COUNT_PER_PAGE <= (count + 1)) {
                return count + 1
            } else {
                return BOOKMARK_COUNT_PER_PAGE + 1
            }
        }

    private fun bindEntity(binding: ListItemBookmarkBinding, bookmarkEntity: BookmarkEntity) {

        binding.let {
            it.bookmarkEntity = bookmarkEntity

            it.listItemBookmarkTextAddBookmarkTiming.text = BookmarkUtil.getPastTimeString(bookmarkEntity.date)

            Picasso.with(context)
                    .load(BookmarkUtil.getIconImageUrlFromId(bookmarkEntity.creator))
                    .transform(RoundedTransformation())
                    .into(it.listItemBookmarkImageUserIcon)
        }

        binding.listItemBookmarkLayoutBookmark.let {
            it.layoutBookmarkTextDescription.toggle(!bookmarkEntity.description.isEmpty())
            Picasso.with(context)
                    .load(bookmarkEntity.articleEntity.iconUrl)
                    .transform(RoundedTransformation())
                    .into(it.layoutBookmarkImageArticleIcon)
        }
    }
}
