package eng.asu.rendefood.Model;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

public class ReservationRepository {
    private ReservationDao reservationDao;
    private User savedUser;
    JsonPlaceholderAPI retrofitInterface;
    Application application;

    public ReservationRepository(Application application, String userEmail) {
        RestaurantRoomDatabase db = RestaurantRoomDatabase.getDatabase(application);
        reservationDao = db.reservationDao();
        retrofitInterface = RetrofitService.getInterface();
        this.application = application;
        retrofitInterface.getUser(userEmail).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(!response.isSuccessful())
                    Log.d("ResREPO","Returned Null, given Email " + userEmail);
                else {
                    Log.d("USER","Added new user");
                    savedUser = response.body();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("ResREPO","Something Happened..." + t.getMessage() + "   " + t.toString());
            }
        });
    }

    public User getUser()
    {
        return savedUser;
    }

    public void createUser(User user){retrofitInterface.createUser(user).enqueue(new Callback<User>() {
        @Override
        public void onResponse(Call<User> call, Response<User> response) {
            if (!response.isSuccessful()){
                Log.d("User Registeration", "--- Not successful");
            } else {
                User sentUser =  response.body();
                Log.d("User Registeration", "Sent User: " +sentUser.getName());
            }
        }

        @Override
        public void onFailure(Call<User> call, Throwable t) {
            Log.d("User Registeration", "--- FAILED " + t.getMessage());
        }
    });}

    public void updateReservationDB(String email){
        retrofitInterface.getReservations(email).enqueue(new Callback<List<Reservation>>() {
            @Override
            public void onResponse(Call<List<Reservation>> call, Response<List<Reservation>> response) {
                if(!response.isSuccessful())
                    Log.d("GettingReservations","Failed");
                else
                {
                    Log.d("GettingReservations","Successful for mail" + email);
                    deleteAllReservations();
                    List<Reservation> reservations = response.body();
                    for(Reservation reservation:reservations) {
                        Log.d("DBReservationUpdate","Inserting Reservation..");
                        insertReservation(reservation);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Reservation>> call, Throwable t) {
                Log.d("GettingReservations","Failure...");
            }
        });
    }

    public void updateUser(String email, User user)
    {
        retrofitInterface.updateUser(user,email).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(!response.isSuccessful())
                    Log.d("UserUPDATE","Change failed for email " + email);
                else {
                    savedUser = response.body();
                    Log.d("UserUPDATE","Change successful " + savedUser.getPassword());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("UserUPDATE","Request Failed");
            }
        });
    }

    public LiveData<List<Reservation>> getReservations(){
        return reservationDao.getAllreservations();
    }

    public LiveData<List<Reservation>> getFinishedReservations(){
        return reservationDao.getFinishedReservations("Incomplete");
    }

    public LiveData<List<Reservation>> getUnfinishedReservations(){
        return reservationDao.getUnfinishedReservations("Incomplete");
    }

    public void insertReservation(Reservation reservation) {
        new ReservationRepository.insertReservationAsyncTask(reservationDao).execute(reservation);
    }

    public void addReservation(Reservation reservation) {
        retrofitInterface.getSlot(reservation.getRestuarant()+getDay(reservation.getDate())+reservation.getTime()).enqueue(new Callback<Slot>() {
            @Override
            public void onResponse(Call<Slot> call, Response<Slot> response) {
                if(!response.isSuccessful())
                    Log.d("slotGet","Unsuccessful.. of id " + reservation.getRestuarant()+getDay(reservation.getDate())+reservation.getTime());
                else{
                    Log.d("slotGet","Successful!!");
                    Slot slot = response.body();
                    if((slot.getRemainingSeats() - reservation.getReservations()) >= 0)
                    {
                        slot.setRemainingSeats(slot.getRemainingSeats() - reservation.getReservations());
                        retrofitInterface.updateSlot(slot,slot.getId()).enqueue(new Callback<Slot>() {
                            @Override
                            public void onResponse(Call<Slot> call, Response<Slot> response) {
                                if(!response.isSuccessful())
                                    Log.d("UpSlot","Unsuccessful..");
                                else{
                                    Log.d("UpSlot","Successful!!");
                                    retrofitInterface.insertReservation(reservation).enqueue(new Callback<Reservation>() {
                                        @Override
                                        public void onResponse(Call<Reservation> call, Response<Reservation> response) {
                                            if(!response.isSuccessful()) {
                                                Log.d("ADDRESERVATION", "Addition unsuccessful:" + response.message() + "   " + response.errorBody());
                                                Toast.makeText(application, "There's already a reservation by this mail on this date", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                Log.d("ADDRESERVATION","Addition successful!! By email "+ response.body().getReservedByEmail());
                                                // Notification ID.
                                                final int NOTIFICATION_ID = 0;
                                                // Notification channel ID.
                                                final String PRIMARY_CHANNEL_ID =
                                                        "primary_notification_channel";
                                                NotificationManager mNotificationManager;

                                                // Set up the Notification Broadcast Intent.
                                                Intent notifyIntent = new Intent(application, AlarmReceiver.class);

                                                boolean alarmUp = (PendingIntent.getBroadcast(application, NOTIFICATION_ID,
                                                        notifyIntent, PendingIntent.FLAG_NO_CREATE) != null);


                                                final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                                                        (application, NOTIFICATION_ID, notifyIntent,
                                                                PendingIntent.FLAG_UPDATE_CURRENT);

                                                final AlarmManager alarmManager = (AlarmManager) application.getSystemService
                                                        (ALARM_SERVICE);

                                                // Create a notification manager object.
                                                mNotificationManager =
                                                        (NotificationManager) application.getSystemService(NOTIFICATION_SERVICE);

                                                // Notification channels are only available in OREO and higher.
                                                // So, add a check on SDK version.
                                                if (android.os.Build.VERSION.SDK_INT >=
                                                        android.os.Build.VERSION_CODES.O) {

                                                    // Create the NotificationChannel with all the parameters.
                                                    NotificationChannel notificationChannel = new NotificationChannel
                                                            (PRIMARY_CHANNEL_ID,
                                                                    "Stand up notification",
                                                                    NotificationManager.IMPORTANCE_HIGH);

                                                    notificationChannel.enableLights(true);
                                                    notificationChannel.setLightColor(Color.RED);
                                                    notificationChannel.enableVibration(true);
                                                    notificationChannel.setDescription("Notifies every 15 minutes to " +
                                                            "stand up and walk");
                                                    mNotificationManager.createNotificationChannel(notificationChannel);

                                                    Date date = new Date();
                                                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                                    try {
                                                        date = formatter.parse(reservation.getDate() + " 0" + reservation.getTime() +":00");
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    System.out.println("Today is " + date);

                                                    if (alarmManager != null) {
                                                        Log.d("ALARMMAN","ONNNN");
                                                        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME,date.getTime(),notifyPendingIntent);
                                                    }
                                                }

                                                new ReservationRepository.insertReservationAsyncTask(reservationDao).execute(reservation);
                                                Toast.makeText(application,"Reservation Saved successfully!",Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Reservation> call, Throwable t) {
                                            Log.d("ADDRESERVATION","Addition failure");
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(Call<Slot> call, Throwable t) {
                                Log.d("UpSlot","Failure..");
                            }
                        });

                    }
                    else{
                        Toast.makeText(application,"Unfortunatly this slot is full, please choose another!",Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Slot> call, Throwable t) {
                Log.d("slotGet","Failure..");
            }
        });

    }

    public void deleteReservation(Reservation reservation){
        retrofitInterface.getSlot(reservation.getRestuarant()+getDay(reservation.getDate())+reservation.getTime()).enqueue(new Callback<Slot>() {
            @Override
            public void onResponse(Call<Slot> call, Response<Slot> response) {
                if(!response.isSuccessful())
                    Log.d("slotGet","Unsuccessful..");
                else{
                    Log.d("slotGet","Successful!!");
                    Slot slot = response.body();
                    slot.setRemainingSeats(slot.getRemainingSeats() + reservation.getReservations());
                    retrofitInterface.updateSlot(slot,slot.getId()).enqueue(new Callback<Slot>() {
                        @Override
                        public void onResponse(Call<Slot> call, Response<Slot> response) {
                            if(!response.isSuccessful())
                                Log.d("UpSlot","Unsuccessful..");
                            else{
                                Log.d("UpSlot","Successful!!");
                                retrofitInterface.deleteReservation(reservation.getId()).enqueue(new Callback<Reservation>() {
                                    @Override
                                    public void onResponse(Call<Reservation> call, Response<Reservation> response) {
                                        if(!response.isSuccessful()) {
                                            Log.d("DeletingReservation", "Delete Unsuccessful");
                                            Toast.makeText(application,"Reservation Deletion Failed!",Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            new ReservationRepository.deleteReservationAsyncTask(reservationDao).execute(reservation);
                                            Log.d("DeletingReservation", "Delete Successful!!");
                                            Toast.makeText(application,"Reservation Cancelled successfully!",Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Reservation> call, Throwable t) {
                                        Log.d("DeletingReservation","Delete Failed..");
                                        Toast.makeText(application,"Reservation failed to delete",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<Slot> call, Throwable t) {
                            Log.d("UpSlot","Failure..");
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Slot> call, Throwable t) {
                Log.d("slotGet","Failure..");
            }
        });
    }

    public void updateReservation(Reservation reservation){
        new ReservationRepository.updateReservationAsyncTask(reservationDao).execute(reservation);
        if(!reservation.getStatus().equals("Complete"))
        {
            retrofitInterface.getSlot(reservation.getRestuarant()+getDay(reservation.getDate())+reservation.getTime()).enqueue(new Callback<Slot>() {
                @Override
                public void onResponse(Call<Slot> call, Response<Slot> response) {
                    if(!response.isSuccessful())
                        Log.d("slotGet","Unsuccessful..");
                    else{
                        Log.d("slotGet","Successful!!");
                        Slot slot = response.body();
                        slot.setRemainingSeats(slot.getRemainingSeats() + reservation.getReservations());
                        retrofitInterface.updateSlot(slot,slot.getId()).enqueue(new Callback<Slot>() {
                            @Override
                            public void onResponse(Call<Slot> call, Response<Slot> response) {
                                if(!response.isSuccessful())
                                    Log.d("UpSlot","Unsuccessful..");
                                else{
                                    Log.d("UpSlot","Successful!!");
                                    retrofitInterface.updateReservation(reservation,reservation.getId()).enqueue(new Callback<Reservation>() {
                                        @Override
                                        public void onResponse(Call<Reservation> call, Response<Reservation> response) {
                                            if(!response.isSuccessful()) {
                                                Log.d("UpdatingReservation", "Update Unsuccessful");
                                                Toast.makeText(application,"Reservation failed to update",Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                Log.d("UpdatingReservation", "Update successful");
                                                if(reservation.getStatus().equals("Cancelled"))
                                                    Toast.makeText(application,"Reservation cancelled successfully!",Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Reservation> call, Throwable t) {
                                            Log.d("UpdatingReservation","Update Failed...");
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(Call<Slot> call, Throwable t) {
                                Log.d("UpSlot","Failure..");
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<Slot> call, Throwable t) {
                    Log.d("slotGet","Failure..");
                }
            });
        }
        else{Toast.makeText(application,"Reservation completed successfully!",Toast.LENGTH_SHORT).show();}
    }

    public void deleteAllReservations() {
        new ReservationRepository.deleteAllReservationsAsyncTask(reservationDao).execute();
    }

    private static class insertReservationAsyncTask extends AsyncTask<Reservation, Void, Void> {

        private ReservationDao mAsyncTaskDao;

        insertReservationAsyncTask(ReservationDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Reservation... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteReservationAsyncTask extends AsyncTask<Reservation, Void, Void> {

        private ReservationDao mAsyncTaskDao;

        deleteReservationAsyncTask(ReservationDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Reservation... params) {
            mAsyncTaskDao.deleteReservation(params[0]);
            return null;
        }
    }

    private static class deleteAllReservationsAsyncTask extends AsyncTask<Void, Void, Void> {

        private ReservationDao mAsyncTaskDao;

        deleteAllReservationsAsyncTask(ReservationDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... params) {
            mAsyncTaskDao.deleteAllReservations();
            return null;
        }
    }

    private static class updateReservationAsyncTask extends AsyncTask<Reservation, Void, Void> {

        private ReservationDao mAsyncTaskDao;

        updateReservationAsyncTask(ReservationDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Reservation... params) {
            mAsyncTaskDao.updateReservation(params[0].getDate(),params[0].getReservedByEmail(),params[0].getStatus());
            return null;
        }
    }

    public String getDay(String date){
        try {
            SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
            Date dt1 = format1.parse(date);
            SimpleDateFormat format2 = new SimpleDateFormat("EEEE");
            String finalDay = format2.format(dt1);
            return finalDay;
        }catch (Exception e){return "";}
    }
}
