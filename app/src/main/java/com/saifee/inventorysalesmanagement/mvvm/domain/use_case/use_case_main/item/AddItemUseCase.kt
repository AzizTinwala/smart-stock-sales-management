package com.saifee.inventorysalesmanagement.mvvm.domain.use_case.use_case_main.item

import com.saifee.inventorysalesmanagement.mvvm.core.base_classes.BaseUseCase
import com.saifee.inventorysalesmanagement.mvvm.data.local.entity.ItemEntity
import com.saifee.inventorysalesmanagement.mvvm.di.IoDispatcher
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.AuthRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.repository.ItemRepository
import com.saifee.inventorysalesmanagement.mvvm.domain.use_case.auth.AuthorizationManager
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class AddItemUseCase @Inject constructor(
    private val repo: ItemRepository,
    private val authRepository: AuthRepository,
    private val authorizationManager: AuthorizationManager,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : BaseUseCase<AddItemUseCase.AddItemParams, Long>(dispatcher) {

    override suspend fun execute(params: AddItemParams): Long {
        val user = authRepository.getCurrentUserProfile()
        val targetCompanyId = params.item.companyId.ifEmpty { user?.companyId ?: "" }
        if (!authorizationManager.canCreateData(user, targetCompanyId)) {
            throw SecurityException("Unauthorized: Cannot create item records.")
        }
        return repo.addItem(params.item)
    }
    data class AddItemParams(val item: ItemEntity)
}
