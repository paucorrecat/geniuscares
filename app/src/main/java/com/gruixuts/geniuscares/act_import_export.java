package com.gruixuts.geniuscares;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;
import android.net.Uri;
import android.provider.DocumentsContract;

public class act_import_export extends AppCompatActivity {


    SimpleDateFormat frmtData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String Separador = ",";
    TextView estat;
    String CarpetaCopies; // Inicialitzada dinàmicament amb FileUtils

    static final int REQUEST_CODE_PICK_FOLDER = 42;  //ppp
    private ActivityResultLauncher<Intent> selectorDeCarpetaLauncher;//ppp
    private static final String CONFIG_NAME = "GeniusCaresConfig";
    private static final String CONFIG_URI = "carpetaUri";//ppp
    private static final String CONFIG_COPIA_DADES = "CopiaDades";//ppp
    private static final String CONFIG_COPIA_TXT = "CopiaTxt";//ppp
    private static final String CONFIG_IMPORT_DADES = "ImportDades";//ppp
    private static final String CONFIG_IMPORT_TXT = "ImportTxt";//ppp
    Uri carpetaUri = null; // ppp
    Uri carpetaCopiaDades = null;
    Uri carpetaCopiaTxt = null;
    Uri carpetaImportDades = null;
    Uri carpetaImportTxt = null;

