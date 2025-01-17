package com.steevsapps.idledaddy.dialogs;

import android.annotation.SuppressLint;
import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import androidx.annotation.NonNull;

import com.steevsapps.idledaddy.R;
import com.steevsapps.idledaddy.steam.SteamWebHandler;
import com.steevsapps.idledaddy.utils.Utils;

import org.json.JSONArray;

import java.util.ArrayDeque;

public class AutoDiscoverViewModel extends AndroidViewModel {

    private final MutableLiveData<String> statusText = new MutableLiveData<>();
    private SteamWebHandler webHandler;
    private boolean finished = true;

    private final ArrayDeque<String> discoveryQueue = new ArrayDeque<>();

    public AutoDiscoverViewModel(@NonNull Application application) {
        super(application);
    }

    public boolean isFinished() {
        return finished;
    }

    void init(SteamWebHandler webHandler) {
        this.webHandler = webHandler;
    }

    LiveData<String> getStatus() {
        return statusText;
    }

    @SuppressLint("StaticFieldLeak")
    public void autoDiscover() {
        finished = false;
        new AsyncTask<Void,String,Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    if (discoveryQueue.isEmpty()) {
                        // Generate new discovery queue
                        publishProgress(getApplication().getString(R.string.generating_discovery));

                        Utils.runWithRetries(3, () -> {
                            final JSONArray newQueue = webHandler.generateNewDiscoveryQueue();
                            for (int i=0, count=newQueue.length();i<count;i++) {
                                discoveryQueue.add(newQueue.getString(i));
                            }
                        });
                    }

                    for (int i=0, count=discoveryQueue.size();i<count;i++) {
                        final String appId = discoveryQueue.getFirst();
                        publishProgress(getApplication().getString(R.string.discovering, appId, i + 1, count));
                        Utils.runWithRetries(3, () -> {
                            webHandler.clearFromQueue(appId);
                            discoveryQueue.pop();
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                statusText.setValue(values[0]);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                finished = true;
                statusText.setValue(getApplication().getString(result ? R.string.discovery_finished : R.string.discovery_error));
            }
        }.execute();
    }
}
