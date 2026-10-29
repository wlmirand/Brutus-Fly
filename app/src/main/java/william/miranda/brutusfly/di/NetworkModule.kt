package william.miranda.brutusfly.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import william.miranda.brutusfly.data.remote.ConfiguredFireflyApi
import william.miranda.brutusfly.data.remote.FireflyApi
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(
            timeout = 30,
            unit = TimeUnit.SECONDS
        )
        .readTimeout(
            timeout = 30,
            unit = TimeUnit.SECONDS
        )
        .callTimeout(
            timeout = 30,
            unit = TimeUnit.SECONDS
        )
        .build()

    @Provides
    @Singleton
    fun provideFireflyApi(
        api: ConfiguredFireflyApi
    ): FireflyApi = api
}
