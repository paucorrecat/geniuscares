package com.gruixuts.geniuscares;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;

import androidx.documentfile.provider.DocumentFile;

import java.util.HashMap;
import java.util.Map;

public class classGlobal {
    public static final String Any = "25";
    public static final String TAG = "GeniusCares";
    public static final String fitxerImportDB = "AImportar.db";
    public static final String fitxerImportTxt = "AImportar.txt";
    // Preferences
    public static final String configNom = "GeniusCaresConfig";  // Nom de la configuració (Id de la app)
    public static final String configUri = "carpetaUri";  // Clau de la Uri de la carpeta externa (/Pau/GeniusCares)
    // Carpeta externa
    public static DocumentFile carpetaExterna = null;
    public static DocumentFile carpetaImatges = null;


    public static void mostraError(Context context, String titol, String missatge) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titol);
        builder.setMessage(missatge);
        builder.setCancelable(false);
        builder.setNeutralButton("D'acord", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    static Map<String, Integer> numerals = new HashMap<>();
    static {
        numerals.put("zero", 0);
        numerals.put("u", 1);
        numerals.put("un", 1);
        numerals.put("una", 1);
        numerals.put("dos", 2);
        numerals.put("segon", 2);
        numerals.put("tres", 3);
        numerals.put("tercer", 3);
        numerals.put("iii", 3);
        numerals.put("quatre", 4);
        numerals.put("cinc", 5);
        numerals.put("sis", 6);
        numerals.put("set", 7);
        numerals.put("vuit", 8);
        numerals.put("nou", 9);
        numerals.put("deu", 10);
        numerals.put("onze", 11);
        numerals.put("dotze", 12);
        numerals.put("tretze", 13);
        numerals.put("catorze", 14);
        numerals.put("quinze", 15);
        numerals.put("setze", 16);
        numerals.put("disset", 17);
        numerals.put("divuit", 18);
        numerals.put("dinou", 19);
        numerals.put("vint", 20);
    }

    public static int paraulaANumero(String text) {
        return numerals.getOrDefault(text.toLowerCase(), -1);
    }



}


