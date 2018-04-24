package com.example.yebuo.organizerbiznesowy;

/**
 * Created by Yebuo on 22.04.2018.
 */

public class User {

    public String imie;
    public String nazwisko;
    public String email;
    public String telefon;

    public User (){
    }

    public User(String imie, String nazwisko, String email, String telefon){
        this.email = email;
        this.nazwisko = nazwisko;
        this.imie = imie;
        this.telefon = telefon;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public String getEmail() {
        return email;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getTelefon() {
        return telefon;
    }

    public String getImie() {
        return imie;
    }
}
