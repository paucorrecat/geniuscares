package com.gruixuts.geniuscares;

import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class classResultats {

    public static SimpleDateFormat frmtData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final String VAL_PERFECTE = "Per";   //
    public static final String VAL_REPASSAR = "Rep";  //Petit fallo que reforça la memòria
    public static final String VAL_APRES = "Apr";  //He tornat a fer un PAV, com si ho acabés d'aprendre
    public static final String VAL_OBLIDAT = "Obl";  //Ho he oblidat, ho he de tornar a aprendre
    public static final String Separador = ",";

    private Date Dia_;
    private Integer IdProva_;
    private Integer IdEntDic_;
    //private String RepasTipus_;
    //private Date RepasData_;
    private String Resposta_; // classResposta.toString() <--> classResposta.separador
    private String Errors_; // Errors comesos: Nom, Cognom1,... Es tracta de tipificar-los
    private Long Temps_;
    private String Valoracio_; // Perfecte, Revisar, Aprèsm, Oblidat... veure classe Resultats


    public classResultats(Date dia,
                          Integer idprova,
                          Integer identdic,
//                          String repastipus,
//                          Date repasdata,
                          String resposta,
                          String errors,
                          Long temps,
                          String valoracio) {
        setDia(dia);
        setIdProva(idprova);
        setIdItem(identdic);
        //      setRepasTipus(repastipus);
        //      setRepasData(repasdata);
        setResposta(resposta);
        setErrors(errors);
        setTemps(temps);
        setValoracio(valoracio);
    }

    public classResultats() {

    }

    public classResultats(String Cadena) {  //Això és per importar
        String[] camps;

        camps = Cadena.split(Separador);
        String R = camps[0];
        assert (R.equals("R"));
        Dia_=GestorDB.AData(camps[1]);
        try {
            IdProva_ = Integer.parseInt(camps[2]);
        } catch (Exception e) {
            IdProva_ = Integer.parseInt(camps[2].substring(1));
        }
        try {
            IdEntDic_ = Integer.parseInt(camps[3]);
        } catch (Exception e) {
            IdEntDic_ = Integer.parseInt(camps[3].substring(1));
        }
//        RepasTipus_ = camps[4];
//        RepasData_ =GestorDB.AData(camps[5]);

        // Abans 6..9; al treure els camps: 4 a 7
        Resposta_=camps[4];
        Errors_ = camps[5];
        try {
            Temps_ =  Long.parseLong(camps[6]);
        } catch (Exception e) {
            Temps_ = Long.parseLong(camps[6].substring(1));
        }
        Valoracio_=camps[7];

    }

    public classResultats(Cursor cursor) {
        Dia_ = cursor.getString(0) == null ? null : GestorDB.AData(cursor.getString(0));
        IdProva_ = cursor.getInt(1);
        IdEntDic_ = cursor.getInt(2);
//        RepasTipus_ = cursor.getString(3);
//        RepasData_ = cursor.getString(4) == null ? null : GestorDB.AData(cursor.getString(4));
        //Idem: desplaçament de camps
        Resposta_ = cursor.getString(3);
        Errors_ = cursor.getString(4);
        Temps_ = cursor.getLong(5);
    }


    public Date getDia() {
        return Dia_;
    }

    public String getDiaTxt() {
        if (Dia_ == null) {
            return "";
        } else {
            return frmtData.format(Dia_);
        }
    }

    public void setDia(Date dia) {
        Dia_ = dia;
    }

    public Integer getIdProva() {
        return IdProva_;
    }

    public void setIdProva(Integer idProva) {
        IdProva_ = idProva;
    }

    public Integer getIdItem() {
        return IdEntDic_;
    }

    public void setIdItem(Integer idEntDic) {
        IdEntDic_ = idEntDic;
    }

    /*
    public String getRepasTipus() {
        return RepasTipus_;
    }

    public void setRepasTipus(String repasTipus) {
        RepasTipus_ = repasTipus;
    }

    public Date getRepasData() {
        return RepasData_;
    }

    public String getRepasDataTxt() {
        if (RepasData_ == null) {
            return "";
        } else {
            return frmtData.format(RepasData_);
        }
    }

    public void setRepasData(Date repasData) {
        RepasData_ = repasData;
    }
*/
    public String getResposta() {
        return Resposta_;
    }

    public void setResposta(String resposta) {
        Resposta_ = resposta;
    }

    public String getErrors() {
        if (Errors_ == null) {   // NO estic segur d'això
            return "";
        } else {
            return Errors_;
        }
    }

    public void setErrors(String errors) {
        Errors_ = errors;
    }

    public Long getTemps() {
        return Temps_;
    }

    public void setTemps(Long temps) {
        Temps_ = temps;
    }

    public String getValoracio() {
        if (Valoracio_ == null) {   // NO estic segur d'això
            return "";
        } else {
            return Valoracio_;
        }
    }

    public void setValoracio(String valoracio) {
        Valoracio_ = valoracio;
    }

    public String toString() {
        String rslt;
        rslt = getDiaTxt() + Separador;
        rslt += IdProva_.toString() + Separador;
        rslt += IdEntDic_.toString() + Separador;
        //      rslt += RepasTipus_ + Separador;
        //      rslt += getRepasDataTxt() + Separador;
        rslt += Resposta_ + Separador;
        rslt += Errors_ + Separador;
        rslt += Temps_.toString() + Separador;
        rslt += Valoracio_;

        return rslt;
    }


    public void setTexte(String Cadena) {
    }


}