package cl.fullpay.causas;

/**
 * Created by mario on 25-09-14.
 */
public class Cause {

    private String rol,rut,name,last_name, stage,comment,exhorto,date;



    public Cause(String rol, String rut, String name, String last_name, String stage, String comment, String exhorto, String date) {
        this.rol = rol;
        this.rut = rut;
        this.name = name;
        this.last_name = last_name;
        this.stage = stage;
        this.comment = comment;
        this.exhorto = exhorto;
        this.date = date;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getExhorto() {
        return exhorto;
    }

    public void setExhorto(String exhorto) {
        this.exhorto = exhorto;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
