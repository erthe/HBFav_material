//package me.rei_m.hbfavmaterial.presentation.fragment
//
//import android.support.v4.widget.SwipeRefreshLayout
//import android.view.View
//import android.widget.ListView
//import android.widget.ProgressBar
//import android.widget.TextView
//import me.rei_m.hbfavmaterial.R
//import me.rei_m.hbfavmaterial.domain.entity.BookmarkEntity
//import me.rei_m.hbfavmaterial.enum.BookmarkCommentFilter
//import me.rei_m.hbfavmaterial.testutil.DriverActivity
//import me.rei_m.hbfavmaterial.testutil.TestUtil
//import me.rei_m.hbfavmaterial.testutil.bindView
//import me.rei_m.hbfavmaterial.presentation.view.adapter.UserListAdapter
//import org.hamcrest.CoreMatchers.`is`
//import org.junit.Assert.assertThat
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.mockito.Mockito.spy
//import org.mockito.Mockito.verify
//import org.robolectric.RobolectricTestRunner
//import org.robolectric.shadows.support.v4.SupportFragmentTestUtil
//
//@RunWith(RobolectricTestRunner::class)
//class BookmarkedUsersFragmentTest {
//
//    lateinit var fragment: BookmarkedUsersFragment
//
//    private val holder: ViewHolder by lazy {
//        val view = fragment.view ?: throw IllegalStateException("fragment's view is Null")
//        ViewHolder(view)
//    }
//
//    private val snackbarTextView: TextView
//        get() = fragment.activity.findViewById(android.support.design.R.id.snackbar_text) as TextView
//
//    private fun getString(resId: Int): String {
//        return fragment.getString(resId)
//    }
//
//    private val bookmarkEntity = TestUtil.createTestBookmarkEntity(0)
//
//    @Before
//    fun setUp() {
//
//        fragment = BookmarkedUsersFragment.newInstance(bookmarkEntity)
//
//        SupportFragmentTestUtil.startFragment(fragment, CustomDriverActivity::class.java)
//    }
//
//    @Test
//    fun testInitialize() {
//        assertThat(holder.listView.visibility, `is`(View.VISIBLE))
//        assertThat(holder.layoutRefresh.visibility, `is`(View.VISIBLE))
//        assertThat(holder.textEmpty.visibility, `is`(View.GONE))
//        assertThat(fragment.hasOptionsMenu(), `is`(true))
//    }
//
//    @Test
//    fun testShowHideUserList() {
//
//        val bookmarkList = arrayListOf<BookmarkEntity>().apply {
//            add(TestUtil.createTestBookmarkEntity(1))
//            add(TestUtil.createTestBookmarkEntity(2))
//            add(TestUtil.createTestBookmarkEntity(3))
//            add(TestUtil.createTestBookmarkEntity(4))
//        }
//
//        holder.listView.visibility = View.GONE
//        holder.layoutRefresh.isRefreshing = true
//
//        fragment.showUserList(bookmarkList)
//
//        val adapter = holder.listView.adapter as UserListAdapter
//
//        assertThat(holder.listView.visibility, `is`(View.VISIBLE))
//        assertThat(adapter.count, `is`(4))
//        assertThat(adapter.getItem(0), `is`(bookmarkList[0]))
//        assertThat(adapter.getItem(3), `is`(bookmarkList[3]))
//
//        assertThat(holder.layoutRefresh.isRefreshing, `is`(false))
//
//        val activity = fragment.activity as CustomDriverActivity
//        assertThat(activity.bookmarkCommentFilter, `is`(fragment.presenter.bookmarkCommentFilter))
//
//        fragment.hideUserList()
//        assertThat(holder.listView.visibility, `is`(View.GONE))
//    }
//
//    @Test
//    fun testShowNetworkErrorMessage() {
//        fragment.showNetworkErrorMessage()
//        assertThat(snackbarTextView.visibility, `is`(View.VISIBLE))
//        assertThat(snackbarTextView.text.toString(), `is`(getString(R.string.message_error_network)))
//    }
//
//    @Test
//    fun testShowHideProgress() {
//        holder.progressBar.visibility = View.GONE
//
//        fragment.showProgress()
//        assertThat(holder.progressBar.visibility, `is`(View.VISIBLE))
//
//        fragment.hideProgress()
//        assertThat(holder.progressBar.visibility, `is`(View.GONE))
//    }
//
//    @Test
//    fun testShowHideEmpty() {
//        holder.textEmpty.visibility = View.GONE
//
//        fragment.showEmpty()
//        assertThat(holder.textEmpty.visibility, `is`(View.VISIBLE))
//
//        fragment.hideEmpty()
//        assertThat(holder.textEmpty.visibility, `is`(View.GONE))
//    }
//
//    @Test
//    fun testNavigateToOthersBookmark() {
//        val navigator = spy(fragment.activityNavigator)
//        fragment.activityNavigator = navigator
//        fragment.navigateToOthersBookmark(bookmarkEntity)
//        verify(navigator).navigateToOthersBookmark(fragment.activity, bookmarkEntity.creator)
//    }
//
//    class CustomDriverActivity : DriverActivity(),
//            BookmarkedUsersFragment.OnFragmentInteractionListener {
//
//        var bookmarkCommentFilter: BookmarkCommentFilter? = null
//
//        override fun onChangeFilter(bookmarkCommentFilter: BookmarkCommentFilter) {
//            this.bookmarkCommentFilter = bookmarkCommentFilter
//        }
//    }
//
//    class ViewHolder(view: View) {
//        val listView by view.bindView<ListView>(R.id.fragment_list_list)
//        val layoutRefresh by view.bindView<SwipeRefreshLayout>(R.id.fragment_list_refresh)
//        val textEmpty by view.bindView<TextView>(R.id.fragment_list_view_empty)
//        val progressBar by view.bindView<ProgressBar>(R.id.fragment_list_progress_list)
//    }
//}
