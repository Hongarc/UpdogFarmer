package com.steevsapps.idledaddy.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.widget.Toast;

import com.steevsapps.idledaddy.R;
import com.steevsapps.idledaddy.steam.model.Game;
import com.steevsapps.idledaddy.preferences.PrefsManager;

import java.util.List;

/**
 * Options dialog shown when you long press a game
 */
public class GameOptionsDialog extends DialogFragment {
    public final static String TAG = GameOptionsDialog.class.getSimpleName();

    private final static String TITLE = "TITLE";
    private final static String APPID = "APPID";
    private String title;
    private String appId;
    private boolean blacklisted;

    public static GameOptionsDialog newInstance(Game game) {
        final GameOptionsDialog fragment = new GameOptionsDialog();
        final Bundle args = new Bundle();
        args.putString(TITLE, game.name);
        args.putString(APPID, String.valueOf(game.appId));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        title = args.getString(TITLE);
        appId = args.getString(APPID);
        blacklisted = PrefsManager.getBlacklist().contains(appId);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String[] options = getResources().getStringArray(R.array.game_long_press_options);
        if (blacklisted) {
            // Already blacklisted
            options[0] = getString(R.string.remove_from_blacklist);
        }
        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setItems(options, (dialogInterface, position) -> {
                    if (position == 0) {
                        addRemoveBlacklist();
                    }
                })
                .create();
    }

    private void addRemoveBlacklist() {
        final List<String> blacklist = PrefsManager.getBlacklist();
        if (blacklisted) {
            blacklist.remove(appId);
        } else {
            blacklist.add(0, appId);
        }
        PrefsManager.writeBlacklist(blacklist);
        Toast.makeText(getActivity(), blacklisted ? R.string.removed_from_blacklist : R.string.added_to_blacklist, Toast.LENGTH_LONG).show();
    }
}
