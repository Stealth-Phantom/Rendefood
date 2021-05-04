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

import eng.asu.rendefood.Model.Reservation;
import eng.asu.rendefood.R;

public class ReservationListAdapter extends RecyclerView.Adapter<ReservationListAdapter.ReservationtViewHolder> {
    private final LayoutInflater mInflater;
    private List<Reservation> reservations; // Cached copy of soldiers
    Intent intent;

    ReservationListAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    @Override
    public ReservationtViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.reservation_recyclerview_item, parent, false);
        return new ReservationtViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReservationtViewHolder holder, int position) {
        if (reservations != null) { //Set data of current restaurant in the UI
            Reservation current = reservations.get(position);
            holder.reservedRestaurantNameText.setText(current.getRestuarant());
            holder.reservedTimeText.setText(""+current.getTime()+":00");
            holder.reservedDateText.setText(current.getDate());
            holder.reservedSeatsText.append(" " + current.getReservations());
            holder.reservationStatusText.setText(current.getStatus());
            if(current.getStatus().equals("Incomplete"))
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        intent = new Intent(view.getContext(), ReservationInfoActivity.class);
                        intent.putExtra("reservation",current);
                        view.getContext().startActivity(intent);
                    }
                });
            if(current.getStatus().equals("Cancelled"))
                holder.reservationStatusText.setTextColor(Color.RED);
            if(current.getStatus().equals("Complete"))
                holder.reservationStatusText.setTextColor(Color.rgb(75,217,67));

        } else {
            // Covers the case of data not being ready yet.
            holder.reservedRestaurantNameText.setText("Haha rest");
            holder.reservedTimeText.setText("12:00");
            holder.reservedDateText.setText("11/11/2011");
            holder.reservedSeatsText.append(" some number");
            holder.reservationStatusText.setText("Maybe complete");
        }
    }

    void setReservations(List<Reservation> Reservations){
        reservations = Reservations;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (reservations != null)
            return reservations.size();
        else return 0;
    }

    class ReservationtViewHolder extends RecyclerView.ViewHolder {//Should have all fields of the recycler view item (Should be final)
        private final TextView reservedRestaurantNameText;
        private final TextView reservedSeatsText;
        private final TextView reservationStatusText;
        private final TextView reservedTimeText;
        private final TextView reservedDateText;
        private ReservationtViewHolder(View itemView) { //Link variables to their respective fields
            super(itemView);
            reservationStatusText = itemView.findViewById(R.id.reservationStatusPlaceholder);
            reservedDateText = itemView.findViewById(R.id.reservationDatePlaceholder);
            reservedTimeText = itemView.findViewById(R.id.reservtionTimePlaceholder);
            reservedRestaurantNameText = itemView.findViewById(R.id.reservedRestaurantPlaceHolder);
            reservedSeatsText = itemView.findViewById(R.id.reservedNumberPlaceholder);
        }
    }
}
