package com.gruixuts.geniuscares;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

/**
 * Created by Usuario on 04/04/2018.
 */

public class GestorDB {

    // 1. Variable estàtica per guardar la única instància
    private static GestorDB instance;
    public static final int DATABASE_VERSION = 7;
    public static final String DATABASE_NAME = "GeniusCares.db";

    public static SimpleDateFormat frmtData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Date AData(String DataTxt) {
        return frmtData.parse(DataTxt, new ParsePosition(0));
    }

    // Definició de les taules
    private static abstract class PersonaDef implements BaseColumns {
        public static final String TABLE_NAME = "Persones";
        public static final String LLISTA_CAMPS = "Id,Imatges,Nom,Cognom,Num,Curs,Codi,PAV,Comentaris,Grup,NextTipus,NextData,AMemoritzar,TeImatge";
        // urgent:  afegir teimatge

        public static final String Id = "Id";
        //public static final String Catala = "Catala";  // A eliminar
        //public static final String Basc = "Basc";   // A eliminar
        public static final String Imatges = "Imatges";
        public static final String Nom = "Nom";
        public static final String Cognom = "Cognom";
        public static final String Num = "Num"; // urgent: canviar el nom del camp a Bum
        public static final String Curs = "Curs";
        public static final String Codi = "Codi";
        public static final String Grup = "Grup";   // Serveix per arganitzar l'aprenentatge o repassos. Poden ser dates
        public static final String PAV = "PAV";
        public static final String Comentaris = "Comentaris";
        public static final String NextTipus = "NextTipus";  // 1h,1d,1s,1m,6m
        public static final String NextData = "NextData";
        public static final String AMemoritzar = "AMemoritzar"; // A eliminar

        public static final String TeImatge = "TeImatge"; // A eliminar
    }

    private static abstract class ProvesDef implements BaseColumns {
        public static final String TABLE_NAME = "Proves";
        public static final String LLISTA_CAMPS = "Id,Dia,TipusProva,Seleccio,NumPreguntes,NumRespostes,Temps,Acabada";

        public static final String Id = "Id";
        public static final String Dia = "Dia";
        public static final String TipusProva = "TipusProva";
        public static final String Seleccio = "Seleccio";
        public static final String NumPreguntes = "NumPreguntes";
        public static final String NumRespostes = "NumRespostes";
        public static final String Temps = "Temps";
        public static final String Acabada = "Acabada";

    }

    private static abstract class ResultatsDef implements BaseColumns {
        public static final String TABLE_NAME = "Resultats";
//        public static final String LLISTA_CAMPS = "Dia,IdProva,IdPers,Pregunta,Resposta,Correcta,Errors,Temps,Valoracio";
        public static final String LLISTA_CAMPS = "Dia,IdProva,IdPers,Resposta,Errors,Temps,Valoracio";

        public static final String Dia = "Dia";
        public static final String IdProva = "IdProva";
        public static final String IdPers = "IdPers";
        //public static final String Pregunta = "Pregunta";
        public static final String Resposta = "Resposta";  // Nom,Cognom,Num,Curs
        //public static final String Correcta = "Correcta"; // A Eliminar --> Errors
        public static final String Errors = "Errors";  // 0 => Correcta
                                                       // Errors comesos: Nom, Cognom,... Es tracta de tipificar-los
        public static final String Temps = "Temps";
        public static final String Valoracio = "Valoracio";  // Per a prioritzar i veure progrés
    }

    // Sentencies per a la creació de taules
    private static final String Persones_TABLE_CREATE = "create table " + PersonaDef.TABLE_NAME
            + " (" + PersonaDef.Id + " integer primary key, "
            //+ PersonesDef.Catala + " text, "
            //+ PersonesDef.Basc + " text, "
            + PersonaDef.Imatges + " text, "
            + PersonaDef.Nom + " text, "
            + PersonaDef.Cognom + " text, "
            + PersonaDef.Num + " text, "
            + PersonaDef.Curs + " text, "
            + PersonaDef.Codi + " text, "
            + PersonaDef.Grup + " text, "
            + PersonaDef.PAV + " text, "
            + PersonaDef.Comentaris + " text, "
            + PersonaDef.NextTipus + " text, "
            + PersonaDef.NextData + " text, "
            + PersonaDef.AMemoritzar + " integer, "
            + PersonaDef.TeImatge + " integer ); ";

