package com.gruixuts.geniuscares;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class classGlobal {
    public static final String Any = "25";
    public static final String TAG = "GeniusCares";
    public static final String fitxerImportDB = "AImportar.db";
    public static final String fitxerImportTxt = "AImportar.txt";

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


