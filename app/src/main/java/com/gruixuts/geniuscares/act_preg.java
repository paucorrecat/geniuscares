package com.gruixuts.geniuscares;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;


public class act_preg extends AppCompatActivity {
    //List<classPersones> Llista;
    SimpleDateFormat frmtData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    GestorDB db;
    Integer Actual;
    Integer avaPer; // ava.. Per fer un seguiment per pantalla dels resultats
    Integer avaRep;
    Integer avaObl;
    classPersones mItem;
    classProves Prova = null;
    String TipusProva;
    int NumProva;
    classResposta resposta;
    classResposta avaluacioInici;
    classResposta avaluacioFinal;
    String TallVeu;
    boolean FetAmbVeu = false;
    String Resultat;

    //String Filtre="";
    //Integer IdEntDic;
    Long TempsIniciProva;
    Long TempsProva;
    Long TempsIniciPregunta;
    Long TempsPregunta;

    private String CarpetaImatges; // = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pau/GeniusCares/Imatges";
//    ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
//    private String CarpetaImatges=contextWrapper.getExternalFilesDir("Imatges").toString();//  o Environment.DIRECTORY_DOCUMENTS enlloc de Copies
    private DocumentFile carpetaImatges = classGlobal.carpetaImatges;
    private DocumentFile[] llistaImatges;
    private int posicioImg = 0;
    //private String CarpetaImatgesItem;  // La carpeta del Item actual
    private Integer numImatge;
    private String nomsImatge[];

    protected static final int REQUEST_CODE = 11;
    protected static final int SPEECH_REQUEST_CODE = 13;
    protected static final String ARG_NUM_PROVA = "tp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preg);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        NumProva = Integer.parseInt(getIntent().getStringExtra(ARG_NUM_PROVA));
        db=new GestorDB(getApplicationContext());
        db.open();
        Prova = db.getProva(NumProva);
        TipusProva = Prova.getTipusProva();
        Actual=0;
        avaRep=0;
        avaPer=0;
        avaObl=0;
        mItem = objLlistaTrobats.ITEMS.get(Actual);
        TempsIniciProva = SystemClock.currentThreadTimeMillis();
        TempsProva=Prova.getTemps();  // Per si ja portava temps
//        CarpetaImatges = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pau/GeniusCares/Imatges";
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        CarpetaImatges=contextWrapper.getExternalFilesDir("Imatges").toString();//  o Environment.DIRECTORY_DOCUMENTS enlloc de Copies

        if (carpetaImatges == null) {
            classGlobal.mostraError(this,"Accés SAF", "No hi ha carpeta d'imatges amb permisos. Torna a iniciar la app");
            finish();
            return;
        }

