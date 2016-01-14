package me.rei_m.hbfavmaterial.network

import com.google.gson.Gson
import me.rei_m.hbfavmaterial.entities.OAuthTokenEntity
import me.rei_m.hbfavmaterial.exeptions.HTTPException
import me.rei_m.hbfavmaterial.network.response.HatenaRestApiBookmarkResponse
import oauth.signpost.basic.DefaultOAuthConsumer
import oauth.signpost.basic.DefaultOAuthProvider
import rx.Observable
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

/**
 * はてなのOAuth認証関連のAPIを管理するクラス.
 */
class HatenaOAuthApi(consumerKey: String, consumerSecret: String) {

    private val mOAuthConsumer = DefaultOAuthConsumer(consumerKey, consumerSecret)

    private val mOAuthProvider = DefaultOAuthProvider(REQUEST_TOKEN_ENDPOINT_URL,
            ACCESS_TOKEN_ENDPOINT_URL,
            AUTHORIZATION_WEBSITE_URL)

    companion object {

        public val CALLBACK = "https://github.com/rei-m/HBFav_material"

        private val REQUEST_TOKEN_ENDPOINT_URL = "https://www.hatena.com/oauth/initiate?scope=read_public,write_public"

        private val ACCESS_TOKEN_ENDPOINT_URL = "https://www.hatena.com/oauth/token"

        private val AUTHORIZATION_WEBSITE_URL = "https://www.hatena.ne.jp/touch/oauth/authorize"

        private val BOOKMARK_ENDPOINT_URL = "http://api.b.hatena.ne.jp/1/my/bookmark"

        public val AUTHORIZATION_DENY_URL = "$AUTHORIZATION_WEBSITE_URL.deny"

        private val TWO_HYPHEN = "--"
        private val EOL = "\r\n"
        private val BOUNDARY = Random().hashCode()
        private val CHARSET = "UTF-8"
    }

    /**
     * リクエストトークンを取得する.
     */
    fun requestRequestToken(): Observable<String> {

        return Observable.create { t ->

            val authUrl = mOAuthProvider.retrieveRequestToken(mOAuthConsumer, CALLBACK)

            if (authUrl != null) {
                t.onNext(authUrl)
            } else {
                t.onError(HTTPException(HttpURLConnection.HTTP_UNAUTHORIZED))
            }

            t.onCompleted()
        }
    }

    /**
     * アクセストークンを取得する.
     */
    fun requestAccessToken(requestToken: String): Observable<OAuthTokenEntity> {

        return Observable.create { t ->

            mOAuthProvider.retrieveAccessToken(mOAuthConsumer, requestToken)

            if (mOAuthConsumer.token != null && mOAuthConsumer.tokenSecret != null) {
                t.onNext(OAuthTokenEntity(mOAuthConsumer.token, mOAuthConsumer.tokenSecret))
            } else {
                t.onError(HTTPException(HttpURLConnection.HTTP_UNAUTHORIZED))
            }

            t.onCompleted()
        }
    }

    /**
     * 指定されたURLのブックマーク情報を取得する.
     */
    fun getBookmark(oauthToken: OAuthTokenEntity,
                    urlString: String): Observable<HatenaRestApiBookmarkResponse> {

        mOAuthConsumer.setTokenWithSecret(oauthToken.token, oauthToken.secretToken)

        return Observable.create { t ->

            val url = URL("$BOOKMARK_ENDPOINT_URL?url=$urlString")
            val connection = url.openConnection() as HttpURLConnection

            mOAuthConsumer.sign(connection)

            connection.connect()

            when (connection.responseCode) {
                HttpURLConnection.HTTP_OK -> {
                    val response = Gson().fromJson(readStream(connection.inputStream), HatenaRestApiBookmarkResponse::class.java)
                    connection.disconnect()
                    t.onNext(response)
                }
                else -> {
                    connection.disconnect()
                    t.onError(HTTPException(connection.responseCode))
                }
            }

            t.onCompleted()
        }
    }

