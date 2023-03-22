package abeljs.xzaragoza;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {
    private MutableLiveData<String> mensajeError;

    public MutableLiveData<String> getMensajeError() {
        if (mensajeError == null) {
            mensajeError = new MutableLiveData<String>();
        }
        return mensajeError;
    }
}
