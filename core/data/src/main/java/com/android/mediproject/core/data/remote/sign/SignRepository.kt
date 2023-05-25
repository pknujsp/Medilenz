package com.android.mediproject.core.data.remote.sign

import com.android.mediproject.core.model.parameters.SignInParameter
import com.android.mediproject.core.model.parameters.SignUpParameter
import com.android.mediproject.core.model.remote.token.ConnectionTokenDto
import com.android.mediproject.core.model.remote.token.TokenState
import kotlinx.coroutines.flow.Flow

interface SignRepository {

    suspend fun signIn(signInParameter: SignInParameter): Flow<Result<Unit>>

    suspend fun signUp(signUpParameter: SignUpParameter): Flow<Result<Unit>>

    suspend fun reissueToken(): Flow<Result<Unit>>

    suspend fun signOut()

    suspend fun getSavedTokens(): Flow<TokenState<ConnectionTokenDto>>

}