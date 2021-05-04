package eng.asu.rendefood.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

import java.io.Serializable;

@Entity(tableName = "reservations_table",primaryKeys = {"date","reservedByEmail"})
public class Reservation implements Serializable {
    @Ignore private String id;
    @NonNull
    private String date;
    private int time;
    private String restuarant;
    private String status;
    private int reservations;
    @NonNull
    private String reservedByEmail;

    public Reservation(String date, int time, String restuarant, String status, int reservations, String reservedByEmail) {
        this.date = date;
        this.time = time;
        this.restuarant = restuarant;
        this.status = status;
        this.reservations = reservations;
        this.reservedByEmail = reservedByEmail;
        id = reservedByEmail+date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRestuarant() {
        return restuarant;
    }

    public void setRestuarant(String restuarant) {
        this.restuarant = restuarant;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getReservations() {
        return reservations;
    }

    public void setReservations(int reservations) {
        this.reservations = reservations;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getReservedByEmail() {
        return reservedByEmail;
    }

    public void setReservedByEmail(String reservedByEmail) {
        this.reservedByEmail = reservedByEmail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
