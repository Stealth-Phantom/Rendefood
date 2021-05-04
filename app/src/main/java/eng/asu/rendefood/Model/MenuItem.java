package eng.asu.rendefood.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(foreignKeys = @ForeignKey(
        entity = Restaurant.class,
        parentColumns = "name",
        childColumns = "ownerId", onDelete = ForeignKey.CASCADE),primaryKeys = {"itemName","ownerId"})
public class MenuItem implements Serializable {
    @NonNull
    @SerializedName("id")
    private String itemName;
    private String description;
    private float price;
    @NonNull
    private String ownerId;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
