package com.gruixuts.geniuscares;

import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Usuario on 04/04/2018.
 */

/*
Data	datetime	Checked
TipusProva	nvarchar(10)	Checked
Inversa	bit	Checked
Catala	nvarchar(100)	Checked
Basc	nvarchar(100)	Checked
Temps	numeric(18, 0)	Checked
Correcta	bit	Checked
CatalaId	nvarchar(100)	Checked
BascId	numeric(18, 0)	Checked
IdProva	numeric(18, 0)	Checked
Pse	bit	Unchecked
 */


public class classResultats {
    public static SimpleDateFormat frmtData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final String VAL_PERFECTE = "Per";
    public static final String VAL_REVISAR = "Rev";
    public static final String VAL_APRES = "Apr";
    public static final String VAL_OBLIDAT = "Obl";

    private Date Dia_;
    private Integer IdProva_;
    private Integer IdEntDic_;
    private String Pregunta_;
    private String Resposta_;
    private String Correcta_;
    private Long Temps_;
    private String Valoracio_;


    public classResultats(Date dia,
                          Integer idprova,
                          Integer identdic,
                          String pregunta,
                          String resposta,
                          String correcta,
                          Long temps,
                          String valoracio) {
        setDia(dia);
        setIdProva(idprova);
        setIdEntDic(identdic);
        setPregunta(pregunta);
        setResposta(resposta);
        setCorrecta(correcta);
        setTemps(temps);
        setValoracio(valoracio);
    }

    public classResultats() {

    }

    public classResultats(String Cadena,String Sep) {
        setTexte(Cadena,Sep);
    }

    public classResultats(Cursor cursor) {
        Dia_ = cursor.getString(0) == null ? null : GestorDB.AData(cursor.getString(0));
        IdProva_ = cursor.getInt(1);
        IdEntDic_ = cursor.getInt(2);
        Pregunta_ = cursor.getString(3);
        Resposta_ = cursor.getString(4);
        Correcta_ = cursor.getString(5);
        Temps_ = cursor.getLong(6);
        Valoracio_ = cursor.getString(7);
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

    public Integer getIdEntDic() {
        return IdEntDic_;
    }

    public void setIdEntDic(Integer idEntDic) {
        IdEntDic_ = idEntDic;
    }

    public String getPregunta() {
        return Pregunta_;
    }

    public void setPregunta(String pregunta) {
        Pregunta_ = pregunta;
    }

    public String getResposta() {
        return Resposta_;
    }

    public void setResposta(String resposta) {
        Resposta_ = resposta;
    }

    public String getCorrecta() {
        return Correcta_;
    }

    public void setCorrecta(String correcta) {
        Correcta_ = correcta;
    }

    public Long getTemps() {
        return Temps_;
    }

    public void setTemps(Long temps) {
        Temps_ = temps;
    }

    public String getValoracio() {
        return Valoracio_;
    }

    public void setValoracio(String valoracio) {
        Valoracio_ = valoracio;
    }

    public String getTexte(String Separador) {
        String rslt="";
        rslt += getDiaTxt() + Separador;
        rslt += IdProva_.toString() + Separador;
        rslt += IdEntDic_.toString() + Separador;
        rslt += Pregunta_ + Separador;
        rslt += Resposta_ + Separador;
        rslt += Correcta_ + Separador;
        rslt += Temps_.toString() + Separador;
        rslt += Valoracio_ + Separador;

        return rslt;
    }


    public void setTexte(String Cadena, String Separador) {
        String[] camps;

        camps = Cadena.split(Separador);
        Dia_=GestorDB.AData(camps[0]);
        try {
            IdProva_ = Integer.parseInt(camps[1]);
        } catch (Exception e) {
            IdProva_ = Integer.parseInt(camps[1].substring(1));
        }
        try {
            IdEntDic_ = Integer.parseInt(camps[2]);
        } catch (Exception e) {
            IdEntDic_ = Integer.parseInt(camps[2].substring(1));
        }
        Pregunta_=camps[3];
        Resposta_=camps[4];
        Correcta_= camps[5];
        try {
            Temps_ =  Long.parseLong(camps[6]);
        } catch (Exception e) {
            Temps_ = Long.parseLong(camps[6].substring(1));
        }
        Valoracio_=camps[7];

    }

}
