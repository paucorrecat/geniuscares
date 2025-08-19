package com.gruixuts.geniuscares;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.net.Uri;
import android.provider.DocumentsContract;


public class act_import_export extends AppCompatActivity {


    SimpleDateFormat frmtData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String Separador = ",";
    TextView estat;
    String CarpetaCopies; // Inicialitzada dinàmicament amb FileUtils

    static final int REQUEST_CODE_PICK_FOLDER = 42;  //ppp
    private static final String CONFIG_COPIA_DADES = "CopiaDades";//ppp
    private static final String CONFIG_COPIA_TXT = "CopiaTxt";//ppp
    private static final String CONFIG_IMPORT_DADES = "ImportDades";//ppp
    private static final String CONFIG_IMPORT_TXT = "ImportTxt";//ppp
    private static final String CONFIG_AIMPORTAR_DB = "AImportar.db";//ppp
    private static final String CONFIG_AIMPORTAR_TXT = "AImportar.txt";//ppp

    Uri carpetaUri = null; // ppp
    DocumentFile carpExt = null; // Carpeta externa (Pau/GeniusCares)
    DocumentFile carpCopiaDades = null;
    DocumentFile carpCopiaTxt = null;
    DocumentFile carpImportDades = null;
    DocumentFile carpImportTxt = null;

    // Boolean permisosOk = true; // Sempre true per Android 11+
    private GestorDB db;

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

        /******* Carpeta Principal  *******/
        // carpetaUri és on hi pengen les carpetes de la app. És de la que demanem permisos
        carpExt = classGlobal.carpetaExterna;
        if (carpExt == null) {
            // Seleccionar carpeta principal i demanar permís per accedir-ho
            classGlobal.mostraError(this, "Accés a carpeta","No hi ha carpetaUri");
            return;
        }
        // Obtenim el documentUri base
        estat.setText("Directori inicialitzat: " + carpExt.toString());

        /******* Carpeta de CopiaDades  *******/
        // Obtenim el documentUri base

