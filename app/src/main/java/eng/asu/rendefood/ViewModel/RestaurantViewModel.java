package eng.asu.rendefood.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import eng.asu.rendefood.Model.MenuItem;
import eng.asu.rendefood.Model.Restaurant;
import eng.asu.rendefood.Model.RestaurantRepository;
import eng.asu.rendefood.Model.Review;

public class RestaurantViewModel extends AndroidViewModel {
    private RestaurantRepository restaurantRepository;
    private LiveData<List<Restaurant>> restaurants;

    public RestaurantViewModel(Application application)
    {
        super(application);
        restaurantRepository = new RestaurantRepository(application);
        restaurants = restaurantRepository.getRestaurants();
    }

    public LiveData<List<Restaurant>> getRestaurants(){return restaurants;}

    public LiveData<List<Review>> getReviewsForRestaurant(String name) {return restaurantRepository.getReviewsForRestaurant(name);}

    public LiveData<List<MenuItem>> getMenuForRestaurant(String name) {return restaurantRepository.getMenuForRestaurant(name);}

    public void insert(Restaurant restaurant){restaurantRepository.insertRestaurant(restaurant);}

    public void insert(Review review, Restaurant restaurant){restaurantRepository.addNewReview(review, restaurant);}

    public void updateDB(Application application){restaurantRepository.updateDb(application);}

    public void updateRestaurantScore(Restaurant restaurant){restaurantRepository.updateRestaurantScore(restaurant);}
}
