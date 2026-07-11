package com.pettrack.app.core.di

import com.pettrack.app.core.session.SessionManager
import com.pettrack.app.core.session.SessionStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SessionModule {
    @Binds
    @Singleton
    abstract fun bindSessionStore(impl: SessionManager): SessionStore
}