    private static final String Proves_TABLE_CREATE = "create table " + ProvesDef.TABLE_NAME
            + "(" + ProvesDef.Id + " integer primary key, "
            + ProvesDef.Dia + " text, "
            + ProvesDef.TipusProva + " text, "
            + ProvesDef.Seleccio + " text, "
            + ProvesDef.NumPreguntes + " integer, "
            + ProvesDef.NumRespostes + " integer, "
            + ProvesDef.Temps + " integer, "
            + ProvesDef.Acabada + " integer );  ";

    private static final String Resultats_TABLE_CREATE = "create table " + ResultatsDef.TABLE_NAME
            + "(" + ResultatsDef.Dia + " text, "
            + ResultatsDef.IdProva + " integer, "
            + ResultatsDef.IdPers + " integer, "
//            + ResultatsDef.Pregunta + " text, "
            + ResultatsDef.Resposta + " text, "
//            + ResultatsDef.Correcta + " text, "
            + ResultatsDef.Errors + " text, "
            + ResultatsDef.Temps + " text, "
            + ResultatsDef.Valoracio + " text) ; ";


    private class DBHandler extends SQLiteOpenHelper {

        public DBHandler(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            Log.w("GestorDB_LIFECYCLE", "DBHandler: S'està creant una nova instància de l'Helper.");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.e("GestorDB_LIFECYCLE", "DBHandler: onCreate() S'ESTÀ EXECUTANT! ES CREARAN TAULES BUIDES.");
            db.execSQL(Persones_TABLE_CREATE);
            db.execSQL(Proves_TABLE_CREATE);
            db.execSQL(Resultats_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.e("GestorDB_LIFECYCLE", "DBHandler: onUpgrade() S'ESTÀ EXECUTANT! S'ESBORRARAN LES DADES.");
            db.execSQL("DROP TABLE IF EXISTS " + PersonaDef.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + ProvesDef.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + ResultatsDef.TABLE_NAME);
            onCreate(db);
        }
    }

    private Context context;
    private SQLiteDatabase db;
    private DBHandler openHelper; //Gestor de base de datos

    public GestorDB(Context context) {
        Log.d("GestorDB_Singleton", "Creant la ÚNICA instància de GestorDB.");
        this.context = context.getApplicationContext();  // Utilitza el context de l'aplicació
        this.openHelper = new DBHandler(this.context);
    }

    // Mètode públic i estàtic per obtenir la única instància
    public static synchronized GestorDB getInstance(Context context) {
        if (instance == null) {
            instance = new GestorDB(context);
        }
        return instance;
    }
    //Obrir i tancar la base de dades

    public GestorDB open() {
        // Si la connexió ja està oberta, no facis res.
        // Si està tancada o és nul·la, obre-la.
        if (this.db == null || !this.db.isOpen()) {
            Log.d("GestorDB_LIFECYCLE", "La connexió és nul·la o tancada. Cridant a getWritableDatabase()...");
            this.db = openHelper.getWritableDatabase();
            Log.d("GestorDB_LIFECYCLE", "getWritableDatabase() cridat.");
        }
        return this;
    }

    public void close() {
        // Sincronitzem per evitar problemes si es crida des de diferents fils
        synchronized(this) {
            if (this.db != null && this.db.isOpen()) {
                this.db.close();
                Log.w("GestorDB_Singleton", "La connexió a la base de dades ha estat tancada.");
            }
            if (this.openHelper != null) {
                this.openHelper.close();
                Log.w("GestorDB_Singleton", "L'SQLiteOpenHelper ha estat tancat.");
            }
            // Important: posem la nostra instància estàtica a null
            // perquè la propera crida a getInstance() la recreï des de zero.
            // Això completa el "reset" total.
            instance = null;
            Log.w("GestorDB_Singleton", "La instància Singleton ha estat destruïda (reset).");
        }
    }

    // Función públiques d'accés a les dades

    /**
     * Tanca completament la connexió actual a la base de dades.
     * S'ha de cridar DESPRÉS d'una operació externa com una importació
     * per forçar que la propera crida a open() obri el nou fitxer.
     */
    public void resetConnection() {
        Log.d("GestorDB", "Reiniciant la connexió a la base de dades.");
        // Primer, tanca la connexió existent de manera segura.
        if (this.db != null && this.db.isOpen()) {
            this.db.close();
        }
        // Després, tanca l'helper, que gestiona el fitxer.
        if (this.openHelper != null) {
            this.openHelper.close();
        }

        // Finalment, recreem l'helper. Això és crucial.
        // La propera vegada que es cridi a open(), aquest nou helper
        // obrirà una connexió fresca al fitxer (que acabem d'importar).
        this.openHelper = new DBHandler(this.context);
        this.db = null; // Assegurem que la propera crida a open() la reobri.
    }

