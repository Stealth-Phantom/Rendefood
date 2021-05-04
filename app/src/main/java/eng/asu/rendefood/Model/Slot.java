package eng.asu.rendefood.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

public class Slot {
    private String id;
    @NonNull
    private String day;
    @NonNull
    private int time;
    @NonNull
    private String ownerId;
    private int remainingSeats;

    public Slot(@NonNull String day, @NonNull int time, @NonNull String ownerId, int remainingSeats) {
        this.day = day;
        this.time = time;
        this.ownerId = ownerId;
        this.remainingSeats = remainingSeats;
        id = ownerId+day+time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public int getRemainingSeats() {
        return remainingSeats;
    }

    public void setRemainingSeats(int remainingSeats) {
        this.remainingSeats = remainingSeats;
    }
}
