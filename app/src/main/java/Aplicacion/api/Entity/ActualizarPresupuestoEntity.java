package Aplicacion.api.Entity;

public class ActualizarPresupuestoEntity {

    private Double cantidadLimite;
    private String descripcion;

    public ActualizarPresupuestoEntity(Double cantidadLimite, String descripcion) {
        this.cantidadLimite = cantidadLimite;
        this.descripcion = descripcion;
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
}
