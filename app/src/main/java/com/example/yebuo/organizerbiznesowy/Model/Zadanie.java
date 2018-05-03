package com.example.yebuo.organizerbiznesowy.Model;

import java.util.List;

/**
 * Created by Yebuo on 03.05.2018.
 */

public class Zadanie {
    String osoba;
    String tresc;
    List<Zadanie> podzadanie;

    public String getOsoba() {
        return osoba;
    }

    public String getTresc() {
        return tresc;
    }

    public List<Zadanie> getPodzadanie() {
        return podzadanie;
    }
}