    // Boolean permisosOk = true; // Sempre true per Android 11+

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_export);

        estat = findViewById(R.id.txtEstat);
        estat.setText("preparat");
        
        // Inicialitzar carpeta amb FileUtils (compatible Android 11+)

        // CarpetaCopies = FileUtils.getCarpetaCopies(this);  //urgent: canviar a Pau/GeniusCares
        
        // No cal sol·licitar permisos per directoris específics de l'app
        // permisosOk = true; ja no val

        selectorDeCarpetaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri uri = data.getData();
                            guardarUriPersistent(uri);  // mètode que ja tens
                         //   copiarBaseDeDadesAmbSAF(uri);  // mètode que ja tens
                        }
                    }
                }
        );
        /******* Carpeta Principal  *******/
        // carpetaUri és on hi pengen les carpetes de la app. És de la que demanem permisos
        carpetaUri = recuperarUriPersistent();
        if (carpetaUri == null) {
            // Seleccionar carpeta principal i demanar permís per accedir-ho
            obrirSelectorDeCarpeta();  // Només es fa si no en tenim cap
            return;
        }
        // Obtenim el documentUri base
        String treeDocumentId = DocumentsContract.getTreeDocumentId(carpetaUri);
        carpetaUri = DocumentsContract.buildDocumentUriUsingTree(carpetaUri, treeDocumentId);

        estat.setText("Directori inicialitzat: " + carpetaUri.toString());

        /******* Carpeta de CopiaDades  *******/
        // Obtenim el documentUri base

        // Mirem si existeix, si no existeix, la creem
        try (Cursor cursor = getContentResolver().query(
                DocumentsContract.buildChildDocumentsUriUsingTree(carpetaUri, treeDocumentId),
                new String[]{DocumentsContract.Document.COLUMN_DOCUMENT_ID, DocumentsContract.Document.COLUMN_DISPLAY_NAME, DocumentsContract.Document.COLUMN_MIME_TYPE},
                null, null, null)) {

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String documentId = cursor.getString(0);
                    String nom = cursor.getString(1);
                    String mime = cursor.getString(2);

                    if (CONFIG_COPIA_DADES.equals(nom) && DocumentsContract.Document.MIME_TYPE_DIR.equals(mime)) {
                        carpetaCopiaDades = DocumentsContract.buildDocumentUriUsingTree(carpetaUri, documentId);
                    }
                    if (CONFIG_COPIA_TXT.equals(nom) && DocumentsContract.Document.MIME_TYPE_DIR.equals(mime)) {
                        carpetaCopiaTxt = DocumentsContract.buildDocumentUriUsingTree(carpetaUri, documentId);
                    }
                    if (CONFIG_IMPORT_DADES.equals(nom) && DocumentsContract.Document.MIME_TYPE_DIR.equals(mime)) {
                        carpetaImportDades = DocumentsContract.buildDocumentUriUsingTree(carpetaUri, documentId);
                    }
                    if (CONFIG_IMPORT_TXT.equals(nom) && DocumentsContract.Document.MIME_TYPE_DIR.equals(mime)) {
                        carpetaImportTxt = DocumentsContract.buildDocumentUriUsingTree(carpetaUri, documentId);
                    }
                }
            }
        }

        // Si no existeix, la creem
        if (carpetaCopiaDades == null) {
            try {
                carpetaCopiaDades = DocumentsContract.createDocument(
                        getContentResolver(),
                        carpetaUri,
                        DocumentsContract.Document.MIME_TYPE_DIR,
                        CONFIG_COPIA_DADES
                );
            } catch (IOException e) {
                classGlobal.mostraError(this,"Error","No s'ha pogut crear la carpeta CopiaDades \n\n" + e.toString());
                e.printStackTrace();
            }
        }
        // Si no existeix, la creem
        if (carpetaCopiaTxt == null) {
            try {
                carpetaCopiaTxt = DocumentsContract.createDocument(
                        getContentResolver(),
                        carpetaUri,
                        DocumentsContract.Document.MIME_TYPE_DIR,
                        CONFIG_COPIA_TXT
                );
            } catch (IOException e) {
                classGlobal.mostraError(this,"Error","No s'ha pogut crear la carpeta CopiaTxt \n\n" + e.toString());
                e.printStackTrace();
            }
        }
        // Si no existeix, la creem
        if (carpetaImportDades == null) {
            try {
                carpetaImportDades = DocumentsContract.createDocument(
                        getContentResolver(),
                        carpetaUri,
                        DocumentsContract.Document.MIME_TYPE_DIR,
                        CONFIG_IMPORT_DADES
                );
            } catch (IOException e) {
                classGlobal.mostraError(this,"Error","No s'ha pogut crear la carpeta ImportTxt \n\n" + e.toString());
                e.printStackTrace();
            }
        }
        // Si no existeix, la creem
        if (carpetaImportTxt == null) {
            try {
                carpetaImportTxt = DocumentsContract.createDocument(
                        getContentResolver(),
                        carpetaUri,
                        DocumentsContract.Document.MIME_TYPE_DIR,
                        CONFIG_IMPORT_TXT
                );
            } catch (IOException e) {
                classGlobal.mostraError(this,"Error","No s'ha pogut crear la carpeta ImportDB \n\n" + e.toString());
                e.printStackTrace();
            }
        }
        // carpetaCopiaDades ja està creada

    }

    /** És pel primer cop que es vol accedir a la carpeta on hi haurà les dades.
     * Fa seleccionar a l'usuari quina carpeta, i demana permisos d'accés
     */
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
        getSharedPreferences(CONFIG_NAME, MODE_PRIVATE)
                .edit()
                .putString(CONFIG_URI, uri.toString())
                .apply();
    }

    @Nullable
    Uri recuperarUriPersistent() {
        String uriString = getSharedPreferences(CONFIG_NAME, MODE_PRIVATE)
                .getString(CONFIG_URI, null);
        return (uriString != null) ? Uri.parse(uriString) : null;
    }

    public Integer ANum(String a) {
        // Resol un fallo del sistema que no tinc controlat, a cops cal posar substr i a cops no
        try {
            return Integer.parseInt(a);
        } catch (Exception e) {
            return Integer.parseInt(a.substring(1));
        }
    }


    public void ImportarDB(View view) {
        exportar(view);
        ImportaBaseDeDadesAmbSAF();
    }

    public void ImportarTxt(View view) {
        GestorDB db = new GestorDB(getApplicationContext());
        TextView elim = findViewById(R.id.edtEliminarOk);
        TextView estat = findViewById(R.id.txtEstat);
        TextView nomfit = findViewById(R.id.edtNomFitxerImport);
        String[] camps;
        File fitxer;
        BufferedReader Buf; // Buffer del fitxer
        String txt; // On es llegeix cada línia
        long NumLin = 0;

        // Provisional, per a fer proves:
        nomfit.setText("AImportar.txt");
        elim.setText("ELIMINAR");
        // Miro si s'ha escrit la paraula ELIMINAR (per seguretat)
        if (elim.getText().toString().compareTo("ELIMINAR") == 0) {
            estat.setText("Buscant fitxers");
            try {
                // Controlem que el fitxer existeix
                fitxer = new File(CarpetaCopies,nomfit.getText().toString());
                if (!fitxer.exists()) {
                    estat.setText("El fitxer no existeix");
                    return;
                }
            } catch (Exception e) {
                estat.setText("Error al mirar si el fitxer existeix: " + e.getMessage());
                return;
            }

            estat.setText("Important de " + nomfit.getText().toString());

            try {
                // Llegim les dades
                Buf = new BufferedReader((new InputStreamReader(new FileInputStream(fitxer))));
                db.open();
                db.delDiccionari();  //Buidem tot lo anterior
                while ((txt = Buf.readLine()) != null) {
                    // Todo: Posar nº de línia en el missatges d'error
                    NumLin++;
                    camps = txt.split(Separador);
                    switch (camps[0]) {
                        case "V": // Versió
                            break; // Mentre no fem noves versions, no cal
                        case "A": // Alumne o persona a recordar
                            db.insDiccionari(new classPersones(
                                    ANum(camps[1]), //Id
                                    camps[2],                   //Imatges
                                    camps[3],                   //Nom
                                    camps[4],                   //Cognom1
                                    camps[5],                   //Cognom2
                                    camps[6],                   //Curs
                                    camps[7],                   //Codi
                                    camps[8],                   //PAV
                                    camps[9],                   //Comentaris
                                    camps[10],                   //Grup
                                    camps[11],                   //NextTipus
                                    camps[12].equals("") ? null : frmtData.parse(camps[12]), //NextData
                                    camps[13].equals("true"),
                                    camps[14].equals("false"))
                            );  //urgent: revisar l'estructura de import per a que contingui TeImatge
                            break; // Mentre no fem noves versions, no cal
                        case "P": // Proves
                            /*
                            try {
                                num1 = Integer.parseInt(camps[0]);
                            } catch (Exception e) {
                                num1 = Integer.parseInt(camps[0].substring(1));
                            }
                            try {
                                num2 = Integer.parseInt(camps[4]);
                            } catch (Exception e) {
                                num2 = Integer.parseInt(camps[4].substring(1));
                            }
                            try {
                                num3 = Integer.parseInt(camps[5]);
                            } catch (Exception e) {
                                num3 = Integer.parseInt(camps[5].substring(1));
                            }
                            try {
                                num4 = Long.parseLong(camps[6]);
                            } catch (Exception e) {
                                num4 = Long.parseLong(camps[6].substring(1));
                            }
                            Boolean p = camps[13].equals("True");
                            db.insProves(new classProves(
                                    num1,                        //Id
                                    GestorDB.AData(camps[1]),                   //Dia
                                    camps[2],                   //TipusProva
                                    camps[3],                   //Seleccio
                                    num2,                   //NumPreguntes
                                    num3,                   //NumRespostes
                                    num4,                   //Temps
                                    !camps[9].equals("0"))
                            );*/
                            break;
                        case "R":  // Resultats
                            /*
                            Resultat = new classResultats(txt, Separador);
                            db.insResultat(Resultat);
                            */
                            break;
                        default:
                            estat.setText("Error al llegir la línia, tipus de registre no conegut " );

                    }
                }
                db.close();
            } catch (Exception e) {
                estat.setText("Dades no carregades: " + e.getMessage());
            }

            estat.setText("Tot importat correctament de " + nomfit.getText().toString());


        } else {
            estat.setText("No s'ha posat ELIMINAR");
        }
    }

    public void ImportarNou(View view) {
        GestorDB db = new GestorDB(getApplicationContext());
        TextView estat = findViewById(R.id.txtEstat);

        String[] camps;
        String Carpeta = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pau";
        File fitxer;
        BufferedReader Buf;
        int NumVer = 1;
        String txtVer = "001";
        int Id;
        String txt;
        classPersones nEntDic;
        classPersones antEntDic;
        String Tip;

        estat.setText("Buscant fitxers");
        try {
            // Buscant la versió correcta
            fitxer = new File(Carpeta, "PcDiccionari001.txt");
            while (!fitxer.exists() && NumVer < 1000) {
                NumVer++;
                txtVer = ("000" + NumVer);
                txtVer = txtVer.substring(txtVer.length() - 3, txtVer.length());
                fitxer = new File(Carpeta, "PcDiccionari" + txtVer + ".txt");
            }
            while (fitxer.exists() && NumVer < 1000) {
                NumVer++;
                txtVer = ("000" + NumVer);
                txtVer = txtVer.substring(txtVer.length() - 3, txtVer.length());
                fitxer = new File(Carpeta, "PcDiccionari" + txtVer + ".txt");
            }
            if (NumVer < 1000) {
                txtVer = ("000" + (NumVer - 1));
                txtVer = txtVer.substring(txtVer.length() - 3, txtVer.length());
            } else {
                return;
            }
        } catch (Exception e) {
            estat.setText("Error al buscar fitxers: " + e.getMessage());
        }
        estat.setText("Important de PcDiccionari" + txtVer + ".txt");
        try {
            // Diccionari
            fitxer = new File(Carpeta, "PcDiccionari" + txtVer + ".txt");
            Buf = new BufferedReader((new InputStreamReader(new FileInputStream(fitxer))));
            db.open();
            while ((txt = Buf.readLine()) != null) {
                camps = txt.split(Separador);
                try {
                    Id = Integer.parseInt(camps[0]);
                } catch (Exception e) {
                    Id = Integer.parseInt(camps[0].substring(1));
                }
                antEntDic = db.selEntDic(Id);
                    if (camps[2].length() == 0) {
                        Tip = "t";
                    } else {
                        Tip = "a";
                    }
                    db.creaDiccionari(new classPersones(
                            Id,                        //Id
                            camps[1],                   //Imatges
                            camps[2],                   //Nom
                            camps[3],                   //Cognom1
                            camps[4],                   //Num
                            camps[5],                   //Curs
                            camps[6],                   //Codi
                            camps[7],                   //PAV
                            camps[8],                   //Comentaris
                            camps[9],                   //Grup
                            Tip,                        //NextTipus
                            null,             //NextData
                            camps[12].equals("true"),    // AMemoritzar
                            camps[13].equals("true")));  // TeImatge
            }
            db.close();
        } catch (Exception e) {
            estat.setText("Diccionari no carregat: " + e.getMessage());
        }
        estat.setText("Importat de PcDiccionari" + txtVer + ".txt");


    }

    void copiarBaseDeDadesAmbSAF(Uri carpetaDesti) {
        String nomFitxer = "GeniusCares_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) +
                ".db";

        try {
            // 1. Crear el fitxer a la carpeta de destí
            Uri destiUri = DocumentsContract.createDocument(
                    getContentResolver(),
                    carpetaDesti,  // Ara utilitzem la carpeta específica CopiaDades
                    "application/vnd.sqlite3",
                    nomFitxer
            );

            if (destiUri != null) {
                // 2. Obrir la base de dades local (forma correcta)
                File dbFile = new File(getApplicationInfo().dataDir + "/databases/GeniusCares.db");
                InputStream input = new FileInputStream(dbFile);
                OutputStream output = getContentResolver().openOutputStream(destiUri);

                // 3. Copiar el contingut
                byte[] buf = new byte[1024];
                int len;
                while ((len = input.read(buf)) > 0) {
                    output.write(buf, 0, len);
                }

                // 4. Tancar fluxos
                input.close();
                output.close();

                Log.i(classGlobal.TAG, "Còpia feta correctament a: " + nomFitxer);
                estat.setText("Base de dades copiada a " + nomFitxer);
            }
        } catch (Exception e) {
            Log.e(classGlobal.TAG, "Error copiant la base de dades: " + e.getMessage());
            estat.setText("Error copiant BD: " + e.getMessage());
        }
    }

    void ImportaBaseDeDadesAmbSAF() {
        if (carpetaImportDades == null) {
            estat.setText("Error: carpeta d'importació no inicialitzada.");
            return;
        }

        try (Cursor cursor = getContentResolver().query(
                DocumentsContract.buildChildDocumentsUriUsingTree(carpetaImportDades,
                        DocumentsContract.getDocumentId(carpetaImportDades)),
                new String[]{DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                        DocumentsContract.Document.COLUMN_DISPLAY_NAME},
                null, null, null)) {

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String documentId = cursor.getString(0);
                    String nom = cursor.getString(1);

                    if ("AImportar.db".equals(nom)) {
                        Uri origenUri = DocumentsContract.buildDocumentUriUsingTree(carpetaImportDades, documentId);

                        File destiBD = new File(getApplicationInfo().dataDir + "/databases/GeniusCares.db");
                        InputStream input = getContentResolver().openInputStream(origenUri);
                        OutputStream output = new FileOutputStream(destiBD, false);

                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = input.read(buf)) > 0) {
                            output.write(buf, 0, len);
                        }

                        input.close();
                        output.close();

                        estat.setText("Base de dades importada correctament.");
                        Log.i(classGlobal.TAG, "Base de dades importada des de AImportar.db");
                        return;
                    }
                }
            }

            estat.setText("Fitxer AImportar.db no trobat a ImportDades.");

        } catch (Exception e) {
            estat.setText("Error important la base de dades: " + e.getMessage());
            Log.e(classGlobal.TAG, "Error en ImportaBaseDeDadesAmbSAF", e);
        }
    }


    public void exportar(View view) {
        GestorDB db = new GestorDB(getApplicationContext());
        ArrayList<classPersones> LlistaDiccionari;
        ArrayList<classProves> LlistaProves;
        ArrayList<classResultats> LlistaResultats;
        estat.setText("Exportant ");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nomFitxer = "Dades_" + timeStamp + ".txt";

        try {
            db.open();
            LlistaDiccionari = db.selDiccionari("", "Id");
            LlistaProves = db.selProves("", "");
            LlistaResultats = db.selResultats("", "");
            db.close();

            // Crear el fitxer dins la carpeta CopiaDades que ja has creat
            Uri destiUri = DocumentsContract.createDocument(
                    getContentResolver(),
                    carpetaCopiaTxt,
                    "text/plain",
                    nomFitxer
            );

            if (destiUri != null) {
                OutputStreamWriter fout = new OutputStreamWriter(
                        getContentResolver().openOutputStream(destiUri));

                // Escriure les dades com ho feies abans
                for (int n = 0; n < LlistaDiccionari.size(); n++) {
                    try {
                        fout.write("A" + Separador);
                        fout.write(LlistaDiccionari.get(n).getId().toString() + Separador);
                        fout.write(LlistaDiccionari.get(n).getImatges().toString() + Separador);
                        fout.write(LlistaDiccionari.get(n).getNom().toString() + Separador);
                        fout.write(LlistaDiccionari.get(n).getCognom1().toString() + Separador);
                        fout.write(LlistaDiccionari.get(n).getNum().toString() + Separador);
                        fout.write(LlistaDiccionari.get(n).getCurs().toString() + Separador);
                        fout.write(LlistaDiccionari.get(n).getCodi().toString() + Separador);
                        fout.write(LlistaDiccionari.get(n).getPAV().toString() + Separador);
                        fout.write(LlistaDiccionari.get(n).getComentaris().toString() + Separador);
                        fout.write(LlistaDiccionari.get(n).getGrup().toString() + Separador);
                        fout.write(LlistaDiccionari.get(n).getNextTipus().toString() + Separador);
                        fout.write(LlistaDiccionari.get(n).getNextDataTxt().toString() + Separador);
                        fout.write(LlistaDiccionari.get(n).getAMemoritzar().toString() + "\r\n");
                        fout.flush();  // Per localitzar errors, quan vagi bé cal treure-ho per accelerar
                    } catch (Exception e) {
                        estat.setText("Diccionari Fallo linia:" + n + " no exportada " + e.getMessage());
                        // MsgBox ¿?
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("Diccionari Fallo linia:" + n + " no exportada " + e.getMessage());
                        builder.setTitle("Atenció!!");
                        builder.setCancelable(false);
                        builder.setNeutralButton("Acceptar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                        // Fi MsgBox
                    }
                }
                for (int n = 0; n < LlistaProves.size(); n++) {
                    try {
                    fout.write("P" + Separador);
                    fout.write(LlistaProves.get(n).getId().toString() + Separador);
                    fout.write(LlistaProves.get(n).getDiaTxt().toString() + Separador);
                    fout.write(LlistaProves.get(n).getTipusProva().toString() + Separador);
                    fout.write(LlistaProves.get(n).getSeleccio().toString() + Separador);
                    fout.write(LlistaProves.get(n).getNumPreguntes().toString() + Separador);
                    fout.write(LlistaProves.get(n).getNumRespostes().toString() + Separador);
                    fout.write(LlistaProves.get(n).getTemps().toString() + Separador);
                    fout.write(LlistaProves.get(n).getAcabada().toString() + "\r\n");
                    fout.flush();
                    } catch (Exception e) {
                        estat.setText("Diccionari Fallo linia:" + n + " no exportada " + e.getMessage());
                        // MsgBox ¿?
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("Proves Fallo linia:" + n + " no exportada " + e.getMessage());
                        builder.setTitle("Atenció!!");
                        builder.setCancelable(false);
                        builder.setNeutralButton("Acceptar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                        // Fi MsgBox
                    }
                }
                for (int n = 0; n < LlistaResultats.size(); n++) {
                    try {
                        fout.write("R" + Separador);
                        fout.write(LlistaResultats.get(n).getDiaTxt().toString() + Separador);
                        fout.write(LlistaResultats.get(n).getIdProva().toString() + Separador);
                        fout.write(LlistaResultats.get(n).getIdEntDic().toString() + Separador);
                        fout.write(LlistaResultats.get(n).getPregunta().toString() + Separador);
                        fout.write(LlistaResultats.get(n).getResposta().toString() + Separador);
                        fout.write(LlistaResultats.get(n).getCorrecta().toString() + Separador);
                        fout.write(LlistaResultats.get(n).getTemps().toString() + Separador);
                        fout.write(LlistaResultats.get(n).getValoracio().toString() + "\r\n");
                        fout.flush();
                    } catch (Exception e) {
                        estat.setText("Diccionari Fallo linia:" + n + " no exportada " + e.getMessage());
                        // MsgBox ¿?
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("Resultats Fallo linia:" + n + " no exportada " + e.getMessage());
                        builder.setTitle("Atenció!!");
                        builder.setCancelable(false);
                        builder.setNeutralButton("Acceptar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
                fout.close();
                estat.setText("Exportat correctament a " + nomFitxer);
            }
        } catch (Exception e) {
            estat.setText("Error en exportar: " + e.getMessage());
            Log.e("ExportError", "Error en exportar", e);
        }

        // Copiar la base de dades
        copiarBaseDeDadesAmbSAF(carpetaCopiaDades);
    }

}
