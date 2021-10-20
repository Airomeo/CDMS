package app.i.cdms.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import app.i.cdms.Constant
import app.i.cdms.data.db.fav.FavDao
import app.i.cdms.data.db.fav.Favorite
import kotlinx.coroutines.CoroutineScope


/**
 * @author ZZY
 * 3/5/21.
 */
// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = [Favorite::class], version = Constant.DB_VERSION, exportSchema = false)
//@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dao(): FavDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "todo_database"
                )
//                    .addCallback(ToDoDatabaseCallback(context, scope))
//                    .addMigrations(MIGRATION_1_10)
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
/*
        private class ToDoDatabaseCallback(
            private val context: Context,
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            *//**
             * Override the onCreate method to populate the database.
             *//*
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // If you want to keep the data through app restarts,
                // comment out the following line.
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(context, database.toDoDao())
                    }
                }
            }
        }*/
    }
}