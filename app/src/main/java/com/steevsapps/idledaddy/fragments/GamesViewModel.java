package com.steevsapps.idledaddy.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Log;

import com.steevsapps.idledaddy.preferences.PrefsManager;
import com.steevsapps.idledaddy.steam.SteamWebHandler;
import com.steevsapps.idledaddy.steam.model.Game;
import com.steevsapps.idledaddy.steam.model.GamesOwnedResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Reminder: Must be public or else you will get a runtime exception
 */
public class GamesViewModel extends ViewModel {
    private final static String TAG = GamesViewModel.class.getSimpleName();

    public final static int SORT_ALPHABETICALLY = 0;
    public final static int SORT_HOURS_PLAYED = 1;
    public final static int SORT_HOURS_PLAYED_REVERSED = 2;

    private SteamWebHandler webHandler;
    private long steamId;
    private MutableLiveData<List<Game>> games;
    private int sortId = SORT_ALPHABETICALLY;

    void init(SteamWebHandler webHandler, long steamId) {
        this.webHandler = webHandler;
        this.steamId = steamId;
        this.sortId = PrefsManager.getSortValue();
    }

    LiveData<List<Game>> getGames() {
        if (games == null) {
            games = new MutableLiveData<>();
        }
        return games;
    }

    void setGames(List<Game> games) {
        if (sortId == SORT_ALPHABETICALLY) {
            Collections.sort(games, (game1, game2) -> game1.name.toLowerCase().compareTo(game2.name.toLowerCase()));
        } else if (sortId == SORT_HOURS_PLAYED) {
            Collections.sort(games, Collections.reverseOrder());
        } else if (sortId == SORT_HOURS_PLAYED_REVERSED) {
            Collections.sort(games);
        }
        this.games.setValue(games);
    }

    void sort(int sortId) {
        if (this.sortId == sortId) {
            return;
        }

        final List<Game> games = this.games.getValue();
        if (games != null && !games.isEmpty()) {
            this.sortId = sortId;
            setGames(games);
        }

        PrefsManager.writeSortValue(sortId);
    }

    void fetchGames() {
        Log.i(TAG, "Fetching games...");
        webHandler.getGamesOwned(steamId).enqueue(new Callback<GamesOwnedResponse>() {
            @Override
            public void onResponse(Call<GamesOwnedResponse> call, Response<GamesOwnedResponse> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Success!");
                    setGames(response.body().getGames());
                } else {
                    Log.i(TAG, "Got error code: " + response.code());
                    setGames(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<GamesOwnedResponse> call, Throwable t) {
                Log.i(TAG, "Got error", t);
                setGames(new ArrayList<>());
            }
        });
    }
}
