package com.geeks.cleanArch.domain.di

import com.geeks.cleanArch.domain.usecase.DeleteTaskUseCase
import com.geeks.cleanArch.domain.usecase.GetAllTasksUseCase
import com.geeks.cleanArch.domain.usecase.GetTaskUseCase
import com.geeks.cleanArch.domain.usecase.InsertTaskUseCase
import com.geeks.cleanArch.domain.usecase.UpdateTaskUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

val domainModule: Module = module {

    single { InsertTaskUseCase(get()) }
    single { GetAllTasksUseCase(get()) }
    single { UpdateTaskUseCase(get()) }
    single { DeleteTaskUseCase(get()) }
    single { GetTaskUseCase(get()) }
}