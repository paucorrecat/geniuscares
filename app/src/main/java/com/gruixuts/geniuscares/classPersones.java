package com.gruixuts.geniuscares;

import android.database.Cursor;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Classe que representa un alumne i les seves dades acadèmiques.
 * Inclou mètodes per gestionar la seva informació i construir objectes
 * a partir de dades individuals o d'un {@link Cursor} de base de dades.
 *
 * @author Pau
 * @version 1.0 2025
 */
public class classPersones {
    /** Format estàndard per mostrar dates:"yyyy-MM-dd HH:mm:ss" */
    public static SimpleDateFormat frmtData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /** Identificador intern de l'alumne. */
    private Integer Id;

    /** Nom propi de l'alumne. */
    private String Nom;

    /** Primer cognom de l'alumne. */
    private String Cognom1;

    /** Curs escolar (ex: "3ESO", "2Btx"). */
    private String Curs;

    /** Grup dins el curs (ex: "A", "B"). */
    private String Grup;

    /** Número d'ordre de l'alumne en format de dos dígits: 01, 02, ... */
    private String Num;

    /** Codi identificador de l'alumne. any-curs-grup-num*/
    private String Codi;

    /** Nom de la carpeta d'imatges associada. */
    private String Imatges;

    /** Dades del PAV (pla d'atenció a la diversitat) si escau. */
    private String PAV;

    /** Comentaris associats a l'alumne. */
    private String Comentaris;

    /** Tipus d'activitat prevista propera. */
    private String NextTipus;

    /** Data de la propera activitat prevista. */
    private Date NextData;

    /** Indica si l'alumne ha de memoritzar. */
    private Boolean AMemoritzar;

    /** Indica si l'alumne té alguna imatge associada. */
    private Boolean TeImatge;

    /** Longitud del nom de carpeta d'imatges. */
    public static final int LongNomImg = 4;

    /**
     * Genera un nom aleatori per a la carpeta d'imatges.
     * @return cadena amb caràcters aleatoris
     */


    public String NovaImatge() {
        // Any
        String any = classGlobal.Any;
        any = classGlobal.Any;
        /*
        String rslt="";
        int n_aleat;
        Random aleatori = new Random(System.currentTimeMillis());
        for ( int n=0;n<LongNomImg;n++) {
            n_aleat=aleatori.nextInt(61);
            char c;
            if (n_aleat < 10) {
                c=(char) (n_aleat+48);
            } else if (n_aleat< 36) {
                c=(char) (n_aleat+55);
            } else {
                c=(char) (n_aleat+61);
            }
            rslt +=c;
        }
        return rslt;

         */
        return any;
    }

    /**
     * Constructor principal amb tots els camps.
     * @param id identificador
     * @param imatges nom de carpeta d'imatges
     * @param nom nom de l'alumne
     * @param cognom1 primer cognom
     * @param num número d'ordre (com a string)
     * @param curs curs escolar
     * @param codi codi identificador
     * @param pav informació PAV
     * @param comentaris observacions
     * @param grup grup de l'alumne
     * @param nexttipus tipus de pròxima activitat
     * @param nextdata data de pròxima activitat
     * @param amemoritzar si ha de memoritzar
     * @param teimatge si hi ha imatges
     */

    public classPersones(Integer id, String imatges, String nom, String cognom1, String num, String curs, String codi, String pav, String comentaris, String grup, String nexttipus, Date nextdata, Boolean amemoritzar, Boolean teimatge) {
        Id = id;
        if (imatges == null) {
            Imatges = NovaImatge();
        } else if (imatges.isEmpty()) {
            Imatges = NovaImatge();
        } else {
            Imatges = imatges;
        }
        Nom = nom;
        Cognom1 = cognom1;
        try {
            int Num = Integer.parseInt(num);
        } catch (NumberFormatException e) {
            System.out.println(nom + " " + cognom1 + " No és té una nombre vàlid");
        }
        Num = num;
        Curs = curs;
        Codi = codi;
        this.PAV = pav;
        Comentaris = comentaris;
        Grup = grup;
        NextTipus = nexttipus;
        NextData = nextdata;
        AMemoritzar = amemoritzar;
        TeImatge = teimatge;
    }

