package com.android.mediproject.feature.interestedmedicine.moreinterestedmedicine

import androidx.lifecycle.viewModelScope
import com.android.mediproject.core.domain.GetInterestedMedicineUseCase
import com.android.mediproject.core.domain.GetTokenUseCase
import com.android.mediproject.core.model.interestedmedicine.MoreInterestedMedicineDto
import com.android.mediproject.core.model.remote.token.CurrentTokenDto
import com.android.mediproject.core.model.remote.token.TokenState
import com.android.mediproject.core.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoreInterestedMedicineViewModel @Inject constructor(
    private val getTokenUseCase: GetTokenUseCase,
    private val getInterestedMedicineUseCase: GetInterestedMedicineUseCase
) : BaseViewModel() {

    private val _interstedMedicineList = MutableStateFlow<List<MoreInterestedMedicineDto>>(listOf())
    val interstedMedicineList get() = _interstedMedicineList

    private val _token = MutableStateFlow<TokenState<CurrentTokenDto>>(TokenState.Empty)
    val token get() = _token.asStateFlow()

    fun loadTokens() = viewModelScope.launch { getTokenUseCase().collect { _token.value = it } }
    fun loadInterestedMedicines() =
        viewModelScope.launch {
            getInterestedMedicineUseCase.getMoreInterestedMedicineList()
                .collect {
                    it.fold(
                        onSuccess = { _interstedMedicineList.value = it },
                        onFailure = { })
                }
        }
}