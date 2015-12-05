package me.rei_m.hbfavmaterial

import android.app.Application
import me.rei_m.hbfavmaterial.managers.ModelLocator
import me.rei_m.hbfavmaterial.models.*
import me.rei_m.hbfavmaterial.managers.ModelLocator.Companion.Tag as ModelTag

public class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Application起動時に実行される。アプリの初期処理など

        // ModelLocatorにModelの参照を登録
        ModelLocator.register(ModelTag.FAVORITE, BookmarkFavoriteModel())
        ModelLocator.register(ModelTag.OWN_BOOKMARK, BookmarkUserModel())
        ModelLocator.register(ModelTag.HOT_ENTRY, HotEntryModel())
        ModelLocator.register(ModelTag.NEW_ENTRY, NewEntryModel())
        ModelLocator.register(ModelTag.USER, UserModel(applicationContext))
        ModelLocator.register(ModelTag.OTHERS_BOOKMARK, BookmarkUserModel())
        ModelLocator.register(ModelTag.USER_REGISTER_BOOKMARK, UserRegisterBookmarkModel());
    }

    public fun resetBookmarks() {

        val favoriteModel = ModelLocator.get(ModelTag.FAVORITE) as BookmarkFavoriteModel
        val ownModel = ModelLocator.get(ModelTag.OWN_BOOKMARK) as BookmarkUserModel
        val hotEntryModel = ModelLocator.get(ModelTag.HOT_ENTRY) as HotEntryModel
        val newEntryModel = ModelLocator.get(ModelTag.NEW_ENTRY) as NewEntryModel

        favoriteModel.bookmarkList.clear()
        ownModel.bookmarkList.clear()
        hotEntryModel.entryList.clear()
        newEntryModel.entryList.clear()
    }
}