    //Selecció

    public ArrayList<classPersones> selPersones(String Filtre, String Ordre) {
        open();
        ArrayList<classPersones> list = new ArrayList<classPersones>();
        String SQLtxt;

        SQLtxt = "Select " + PersonaDef.LLISTA_CAMPS + " from " + PersonaDef.TABLE_NAME;
        if (Filtre.length() > 0) {
            SQLtxt += " where " + Filtre;
        }
        if (Ordre.length() > 0) {
            SQLtxt += " order by " + Ordre;
        }
        SQLtxt += ";";
        Cursor cursor = this.db.rawQuery(SQLtxt, null);
        if (cursor.moveToFirst()) {
            do {
                try {
                    classPersones entrada = new classPersones(cursor);
                    list.add(entrada);
                } catch (Exception ex) {
                    Log.e("GestorDB", "selPersones: Error al crear Persones");
                }
            } while (cursor.moveToNext());
        }

        if (cursor != null && !cursor.isClosed()) {//Se cierra el cursor si no está cerrado ya
            cursor.close();
        }
        if (Ordre.length() > 0) {
            return list;
        } else {
            return Desordena(list);
        }
    }

    public classPersones selPers(Integer Id) {
        open();
        String SQLtxt;

        SQLtxt = "Select " + PersonaDef.LLISTA_CAMPS + " from " + PersonaDef.TABLE_NAME + " where Id=" + Id + ";";
        Cursor cursor = this.db.rawQuery(SQLtxt, null);
        if (cursor.moveToFirst()) {
                try {
                    classPersones entrada = new classPersones(cursor);
                    cursor.close();
                    return entrada;
                } catch (Exception ex) {
                    Log.e("GestorDB", "selPersones: Error al crear Persones");
                }
        }

        if (cursor != null && !cursor.isClosed()) {//Se cierra el cursor si no está cerrado ya
            cursor.close();
        }
        return null;
    }

    public ArrayList<classPersones> selPersonesAntiguetat(String Filtre, String Ordre, Integer NumEntrades) {
        open();
        ArrayList<classPersones> list = new ArrayList<classPersones>();
        String SQLtxt;


        SQLtxt = "Select ";
        SQLtxt += "Persones.* from ((select * from ( ";
        SQLtxt += "( SELECT Max(Dia) AS UltDeDia, IdPers as UltIdPers FROM Resultats GROUP BY Resultats.IdPers ) as Ult ";
        SQLtxt += "INNER JOIN Resultats as Rst ON (Ult.UltIdPers = Rst.IdPers) AND (Ult.UltDeDia = Rst.Dia))) as pp ";
        SQLtxt += "INNER JOIN Persones on IdPers=Persones.Id) where ((Valoracio <> 'Obl') ";
        if (Filtre.length() > 0) {
            SQLtxt += " and " + Filtre + ") ";
        } else {
            SQLtxt += ") ";
        }
            SQLtxt += " ORDER BY Dia";
        if (NumEntrades > 0) {
            SQLtxt += " limit " + NumEntrades + " ";
        }
        SQLtxt += ";";

        Cursor cursor = this.db.rawQuery(SQLtxt, null);
        if (cursor.moveToFirst()) {
            do {
                try {
                    classPersones entrada = new classPersones(cursor);
                    list.add(entrada);
                } catch (Exception ex) {
                    Log.e("GestorDB", "selPersones: Error al crear Persones");
                }
            } while (cursor.moveToNext());
        }

        if (cursor != null && !cursor.isClosed()) {//Se cierra el cursor si no está cerrado ya
            cursor.close();
        }
        if (NumEntrades == 0) {
            return list;
        } else {
            return Desordena(list);
        }
    }


