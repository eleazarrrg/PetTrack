package com.pettrack.app.core.di

import com.pettrack.app.core.notifications.AppNotifier
import com.pettrack.app.core.notifications.Notifier
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {
    @Binds
    @Singleton
    abstract fun bindNotifier(impl: AppNotifier): Notifier
}