    /**
     * ブックマーク情報を更新する.
     */
    fun postBookmark(oauthToken: OAuthTokenEntity,
                     urlString: String,
                     comment: String,
                     isOpen: Boolean): Observable<HatenaRestApiBookmarkResponse> {

        mOAuthConsumer.setTokenWithSecret(oauthToken.token, oauthToken.secretToken)

        return Observable.create { t ->

            // Content作成開始
            val sb = StringBuilder()

            sb.append(EOL)

            // multipart/form-dataでパラメータ作成
            sb.append(createFormDataParameter("url", urlString))
                    .append(createFormDataParameter("comment", comment))
                    .append(createFormDataParameter("private", if (isOpen) "0" else "1"))

            // RequestHeaderに設定するためPostデータのLengthを取得
            var contentLength = sb.toString().toByteArray(CHARSET).size

            // Postデータにフッタ追加
            sb.append("$TWO_HYPHEN$BOUNDARY$TWO_HYPHEN$EOL")

            // コネクション取得
            val connection = createPostConnection(BOOKMARK_ENDPOINT_URL, contentLength)

            // OAuth認証
            mOAuthConsumer.sign(connection)

            // Postデータ書き込み
            DataOutputStream(connection.outputStream).apply {
                write(sb.toString().toByteArray(CHARSET))
                flush()
                close()
            }

            when (connection.responseCode) {
                HttpURLConnection.HTTP_OK -> {
                    val response = Gson().fromJson(readStream(connection.inputStream), HatenaRestApiBookmarkResponse::class.java)
                    connection.disconnect()
                    t.onNext(response)
                }
                else -> {
                    connection.disconnect()
                    t.onError(HTTPException(connection.responseCode))
                }
            }

            t.onCompleted()
        }
    }

    /**
     * ブックマーク情報を削除する.
     */
    fun deleteBookmark(oauthToken: OAuthTokenEntity, urlString: String): Observable<Boolean> {

        mOAuthConsumer.setTokenWithSecret(oauthToken.token, oauthToken.secretToken)

        return Observable.create { t ->

            val url = URL("$BOOKMARK_ENDPOINT_URL?url=$urlString")
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "DELETE"

            mOAuthConsumer.sign(connection)

            connection.connect()

            when (connection.responseCode) {
                HttpURLConnection.HTTP_NO_CONTENT -> {
                    connection.disconnect()
                    t.onNext(true)
                }
                else -> {
                    connection.disconnect()
                    t.onError(HTTPException(connection.responseCode))
                }
            }

            t.onCompleted()
        }
    }

    /**
     * POST用のパラメータ文字列を作成する.
     */
    private fun createFormDataParameter(key: String, value: String): String {
        val sb = StringBuilder()
        sb.append("$TWO_HYPHEN$BOUNDARY$EOL")
                .append("Content-Disposition: form-data; name=\"$key\"$EOL")
                .append(EOL)
                .append("$value$EOL")

        return sb.toString()
    }

    /**
     * POST用のURLConnectionを作成する.
     */
    private fun createPostConnection(urlString: String, contentLength: Int): HttpURLConnection {

        val url = URL(urlString)

        val connection = url.openConnection() as HttpURLConnection
        return connection.apply {
            requestMethod = "POST"
            doInput = true
            doOutput = true
            readTimeout = 10 * 1000
            connectTimeout = 10 * 1000
            useCaches = false
            setChunkedStreamingMode(0)

            addRequestProperty("Connection", "Keep-Alive")
            addRequestProperty("Content-Type", "multipart/form-data; boundary=$BOUNDARY")
            addRequestProperty("Content-Length", contentLength.toString())
        }
    }

    /**
     * APIのリクエストのStreamを読み込む.
     */
    private fun readStream(stream: InputStream): String {

        val sb = StringBuilder()

        val br = BufferedReader(InputStreamReader(stream))

        var line = br.readLine()
        while (line != null) {
            sb.append(line)
            line = br.readLine()
        }

        stream.close()

        return sb.toString()
    }
}
