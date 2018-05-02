package com.example.yebuo.organizerbiznesowy.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Yebuo on 24.04.2018.
 */

public class Resource implements Parcelable{

    String nazwa;
    String url;

    public Resource(){

    }

    public Resource(String nazwa, String url){
        this.nazwa = nazwa;
        this.url = url;
    }

    public static final Creator<Resource> CREATOR = new Creator<Resource>() {
        @Override
        public Resource createFromParcel(Parcel in) {
            return new Resource(in);
        }

        @Override
        public Resource[] newArray(int size) {
            return new Resource[size];
        }
    };

    private Resource(Parcel in) {
        nazwa = in.readString();
        url = in.readString();
    }

    public String getNazwa() {
        return nazwa;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(nazwa);
        parcel.writeString(url);
    }
}
