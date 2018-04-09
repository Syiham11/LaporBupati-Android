package id.go.pekalongankab.laporbupati.Util;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Tanggal {

    public String tanggal(String tgl) {
        String t = null;
        Date date = new Date();
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd HH:ss");
            date = simpleDateFormat.parse(tgl);
            SimpleDateFormat lahir = new SimpleDateFormat("dd/mm/yy HH:ss");
            t = lahir.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return t;
    }

}
