package me.rei_m.hbfavmaterial.presentation.fragment

import android.os.Bundle
import android.support.v7.widget.AppCompatTextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.ContributesAndroidInjector
import dagger.android.support.DaggerFragment
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.extension.getAppContext
import me.rei_m.hbfavmaterial.extension.openUrl
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import javax.inject.Inject

/**
 * このアプリについてを表示するFragment.
 */
class ExplainAppFragment : DaggerFragment() {

    companion object {
        fun newInstance() = ExplainAppFragment()
    }

    @Inject
    lateinit var navigator: Navigator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_explain_app, container, false)

        view.findViewById<View>(R.id.fragment_explain_app_layout_review).setOnClickListener {
            getAppContext()?.openUrl(getString(R.string.url_review))
        }

        view.findViewById<View>(R.id.fragment_explain_app_layout_opinion).setOnClickListener {
            getAppContext()?.openUrl(getString(R.string.url_opinion))
        }

        view.findViewById<View>(R.id.fragment_explain_app_layout_from_developer).setOnClickListener {
            navigator.navigateToFromDeveloper()
        }

        view.findViewById<View>(R.id.fragment_explain_app_layout_credit).setOnClickListener {
            navigator.navigateToCredit()
        }

        val versionName = getAppContext()?.packageManager?.getPackageInfo(context?.packageName, 0)?.versionName
        with(view.findViewById<View>(R.id.fragment_explain_app_text_version)) {
            this as AppCompatTextView
            text = "${getString(R.string.text_version)} : $versionName"
        }

        return view
    }

    @dagger.Module
    abstract inner class Module {
        @ForFragment
        @ContributesAndroidInjector
        internal abstract fun contributeInjector(): ExplainAppFragment
    }
}
