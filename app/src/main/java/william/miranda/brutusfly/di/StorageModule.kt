package william.miranda.brutusfly.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import william.miranda.brutusfly.data.local.datastore.categoryicon.CategoryIconStore
import william.miranda.brutusfly.data.local.datastore.categoryicon.DataStoreCategoryIconStore
import william.miranda.brutusfly.data.local.datastore.selectedaccount.DataStoreSelectedAccountStore
import william.miranda.brutusfly.data.local.datastore.selectedaccount.SelectedAccountStore
import william.miranda.brutusfly.data.local.datastore.serverconfiguration.DataStoreServerConfigurationStore
import william.miranda.brutusfly.data.local.datastore.serverconfiguration.ServerConfigurationStore
import william.miranda.brutusfly.data.security.KeystoreTokenCipher
import william.miranda.brutusfly.data.security.TokenCipher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule {
    @Binds
    abstract fun bindCategoryIconStore(
        impl: DataStoreCategoryIconStore
    ): CategoryIconStore

    @Binds
    abstract fun bindSelectedAccountStore(
        impl: DataStoreSelectedAccountStore
    ): SelectedAccountStore

    @Binds
    abstract fun bindConfigurationStore(
        impl: DataStoreServerConfigurationStore
    ): ServerConfigurationStore

    @Binds
    abstract fun bindTokenCipher(
        impl: KeystoreTokenCipher
    ): TokenCipher

    companion object {
        @Provides
        @Singleton
        fun providePreferences(
            @ApplicationContext context: Context
        ): DataStore<Preferences> =
            PreferenceDataStoreFactory.create {
                context.preferencesDataStoreFile(
                    name = "server_configuration"
                )
            }
    }
}
