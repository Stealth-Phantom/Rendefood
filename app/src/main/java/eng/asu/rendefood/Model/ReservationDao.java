package eng.asu.rendefood.Model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ReservationDao {
    @Insert
    void insert(Reservation reservation);

    @Query("DELETE FROM reservations_table")
    void deleteAllReservations();

    @Query("SELECT * from reservations_table")
    LiveData<List<Reservation>> getAllreservations();

    @Query("SELECT * FROM reservations_table WHERE NOT status is :stat ")
    LiveData<List<Reservation>> getFinishedReservations(String stat);

    @Query("SELECT * FROM reservations_table WHERE status is :stat ")
    LiveData<List<Reservation>> getUnfinishedReservations(String stat);

    @Query("UPDATE reservations_table SET status= :status WHERE date = :date AND reservedByEmail = :email")
    void updateReservation(String date, String email, String status);

    @Delete
    void deleteReservation(Reservation reservation);
}
