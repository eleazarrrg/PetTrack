package com.pettrack.app.core.di

import com.pettrack.app.core.location.LocationProvider
import com.pettrack.app.core.location.LocationSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {
    @Binds
    @Singleton
    abstract fun bindLocationSource(impl: LocationProvider): LocationSource
}
