package eng.asu.rendefood.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "restaurant_table")
public class Restaurant implements Serializable {
    @NonNull
    @PrimaryKey
    @SerializedName("id")
    private String name;
    private String category;
    private String location;
    private float totalScore;
    private int openTime;
    private int closeTime;
    private String ownerId;
    private int maxSeatsPerSlot;

    public Restaurant(@NonNull String name, String category, String location, float totalScore, int openTime, int closeTime, String ownerId, int maxSeatsPerSlot) {
        this.name = name;
        this.category = category;
        this.location = location;
        this.totalScore = totalScore;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.ownerId = ownerId;
        this.maxSeatsPerSlot = maxSeatsPerSlot;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public float getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(float totalScore) {
        this.totalScore = totalScore;
    }

    public int getOpenTime() {
        return openTime;
    }

    public void setOpenTime(int openTime) {
        this.openTime = openTime;
    }

    public int getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(int closeTime) {
        this.closeTime = closeTime;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public int getMaxSeatsPerSlot() {
        return maxSeatsPerSlot;
    }

    public void setMaxSeatsPerSlot(int maxSeatsPerSlot) {
        this.maxSeatsPerSlot = maxSeatsPerSlot;
    }
}
