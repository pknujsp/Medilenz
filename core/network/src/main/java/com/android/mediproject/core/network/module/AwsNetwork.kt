package com.android.mediproject.core.network.module

import com.android.mediproject.core.common.BuildConfig
import com.android.mediproject.core.model.remote.sign.SignInResponse
import com.android.mediproject.core.model.remote.sign.SignUpResponse
import com.android.mediproject.core.model.remote.token.AccessTokenResponse
import com.android.mediproject.core.network.datasource.comments.CommentsDataSource
import com.android.mediproject.core.network.datasource.comments.CommentsDataSourceImpl
import com.android.mediproject.core.network.datasource.sign.SignDataSource
import com.android.mediproject.core.network.datasource.sign.SignDataSourceImpl
import com.android.mediproject.core.network.parameter.SignInRequestParameter
import com.android.mediproject.core.network.parameter.SignUpRequestParameter
import com.android.mediproject.core.network.tokens.TokenServer
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AwsNetwork {


    @Singleton
    @Provides
    @Named("awsNetworkApiWithAccessTokens")
    fun providesAwsNetworkApi(
        @Named("okHttpClientWithAccessTokens") okHttpClient: OkHttpClient,
    ): AwsNetworkApi = Retrofit.Builder().client(okHttpClient).addConverterFactory(
        Json.asConverterFactory("application/json".toMediaType())
    ).baseUrl(BuildConfig.AWS_BASE_URL).build().create(AwsNetworkApi::class.java)

    @Singleton
    @Provides
    @Named("awsNetworkApiWithRefreshTokens")
    fun providesReissueTokenAwsNetworkApi(
        @Named("okHttpClientWithReissueTokens") okHttpClient: OkHttpClient,
    ): AwsNetworkApi = Retrofit.Builder().client(okHttpClient).addConverterFactory(
        Json.asConverterFactory("application/json".toMediaType())
    ).baseUrl(BuildConfig.AWS_BASE_URL).build().create(AwsNetworkApi::class.java)

    @Provides
    @Singleton
    fun providesCommentsDataSource(awsNetworkApi: AwsNetworkApi): CommentsDataSource = CommentsDataSourceImpl(awsNetworkApi)

    @Provides
    @Singleton
    fun providesSignDataSource(awsNetworkApi: AwsNetworkApi, tokenServer: TokenServer): SignDataSource = SignDataSourceImpl(
        awsNetworkApi, tokenServer
    )
}

interface AwsNetworkApi {
    @POST(value = "user/register")
    suspend fun signUp(
        @Body signUpRequestParameter: SignUpRequestParameter
    ): Response<SignUpResponse>

    @POST(value = "user/login")
    suspend fun signIn(
        @Body signInRequestParameter: SignInRequestParameter
    ): Response<SignInResponse>


    @POST(value = "user/reissue")
    suspend fun reissueTokens(
    ): Response<AccessTokenResponse>
}