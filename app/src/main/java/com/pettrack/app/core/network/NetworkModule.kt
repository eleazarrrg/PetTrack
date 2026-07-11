package com.pettrack.app.core.network

import com.pettrack.app.BuildConfig
import com.pettrack.app.data.remote.api.AuthApi
import com.pettrack.app.data.remote.api.NotificationApi
import com.pettrack.app.data.remote.api.PetApi
import com.pettrack.app.data.remote.api.ProfileApi
import com.pettrack.app.data.remote.api.RpcApi
import com.pettrack.app.data.remote.api.StorageApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        coerceInputValues = true
    }

    @Provides
    @Named("supabaseUrl")
    fun provideSupabaseUrl(): String = BuildConfig.SUPABASE_URL

    /** Bare client with no interceptors/authenticator — used only for token refresh. */
    @Provides
    @Named("bareClient")
    @Singleton
    fun provideBareClient(): OkHttpClient = OkHttpClient.Builder().build()

    @Provides
    @Singleton
    fun provideOkHttp(
        headerInterceptor: HeaderInterceptor,
        tokenAuthenticator: TokenAuthenticator,
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .addInterceptor(headerInterceptor)
            .addInterceptor(logging)
            .authenticator(tokenAuthenticator)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, json: Json): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("${BuildConfig.SUPABASE_URL}/")
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideProfileApi(retrofit: Retrofit): ProfileApi = retrofit.create(ProfileApi::class.java)

    @Provides
    @Singleton
    fun providePetApi(retrofit: Retrofit): PetApi = retrofit.create(PetApi::class.java)

    @Provides
    @Singleton
    fun provideRpcApi(retrofit: Retrofit): RpcApi = retrofit.create(RpcApi::class.java)

    @Provides
    @Singleton
    fun provideStorageApi(retrofit: Retrofit): StorageApi = retrofit.create(StorageApi::class.java)

    @Provides
    @Singleton
    fun provideNotificationApi(retrofit: Retrofit): NotificationApi = retrofit.create(NotificationApi::class.java)
}