    /**
     * Constructor a partir d'un {@link Cursor} de base de dades.
     * @param cursor fila de dades d'un alumne
     */
    public classPersones(Cursor cursor) {
        Id = cursor.getInt(0);
        Imatges = cursor.getString(1);
        Nom = cursor.getString(2);
        Cognom1 = cursor.getString(3);
        Num = cursor.getString(4);
        Curs = cursor.getString(5);
        Codi = cursor.getString(6);
        PAV = cursor.getString(7);
        Comentaris = cursor.getString(8);
        Grup = cursor.getString(9);
        NextTipus = cursor.getString(10);
        NextData = cursor.getString(11) == null ? null : GestorDB.AData(cursor.getString(11));
        AMemoritzar = cursor.getInt(12) == 0 ? false : true;
    }

    ;

    /**
     * Constructor amb id, nom i cognom.
     * @param id identificador
     * @param nom nom de l'alumne
     * @param cognom1 primer cognom
     */
    public classPersones(Integer id, String nom, String cognom1) {
        Id = id;
        Codi = "";
        Imatges = NovaImatge();
        Nom = nom;
        Cognom1=cognom1;
        this.PAV = "";
        Comentaris = "";
        Grup = "";
        NextTipus = "a";
        NextData = null;
        AMemoritzar = true;

    }

    /** Constructor buit. */
    public classPersones() {
        Id = 0;
        Codi = "";
        Imatges = NovaImatge();
        Nom = "";
        Cognom1 = "";
        Num = "";
        Curs = "";
        PAV = "";
        Comentaris = "";
        Grup = "";
        NextTipus = "a";
        NextData = null;
        AMemoritzar = true;
    }

    // Getters i setters
    /**
     * Retorna l'identificador de l'alumne.
     * @return identificador
     */
    public Integer getId() {
        return Id;
    }

    /**
     * Assigna l'identificador de l'alumne.
     * @param id identificador
     */
    public void setId(Integer id) {
        Id = id;
    }

    public String getCodi() {
        return Codi;
    }

    public void setCodi(String codi) {
        Codi = codi;
    }

    public String getImatges() {
        return Imatges;
    }

    public void setImatges(String imatges) {
        Imatges = imatges;
    }

    public String getNom() {
        return Nom;
    }

    public void setNom(String nom) { Nom = nom; }

    public String getCognom1() {
        return Cognom1;
    }

    public void setCognom1(String cognom1) { Cognom1 = cognom1; }

    public String getNum() {
        return Num;
    }

    public void setNum(String num) { Num = num; }

    public String getCurs() { return Curs; }

    public void setCurs(String curs) { Curs = curs; }

    public String getPAV() {
        return PAV;
    }

    public void setPAV(String PAV) {
        this.PAV = PAV;
    }

    public String getComentaris() {
        return Comentaris;
    }

    public void setComentaris(String comentaris) {
        Comentaris = comentaris;
    }

    public String getGrup() {
        return Grup;
    }

    public void setGrup(String grup) {
        Grup = grup;
    }

    public String getNextTipus() {
        return NextTipus;
    }

    public void setNextTipus(String nexttipus) {
        NextTipus = nexttipus;
    }

    /**
     * Retorna la data de la propera activitat en format text.
     * @return data com a tipus Date
     */
    public Date getNextData() {
        return NextData;
    }

    /**
     * Retorna la data de la propera activitat en format text.
     * @return data com a text, i buida si no hi ha data
     */
    public String getNextDataTxt() {
        if (NextData == null) {
            return "";
        } else {
            return frmtData.format(NextData);
        }
    }

    public void setNextData(Date nextdata) {
        NextData = nextdata;
    }

    public void setNextData(String nextdata) {
        if (nextdata=="") {
            NextData = null;
        } else {
            try {
                NextData = frmtData.parse(nextdata);
            } catch (Exception ex) {
                Log.d("class Diccionari", "setNextData: Error al parse");
                NextData=null;
            }
        }
    }

    /**
     * Retorna si l'alumne ha de memoritzar.
     * @return true si cal memoritzar
     */
    public Boolean getAMemoritzar() {
        return AMemoritzar;
    }

    public void setAMemoritzar(Boolean amemoritzar) {
        AMemoritzar = amemoritzar;
    }
}
