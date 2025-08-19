package com.gruixuts.geniuscares;

public class classResposta {
    private String nom_;
    private String cognom_;
    private String curs_;
    private String grup_;
    private String num_;

    public static final String Separador=":";

    public  classResposta () {
        nom_="";
        cognom_ ="";
        curs_="";
        grup_="";
        num_="";
    }

    public  classResposta (String Llista) {
        String[]  ll;
        ll = ("i"+Separador+Llista+Separador+"f").split(Separador);
        // ll = Llista.split(Separador);
        // Es fa així per si ve una llista buida, que split no inclou les buides d'inici ni de final
        assert(ll.length==6);
        nom_ = ll[1];
        cognom_ = ll[2];
        curs_ = ll[4];
        grup_ = ll[3];
        num_ = ll[4];
    }

    public String getNom() {
        return nom_;
    }

    public void setNom(String nom) {
        assert(nom.indexOf(Separador)==-1);
        nom_ = nom;
    }

    public String getCognom() {
        return cognom_;
    }

    public void setCognom(String cognom) {
        assert(cognom.indexOf(Separador)==-1);
        cognom_ = cognom;
    }

    public String getCurs() {
        return curs_;
    }

    public void setCurs(String curs) {
        assert(curs.indexOf(Separador)==-1);
        curs_ = curs;
    }

    public String getGrup() {
        return grup_;
    }

    public void setGrup(String grup) {
        assert(grup.indexOf(Separador)==-1);
        grup_ = grup;
    }

    public String getNum() {
        return num_;
    }

    public void setNum(String num) {
        assert(num.indexOf(Separador)==-1);
        grup_ = num;
    }

    public String toString() {
        return nom_ + Separador + cognom_ +  Separador + curs_+ Separador + grup_ + Separador + num_;
    }
}