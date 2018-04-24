package com.example.yebuo.organizerbiznesowy;

/**
 * Created by Yebuo on 24.04.2018.
 */

public class Notatka {

    String nazwa;
    String url;

    public Notatka(){

    }

    public Notatka(String nazwa, String url){
        this.nazwa = nazwa;
        this.url = url;
    }

    public String getNazwa() {
        return nazwa;
    }

    public String getUrl() {
        return url;
    }
}
