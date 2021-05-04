package eng.asu.rendefood.Model;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepository {
    private RestaurantDao restaurantDao;

    JsonPlaceholderAPI retrofitInterface;

    public RestaurantRepository(Application application) {

        RestaurantRoomDatabase db = RestaurantRoomDatabase.getDatabase(application);
        restaurantDao = db.restaurantDao();
        retrofitInterface = RetrofitService.getInterface();
    }

    public LiveData<List<Restaurant>> getRestaurants(){
        return restaurantDao.getAllRestaurants();
    }

    public LiveData<List<Review>> getReviewsForRestaurant(String name) {return restaurantDao.getReviewsForRestaurant(name);}

    public LiveData<List<MenuItem>> getMenuForRestaurant(String name) {return restaurantDao.getMenuForRestaurant(name);}

    public void updateDb(Application application)
    {
        ConnectivityManager cm = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        cm.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(Network network) {
                retrofitInterface.getRestaurants().enqueue(new Callback<List<Restaurant>>() {
                    @Override
                    public void onResponse(Call<List<Restaurant>> call, Response<List<Restaurant>> response) {
                        if (!response.isSuccessful()){
                            Log.d("MVVMX", "--- Not successful");
                        } else {
                            deleteAllRestaurants();
                            List<Restaurant> mAllRestaurants =  response.body();
                            for (Restaurant restaurant:mAllRestaurants) {
                                Log.d("MVVMX", "Inserting Restaurant: " +restaurant.getName());
                                insertRestaurant(restaurant);
                            }
                            insertSavedReviews();
                            insertSavedMenus();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Restaurant>> call, Throwable t) {
                        Log.d("MVVMX", "--- FAILED " + t.getMessage());
                    }
                });
            }
            @Override
            public void onLost(Network network) {
                //Do nothing
            }
        }
        );
    }

    public void insertSavedMenus(){
        retrofitInterface.getMenuItems().enqueue(new Callback<List<MenuItem>>() {
            @Override
            public void onResponse(Call<List<MenuItem>> call, Response<List<MenuItem>> response) {
                if (!response.isSuccessful()){
                    Log.d("MVVMX", "--- Not successful");
                } else {
                    List<MenuItem> menu = response.body();
                    for (MenuItem menuItem:menu) {
                        Log.d("MVVMX", "Inserting menu item of Restaurant " +menuItem.getOwnerId());
                        insertMenuItem(menuItem);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MenuItem>> call, Throwable t) {
                Log.d("MVVMX", "--- FAILED " + t.getMessage());
            }
        });
    }
    public void insertSavedReviews(){
        retrofitInterface.getReviews().enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if (!response.isSuccessful()){
                    Log.d("MVVMX", "--- Not successful");
                } else {
                    List<Review> allReviews =  response.body();
                    for (Review review:allReviews) {
                        Log.d("MVVMX", "Inserting Review of restaurant " +review.getReviewedPlace());
                        insertReview(review);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                Log.d("MVVMX", "--- FAILED " + t.getMessage());
            }
        });
    }

    public void insertRestaurant(Restaurant restaurant) {
        new insertRestaurantAsyncTask(restaurantDao).execute(restaurant);
    }
    public void deleteAllRestaurants() {
        new deleteRestaurantAsyncTask(restaurantDao).execute();
    }

    public void updateRestaurantScore(Restaurant restaurant){
        new updateRestaurantScoreAsyncTask(restaurantDao,restaurant,retrofitInterface).execute();
    }

    private static class insertRestaurantAsyncTask extends AsyncTask<Restaurant, Void, Void> {

        private RestaurantDao mAsyncTaskDao;

        insertRestaurantAsyncTask(RestaurantDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Restaurant... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteRestaurantAsyncTask extends AsyncTask<Void, Void, Void> {

        private RestaurantDao mAsyncTaskDao;

        deleteRestaurantAsyncTask(RestaurantDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... params) {

            mAsyncTaskDao.deleteAllRestaurants();
            return null;
        }
    }

    public void insertReview(Review review) {
        new insertReviewAsyncTask(restaurantDao).execute(review);
    }
    public void addNewReview(Review review, Restaurant restaurant)
    {
        retrofitInterface.insertReview(review).enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                if(!response.isSuccessful()) {
                    Log.d("ReviewInsertion", "Review insertion failed of name " + review.getReviewedPlace());
                }
                else {
                    Log.d("ReviewInsertion", "Review insertion successful of username: " + review.getUsername());
                    new insertReviewAsyncTask(restaurantDao).execute(review);
                    updateRestaurantScore(restaurant);
                }
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {
                Log.d("ReviewInsertion","Something bad Happened..." + t.getMessage() + "   " + t.toString());
            }
        });
    }

    private static class insertReviewAsyncTask extends AsyncTask<Review, Void, Void> {

        private RestaurantDao mAsyncTaskDao;

        insertReviewAsyncTask(RestaurantDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Review... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    public void insertMenuItem(MenuItem menuItem) {
        new insertMenuItemAsyncTask(restaurantDao).execute(menuItem);
    }

    private static class insertMenuItemAsyncTask extends AsyncTask<MenuItem, Void, Void> {

        private RestaurantDao mAsyncTaskDao;

        insertMenuItemAsyncTask(RestaurantDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final MenuItem... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }


    private static class updateRestaurantScoreAsyncTask extends AsyncTask<Void, Void, Void> {

        private RestaurantDao mAsyncTaskDao;
        private Restaurant mRestaurant;
        private JsonPlaceholderAPI mRetrofit;

        updateRestaurantScoreAsyncTask(RestaurantDao dao, Restaurant rest, JsonPlaceholderAPI retrofit) {
            mAsyncTaskDao = dao;
            mRestaurant = rest;
            mRetrofit = retrofit;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            float avg = mAsyncTaskDao.updateRestaurantScore(mRestaurant.getName());
            avg = Math.round(avg * 100) / 100f;
            mRestaurant.setTotalScore(avg);
            Log.d("AVGSCORE","Average score successful!"+avg);
            mRetrofit.updateRestaurant(mRestaurant,mRestaurant.getName()).enqueue(new Callback<Restaurant>() {
                @Override
                public void onResponse(Call<Restaurant> call, Response<Restaurant> response) {
                    if(!response.isSuccessful())
                        Log.d("AVGSCORE","Average score unsuccessful!");
                    else
                        Log.d("AVGSCORE","Average score successful!"+mRestaurant.getTotalScore());
                }

                @Override
                public void onFailure(Call<Restaurant> call, Throwable t) {
                    Log.d("AVGSCORE","Average score failed!");
                }
            });
            return null;
        }
    }
}
