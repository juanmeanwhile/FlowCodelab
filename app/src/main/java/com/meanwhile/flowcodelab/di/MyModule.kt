package com.meanwhile.flowcodelab.di

import com.meanwhile.flowcodelab.domain.GetDaysUntilWeekendUseCase
import com.meanwhile.flowcodelab.domain.IGetDaysUntilWeekendUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MyModule {

    @Binds
    abstract fun bindAnalyticsService(
        getDaysUseCaseImpl: GetDaysUntilWeekendUseCase
    ): IGetDaysUntilWeekendUseCase
}