package com.steevsapps.idledaddy.steam.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Game implements Comparable<Game>, Parcelable {

    @SerializedName("appid")
    public int appId;
    @SerializedName("name")
    public String name;
    @SerializedName("img_logo_url")
    public String iconUrl;
    @SerializedName("playtime_forever")
    public float hoursPlayed;
    @SerializedName("drops_remaining")
    public int dropsRemaining;

    public Game(int appId, String name, float hoursPlayed, int dropsRemaining) {
        this.appId = appId;
        this.name = name;
        this.iconUrl = "http://cdn.akamai.steamstatic.com/steam/apps/" + appId + "/header_292x136.jpg";
        this.hoursPlayed = hoursPlayed;
        this.dropsRemaining = dropsRemaining;
    }

    private Game(Parcel parcel) {
        appId = parcel.readInt();
        name = parcel.readString();
        iconUrl = parcel.readString();
        hoursPlayed = parcel.readFloat();
        dropsRemaining = parcel.readInt();
    }

    @Override
    public int compareTo(@NonNull Game game) {
        if (hoursPlayed == game.hoursPlayed) {
            return 0;
        }
        return hoursPlayed < game.hoursPlayed ? -1 : 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        final Game otherGame = (Game) obj;
        return otherGame.appId == appId;
    }

    @Override
    public int hashCode() {
        // Start with a non-zero constant. Prime is preferred
        int result = 17;
        // Include a hash for each field
        result = 31 * result + appId;
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(appId);
        parcel.writeString(name);
        parcel.writeString(iconUrl);
        parcel.writeFloat(hoursPlayed);
        parcel.writeInt(dropsRemaining);
    }

    public final static Parcelable.Creator<Game> CREATOR = new Parcelable.Creator<Game>() {
        @Override
        public Game createFromParcel(Parcel parcel) {
            return new Game(parcel);
        }

        @Override
        public Game[] newArray(int i) {
            return new Game[i];
        }
    };
}
