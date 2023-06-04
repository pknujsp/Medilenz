package com.android.mediproject.core.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.android.mediproject.core.common.util.AesCoder
import com.android.mediproject.core.model.remote.token.CurrentTokenDto
import com.android.mediproject.core.model.remote.token.NewTokensFromAws
import com.android.mediproject.core.model.remote.token.RequestBehavior
import com.android.mediproject.core.model.remote.token.TokenState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * 로컬에 토큰 정보를 저장/삭제하는 클래스
 *
 * @property tokenDataStore
 * @property aesCoder
 * @property tokenServer
 *
 * 로컬 토큰 서버에 토큰을 저장/삭제 하고,
 * 보조 기억 장치에 토큰을 저장/삭제 한다.
 */
class TokenDataSourceImpl @Inject constructor(
    private val tokenDataStore: DataStore<SavedToken>,
    private val aesCoder: AesCoder,
    private val tokenServer: TokenServer,
) : TokenDataSource {

    init {
        Log.d("wap", "TokenDataSourceImpl init")
        runBlocking {
            // 앱 프로세스가 첫 시작된 직후에 로컬에 저장된 토큰을 불러온다.
            loadSavedTokens()
        }
    }

    /**
     * 현재 토큰을 반환한다.
     *
     * DataStore로 저장된 토큰값은 이미 로드되어 있다.
     */
    override fun currentTokens(): Flow<TokenState<CurrentTokenDto>> = flow {
        Log.d("wap", "currentTokens() 호출됨")
        val tokenState = tokenServer.currentTokens

        Log.d("wap", "currentTokens : $tokenState")

        when (tokenState) {
            is EndpointTokenState.SavedToken -> {
                Log.d("wap", "currentTokens : SavedToken")

                // 만료 확인
                val currentToken = tokenState.token
                val now = LocalDateTime.now()

                if (now > currentToken.accessTokenExpiresIn) {
                    // 액세스 만료
                    emit(TokenState.AccessExpiration(CurrentTokenDto(accessToken = currentToken.accessToken,
                        refreshToken = currentToken.refreshToken)))
                } else if (now > currentToken.refreshTokenExpiresIn) {
                    // 리프레시 만료
                    emit(TokenState.RefreshExpiration(CurrentTokenDto(accessToken = currentToken.accessToken,
                        refreshToken = currentToken.refreshToken)))
                } else {
                    // 토큰 유효
                    emit(TokenState.Valid(CurrentTokenDto(accessToken = currentToken.accessToken,
                        refreshToken = currentToken.refreshToken)))
                }
            }

            is EndpointTokenState.NoToken -> {
                Log.d("wap", "currentTokens : NoToken")
                emit(TokenState.Empty)
            }
        }

    }

    /**
     * 로컬에 저장된 토큰을 불러온다.
     *
     * @return Unit
     *
     * 앱 프로세스가 첫 시작된 직후에 호출된다.
     */
    private suspend fun loadSavedTokens() {
        Log.d("wap", "loadSavedTokens")

        tokenDataStore.data.firstOrNull()?.also { savedTokens ->
            Log.d("wap", "토큰이 로컬에 저장되어있음")
            Log.d("wap", "저장된 토큰 정보 -> 액세스 : ${savedTokens.accessToken}, 갱신 : ${savedTokens.refreshToken}")
            tokenServer.tokens = TokenServer.Tokens(
                refreshToken = savedTokens.refreshToken.toCharArray(),
                accessToken = savedTokens.accessToken.toCharArray(),
                accessTokenExpiresIn = LocalDateTime.parse(savedTokens.accessTokenExpiresIn),
                refreshTokenExpiresIn = LocalDateTime.parse(savedTokens.refreshTokenExpiresIn),
            )
        } ?: {
            Log.d("wap", "토큰이 로컬에 저장되어있지 않음")
            tokenServer.tokens = null
        }
    }


    /**
     * 토큰을 로컬에 저장한다.
     *
     * @param newTokensFromAws 새로 발급받은 토큰
     *
     * 서버에서 응답 받으면 가장 먼저 호출되는 함수이다.
     */
    override suspend fun saveTokensToLocal(newTokensFromAws: NewTokensFromAws) {
        tokenDataStore.updateData {
            it.toBuilder().setAccessToken(aesCoder.encode(newTokensFromAws.accessToken))
                .setRefreshToken(aesCoder.encode(newTokensFromAws.refreshToken)).let { builder ->

                    val savedToken = tokenServer.currentTokens
                    when (newTokensFromAws.requestBehavior) {
                        is RequestBehavior.NewTokens -> {
                            // 새로 모든 토큰을 받았으므로 모든 시각을 저장한다.
                            builder.setAccessTokenExpiresIn(newTokensFromAws.accessTokenExpireDateTime.toString())
                                .setRefreshTokenExpiresIn(newTokensFromAws.refreshTokenExpireDateTime.toString())
                        }

                        is RequestBehavior.ReissueTokens -> {
                            // 액세스 만료 시각만 저장한다.
                            builder.setAccessTokenExpiresIn(newTokensFromAws.accessTokenExpireDateTime.toString())
                        }
                    }
                }.build()
        }
    }

    /**
     * 로컬에 저장된 토큰을 모두 제거한다.
     */
    override suspend fun removeTokens() {
        tokenServer.removeToken()
        tokenDataStore.updateData {
            it.toBuilder().clear().build()
        }
    }
}