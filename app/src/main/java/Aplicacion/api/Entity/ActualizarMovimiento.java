package Aplicacion.api.Entity;

public class ActualizarMovimiento {
    private Double cantidad;
    private String descripcion;
    private Tipo tipo;

    public ActualizarMovimiento(Double cantidad, String descripcion, Tipo tipo) {
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.tipo = tipo;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }
}
