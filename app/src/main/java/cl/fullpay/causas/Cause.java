package cl.fullpay.causas;

/**
 * Created by mario on 25-09-14.
 */
public class Cause implements Comparable {

    private String rol;
    private String rut;
    private String name;
    private String last_name;
    private String stage;
    private String comment;
    private String exhorto;
    private String date;



    private String rolNum;
    private String rolYear;



    public Cause(String rolNum,String rolYear, String rut, String name, String last_name, String stage, String comment, String exhorto, String date){

        this.rolNum = rolNum;
        this.rolYear = rolYear;
        this.rut = rut;
        this.name = name;
        this.last_name = last_name;
        this.stage = stage;
        this.comment = comment;
        this.exhorto = exhorto;
        this.date = date;
    }

    public String getRolNum() {
        return rolNum;
    }

    public void setRolNum(String rolNum) {
        this.rolNum = rolNum;
    }

    public String getRolYear() {
        return rolYear;
    }

    public void setRolYear(String rolYear) {
        this.rolYear = rolYear;
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

    @Override
    public int compareTo(Object o) {
        Cause aux = (Cause) o;
        int yearComp = rolYear.compareTo(aux.rolYear);

        if(yearComp == 0){
            return rolNum.compareTo(aux.rolNum);
        }
        return yearComp;
    }
}
