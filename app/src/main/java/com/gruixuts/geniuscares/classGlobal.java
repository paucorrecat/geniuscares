package com.gruixuts.geniuscares;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;

import androidx.documentfile.provider.DocumentFile;

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


}


