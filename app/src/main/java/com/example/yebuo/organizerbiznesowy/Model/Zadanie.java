package com.example.yebuo.organizerbiznesowy.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Yebuo on 03.05.2018.
 */

public class Zadanie implements Parcelable{
    String osoba;
    String tresc;
    String uid;
    String osobaEmail;
    int stan;
    List<Zadanie> podzadanie;

    public Zadanie(){

    }

    public String getOsobaEmail() {
        return osobaEmail;
    }

    public void setOsobaEmail(String osobaEmail) {
        this.osobaEmail = osobaEmail;
    }

    protected Zadanie(Parcel in) {
        osoba = in.readString();
        tresc = in.readString();
        uid = in.readString();
        osobaEmail = in.readString();
        stan = in.readInt();

//        podzadanie = in.createTypedArrayList(Zadanie.CREATOR);
    }

    public void setOsoba(String osoba) {
        this.osoba = osoba;
    }

    public void setTresc(String tresc) {
        this.tresc = tresc;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setPodzadanie(List<Zadanie> podzadanie) {
        this.podzadanie = podzadanie;
    }

    public static final Creator<Zadanie> CREATOR = new Creator<Zadanie>() {
        @Override
        public Zadanie createFromParcel(Parcel in) {
            return new Zadanie(in);
        }

        @Override
        public Zadanie[] newArray(int size) {
            return new Zadanie[size];
        }
    };

    public String getOsoba() {
        return osoba;
    }

    public String getTresc() {
        return tresc;
    }

    public List<Zadanie> getPodzadanie() {
        return podzadanie;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getStan() {
        return stan;
    }

    public void setStan(int stan) {
        this.stan = stan;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(osoba);
        parcel.writeString(tresc);
        parcel.writeString(uid);
        parcel.writeString(osobaEmail);
        parcel.writeInt(stan);
        parcel.writeTypedList(podzadanie);
    }
}
