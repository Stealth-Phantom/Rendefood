package eng.asu.rendefood.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import eng.asu.rendefood.Model.Restaurant;
import eng.asu.rendefood.R;

public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.RestaurantViewHolder> {
    private final LayoutInflater mInflater;
    private List<Restaurant> restaurants; // Cached copy of soldiers
    Intent intent;

    RestaurantListAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    @Override
    public RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.restaurant_recyclerview_item, parent, false);
        return new RestaurantViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RestaurantViewHolder holder, int position) {
        if (restaurants != null) { //Set data of current restaurant in the UI
            Restaurant current = restaurants.get(position);
            holder.restaurantNameText.setText(current.getName());
            holder.restaurantCategoryText.setText(current.getCategory());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intent = new Intent(view.getContext(), RestaurantActivity.class);
                    intent.putExtra("restaurant",current);
                    view.getContext().startActivity(intent);
                }
            });
            try{
                int opening = current.getOpenTime();
                int closing = current.getCloseTime();
                String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                int currentMins,currentHour,openingMinsDiff,closingMinsDiff;
                currentHour = Integer.parseInt(currentTime.substring(0,currentTime.indexOf(":")));
                currentMins = Integer.parseInt(currentTime.substring(currentTime.indexOf(":")+1));
                openingMinsDiff = (opening-currentHour)*60+currentMins;
                closingMinsDiff = (closing-currentHour)*60;
                if ((openingMinsDiff <= 0) && (closingMinsDiff > 0)) {
                    //checkes whether the current time is between Opening and Closing time
                    holder.restaurantStatusText.setTextColor(Color.rgb(75,217,67));
                    holder.restaurantStatusText.setText("OPEN");
                }
                else{
                    holder.restaurantStatusText.setTextColor(Color.RED);
                    holder.restaurantStatusText.setText("CLOSE");
                }

            }
            catch (Exception e){e.printStackTrace();}

        } else {
            // Covers the case of data not being ready yet.
            holder.restaurantNameText.setText("----");
            holder.restaurantCategoryText.setText("_____");
            holder.restaurantStatusText.setText("UNKNOWN");
        }
    }

    void setRestaurants(List<Restaurant> Restaurants){
        restaurants = Restaurants;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (restaurants != null)
            return restaurants.size();
        else return 0;
    }

    class RestaurantViewHolder extends RecyclerView.ViewHolder {//Should have all fields of the recycler view item (Should be final)
        private final TextView restaurantNameText;
        private final TextView restaurantCategoryText;
        private final TextView restaurantStatusText;
        private RestaurantViewHolder(View itemView) { //Link variables to their respective fields
            super(itemView);
            restaurantNameText = itemView.findViewById(R.id.reservedRestaurantPlaceHolder);
            restaurantCategoryText = itemView.findViewById(R.id.restaurantCategroyPlaceholder);
            restaurantStatusText = itemView.findViewById(R.id.restaurantStatusPlaceholder);
        }
    }
}