        // comprovem que existeix la carpeta Imatges i si no, la creem
        carpCopiaDades = carpExt.findFile(CONFIG_COPIA_DADES);
        if (carpCopiaDades == null || !carpCopiaDades.isDirectory()) {
            carpCopiaDades = carpExt.createDirectory(CONFIG_COPIA_DADES);
        }
        carpCopiaTxt = carpExt.findFile(CONFIG_COPIA_TXT);
        if (carpCopiaTxt == null || !carpCopiaTxt.isDirectory()) {
            carpCopiaTxt = carpExt.createDirectory(CONFIG_COPIA_TXT);
        }
        carpImportDades = carpExt.findFile(CONFIG_IMPORT_DADES);
        if (carpImportDades == null || !carpImportDades.isDirectory()) {
            carpImportDades = carpExt.createDirectory(CONFIG_IMPORT_DADES);
        }
        carpImportTxt = carpExt.findFile(CONFIG_IMPORT_TXT);
        if (carpImportTxt == null || !carpImportTxt.isDirectory()) {
            carpImportTxt = carpExt.createDirectory(CONFIG_IMPORT_TXT);
        }
        // carpetaCopiaDades ja està creada
       db = GestorDB.getInstance(this);


    }

    /** És pel primer cop que es vol accedir a la carpeta on hi haurà les dades.
     * Fa seleccionar a l'usuari quina carpeta, i demana permisos d'accés
     */

    public Integer ANum(String a) {
        // Resol un fallo del sistema que no tinc controlat, a cops cal posar substr i a cops no
        try {
            return Integer.parseInt(a);
        } catch (Exception e) {
            return Integer.parseInt(a.substring(1));
        }
    }


    public void ImportarDB(View view) {
        //exportar(view);
        //TODO: Tornar a posar exportar
        //ImportaBaseDeDadesAmbSAF();
        iniciarImportacio();
    }

    public void ImportarTxt(View view) {
        GestorDB db = GestorDB.getInstance(getApplicationContext());
        TextView elim = findViewById(R.id.edtEliminarOk);
        TextView estat = findViewById(R.id.txtEstat);
        TextView nomfit = findViewById(R.id.edtNomFitxerImport);
        String[] camps;
        String txt;
        long NumLin = 0;

        // tEMPORAL, PER A FER PROVES
        nomfit.setText(CONFIG_AIMPORTAR_TXT);
        // Temporal
        elim.setText("ELIMINAR");

        if (!"ELIMINAR".equals(elim.getText().toString())) return;

        estat.setText("Buscant fitxers");

        try {
            DocumentFile fitxerDoc = carpImportTxt.findFile(nomfit.getText().toString());
            if (fitxerDoc == null || !fitxerDoc.exists()) {
                estat.setText("El fitxer no existeix");
                return;
            }

            estat.setText("Important de " + nomfit.getText().toString());

            try (InputStream inputStream = getContentResolver().openInputStream(fitxerDoc.getUri());
                 BufferedReader Buf = new BufferedReader(new InputStreamReader(inputStream))) {

                db.open();
                db.delPersones();

                while ((txt = Buf.readLine()) != null) {
                    NumLin++;
                    camps = txt.split(Separador);

                    switch (camps[0]) {
                        case "V":
                            break;
                        case "A":
                            db.insPersones(new classPersones(
                                    ANum(camps[1]),
                                    camps[2],
                                    camps[3],
                                    camps[4],
                                    camps[5],
                                    camps[6],
                                    camps[7],
                                    camps[8],
                                    camps[9],
                                    camps[10],
                                    camps[11],
                                    camps[12].equals("") ? null : frmtData.parse(camps[12]),
                                    camps[13].equals("true"),
                                    camps[14].equals("true"))
                            );
                            break;
                        case "P":
                            // pendent
                            break;
                        case "R":
                            // pendent
                            break;
                        default:
                            estat.setText("Error a la línia " + NumLin + ": tipus desconegut");
                            break;
                    }
                }

                db.close();
                estat.setText("Importació completada");

            } catch (Exception e) {
                estat.setText("Error de lectura: " + e.getMessage());
            }

        } catch (Exception e) {
            estat.setText("Error: " + e.getMessage());
        }
    }


    void exportFitxerBaseDeDades() {  //Exportació
        String nomFitxer = "GeniusCares_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) +
                ".db";

        try {
            // 1. Crear el fitxer a la carpeta de destí
//            Uri destiUri = DocumentsContract.createDocument(
//                    getContentResolver(),
//                    carpCopiaDades.getUri(),  // Ara utilitzem la carpeta específica CopiaDades
//                    "application/vnd.sqlite3",
//                    nomFitxer
//            );

            if (carpCopiaDades != null) {
                // 2. Obrir la base de dades local (forma correcta)
                //File dbFile = new File(getApplicationInfo().dataDir + "/databases/GeniusCares.db");
                File dbFile = getDatabasePath("GeniusCares.db");
                InputStream input = new FileInputStream(dbFile);
//                OutputStream output = getContentResolver().openOutputStream(carpCopiaDades.getUri());

                DocumentFile fitxer = carpCopiaDades.createFile(
                        "application/octet-stream", // mime type genèric per binaris
                                                        // també podria ser "application/vnd.sqlite3"
                        nomFitxer);

                OutputStream output = getContentResolver().openOutputStream(fitxer.getUri());

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
                estat.setText("Base de dades copiada a " + carpCopiaDades.getName() + "/" + nomFitxer);
            }
        } catch (Exception e) {
            Log.e(classGlobal.TAG, "Error copiant la base de dades: " + e.getMessage());
            estat.setText("Error copiant BD: " + e.getMessage());
        }
    }

    // Aquesta funció es crida des del botó
    public void iniciarImportacio() {
        // Mostra un missatge a l'usuari
        estat.setText("Important dades...");

        // Tanca qualsevol connexió a la BD que pugui estar oberta
        // Això és CRUCIAL fer-ho abans de començar
        if (db != null) {
            db.close();
            Log.d("Importacio", "GestorDB tancat abans de la còpia.");
        }

        // Crida a la funció de còpia
        boolean exit = importFitxerBaseDeDades();

        // Gestiona el resultat
        if (exit) {
            estat.setText("Importació completada amb èxit.");
            Log.i("Importacio", "El fitxer s'ha copiat correctament.");

            // Ara, per veure les dades, has de refrescar la teva vista.
            // Aquesta acció forçarà a GestorDB a obrir el nou fitxer.
            // Ex: carregarLlistaDePersones();

        } else {
            estat.setText("Error durant la importació.");
            Log.e("Importacio", "La còpia del fitxer ha fallat.");
        }
    }

    // Aquesta és la funció que NOMÉS copia el fitxer
    private boolean importFitxerBaseDeDades() {
        if (carpImportDades == null) {
            Log.e("Importacio", "La carpeta d'importació és nul·la.");
            return false;
        }

        DocumentFile aImportar = carpImportDades.findFile(CONFIG_AIMPORTAR_DB);

        if (aImportar == null || !aImportar.exists() || aImportar.length() == 0) {
            Log.e("Importacio", "El fitxer AImportar.db no es troba o està buit.");
            return false;
        }

        File destiBD = getDatabasePath("GeniusCares.db");

        // Utilitzem try-with-resources per assegurar que els streams es tanquen sempre
        try (InputStream input = getContentResolver().openInputStream(aImportar.getUri());
             OutputStream output = new FileOutputStream(destiBD)) {

            if (input == null) {
                Log.e("Importacio", "El InputStream és nul. No es pot llegir el fitxer d'origen.");
                return false;
            }

            byte[] buf = new byte[8192];
            int len;
            while ((len = input.read(buf)) > 0) {
                output.write(buf, 0, len);
            }

            output.flush(); // Forcem l'escriptura de qualsevol buffer restant

        } catch (Exception e) {
            Log.e("Importacio", "EXCEPCIÓ durant la còpia del fitxer", e); // Mostrem l'error complet
            return false;
        }

        // Verificació final
        if (destiBD.exists() && destiBD.length() > 0) {
            Log.i("Importacio", "Verificació correcta. El fitxer destí existeix i no està buit. Mida: " + destiBD.length());
            return true;
        } else {
            Log.e("Importacio", "Verificació fallida. El fitxer destí no existeix o està buit després de la còpia.");
            return false;
        }
    }


    public void exportar(View view) {
        GestorDB db = GestorDB.getInstance(getApplicationContext());
        ArrayList<classPersones> LlistaPersones;
        ArrayList<classProves> LlistaProves;
        ArrayList<classResultats> LlistaResultats;
        estat.setText("Exportant ");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nomFitxer = "Dades_" + timeStamp + ".txt";

        try {
            db.open();
            LlistaPersones = db.selPersones("", "Id");
            LlistaProves = db.selProves("", "");
            LlistaResultats = db.selResultats("", "");
            db.close();

            // Crear el fitxer dins la carpeta CopiaDades que ja has creat
            Uri destiUri = DocumentsContract.createDocument(
                    getContentResolver(),
                    carpCopiaTxt.getUri(),
                    "text/plain",
                    nomFitxer
            );

            if (destiUri != null) {
                OutputStreamWriter fout = new OutputStreamWriter(
                        getContentResolver().openOutputStream(destiUri));

                // Escriure les dades com ho feies abans
                for (int n = 0; n < LlistaPersones.size(); n++) {
                    try {
                        fout.write("A" + Separador);
                        fout.write(LlistaPersones.get(n).getId().toString() + Separador);
                        fout.write(LlistaPersones.get(n).getImatges().toString() + Separador);
                        fout.write(LlistaPersones.get(n).getNom().toString() + Separador);
                        fout.write(LlistaPersones.get(n).getCognom().toString() + Separador);
                        fout.write(LlistaPersones.get(n).getNum().toString() + Separador);
                        fout.write(LlistaPersones.get(n).getCurs().toString() + Separador);
                        fout.write(LlistaPersones.get(n).getCodi().toString() + Separador);
                        fout.write(LlistaPersones.get(n).getPAV().toString() + Separador);
                        fout.write(LlistaPersones.get(n).getComentaris().toString() + Separador);
                        fout.write(LlistaPersones.get(n).getGrup().toString() + Separador);
                        fout.write(LlistaPersones.get(n).getNextTipus().toString() + Separador);
                        fout.write(LlistaPersones.get(n).getNextDataTxt().toString() + Separador);
                        fout.write(LlistaPersones.get(n).getAMemoritzar().toString() + "\r\n");
                        fout.flush();  // Per localitzar errors, quan vagi bé cal treure-ho per accelerar
                    } catch (Exception e) {
                        estat.setText("Persones Fallo linia:" + n + " no exportada " + e.getMessage());
                        // MsgBox ¿?
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("Persones Fallo linia:" + n + " no exportada " + e.getMessage());
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
                        estat.setText("Persones Fallo linia:" + n + " no exportada " + e.getMessage());
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
                        fout.write(LlistaResultats.get(n).getIdPers().toString() + Separador);
                        fout.write(LlistaResultats.get(n).getPregunta().toString() + Separador);
                        fout.write(LlistaResultats.get(n).getResposta().toString() + Separador);
                        fout.write(LlistaResultats.get(n).getCorrecta().toString() + Separador);
                        fout.write(LlistaResultats.get(n).getTemps().toString() + Separador);
                        fout.write(LlistaResultats.get(n).getValoracio().toString() + "\r\n");
                        fout.flush();
                    } catch (Exception e) {
                        estat.setText("Persones Fallo linia:" + n + " no exportada " + e.getMessage());
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
        exportFitxerBaseDeDades();
    }

}
