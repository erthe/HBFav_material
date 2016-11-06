package me.rei_m.hbfavmaterial.presentation.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.di.OAuthActivityModule
import me.rei_m.hbfavmaterial.extension.hide
import me.rei_m.hbfavmaterial.extension.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.infra.network.HatenaOAuthManager
import me.rei_m.hbfavmaterial.usecase.AuthorizeHatenaUsecase
import me.rei_m.hbfavmaterial.usecase.UnAuthorizeHatenaUsecase
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

class OAuthActivity : BaseSingleActivity() {

    companion object {

        const val ARG_AUTHORIZE_STATUS = "ARG_AUTHORIZE_STATUS"
        const val ARG_IS_AUTHORIZE_DONE = "ARG_IS_AUTHORIZE_DONE"

        fun createIntent(context: Context): Intent = Intent(context, OAuthActivity::class.java)
    }

    @Inject
    lateinit var authorizeHatenaUsecase: AuthorizeHatenaUsecase

    @Inject
    lateinit var unAuthorizeHatenaUsecase: UnAuthorizeHatenaUsecase

    private var subscription: CompositeSubscription? = null

    private var isLoading = false

    private val webView: WebView by lazy {
        WebView(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        subscription = CompositeSubscription()

        webView.apply {
            clearCache(true)
            settings.javaScriptEnabled = true
            setWebChromeClient(WebChromeClient())
            setWebViewClient(object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    if (url?.startsWith(HatenaOAuthManager.CALLBACK) ?: false) {
                        stopLoading()
                        hide()
                        val oauthVerifier = Uri.parse(url).getQueryParameter("oauth_verifier")
                        oauthVerifier ?: finish()
                        fetchAccessToken(oauthVerifier)
                    } else if (url?.startsWith(HatenaOAuthManager.AUTHORIZATION_DENY_URL) ?: false) {
                        stopLoading()
                        unAuthorizeHatenaUsecase.unAuthorize()
                        setAuthorizeResult(false, true)
                        finish()
                    } else {
                        super.onPageStarted(view, url, favicon)
                    }
                }

                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    return super.shouldOverrideUrlLoading(view, url)
                }
            })
        }

        with(findViewById(R.id.content) as FrameLayout) {
            addView(webView)
        }
        findViewById(R.id.fab)?.hide()
    }

    override fun onResume() {
        super.onResume()
        fetchRequestToken()
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription?.unsubscribe()
        subscription = null
    }

    private fun fetchRequestToken() {

        if (isLoading) return

        isLoading = true

        subscription?.add(authorizeHatenaUsecase.fetchRequestToken()
                .doOnUnsubscribe { isLoading = false }
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    webView.loadUrl(it)
                }, {
                    showSnackbarNetworkError()
                }))
    }

    private fun fetchAccessToken(oauthVerifier: String) {

        if (isLoading) return

        isLoading = true

        subscription?.add(authorizeHatenaUsecase.authorize(oauthVerifier)
                .doOnUnsubscribe { isLoading = false }
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    setAuthorizeResult(true, true)
                    finish()
                }, {
                    setAuthorizeResult(false, false)
                    finish()
                }))
    }

    private fun setAuthorizeResult(isAuthorize: Boolean, isDone: Boolean) {
        val intent = Intent().apply {
            putExtras(Bundle().apply {
                putBoolean(ARG_AUTHORIZE_STATUS, isAuthorize)
                putBoolean(ARG_IS_AUTHORIZE_DONE, isDone)
            })
        }
        setResult(RESULT_OK, intent)
    }

    override fun setupActivityComponent() {
        val component = (application as App).component
                .plus(OAuthActivityModule(this))

        component.inject(this)
    }
}
