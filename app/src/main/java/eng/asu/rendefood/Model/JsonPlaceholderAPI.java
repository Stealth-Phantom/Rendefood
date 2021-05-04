package eng.asu.rendefood.Model;

import androidx.lifecycle.LiveData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JsonPlaceholderAPI {
    @GET("restaurants")
    Call<List<Restaurant>> getRestaurants();

    @GET("reviews")
    Call<List<Review>> getReviews();

    @GET("menuItems")
    Call<List<MenuItem>> getMenuItems();

    @GET("slots")
    Call<List<Slot>> getSlots(@Query("ownerId") String restaurantName);

    @GET("slots/{id}")
    Call<Slot> getSlot(@Path("id") String id);

    @GET("reservations")
    Call<List<Reservation>> getReservations(@Query("reservedByEmail") String email);

    @POST("reviews")
    Call<Review> insertReview(@Body Review review);

    @POST("reservations")
    Call<Reservation> insertReservation(@Body Reservation reservation);

    @PUT("reservations/{id}")
    Call<Reservation> updateReservation(@Body Reservation reservation, @Path("id") String id);

    @PUT("restaurants/{id}")
    Call<Restaurant> updateRestaurant(@Body Restaurant restaurant, @Path("id") String name);

    @DELETE("reservations/{id}")
    Call<Reservation> deleteReservation(@Path("id") String id);

    @POST("users")
    Call<User> createUser(@Body User user);

    @GET("users/{id}")
    Call<User> getUser(@Path("id") String email);

    @PUT("users/{id}")
    Call<User> updateUser( @Body User user, @Path("id") String email);

    @PUT("slots/{id}")
    Call<Slot> updateSlot(@Body Slot slot, @Path("id") String id);
}