        //PreguntaSeguent();
        if (objLlistaTrobats.ITEMS.size()==0) {
            classGlobal.mostraError(this, "Error","No hi ha cap pregunta");
            finish();
            return;
        }
        CarregaItem(mItem);
        CarregaResp(new classResposta());

    }

    private void CarregaItem(classPersones item) {
        // Posa a la pantalla la informació de l'alumne
        String Estat;
        assert (item != null);
        Estat = "Actual: " + (Actual + 1) + "/" + objLlistaTrobats.ITEMS.size();
        Estat += " // Perf: " + avaPer;
        Estat += " // Rep: " + avaRep;
        Estat += " // Obl: " + avaObl;
        ((TextView) findViewById(R.id.txtPregCompta)).setText(Estat);

        llistaImatges = obtenirLlistaImatgesSAF(mItem.getImatges());
        if (llistaImatges.length > 0) {
            posicioImg = 0;
            mostraImatgeSAF(llistaImatges[posicioImg]);
        }

        // Imatges:

        /*
        if (item.getImatges() != null) {
            assert (item.getImatges().length() > 0);
            CarpetaImatgesItem = CarpetaImatges + "/" + item.getImatges();
            File carpeta = new File(CarpetaImatgesItem);
            if (!carpeta.exists()) { // Crear-la
                // Global.MissatgeError("La cerpeta d'immatges " + CarpetaImatgesItem + " no existeix",this);
                numImatge = 0;
                //carpeta.mkdirs();
                nomsImatge = new String[0];

            } else {
                nomsImatge = carpeta.list();
            }
            if (nomsImatge.length != 0) {
                numImatge = 1;
                Drawable d = Drawable.createFromPath(CarpetaImatgesItem + "/" + nomsImatge[numImatge - 1]);
                ((ImageView) findViewById(R.id.imgImatges)).setImageDrawable(d);
            } else {
                numImatge = 0;
            }
        } else {
            CarpetaImatgesItem = "";
            numImatge = 0;
            nomsImatge = new String[0];
        }
        if (numImatge==0) {
            ((ImageView) findViewById(R.id.imgImatges)).setImageResource(R.mipmap.ic_launcher);
        }

         */
        TempsIniciPregunta = SystemClock.currentThreadTimeMillis();
    }

    private DocumentFile[] obtenirLlistaImatgesSAF(String nomSubcarpeta) {

        // Si nom està buit
        if (nomSubcarpeta==null || nomSubcarpeta.isEmpty()) {
            classGlobal.mostraError(this,"Error","Aquesta persona no té nom de carpeta d'imatges");
            return new DocumentFile[0];
        }
        // Miro si existeix
        DocumentFile carpImg = carpetaImatges.findFile(nomSubcarpeta);
        if (carpImg==null) {
            return new DocumentFile[0]; // Si no existeix, torno llista buida
        }

        return carpImg.listFiles();
    }

    private void mostraImatgeSAF(DocumentFile docFile) {
        try {
            ImageView imgView = findViewById(R.id.imgImatges);
            Uri uri = docFile.getUri();
            imgView.setImageURI(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fotoSeguent(View view) {
        if (llistaImatges != null && posicioImg < llistaImatges.length - 1) {
            posicioImg++;
            mostraImatgeSAF(llistaImatges[posicioImg]);
        }
    }

    public void fotoAnterior(View view) {
        if (llistaImatges != null && posicioImg > 0) {
            posicioImg--;
            mostraImatgeSAF(llistaImatges[posicioImg]);
        }
    }



    public void CarregaResp(classResposta resp) {
        //
        ((TextView) findViewById(R.id.edtPregNom)).setText(resp.getNom());
        ((TextView) findViewById(R.id.edtPregCognom)).setText(resp.getCognom());
        ((TextView) findViewById(R.id.edtPregCurs)).setText(resp.getCurs());
        ((TextView) findViewById(R.id.edtPregGrup)).setText(resp.getGrup());
        ((TextView) findViewById(R.id.edtPregNum)).setText(resp.getNum());
        FetAmbVeu = false;
    }

    // Imatges:

/* aNTIGUES
    public void fotoSeguent(View view) {
        if ((numImatge < nomsImatge.length) && (numImatge >0) ) {
            numImatge++;
            Drawable d = Drawable.createFromPath(CarpetaImatges + "/" + mItem.getImatges() + "/" + nomsImatge[numImatge - 1]);
            ((ImageView) findViewById(R.id.imgImatges)).setImageDrawable(d);
        }
    }
    public void fotoAnterior(View view) {
        if (numImatge > 1 ) {
            numImatge--;
            Drawable d = Drawable.createFromPath(CarpetaImatges + "/" + mItem.getImatges() + "/" + nomsImatge[numImatge - 1]);
            ((ImageView) findViewById(R.id.imgImatges)).setImageDrawable(d);
        }
    }

 */


    public void cmd_Veu (View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);

    }

    public classResposta Tradueix(List<String> llistaS) {
        // Interpreta el dit des de la llista de paraules a una resposta
        classResposta Rslt;
        TallVeu = llistaS.get(0);
        String valnum;
        String[] llista = TallVeu.replace('-',' ').split(" ");
        Rslt = new classResposta();
        Log.d ("VEU","Original = " +TallVeu);
        for(int i=0; i<llista.length ; i++){
            Log.d ("VEU","llista[" + i + "]=" + llista[i]);
        }
        if (llista.length >= 1) {
            Rslt.setNom(llista[0]);
        }
        if (llista.length >= 2) {
            Rslt.setCognom(llista[1]);
        }
        if (llista.length >= 3) {
            if (classGlobal.paraulaANumero(llista[2])>=0) {
                Rslt.setCurs(String.format("%01d", classGlobal.paraulaANumero(llista[2])));
            } else {
                Rslt.setCurs(llista[2]);
            }
        }
        if (llista.length >= 4) {
            if (llista[3].matches("[a-zA-Z]\\d+")) {  //c24
                // Separar: primera lletra i la resta de números
                Rslt.setGrup(llista[3].substring(0, 1));         // "c"
                String nm = llista[3].substring(1);
                if (classGlobal.paraulaANumero(nm)>=0) nm= String.format("%02d", classGlobal.paraulaANumero(nm));
                Rslt.setNum(nm);   // "24"
            } else {
                Rslt.setGrup(llista[3]);      // "c"
                if (llista.length >= 5) {
                    if (classGlobal.paraulaANumero(llista[4])>=0) {
                        Rslt.setNum(String.format("%02d", classGlobal.paraulaANumero(llista[4])));
                    } else {
                        Rslt.setNum(llista[4]);
                    }
                }
            }
        }

        Log.d ("VEU","Nom:"+Rslt.getNom());
        Log.d ("VEU","Cognom:"+Rslt.getCognom());
        Log.d ("VEU","Curs:"+Rslt.getCurs());
        Log.d ("VEU","Grup:"+Rslt.getGrup());
        Log.d ("VEU","Num:"+Rslt.getNum());
        return Rslt;
    }



    public void cmd_Ok(View view) {

        avaluacioInici = new classResposta();
        resposta = new classResposta();
        resposta.setNom(((TextView) findViewById(R.id.edtPregNom)).getText().toString());
        resposta.setCognom(((TextView) findViewById(R.id.edtPregCognom)).getText().toString());
        resposta.setCurs(((TextView) findViewById(R.id.edtPregCurs)).getText().toString());
        resposta.setGrup(((TextView) findViewById(R.id.edtPregGrup)).getText().toString());
        resposta.setNum(((TextView) findViewById(R.id.edtPregNum)).getText().toString());

        avaluacioInici.setNom(Avalua(mItem.getNom(),resposta.getNom()));
        avaluacioInici.setCognom(Avalua(mItem.getCognom(),resposta.getCognom()));
        avaluacioInici.setCurs(Avalua(mItem.getCurs(),resposta.getCurs()));
        avaluacioInici.setGrup(Avalua(mItem.getGrup(),resposta.getGrup()));
        avaluacioInici.setNum(Avalua(mItem.getNum(),resposta.getNum()));

        TempsPregunta = SystemClock.currentThreadTimeMillis() - TempsIniciPregunta;

        Intent intent = new Intent(act_preg.this, act_preg_aval.class);
        intent.putExtra( act_preg_aval.ARG_ID_ITEM, Actual.toString());
        intent.putExtra(act_preg_aval.ARG_RESP, resposta.toString());
        intent.putExtra(act_preg_aval.ARG_AVAL, avaluacioInici.toString());
        intent.putExtra(act_preg_aval.ARG_TALLVEU, TallVeu);
        startActivityForResult(intent, REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        classResposta rsp;
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Todo: Registrar resultats
                String results = data.getStringExtra(act_preg_aval.ARG_CORR);
                avaluacioFinal = new classResposta(results);
                RegistraResposta();
                Seguent();
                return; // Temporal
            } else {
                return;
            }
        } else if (requestCode == SPEECH_REQUEST_CODE) {    // Reconeixement de veu
            if (resultCode == RESULT_OK) {
                List<String> results = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                rsp=Tradueix(results);
                CarregaResp(rsp);
                FetAmbVeu=true;
            }
            super.onActivityResult(requestCode, resultCode, data);

        }
    }


    private String Avalua(String correcta, String resposta) {
        correcta = correcta.toLowerCase().trim();
        resposta = resposta.toLowerCase().trim();
        if (correcta.equals(resposta)) {
            return classResultats.VAL_PERFECTE;
        } else if (resposta.length() == 0) {
            return classResultats.VAL_OBLIDAT;
        } else {
            return classResultats.VAL_REPASSAR;
        }
    }


    private void RegistraResposta() {

        classResultats Result;
        classPersones Item;
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Madrid"));
        classResposta combinada= new classResposta();

        combinada.setNom(avaluacioInici.getNom()+"-" + avaluacioFinal.getNom());
        combinada.setCognom(avaluacioInici.getCognom()+"-" + avaluacioFinal.getCognom());
        combinada.setGrup(avaluacioInici.getGrup()+"-" + avaluacioFinal.getGrup());
        combinada.setCurs(avaluacioInici.getCurs()+"-" + avaluacioFinal.getCurs());

        if (avaluacioFinal.getNom().equals(classResultats.VAL_PERFECTE) &&
                avaluacioFinal.getCognom().equals(classResultats.VAL_PERFECTE) &&
                avaluacioFinal.getCurs().equals(classResultats.VAL_PERFECTE))
            Resultat = classResultats.VAL_PERFECTE;
        else if ((avaluacioFinal.getNom().equals(classResultats.VAL_PERFECTE) || avaluacioFinal.getNom().equals(classResultats.VAL_REPASSAR) ) &&
                (avaluacioFinal.getCognom().equals(classResultats.VAL_PERFECTE)  || avaluacioFinal.getCognom().equals(classResultats.VAL_REPASSAR) ) &&
                (avaluacioFinal.getCurs().equals(classResultats.VAL_PERFECTE)  || avaluacioFinal.getCurs().equals(classResultats.VAL_REPASSAR) ) )
            Resultat = classResultats.VAL_REPASSAR;
        else if (!avaluacioFinal.getNom().equals(classResultats.VAL_OBLIDAT) &&
                !avaluacioFinal.getCognom().equals(classResultats.VAL_OBLIDAT) &&
                !avaluacioFinal.getCurs().equals(classResultats.VAL_OBLIDAT) )
            Resultat = classResultats.VAL_APRES;
        else
            Resultat = classResultats.VAL_OBLIDAT;




        //Crea Resultat
        Result = new classResultats(
                cal.getTime(),
                NumProva,
                mItem.getId(),
//                mItem.getNextTipus(),
//                mItem.getNextData(),
                resposta.toString(),
                (FetAmbVeu ? "Veu":"Teclat") + "," + combinada.toString(),
                TempsPregunta,
                Resultat
        );

        // Actualitza Prova
        Prova.setTemps(Prova.getTemps()+ TempsPregunta);
        Prova.setNumRespostes(Prova.getNumRespostes()+1);
        switch (Resultat) {
            case classResultats.VAL_PERFECTE:
                avaPer++; break;
            case classResultats.VAL_REPASSAR:
                avaRep ++; break;
            case classResultats.VAL_OBLIDAT:
                avaObl ++; break;
        }

        db.open();
        db.insResultat(Result);
        db.actProves(Prova);

        //cal.setTime(new Date());
        if ((Resultat.equals(classResultats.VAL_PERFECTE)) && (cal.getTime().after(mItem.getNextData()))) {
            if (TipusProva.equals( classProves.TIP_REPAS)) {
                switch (mItem.getNextTipus()) {
                    case "1h":
                        mItem.setNextTipus("1d");
                        cal.add(Calendar.HOUR, 23);
                        break;
                    case "1d":
                        mItem.setNextTipus("1s");
                        cal.add(Calendar.DATE, 6);
                        break;
                    case "1s":
                        mItem.setNextTipus("1m");
                        cal.add(Calendar.DATE, 21);
                        break;
                    case "1m":
                        mItem.setNextTipus("6m");
                        cal.add(Calendar.DATE, 28 * 5);
                        break;
                    case "6m":
                        cal.add(Calendar.DATE, 28 * 6);
                }
                mItem.setNextData(cal.getTime());
                db.actPersones(mItem);
            }
        } else if (Resultat.equals(classResultats.VAL_APRES)) {
            mItem.setNextTipus("1h");
            cal.add(Calendar.HOUR, 1);
            mItem.setNextData(cal.getTime());
            db.actPersones(mItem);
        } else if (Resultat.equals(classResultats.VAL_OBLIDAT)) {
            mItem.setNextTipus("a");
            mItem.setNextData("");
            db.actPersones(mItem);
        }
        // si és VAL_REVISIO no s'ha de gravar res
        // Si és examen i és perfecte, tampoc

        db.close();
    }


    public void Seguent() {
        if (Actual<objLlistaTrobats.ITEMS.size()) {
            Actual++;
            mItem = objLlistaTrobats.ITEMS.get(Actual);
            CarregaResp(new classResposta());
            CarregaItem(mItem);
        } else {
            Toast msg = Toast.makeText(this, "No hi ha més cares a memoritzar", Toast.LENGTH_SHORT);
            msg.setDuration(Toast.LENGTH_LONG);
            msg.show();
            finish();
        }

    }

    public void cmd_Edita(View view) {
        Context context = view.getContext();
        Intent intent = new Intent(context, act_manteniment_modificar.class);
        //No! num ordre obj...
        intent.putExtra(act_manteniment_modificar.ARG_ITEM_ID, Actual);
        context.startActivity(intent);
    }


}