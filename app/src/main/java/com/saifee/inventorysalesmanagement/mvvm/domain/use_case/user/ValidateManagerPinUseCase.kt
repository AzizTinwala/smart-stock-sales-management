package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.user

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.di.IoDispatcher
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.AuthRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.UserManagementRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

data class ValidateManagerPinParams(
    val pin: String,
    val companyId: String = ""
)

class ValidateManagerPinUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userManagementRepository: UserManagementRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<ValidateManagerPinParams, Result<Boolean>>(dispatcher) {

    override suspend fun execute(params: ValidateManagerPinParams): Result<Boolean> {
        val targetCompanyId = params.companyId.ifEmpty {
            val actor = authRepository.getCurrentUserProfile()
            actor?.companyId ?: ""
        }
        if (targetCompanyId.isEmpty()) {
            return Result.failure(IllegalStateException("No valid company context found."))
        }
        return userManagementRepository.verifyManagerPin(targetCompanyId, params.pin)
    }
}
