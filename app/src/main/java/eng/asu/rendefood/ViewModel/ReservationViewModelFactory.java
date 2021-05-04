package eng.asu.rendefood.ViewModel;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ReservationViewModelFactory implements ViewModelProvider.Factory {
    private Application mApplication;
    private String mParam;


    public ReservationViewModelFactory(Application application, String param) {
        mApplication = application;
        mParam = param;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new ReservationViewModel(mApplication, mParam);
    }
}
