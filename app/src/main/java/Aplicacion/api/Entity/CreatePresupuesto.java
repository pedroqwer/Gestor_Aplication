package Aplicacion.api.Entity;

public class CreatePresupuesto {
    private Double cantidadLimite;
    private String descripcion;
    private String presupuesto_del_mes;
    private long id_user;

    public CreatePresupuesto(Double cantidadLimite, String descripcion, String presupuesto_del_mes, long id_user) {
        this.cantidadLimite = cantidadLimite;
        this.descripcion = descripcion;
        this.presupuesto_del_mes = presupuesto_del_mes;
        this.id_user = id_user;
    }

    public Double getCantidadLimite() {
        return cantidadLimite;
    }

    public void setCantidadLimite(Double cantidadLimite) {
        this.cantidadLimite = cantidadLimite;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPresupuesto_del_mes() {
        return presupuesto_del_mes;
    }

    public void setPresupuesto_del_mes(String presupuesto_del_mes) {
        this.presupuesto_del_mes = presupuesto_del_mes;
    }

    public long getId_user() {
        return id_user;
    }

    public void setId_user(long id_user) {
        this.id_user = id_user;
    }
}
