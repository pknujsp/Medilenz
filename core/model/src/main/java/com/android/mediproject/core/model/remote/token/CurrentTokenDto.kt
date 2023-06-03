package com.android.mediproject.core.model.remote.token

import java.time.LocalDateTime


/**
 * 토큰 정보를 담는 데이터 클래스
 *
 * @property accessToken
 * @property refreshToken
 * @property expirationTimeOfAccessToken
 */
data class CurrentTokenDto(
    val accessToken: CharArray,
    val refreshToken: CharArray,
    val expirationTimeOfAccessToken: LocalDateTime
) {

    private fun CharArray.toStr(): String {
        return this.joinToString("")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CurrentTokenDto

        if (!accessToken.contentEquals(other.accessToken)) return false
        if (!refreshToken.contentEquals(other.refreshToken)) return false
        if (expirationTimeOfAccessToken != other.expirationTimeOfAccessToken) return false


        return true
    }

    override fun hashCode(): Int {
        var result = accessToken.contentHashCode()
        result = 31 * result + refreshToken.contentHashCode()
        result = 31 * result + expirationTimeOfAccessToken.hashCode()

        return result
    }
}