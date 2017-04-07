package me.rei_m.hbfavmaterial.constant

import me.rei_m.hbfavmaterial.R

/**
 * 後で見るのフィルタ.
 */
enum class ReadAfterFilter(override val menuId: Int,
                           override val titleResId: Int) : FilterItem {
    ALL(
            R.id.fragment_bookmark_user_menu_filter_bookmark_all,
            R.string.filter_bookmark_users_all
    ),
    AFTER_READ(
            R.id.fragment_bookmark_user_menu_filter_bookmark_read_after,
            R.string.text_read_after
    );

    companion object {
        fun forMenuId(menuId: Int): ReadAfterFilter {
            values().filter { it.menuId == menuId }.forEach { return it }
            throw AssertionError("no enum found for the id. you forgot to implement?")
        }
    }
}