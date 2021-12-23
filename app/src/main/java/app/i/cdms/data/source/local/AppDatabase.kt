package app.i.cdms.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import app.i.cdms.Constant
import app.i.cdms.data.source.local.fav.FavDao
import app.i.cdms.data.source.local.fav.Favorite


/**
 * @author ZZY
 * 3/5/21.
 */
// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = [Favorite::class], version = Constant.DB_VERSION, exportSchema = false)
//@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): FavDao
}