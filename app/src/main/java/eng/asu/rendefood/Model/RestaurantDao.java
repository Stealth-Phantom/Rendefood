package eng.asu.rendefood.Model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RestaurantDao {
    @Insert
    void insert(Restaurant restaurant);

    @Insert
    void insert(Review review);

    @Insert
    void insert(MenuItem menuItem);

    @Query("DELETE FROM restaurant_table")
    void deleteAllRestaurants();

    @Query("DELETE FROM Review")
    void deleteAllReviews();

    @Query("DELETE FROM MenuItem")
    void deleteAllMenuItems();

    @Query("SELECT * from restaurant_table ORDER BY name ASC")
    LiveData<List<Restaurant>> getAllRestaurants();

    @Query("SELECT * FROM Review WHERE reviewedPlace IS :restaurantName")
    LiveData<List<Review>> getReviewsForRestaurant(String restaurantName);

    @Query("SELECT * FROM MenuItem WHERE ownerId IS :restaurantName")
    LiveData<List<MenuItem>> getMenuForRestaurant(String restaurantName);

    @Query("SELECT AVG(score) FROM review WHERE reviewedPlace = :restaurantName")
    float updateRestaurantScore(String restaurantName);
}
