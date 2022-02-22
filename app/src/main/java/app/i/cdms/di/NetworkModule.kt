package app.i.cdms.di

import android.content.Context
import android.util.Log
import app.i.cdms.BuildConfig
import app.i.cdms.Constant
import app.i.cdms.api.ApiService
import app.i.cdms.data.model.Token
import app.i.cdms.data.source.local.UserPrefDataSource
import app.i.cdms.utils.isInternetAvailable
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * @author ZZY
 * 2021/12/20.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class InterceptorOkHttpClient

    @Singleton
    @Provides
    fun provideApiService(
        // Potential dependencies of this type
        @InterceptorOkHttpClient okHttpClient: OkHttpClient
    ): ApiService {
        return Retrofit.Builder()
            .baseUrl("https://localhost/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(ApiService::class.java)
    }

    @Singleton
    @InterceptorOkHttpClient
    @Provides
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        userPrefDataSource: UserPrefDataSource
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) {
                level = HttpLoggingInterceptor.Level.BODY
            }
        }

        return OkHttpClient().newBuilder()
            .addInterceptor(CacheInterceptor(context))
            .addInterceptor(TokenInterceptor(userPrefDataSource.tokenFlow))
            .addInterceptor(loggingInterceptor)
            .retryOnConnectionFailure(true)//默认重试一次，若需要重试N次，则要实现拦截器。
            .cache(Cache(File(context.cacheDir, "ResponsesCache"), (30 * 1024 * 1024).toLong()))
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    class CacheInterceptor(private val context: Context) : Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            var request = chain.request()

            if (!context.isInternetAvailable()) {
                Log.d("TAG", "intercept: InternetUnavailable")
//                EventBus.produceEvent(BaseEvent.Failed("网络异常"))
                request = request.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + 2419200)
                    .build()
            }

            return chain.proceed(request)
        }
    }

    class TokenInterceptor(private val tokenFlow: Flow<Token>) : Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            var request = chain.request()

            val ignored = Constant.ignoreTokenList.any {
                request.url.toString().contains(it)
            }
            if (!ignored) {
                val token = runBlocking {
                    tokenFlow.first()
                }

                if (token.token.isBlank()) {
                    Log.d("TAG", "ApiClient token null. Cancel request")
                    // EventBus.produceEvent(Event.NeedLogin)
                    // cancel request
                    chain.call().cancel()
                }
                request = request.newBuilder()
                    .addHeader("authorization", "Bearer ${token.token}")
                    .build()
            }

            return chain.proceed(request)
        }
    }
}