package de.hs_kl.libris.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.hs_kl.libris.data.api.BookSearchService
import de.hs_kl.libris.data.api.GoogleBooksService
import de.hs_kl.libris.util.LanguageManager
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SearchModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient()
    }

    @Provides
    @Singleton
    fun provideGoogleBooksService(
        okHttpClient: OkHttpClient,
        languageManager: LanguageManager
    ): GoogleBooksService {
        return GoogleBooksService(okHttpClient, languageManager)
    }

    @Provides
    @Singleton
    fun provideBookSearchServices(
        googleBooksService: GoogleBooksService
    ): List<BookSearchService> {
        return listOf(googleBooksService)
    }
}