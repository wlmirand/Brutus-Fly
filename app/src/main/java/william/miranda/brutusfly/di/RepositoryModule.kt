package william.miranda.brutusfly.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import william.miranda.brutusfly.data.repository.category.CategoryRepositoryImpl
import william.miranda.brutusfly.data.repository.categoryicon.CategoryIconRepositoryImpl
import william.miranda.brutusfly.domain.repository.categoryicon.CategoryIconRepository
import william.miranda.brutusfly.domain.repository.category.CategoryRepository
import william.miranda.brutusfly.data.repository.selectedaccount.SelectedAccountRepositoryImpl
import william.miranda.brutusfly.domain.repository.selectedaccount.SelectedAccountRepository
import william.miranda.brutusfly.data.repository.transaction.TransactionRepositoryImpl
import william.miranda.brutusfly.data.repository.account.AccountRepositoryImpl
import william.miranda.brutusfly.domain.repository.account.AccountRepository
import william.miranda.brutusfly.domain.repository.transaction.TransactionRepository
import william.miranda.brutusfly.data.repository.serverconfiguration.ServerConfigurationRepositoryImpl
import william.miranda.brutusfly.domain.repository.serverconfiguration.ServerConfigurationRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCategoryIconRepository(
        impl: CategoryIconRepositoryImpl
    ): CategoryIconRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        impl: CategoryRepositoryImpl
    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindSelectedAccountRepository(
        impl: SelectedAccountRepositoryImpl
    ): SelectedAccountRepository

    @Binds
    @Singleton
    abstract fun bindAccountRepository(
        impl: AccountRepositoryImpl
    ): AccountRepository

    @Binds
    @Singleton
    abstract fun bindServerConfigurationRepository(
        impl: ServerConfigurationRepositoryImpl
    ): ServerConfigurationRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        impl: TransactionRepositoryImpl
    ): TransactionRepository
}
