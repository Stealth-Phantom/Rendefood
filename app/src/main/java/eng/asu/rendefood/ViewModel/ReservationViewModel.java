package eng.asu.rendefood.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import eng.asu.rendefood.Model.Reservation;
import eng.asu.rendefood.Model.ReservationRepository;
import eng.asu.rendefood.Model.User;

public class ReservationViewModel extends AndroidViewModel {
    private ReservationRepository reservationRepository;

    public ReservationViewModel(Application application, String email)
    {
        super(application);
        reservationRepository = new ReservationRepository(application,email);
    }



    public LiveData<List<Reservation>> getReservations(){return reservationRepository.getReservations();}

    public LiveData<List<Reservation>> getFinishedReservations(){
        return reservationRepository.getFinishedReservations();
    }

    public LiveData<List<Reservation>> getUnfinishedReservations(){
        return reservationRepository.getUnfinishedReservations();
    }

    public void updateUser(String email, User normalUser){reservationRepository.updateUser(email,normalUser);}

    public User getUser(){return reservationRepository.getUser();}

    public void createUser(User normalUser){reservationRepository.createUser(normalUser);}

    public void insert(Reservation reservation){
        reservationRepository.addReservation(reservation);
    }
    public void updateReservationDB(String email){reservationRepository.updateReservationDB(email);}

    public void updateReservation(Reservation reservation){reservationRepository.updateReservation(reservation);}

    public void deleteReservation(Reservation reservation){reservationRepository.deleteReservation(reservation);}
}
