package Aplicacion.api.Entity;

public class Movimiento {

    private long id;
    private Double cantidad;
    private String descripcion;
    private String fecha;  // Cambié 'LocalDateTime' a 'String'
    private Tipo tipo;

    public Movimiento(long id, Double cantidad, String descripcion, String fecha, Tipo tipo) {
        this.id = id;
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.tipo = tipo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }
}