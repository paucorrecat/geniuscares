package com.gruixuts.geniuscares;

import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    /** Sistema d'accés SAF (Storage Access Framework)
     * Selector de carpeta on hi ha copies, importació, imatges, ...
     * Normalment cal seleccionar /Pau/GeniusCares, però l'usuari podrà triar el primer cop i donar permisos
     */
    private ActivityResultLauncher<Intent> selectorDeCarpetaLauncher;
    Uri carpetaUri = null; // ppp
    DocumentFile carpetaExterna = null;
    DocumentFile carpetaBase = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Registrem el launcher que demana la carpeta, per si l'hem de menester
        selectorDeCarpetaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri uri = data.getData();
                            guardarUriPersistent(uri); // Ho desa a preferences
                            carpetaExterna = DocumentFile.fromTreeUri(this,uri); // Assigna valor local
                            classGlobal.carpetaExterna = carpetaExterna; // Desa valor a classGlobal per a us desde les altres activitats
                        }
                    }
                }
        );
        // Registrat el Launcher
        // Intentem recuperar la carpetaUri. Si ja la tenim autoritzada, actualitzem el valor a classGlobal. Si no li
        // hem de demanar a l'usuari que la seleccioni i ens doni permisos
        carpetaUri = recuperarUriPersistent();
        if (carpetaUri == null) {
            // Seleccionar carpeta principal i demanar permís per accedir-ho
            obrirSelectorDeCarpeta();  // Només es fa si no en tenim cap
            //return;
        } else {
            carpetaExterna = DocumentFile.fromTreeUri(this, carpetaUri);
            classGlobal.carpetaExterna = carpetaExterna;
            //DocumentFile carpeta = DocumentFile.fromTreeUri(context, uri);
        }
        assert carpetaExterna != null: "carpetaExterna ha quedat indefinida";


        // comprovem que existeix la carpeta Imatges i si no, la creem
        DocumentFile carpetaImatges = carpetaExterna.findFile("Imatges");

        if (carpetaImatges == null || !carpetaImatges.isDirectory()) {
            carpetaImatges = carpetaExterna.createDirectory("Imatges");
        }
        classGlobal.carpetaImatges = carpetaImatges;

    }

    void obrirSelectorDeCarpeta() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION |
                Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);  // Això dona permisos per a totes les subcarpetes
        selectorDeCarpetaLauncher.launch(intent);
    }

    void guardarUriPersistent(Uri uri) {
        getContentResolver().takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        );
        getSharedPreferences(classGlobal.configNom, MODE_PRIVATE)
                .edit()
                .putString(classGlobal.configUri, uri.toString())
                .apply();
    }

    @Nullable
    Uri recuperarUriPersistent() {
        String uriString = getSharedPreferences(classGlobal.configNom, MODE_PRIVATE)
                .getString(classGlobal.configUri, null);
        return (uriString != null) ? Uri.parse(uriString) : null;
    }



    public void goToExamen(View view) {
        Intent myIntent = new Intent(MainActivity.this, act_examen_sel.class);
        MainActivity.this.startActivity(myIntent);
    }

    public void goToRepas(View view) {
        Intent myIntent = new Intent(MainActivity.this, act_repas_sel.class);
        MainActivity.this.startActivity(myIntent);
    }

    public void goToMemoritzar(View view) {
        Intent myIntent = new Intent(MainActivity.this, act_memoritzar_sel.class);
        MainActivity.this.startActivity(myIntent);
    }

    public void goToManteniment(View view) {
        Intent myIntent = new Intent(MainActivity.this, act_manteniment_sel.class);
        MainActivity.this.startActivity(myIntent);
    }

    public void goToImportExport(View view) {
        Intent myIntent = new Intent(MainActivity.this, act_import_export.class);
        MainActivity.this.startActivity(myIntent);
    }

    public void goToStats(View view) {
        Intent myIntent = new Intent(MainActivity.this, act_estadistica.class);
        MainActivity.this.startActivity(myIntent);
    }


};


