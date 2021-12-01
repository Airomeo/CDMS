package app.i.cdms.api

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.widget.Toast
import app.i.cdms.BuildConfig.DEBUG
import app.i.cdms.Constant
import app.i.cdms.data.model.ApiResultJsonAdapter
import app.i.cdms.data.model.Token
import app.i.cdms.data.source.local.UserPrefDataSource
import app.i.cdms.utils.Event
import app.i.cdms.utils.EventBus
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


/**
 * @author ZZY
 * 2021/10/18.
 */
object ApiClient {
    fun create(context: Context, userPrefDataSource: UserPrefDataSource): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = if (DEBUG) Level.BODY else Level.NONE

        val okHttpClient = OkHttpClient().newBuilder()
            .addInterceptor(CacheInterceptor(context))
            .addInterceptor(TokenInterceptor(userPrefDataSource.tokenFlow))
            .addInterceptor(loggingInterceptor)
            .retryOnConnectionFailure(true)//默认重试一次，若需要重试N次，则要实现拦截器。
            .cache(Cache(File(context.cacheDir, "ResponsesCache"), (30 * 1024 * 1024).toLong()))
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://localhost/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()

        return retrofit.create(ApiService::class.java)
    }

    class CacheInterceptor(private val context: Context) : Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            var request = chain.request()

            if (!context.isInternetAvailable()) {
                Toast.makeText(context, "InternetUnavailable", Toast.LENGTH_SHORT).show()
                request = request.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + 2419200)
                    .build()
            }

            return chain.proceed(request)
        }
    }

    class TokenInterceptor(private val tokenFlow: Flow<Token>) :
        Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            var request = chain.request()

            val ignore = listOf(Constant.API_GET_CAPTCHA, Constant.API_LOGIN)
            if (request.url.toString() !in ignore) {

                val token = runBlocking {
                    tokenFlow.first()
                }
                Log.d("TAG", "intercept runBlocking token: $token")

                if (token.token.isBlank()) {
                    Log.d("TAG", "ApiClient token null. Cancel request")
                    // EventBus.produceEvent(Event.NeedLogin)
                    // cancel request
                    chain.call().cancel()
                }
                request = request.newBuilder()
                    .addHeader("authorization", "Bearer ${token.token}")
                    .build()

                val response = chain.proceed(request)
                val body = response.peekBody(Long.MAX_VALUE)
                if (response.isSuccessful && body != null) {
                    val apiResult = ApiResultJsonAdapter<Any>(
                        Moshi.Builder().build(),
                        arrayOf(Any::class.java)
                    ).fromJson(body.string())
                    if (apiResult != null && apiResult.code == 401) {
                        Log.d("TAG", "intercept: Go to Login Fragment ")
                        // TODO: 2021/11/16 Go to Login Fragment
                        EventBus.produceEvent(Event.NeedLogin)
                    }
                }
                return response
            }

            return chain.proceed(request)
        }
    }
}


fun Context.isInternetAvailable(): Boolean {
    var result = false
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connectivityManager.run {
            connectivityManager.activeNetworkInfo?.run {
                result = when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
    }

    return result
}