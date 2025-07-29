package com.gruixuts.geniuscares;

/**
 * Created by Usuario on 11/09/2018.
 */

import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class classProves {

    public static final String TIP_EXAMEN = "Ex";
    public static final String TIP_REPAS = "Rp";
    public static final String TIP_REVISIO = "Rv";
    public static final String TIP_APRENDRE = "Ap";


    public static SimpleDateFormat frmtData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Integer Id;
    private Date Dia;
    private String TipusProva;
    private String Seleccio;
    private Integer NumPreguntes;
    private Integer NumRespostes;
    private Long Temps;
    private Boolean Acabada;

    public classProves(Cursor cursor) {
        Id = cursor.getInt(0);
        Dia = cursor.getString(1) == null ? null : GestorDB.AData(cursor.getString(1));
        TipusProva = cursor.getString(2);
        Seleccio = cursor.getString(3);
        NumPreguntes = cursor.getInt(4);
        NumRespostes = cursor.getInt(5);
        Temps = cursor.getLong(6);
        Acabada = cursor.getInt(7) == 0 ? false : true;
    }

    public classProves() {

    }

    public classProves(Integer id,
                       Date dia,
                       String tipusprova,
                       String seleccio,
                       int numpreguntes,
                       int numrespostes,
                       Long temps,
                       Boolean acabada) {
        Id=id;
        Dia = dia;
        TipusProva = tipusprova;
        Seleccio = seleccio;
        NumPreguntes = numpreguntes;
        NumRespostes = numrespostes;
        Temps = temps;
        Acabada = acabada;
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public Date getDia() {
        return Dia;
    }

    public void setDia(Date dia) {
        Dia = dia;
    }

    public String getDiaTxt() {
        if (Dia == null) {
            return "";
        } else {
            return frmtData.format(Dia);
        }
    }


    public String getTipusProva() {
        return TipusProva;
    }

    public void setTipusProva(String tipusProva) {
        TipusProva = tipusProva;
    }

    public String getSeleccio() {
        return Seleccio;
    }

    public void setSeleccio(String seleccio) {
        Seleccio = seleccio;
    }

    public Integer getNumPreguntes() {
        return NumPreguntes;
    }

    public void setNumPreguntes(Integer numPreguntes) {
        NumPreguntes = numPreguntes;
    }

    public Integer getNumRespostes() {
        return NumRespostes;
    }

    public void setNumRespostes(Integer numRespostes) {
        NumRespostes = numRespostes;
    }

    public Long getTemps() {
        return Temps;
    }

    public void setTemps(Long temps) {
        Temps = temps;
    }

    public Boolean getAcabada() {
        return Acabada;
    }

    public void setAcabada(Boolean acabada) {
        Acabada = acabada;
    }
}
