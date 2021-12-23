package app.i.cdms.di

import android.content.Context
import androidx.room.Room
import app.i.cdms.Constant
import app.i.cdms.data.source.local.AppDatabase
import app.i.cdms.data.source.local.fav.FavDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            Constant.DB_NAME
        )
            .createFromAsset("database/myapp.db")
            .build()
    }

    @Provides
    fun provideFavDao(database: AppDatabase): FavDao {
        return database.dao()
    }
}