    public ArrayList<classPersones> selPersonesRevisar(String Filtre, String Ordre, Integer NumEntrades) {
        open();
        ArrayList<classPersones> list = new ArrayList<classPersones>();
        String SQLtxt;
        Cursor cursor;

        SQLtxt = "Select ";
        SQLtxt += "Persones.* from ((select * from ( ";
        SQLtxt += "( SELECT Max(Dia) AS UltDeDia, IdPers as UltIdPers FROM Resultats GROUP BY Resultats.IdPers ) as Ult ";
        SQLtxt += "INNER JOIN Resultats as Rst ON (Ult.UltIdPers = Rst.IdPers) AND (Ult.UltDeDia = Rst.Dia))) as pp ";
        SQLtxt += "INNER JOIN Persones on IdPers=Persones.Id) where Valoracio = 'Rev' ";
        if (Ordre == "Ant") {
            SQLtxt += " ORDER BY Dia";
        }
        if (NumEntrades > 0) {
            SQLtxt += " limit " + NumEntrades + " ";
        }
        SQLtxt += ";";

        try {
             cursor = this.db.rawQuery(SQLtxt, null);
        } catch (Exception ex) {
            Log.e("GestorDB", "selPersonesRevisar: Error al obrir el cursor del Persones");
            return list;
        }
        if (cursor.moveToFirst()) {
            do {
                try {
                    classPersones entrada = new classPersones(cursor);
                    list.add(entrada);
                } catch (Exception ex) {
                    Log.e("GestorDB", "selPersonesRevisar: Error al crear Persones");
                }
            } while (cursor.moveToNext());
        }

        if (cursor != null && !cursor.isClosed()) {//Se cierra el cursor si no está cerrado ya
            cursor.close();
        }
        if (Ordre.length() > 0) {
            return list;
        } else {
            return Desordena(list);
        }
    }


    public ArrayList<classPersones> selPersonesSeguir() {
        open();
        ArrayList<classPersones> list = new ArrayList<classPersones>();
        String SQLtxt;
        Cursor cursor;

        Integer NumEntrades;
        String Ordre ="";
        String Filtre;
        String Top;




        SQLtxt = "Select ";
        SQLtxt += "Persones.* from ((select * from ( ";
        SQLtxt += "( SELECT Max(Dia) AS UltDeDia, IdPers as UltIdPers FROM Resultats GROUP BY Resultats.IdPers ) as Ult ";
        SQLtxt += "INNER JOIN Resultats as Rst ON (Ult.UltIdPers = Rst.IdPers) AND (Ult.UltDeDia = Rst.Dia))) as pp ";
        SQLtxt += "INNER JOIN Persones on IdPers=Persones.Id) where Valoracio = 'Rev' ";
        SQLtxt += ";";

        try {
            cursor = this.db.rawQuery(SQLtxt, null);
        } catch (Exception ex) {
            Log.e("GestorDB", "selPersonesRevisar: Error al obrir el cursor del Persones");
            return list;
        }
        if (cursor.moveToFirst()) {
            do {
                try {
                    classPersones entrada = new classPersones(cursor);
                    list.add(entrada);
                } catch (Exception ex) {
                    Log.e("GestorDB", "selPersonesRevisar: Error al crear Persones");
                }
            } while (cursor.moveToNext());
        }

        if (cursor != null && !cursor.isClosed()) {//Se cierra el cursor si no está cerrado ya
            cursor.close();
        }
        if (Ordre.length() > 0) {
            return list;
        } else {
            return Desordena(list);
        }
    }




    public ArrayList<classProves> selProves(String Filtre, String Ordre) {
        open();
        ArrayList<classProves> list = new ArrayList<classProves>();
        String SQLtxt;

        SQLtxt = "Select " + ProvesDef.LLISTA_CAMPS + " from " + ProvesDef.TABLE_NAME;
        if (Filtre.length() > 0) {
            SQLtxt += " where " + Filtre;
        }
        if (Ordre.length() > 0) {
            SQLtxt += " order by " + Ordre;
        }
        SQLtxt += " ;";
        Cursor cursor = this.db.rawQuery(SQLtxt, null);
        if (cursor.moveToFirst()) {
            do {
                try {
                    classProves entrada = new classProves(cursor);
                    list.add(entrada);
                } catch (Exception ex) {
                    Log.e("GestorDB", "selProves: Error al crear Proves");
                }
            } while (cursor.moveToNext());
        }

        if (cursor != null && !cursor.isClosed()) {//Se cierra el cursor si no está cerrado ya
            cursor.close();
        }
        return list;
    }

