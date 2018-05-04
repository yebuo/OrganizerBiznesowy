package com.example.yebuo.organizerbiznesowy.Model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;
import java.util.Map;

/**
 * Created by Yebuo on 03.05.2018.
 */

public class Projekt implements Parcelable {

    String uid;
    String daneTytul;
    String daneTermRozp;
    String daneTermZak;
    Map<User, String> osoby;
    List<Zadanie> zadania;
    List<Resource> notatki;
    List<Resource> pliki;

    public Projekt(){

    }

    protected Projekt(Parcel in) {
        uid = in.readString();
        daneTytul = in.readString();
        daneTermRozp = in.readString();
        daneTermZak = in.readString();
//        notatki = in.createTypedArrayList(Resource.CREATOR);
//        pliki = in.createTypedArrayList(Resource.CREATOR);
    }

    public static final Creator<Projekt> CREATOR = new Creator<Projekt>() {
        @Override
        public Projekt createFromParcel(Parcel in) {
            return new Projekt(in);
        }

        @Override
        public Projekt[] newArray(int size) {
            return new Projekt[size];
        }
    };

    public void setDaneTytul(String daneTytul) {
        this.daneTytul = daneTytul;
    }

    public void setDaneTermRozp(String daneTermRozp) {
        this.daneTermRozp = daneTermRozp;
    }

    public void setDaneTermZak(String daneTermZak) {
        this.daneTermZak = daneTermZak;
    }

    public void setOsoby(Map<User, String> osoby) {
        this.osoby = osoby;
    }

    public void setZadania(List<Zadanie> zadania) {
        this.zadania = zadania;
    }

    public void setNotatki(List<Resource> notatki) {
        this.notatki = notatki;
    }

    public void setPliki(List<Resource> pliki) {
        this.pliki = pliki;
    }

    public String getDaneTytul() {
        return daneTytul;
    }

    public String getDaneTermRozp() {
        return daneTermRozp;
    }

    public String getDaneTermZak() {
        return daneTermZak;
    }

    public Map<User, String> getOsoby() {
        return osoby;
    }

    public List<Zadanie> getZadania() {
        return zadania;
    }

    public List<Resource> getNotatki() {
        return notatki;
    }

    public List<Resource> getPliki() {
        return pliki;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(daneTytul);
        parcel.writeString(daneTermRozp);
        parcel.writeString(daneTermZak);

        Map<User, String> osoby;
        List<Zadanie> zadania;
        List<Resource> notatki;
        List<Resource> pliki;
    }
}
