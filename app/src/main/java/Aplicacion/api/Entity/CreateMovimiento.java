package Aplicacion.api.Entity;

public class CreateMovimiento {

    long id_User;
    double cantidad;
    String descripcion;
    int tipo;

    public CreateMovimiento(long id_User, double cantidad, String descripcion, int tipo) {
        this.id_User = id_User;
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.tipo = tipo;
    }

    public long getId_User() {
        return id_User;
    }

    public void setId_User(long id_User) {
        this.id_User = id_User;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
}
