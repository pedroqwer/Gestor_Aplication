package Aplicacion.api.Entity;

public class PresupuestoEntity {
    private long id;
    private Double cantidadLimite;
    private String descripcion;
    //private String presupuestomes;
    private String fecha;

    public PresupuestoEntity(long id, Double cantidadLimite, String descripcion, String fecha) {
        this.id = id;
        this.cantidadLimite = cantidadLimite;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
