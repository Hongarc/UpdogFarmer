package com.steevsapps.idledaddy;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.os.Handler;
import android.util.Log;

import com.steevsapps.idledaddy.steam.SteamWebHandler;
import com.steevsapps.idledaddy.steam.model.TimeQuery;
import com.steevsapps.idledaddy.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {
    private final static String TAG = LoginViewModel.class.getSimpleName();
    private final static int TIMEOUT_MILLIS = 30000;

    private SteamWebHandler webHandler;
    private final Handler timeoutHandler = new Handler();
    private final MutableLiveData<Integer> timeDifference = new MutableLiveData<>();

    private final SingleLiveEvent<Void> timeoutEvent = new SingleLiveEvent<>();
    private final Runnable timeoutRunnable = () -> {
        // Trigger event to show a timeout error
        timeoutEvent.call();
    };

    private boolean timeAligned = false;

    void init(SteamWebHandler webHandler) {
        this.webHandler = webHandler;
    }

    LiveData<Integer> getTimeDifference() {
        if (!timeAligned) {
            alignTime();
        }
        return timeDifference;
    }

    SingleLiveEvent<Void> getTimeout() {
        return timeoutEvent;
    }

    public void startTimeout() {
        Log.i(TAG, "Starting login timeout");
        timeoutHandler.postDelayed(timeoutRunnable, TIMEOUT_MILLIS);
    }

    public void stopTimeout() {
        Log.i(TAG, "Stopping login timeout");
        timeoutHandler.removeCallbacks(timeoutRunnable);
    }

    private void alignTime() {
        final long currentTime = Utils.getCurrentUnixTime();
        webHandler.queryServerTime().enqueue(new Callback<TimeQuery>() {
            @Override
            public void onResponse(Call<TimeQuery> call, Response<TimeQuery> response) {
                if (response.isSuccessful()) {
                    timeDifference.setValue((int) (response.body().getResponse().getServerTime() - currentTime));
                    timeAligned = true;
                }
            }

            @Override
            public void onFailure(Call<TimeQuery> call, Throwable t) {
                Log.e(TAG, "Failed to get server time", t);
            }
        });
    }
}