    public ArrayList<classResultats> selResultats(String Filtre, String Ordre) {
        open();
        ArrayList<classResultats> list = new ArrayList<classResultats>();
        String SQLtxt;

        SQLtxt = "Select " + ResultatsDef.LLISTA_CAMPS + " from " + ResultatsDef.TABLE_NAME;
        if (Filtre.length() > 0) {
            SQLtxt += " where " + Filtre;
        }
        if (Ordre.length() > 0) {
            SQLtxt += " order by " + Ordre;
        }
        SQLtxt += " ;";
        Cursor cursor = this.db.rawQuery(SQLtxt, null);
        if (cursor.moveToFirst()) {
            do {
                try {
                    classResultats entrada = new classResultats(cursor);
                    list.add(entrada);
                } catch (Exception ex) {
                    Log.e("GestorDB", "selResultats: Error al crear Resultats");
                }
            } while (cursor.moveToNext());
        }

        if (cursor != null && !cursor.isClosed()) {//Se cierra el cursor si no está cerrado ya
            cursor.close();
        }
        return list;
    }


 
    public ArrayList<classPersones> selExamenFallosUlt() {
        open();
        ArrayList<classPersones> list = new ArrayList<classPersones>();
        String SQLtxt;
        Integer UltProva;
        Cursor cursor;

        SQLtxt = "select max(IdProva) from " + ResultatsDef.TABLE_NAME;
        cursor = this.db.rawQuery(SQLtxt, null);
        cursor.moveToFirst();
        UltProva = cursor.getInt(0);


        SQLtxt = "select BascId from " + ResultatsDef.TABLE_NAME;
        SQLtxt += " WHERE (IdProva=" + UltProva + ") AND (Correcta = 0);";
        cursor = this.db.rawQuery(SQLtxt, null);
        if (cursor.moveToFirst()) {
            do {
                SQLtxt = "Select Id,Catala,Basc from Persones where (Id = " + cursor.getInt(0) + ");";
                Cursor cursor2 = this.db.rawQuery(SQLtxt, null);
                cursor2.moveToFirst();

                classPersones entrada = new classPersones(cursor2.getInt(0),
                        cursor2.getString(1),
                        cursor2.getString(2));

                list.add(entrada);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {//Se cierra el cursor si no está cerrado ya
            cursor.close();
        }

        return Desordena(list);
    }

    private ArrayList<classPersones> Desordena(ArrayList<classPersones> list) {
        open();
        ArrayList<classPersones> result = new ArrayList<classPersones>();
        Random rand = new Random();

        while (list.size() > 0) {
            result.add(list.remove(rand.nextInt(list.size())));
        }
        return result;
    }


    public Integer UltimaProva() {
        open();
        String SQLtxt = "select max(IdProva) from " + ResultatsDef.TABLE_NAME;
        Cursor cursor = this.db.rawQuery(SQLtxt, null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public Integer QuantsRep(String TipRep) {
        open();
        String[] Camps = new String[2];
        String Selec;
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Madrid"));
        Camps[0] = "Count(Id)";
        Camps[1] = "datetime(NextData)";
        Selec = "(NextTipus = '" + TipRep + "' and datetime(NextData)<'" + frmtData.format(cal.getTime()) +  "')";
        try {
            Cursor cr = db.query(PersonaDef.TABLE_NAME, Camps, Selec, null, null, null, null);
            if (cr.moveToFirst()) {

                return cr.getInt(0);

            }
        } catch (Exception e) {
            Log.d("GestorDB", "QuantsRep: Accés al fer query '" + Selec + "'");
        }
        return 0;
    }

    //Eliminació
    public void delPersones() {
        open();
        db.delete(PersonaDef.TABLE_NAME, "-1", null);
    }
    public void delPers(Integer Id) {
        open();
        db.delete(PersonaDef.TABLE_NAME, "Id=" + Id, null);
    }
    public void delProves() {
        open();
        db.delete(ProvesDef.TABLE_NAME, "-1", null);
    }
    public void delResultats() {
        open();
        db.delete(ResultatsDef.TABLE_NAME, "-1", null);
    }

    //Insertar
    public void insPersones(classPersones ent) {
        open();
        ContentValues values = new ContentValues();

        // Parells clau-valor
        values.put(PersonaDef.Id, ent.getId());
        values.put(PersonaDef.Imatges, ent.getImatges());
        values.put(PersonaDef.Nom, ent.getNom());
        values.put(PersonaDef.Cognom, ent.getCognom());
        values.put(PersonaDef.Num, ent.getNum());
        values.put(PersonaDef.Curs, ent.getCurs());
        values.put(PersonaDef.Codi, ent.getCodi());
        values.put(PersonaDef.PAV, ent.getPAV());
        values.put(PersonaDef.Comentaris, ent.getComentaris());
        values.put(PersonaDef.Grup, ent.getGrup());
        values.put(PersonaDef.NextTipus, ent.getNextTipus());
        if (ent.getNextData() == null) {
            values.put(PersonaDef.NextData,"null");
        } else {
            values.put(PersonaDef.NextData, ent.getNextDataTxt());
        }
        values.put(PersonaDef.AMemoritzar, ent.getAMemoritzar());
        values.put(PersonaDef.TeImatge, ent.getTeImatge());
        // Insertar...
        db.insert(PersonaDef.TABLE_NAME, null, values);
    }

    public void insProves(classProves prova) {   // Per importació
        open();
        ContentValues values = new ContentValues();
        values.put(ProvesDef.Id, prova.getId());
        values.put(ProvesDef.Dia, prova.getDiaTxt() );
        values.put(ProvesDef.TipusProva, prova.getTipusProva());
        values.put(ProvesDef.Seleccio, prova.getSeleccio());
        values.put(ProvesDef.NumPreguntes, prova.getNumPreguntes());
        values.put(ProvesDef.NumRespostes, prova.getNumRespostes());
        values.put(ProvesDef.Temps, prova.getTemps());
        values.put(ProvesDef.Acabada, prova.getAcabada());
        db.insert(ProvesDef.TABLE_NAME, null, values);
    }
    /*
    public classProves(Integer id,
                       Date dia,
                       String tipusprova,
                       String seleccio,
                       int numpreguntes,
                       int numrespostes,
                       Long temps,
                       Boolean acabada) {

     */
    public classProves insNovaProvaAprendre(String sel, Integer numpreg, Long temps) {
        open();
        String SQLtxt = "select max(Id) from " + ProvesDef.TABLE_NAME;
        Cursor cursor = this.db.rawQuery(SQLtxt, null);
        classProves Prova;
        Date Ara =  Calendar.getInstance(TimeZone.getTimeZone("Europe/Madrid")).getTime();

        cursor.moveToFirst();
        int NouId = cursor.getInt(0);
        Prova = new classProves(NouId,Ara,"a",sel,numpreg,1,temps,false);
        return Prova;
    }

    public Integer NumNovaProva() {
        open();
        String SQLtxt = "select max(Id) from " + ProvesDef.TABLE_NAME;
        Cursor cursor = this.db.rawQuery(SQLtxt, null);
        cursor.moveToFirst();
        return (cursor.getInt(0)+1);
    }

    public classProves getProva(Integer NumProva) {

        String SQLtxt = "Select "+ProvesDef.LLISTA_CAMPS + " from " + ProvesDef.TABLE_NAME + " where (Id = " + NumProva.toString() + ");";
        Cursor cursor = this.db.rawQuery(SQLtxt, null);
        cursor.moveToFirst();
        return new classProves(cursor);
    }

    public void insResultat(classResultats Rslt) {
        open();
        ContentValues values = new ContentValues();

        // Parells clau-valor
        values.put(ResultatsDef.Dia, Rslt.getDiaTxt());
        values.put(ResultatsDef.IdProva, Rslt.getIdProva());
        // values.put(ResultatsDef.IdPers, Rslt.getIdPers()); TODO: Compte! Revisar
        //values.put(ResultatsDef.Pregunta, Rslt.getPregunta());
        values.put(ResultatsDef.Resposta, Rslt.getResposta());
        //values.put(ResultatsDef.Correcta, Rslt.getCorrecta());
        values.put(ResultatsDef.Temps, Rslt.getTemps());
        values.put(ResultatsDef.Valoracio, Rslt.getValoracio());


        db.insert(ResultatsDef.TABLE_NAME, null, values);

    }

    public void creaPersones(classPersones ent) {
        open();
        Cursor cursor;
        if (ent.getId() != 0) {
            String SQLtxt = "select Id from " + PersonaDef.TABLE_NAME + " where (Id=" + ent.getId() + ")" ;
            cursor = this.db.rawQuery(SQLtxt, null);
            if (cursor.moveToFirst())
                ent.setId(0);
        }
        if (ent.getId()==0) {

        String SQLtxt = "select max(Id) from " + PersonaDef.TABLE_NAME;
        cursor = this.db.rawQuery(SQLtxt, null);
        cursor.moveToFirst();
        int NouId = cursor.getInt(0);
        ent.setId(NouId + 1);
        }
        insPersones(ent);
    }

    //Actualitzar
    public void actRepassat(int IdEntrada, String Dia) {
        open();
        ContentValues values = new ContentValues();
        String[] Camps = {"Apres", "Repas1h", "Repas1d", "Repas1s", "Repas1m", "Repas6m"};
        Cursor cursor = db.query(PersonaDef.TABLE_NAME, Camps, "Id=" + IdEntrada, null, null, null, null);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Madrid"));
        String Actual = frmtData.format(cal.getTime());
        cal.add(Calendar.HOUR, -1);
        String RefData = frmtData.format(cal.getTime());
        cursor.moveToFirst();
        if ((cursor.getString(0).compareTo(RefData) < 0) && (cursor.getString(1) == null)) {
            values.put("Repas1h", Actual);
        }
        cal.add(Calendar.HOUR, -23);
        RefData = frmtData.format(cal.getTime());
        if ((cursor.getString(0).compareTo(RefData) < 0) && (cursor.getString(2) == null)) {
            values.put("Repas1d", Actual);
        }
        cal.add(Calendar.DATE, -6);
        RefData = frmtData.format(cal.getTime());
        if ((cursor.getString(0).compareTo(RefData) < 0) && (cursor.getString(3) == null)) {
            values.put("Repas1s", Actual);
        }
        cal.add(Calendar.DATE, -21);
        RefData = frmtData.format(cal.getTime());
        if ((cursor.getString(0).compareTo(RefData) < 0) && (cursor.getString(4) == null)) {
            values.put("Repas1m", Actual);
        }
        cal.add(Calendar.DATE, -(5 * 28));
        RefData = frmtData.format(cal.getTime());
        if ((cursor.getString(0).compareTo(RefData) < 0) && (cursor.getString(5) == null)) {
            values.put("Repas6m", Actual);
        }
        db.update(PersonaDef.TABLE_NAME, values, "Id=" + IdEntrada, null);

    }


    public void actPersones(classPersones ent) {
        open();
        ContentValues values = new ContentValues();
        values.put(PersonaDef.Imatges, ent.getImatges());
        values.put(PersonaDef.Nom, ent.getNom());
        values.put(PersonaDef.Cognom, ent.getCognom());
        values.put(PersonaDef.Num, ent.getNum());  // Compre camps DB
        values.put(PersonaDef.Curs, ent.getCurs());
        values.put(PersonaDef.Codi, ent.getCodi());
        values.put(PersonaDef.Grup, ent.getGrup());
        values.put(PersonaDef.PAV, ent.getPAV());
        values.put(PersonaDef.Comentaris, ent.getComentaris());
        values.put(PersonaDef.NextTipus, ent.getNextTipus());
        values.put(PersonaDef.NextData, ent.getNextDataTxt());
        values.put(PersonaDef.AMemoritzar, ent.getAMemoritzar());
        values.put(PersonaDef.TeImatge, ent.getTeImatge());
        try {
            db.update(
                    PersonaDef.TABLE_NAME,
                    values,
                    "Id = ?",
                    new String[]{String.valueOf(ent.getId())}
            );
        }
        catch (Exception e) {
            Log.d("DB","Error al update " + e.toString());
        }

    }


    public void actProves(classProves prova) {   // Per importació
        open();
        ContentValues values = new ContentValues();
        //values.put(ProvesDef.Id, prova.getId());
        values.put(ProvesDef.Dia, prova.getDiaTxt() );
        values.put(ProvesDef.TipusProva, prova.getTipusProva());
        values.put(ProvesDef.Seleccio, prova.getSeleccio());
        values.put(ProvesDef.NumPreguntes, prova.getNumPreguntes());
        values.put(ProvesDef.NumRespostes, prova.getNumRespostes());
        values.put(ProvesDef.Temps, prova.getTemps());
        values.put(ProvesDef.Acabada, prova.getAcabada());
        db.update(ProvesDef.TABLE_NAME, values, "Id=" + prova.getId(), null);
    }

}
