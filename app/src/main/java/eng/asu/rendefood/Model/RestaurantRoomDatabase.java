package eng.asu.rendefood.Model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Restaurant.class, Review.class, MenuItem.class, Reservation.class}, version = 1, exportSchema = false)
public abstract class RestaurantRoomDatabase extends RoomDatabase {
    private static RestaurantRoomDatabase INSTANCE;

    public static RestaurantRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RestaurantRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RestaurantRoomDatabase.class, "restaurant_database")
                            // Wipes and rebuilds instead of migrating
                            // if no Migration object.
                            // Migration is not part of this practical.
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract RestaurantDao restaurantDao();
    public abstract ReservationDao reservationDao();
}
