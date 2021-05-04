package eng.asu.rendefood.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(foreignKeys = @ForeignKey(
        entity = Restaurant.class,
        parentColumns = "name",
        childColumns = "reviewedPlace", onDelete = ForeignKey.CASCADE), primaryKeys = {"username","reviewedPlace"})
public class Review implements Serializable {
    @Ignore private String id;
    @NonNull
    private String username;
    private String opinion;
    private float score;
    @NonNull
    private String reviewedPlace;

    public Review(@NonNull String username, String opinion, float score, @NonNull String reviewedPlace) {
        this.username = username;
        this.opinion = opinion;
        this.score = score;
        this.reviewedPlace = reviewedPlace;
        this.id = username+reviewedPlace;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getReviewedPlace() {
        return reviewedPlace;
    }

    public void setReviewedPlace(String reviewedPlace) {
        this.reviewedPlace = reviewedPlace;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
